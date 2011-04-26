/*
 * Copyright 2010 kk-electronic a/s. 
 * 
 * This file is part of KKPortal.
 *
 * KKPortal is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KKPortal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KKPortal.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.kk_electronic.gwt.rebind;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JGenericType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.inject.Singleton;
import com.kk_electronic.kkportal.core.rpc.FrameEncoder;
import com.kk_electronic.kkportal.core.rpc.JsonEncoderHelper;
import com.kk_electronic.kkportal.core.rpc.RemoteService;
import com.kk_electronic.kkportal.core.rpc.Rename;
import com.kk_electronic.kkportal.core.rpc.jsonformat.JsonValue;
import com.kk_electronic.kkportal.core.rpc.jsonformat.UnableToDeserialize;
import com.kk_electronic.kkportal.core.rpc.jsonformat.UnableToSerialize;

/**
 * Generates the Helper class for JsonEncoder as well as attempts to implement any missing JsonValue classes. 
 * 
 * @author Rasmus Carlsen
 *
 */
public class JsonEncoderGenerator extends Generator{

	private String packageName;
	private String className;
	private TypeOracle typeOracle;
	private JClassType classType;
	private TreeLogger logger;
	
	private HashMap<String, String> map = new HashMap<String, String>(); 
	private ArrayList<JClassType> worklist = new ArrayList<JClassType>();
	
	@Override
	public String generate(TreeLogger logger, GeneratorContext context,
			String typeName) throws UnableToCompleteException {
		try {
	    	typeOracle = context.getTypeOracle();
	    	classType = typeOracle.getType(typeName);
	    	packageName = classType.getPackage().getName();
	    	className = classType.getSimpleSourceName() + "Impl";
	    	this.logger = logger;
	    	
	    	retrieveInformation();
	    	generateJsonFormatterClasses(context);
	    	generateHelperClass(context);
	    	
		} catch (NotFoundException e) {
			logger.log(TreeLogger.ERROR, "Exception during ClassMap creation.", e);
			throw new UnableToCompleteException();
		}
		return packageName + "." + className;
		//return null;
	}

	/**
	 * @throws UnableToCompleteException 
	 * 
	 */
	private void retrieveInformation() throws UnableToCompleteException {
		JClassType desiredInterface;
		try {
			desiredInterface = typeOracle.getType(AsyncCallback.class.getCanonicalName());
		} catch (NotFoundException e) {
			logger.log(TreeLogger.ERROR, "Unable to retrieve the AsyncCallback class");
			return;
		}
		
		processJsonValueClasses();
		JClassType[] remoteServices = getClasses(RemoteService.class.getCanonicalName(), true);
		for (JClassType service : remoteServices) {
			JMethod[] methods = service.getMethods();
			for (JMethod m : methods) {
				JParameter[] paras = m.getParameters();
				for (JParameter p : paras) {
					JType t = p.getType();

					if (t == null) {
						continue;
					}
					
					JClassType jct = t.isInterface(); 
					if (jct != null) {
						if (jct.isAssignableTo(desiredInterface)) {
							processGenerics(jct);
							jct = null;
						} 
					} else { 
						jct = t.isClass();
					}
					if (jct == null){
						continue;
					}
					processClass(jct);
				}
			}
		}
		
	}

	private void processClass(JClassType jct) {
		processGenerics(jct);
		if(checkClass(jct) && jct.isInterface() == null) {
			processFields(jct);
		}
	}
	
	private void processGenerics(JType jct) {
		JParameterizedType jpt = jct.isParameterized();
		if (jpt == null) {
			return;
		}
		JClassType[] cd = jpt.getTypeArgs();
		for (JClassType type : cd) {
			processClass(type);
		}
	}
	
	private void processFields(JClassType jct) {
		JField[] fields = jct.getFields();
		for (JField jField : fields) {
			JClassType jClass = jField.getType().isClass();
			processGenerics(jField.getType());
			if (jClass != null && !jField.isTransient()) {
				processClass(jClass);
			}
		}
	}
	
	private boolean checkClass(JClassType type) {
		boolean b = false;
		if (!map.containsKey(type.getQualifiedSourceName()) && type.isWildcard() == null && !worklist.contains(type)) {
			b = worklist.add(type); 
		}
		return b;
	}
	
	/**
	 * @throws UnableToCompleteException 
	 * 
	 */
	private void processJsonValueClasses() throws UnableToCompleteException {
		JClassType desiredInterface;
		try {
			desiredInterface = typeOracle.getType(JsonValue.class.getCanonicalName());
		} catch (NotFoundException e) {
			logger.log(TreeLogger.ERROR, "Unable to retrieve the JsonValue class");
			throw new UnableToCompleteException();
		}

		JClassType[] types = getClasses(JsonValue.class.getCanonicalName(), false);
		for (JClassType jClassType : types) {
			JClassType[] interfaces = jClassType.getImplementedInterfaces();
			for (JClassType classType : interfaces) {
				JParameterizedType parameterizedType = classType.isParameterized();
				if (parameterizedType == null || !classType.isAssignableFrom(desiredInterface)) {
					continue;
				}
				String jsonValueClassName = jClassType.getQualifiedSourceName();
				JClassType[] cd = parameterizedType.getTypeArgs();
				for (JClassType typeArg : cd) {
					String originalClassName = typeArg.getQualifiedSourceName();
					map.put(originalClassName, jsonValueClassName);
				}
			}
		}
	}

	/**
	 * @param context
	 * @throws UnableToCompleteException 
	 */
	private void generateJsonFormatterClasses(GeneratorContext context) throws UnableToCompleteException {
		if (worklist == null || worklist.size() == 0) {
			return;
		}
		for (JClassType jc : worklist) {			
			if (!jc.isDefaultInstantiable()) {
				logger.log(TreeLogger.ERROR, "Unable to generate class for " + jc.getSimpleSourceName() + " mising default constructor");
			} else {
				SubClassGenerator gen = new SubClassGenerator();
				String implentationClassName;
				implentationClassName = gen.generateSubClass(context, jc);
				if(implentationClassName == null){
					return;
				}
				map.put(jc.getQualifiedSourceName(), implentationClassName);
			}
		}
	}

	/**
	 * @param context
	 */
	private void generateHelperClass(GeneratorContext context) throws UnableToCompleteException, NotFoundException {
		PrintWriter printWriter = context.tryCreate(logger, packageName, className);
		
		if (printWriter == null) {
			return;
		}
		
		ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName,className);
		
		composer.addImplementedInterface(JsonEncoderHelper.class.getCanonicalName());
		
		composer.addImport(Singleton.class.getCanonicalName());
		composer.addImport(Map.class.getCanonicalName());
		composer.addImport(HashMap.class.getCanonicalName());
		composer.addImport(JsonEncoderHelper.class.getCanonicalName());
		composer.addImport(JsonValue.class.getCanonicalName());
		composer.addAnnotationDeclaration("@Singleton");

		SourceWriter sourceWriter = composer.createSourceWriter(context,printWriter);
		
		writeHelperClass(sourceWriter);
		
		sourceWriter.outdent();
		sourceWriter.println("}");
		
		context.commit(logger, printWriter);		
	}
	
	private void writeHelperClass(SourceWriter sw) {
		//Write variables
		sw.println("private HashMap<Class<?>, JsonValue<?>> map = new HashMap<Class<?>, JsonValue<?>>();");
		sw.println(); // Empty line for style
		
		//Write constructor
		sw.println("public " + className + "() {");
		sw.indent();
		for (Entry<String, String> entry : map.entrySet()) {
			sw.println("map.put(" + entry.getKey() + ".class, new " + entry.getValue() + "());");
		}
		sw.outdent();
		sw.println("}");
		sw.println(); // Empty line for style
		
		//Write functions
		sw.println("@Override");
		sw.println("public Map<Class<?>, JsonValue<?>> getGeneratedMap() {");
		sw.indent();
		sw.println("return map;");
		sw.outdent();
		sw.println("}");
	}

	private JClassType[] getClasses(String classCanonicalName, boolean allowAbstract) {
		Vector<JClassType> vector = new Vector<JClassType>();
		JClassType desiredInterface;
		try {
			desiredInterface = typeOracle.getType(classCanonicalName);
		} catch (NotFoundException e) {
			logger.log(TreeLogger.ERROR, "Can't find marker interface: " + classCanonicalName, e);
			return new JClassType[]{};
		}
		for(JClassType j : typeOracle.getTypes()){
			if(j.isAssignableTo(desiredInterface) && (!j.isAbstract() || allowAbstract)){
				vector.add(j);
			}
		}
		JClassType[] classArray = new JClassType[vector.size()];
		vector.copyInto(classArray);
		return classArray;
	}
	
	private class SubClassGenerator {
		private String packageName;
		private String className;
		private JField[] fields;
		private JClassType jc;
		private String suffix;

		private String getParameterizedSimpleSourceName(JGenericType type){
			 StringBuffer sb = new StringBuffer();

			    sb.append('<');
			    boolean needComma = false;
			    for (JClassType typeParam : type.getTypeParameters()) {
			      if (needComma) {
			        sb.append(", ");
			      } else {
			        needComma = true;
			      }
			      sb.append(typeParam.getParameterizedQualifiedSourceName());
			    }
			    sb.append('>');
			    return sb.toString();
		}
		/**
		 * @param context
		 * @param localPackageName
		 * @param jc
		 * @param localClassName
		 */
		public String generateSubClass(GeneratorContext context, JClassType jc) {
			this.jc = jc;
			this.className = "Json" + jc.getSimpleSourceName();
			JParameterizedType parameterizedType = jc.isParameterized();
			if(parameterizedType != null){
				this.jc = parameterizedType.getBaseType();
				this.suffix = getParameterizedSimpleSourceName(parameterizedType.getBaseType());
			} else {
				this.suffix = "";
			}
			this.packageName = jc.getPackage().getName();
			PrintWriter printWriter = context.tryCreate(logger, packageName, className);
			
			if (printWriter == null) {
				return null;
			}

			ClassSourceFileComposerFactory composer = null;
			
			/*
			 * Changes the name to fit with any generics of the object.
			 */
			composer = new ClassSourceFileComposerFactory(packageName, className + suffix);
			fields = getFields(this.jc);
			
			composer.addImport(List.class.getCanonicalName());
			composer.addImport(JSONValue.class.getCanonicalName());
			composer.addImport(JSONObject.class.getCanonicalName());
			composer.addImport(FrameEncoder.class.getCanonicalName());
			composer.addImport(UnableToSerialize.class.getCanonicalName());
			composer.addImport(UnableToDeserialize.class.getCanonicalName());
			
			composer.addImplementedInterface(JsonValue.class.getCanonicalName() + "<" + jc.getQualifiedSourceName() + suffix + ">");
			
			SourceWriter sourceWriter = composer.createSourceWriter(context,printWriter);
			
			writeFromJson(sourceWriter);
			writeToJson(sourceWriter);
			writeNatives(sourceWriter);
			
			sourceWriter.outdent();
			sourceWriter.println("}");
			
			context.commit(logger, printWriter);
			return packageName + "." + className;
		}

		private void writeNatives(SourceWriter sw) {
			String s = "obj.@" + jc.getQualifiedSourceName() + "::"; 

			//Native set function
			sw.println();
			sw.print("private native void setFields(" + jc.getQualifiedSourceName() + suffix + " obj");
			for (JField f : fields) {
				sw.print(", " + getFieldType(f) + " " + f.getName());
			}
			sw.println(") /*-{");
			sw.indent();
			for (JField f : fields) {
				sw.println(s + f.getName() + " = " + f.getName() + ";");
			}
			sw.outdent();
			sw.println("}-*/;");
			
			//Native get functions

			for (JField f : fields) {
				sw.println();
				sw.println("private native " + getFieldType(f) + " get" + f.getName() + "(" + jc.getQualifiedSourceName() + suffix + " obj) /*-{");
				sw.indent();
				sw.println("return " + s + f.getName() + ";");
				sw.outdent();
				sw.println("}-*/;");
			}
		}

		private String getFieldType(JField f) {
			return f.getType().getParameterizedQualifiedSourceName();
		}
						
		private String getFieldType(JField f, boolean addClass) {
			JType j = f.getType();
			String type;
			if (addClass) {
				if (j.isParameterized() != null) {
					type = recursiveTypeFind(j);
				} else {
					type = j.getQualifiedSourceName();
					if (addClass) {
						type = type + ".class";
					}
				}
			} else {
				type = j.getParameterizedQualifiedSourceName();
			}
			return type;
		}
		
		private String recursiveTypeFind(JType j) {
			String type = j.getQualifiedSourceName();
			type = type + ".class";

			if (j.isParameterized() != null) {
				JClassType[] a = j.isParameterized().getTypeArgs();

				boolean first = true;
				for (JClassType jClassType : a) {
					if (!first) {
						type = type + ", ";
						first = false;
					}
					type = type + "," + recursiveTypeFind(jClassType);
				}
			}
			return type;
		}
		
		private void writeToJson(SourceWriter sw) {
			sw.println();
			sw.println("@Override");
			sw.println("public void toJson(StringBuilder response, " + jc.getQualifiedSourceName() + suffix + " object,");
			sw.indent();
			sw.indentln("FrameEncoder<JSONValue> encoder) throws UnableToSerialize {");
			sw.println("response.append(\"{\");");
			Boolean first = true;
			String t = "response.append(\",\");";
			for (JField f : fields) {
				if (!first) {
					sw.println();
					sw.println(t);
					sw.println();
				} else {
					sw.println();
					first = false;
				}
				sw.println("encoder.encode(\"" + getJsonName(f) + "\", response);");
				sw.println("response.append(\":\");");
				sw.println("encoder.encode(get" + f.getName() + "(object), response);");
			}
			sw.println();
			sw.println("response.append(\"}\");");
			sw.outdent();
			sw.println("}");
		}

		private void writeFromJson(SourceWriter sw) {
			//Begin Function
			sw.println();
			sw.println("@Override");
			sw.println("public " + jc.getQualifiedSourceName() + suffix + " fromJson(JSONValue jsonValue, List<Class<?>> subtypes,");
			sw.indent();
			sw.indentln("FrameEncoder<JSONValue> encoder) throws UnableToDeserialize {");

			//boiler plate null check
			sw.println("if (jsonValue.isObject() == null)");
			sw.indentln("throw new UnableToDeserialize(\"Expected Json Object\");");
			sw.println("JSONObject jsonObject = jsonValue.isObject();");
			sw.println();
			
			//make vars
			for (JField f : fields) {
				sw.println(getFieldType(f) + " " + f.getName() + " = null;");
			}
			sw.println();
			
			//validate
			for (JField f : fields) {
				sw.print(f.getName() + " = encoder.validate(jsonObject.get(\"" + getJsonName(f) + "\"), ");
				if(f.getType().isTypeParameter() == null) {
					sw.println(f.getName() + ", new Class<?>[]{" + getFieldType(f, true) + "});");
				} else {
					sw.println(f.getName() + ", subtypes);");
				}
			}
			sw.println();
			
			//create new object
			sw.println(jc.getQualifiedSourceName() + suffix + " obj = new " + jc.getQualifiedSourceName() + suffix + "();");
			
			//Call native set
			sw.print("setFields(obj");
			for (JField f : fields) {
				sw.print(", " + f.getName());
			}
			sw.println(");");
			
			//return object
			sw.println("return obj;");
			
			//End function
			sw.outdent();
			sw.println("}");
		}

		/**
		 * @param f
		 * @return
		 */
		private String getJsonName(JField f) {
			Rename rename = f.getAnnotation(Rename.class);
			if (rename != null) {
				return rename.value();
			} else {
				return f.getName();
			}
		}
		
		private JField[] getFields(JClassType j) {
			assert(j != null);
			Vector<JField> vector = new Vector<JField>();
			 
			for (JField f : j.getFields()) {
				if(!f.isTransient()){
					vector.add(f);
				}
			}
			JField[] fieldArray = new JField[vector.size()];
			vector.copyInto(fieldArray);
			Arrays.sort(fieldArray, new Comparator<JField>() {
				@Override
				public int compare(JField o1, JField o2) {
					return getJsonName(o1).compareTo(getJsonName(o2));
				}
			});
			return fieldArray;
		}


	}
}
