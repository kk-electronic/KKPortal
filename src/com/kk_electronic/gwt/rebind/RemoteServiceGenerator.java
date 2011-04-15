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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.JWildcardType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.kk_electronic.kkportal.core.reflection.Injection;
import com.kk_electronic.kkportal.core.rpc.DelayingDispatcher;
import com.kk_electronic.kkportal.core.rpc.Dispatcher;
import com.kk_electronic.kkportal.core.rpc.Request;
import com.kk_electronic.kkportal.core.rpc.RpcDispatcher;

public class RemoteServiceGenerator extends Generator {

	private String packageName;
	private String className;
	private TypeOracle typeOracle;
	private JClassType classType;
	private Class<? extends Dispatcher> dispatcher;
	
	@Override
	public String generate(TreeLogger logger, GeneratorContext context,
			String typeName) throws UnableToCompleteException {
		try {
	    	typeOracle = context.getTypeOracle();
	    	classType = typeOracle.getType(typeName);
	    	if(classType.isClass() != null){
	    		return typeName;
	    	}
	    	packageName = classType.getPackage().getName();
	    	className = classType.getSimpleSourceName() + "Impl";
	    	classType.getAnnotation(GinModules.class);
	    	generateClass(logger, context);
		} catch (NotFoundException e) {
			logger.log(TreeLogger.ERROR, "Exception during ClassMap creation.", e);
			throw new UnableToCompleteException();
		}
		return packageName + "." + className;
	}

	private void generateClass(TreeLogger logger, GeneratorContext context) throws UnableToCompleteException, NotFoundException {
		PrintWriter printWriter = context.tryCreate(logger, packageName, className);
		
		if (printWriter == null){
			return;
		}
		
		ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName,className);

		dispatcher=findTargetDispatcher(classType);

		composer.addImport(dispatcher.getCanonicalName());
		composer.addImport(Dispatcher.class.getCanonicalName());
		composer.addImport(Request.class.getCanonicalName());
		composer.addImport(AsyncCallback.class.getCanonicalName());
		composer.addImport(DelayingDispatcher.class.getCanonicalName());
		composer.addImport(Injection.class.getCanonicalName());
		composer.addImport(GWT.class.getCanonicalName());
		
		composer.addImplementedInterface(classType.getQualifiedSourceName());
		
		SourceWriter sourceWriter = composer.createSourceWriter(context,printWriter);
		
		writeConstructor(sourceWriter);
		writeMethods(sourceWriter);
		
		sourceWriter.outdent();
		sourceWriter.println("}");
		
		context.commit(logger, printWriter);
	}
		
	private Class<? extends Dispatcher> findTargetDispatcher(JClassType classType2) {
		Dispatch dispatch = classType2.getAnnotation(Dispatch.class);
		if (dispatch != null){
			return dispatch.value();
		}
		return RpcDispatcher.class;
	}

	private void writeConstructor(SourceWriter sw) {
		sw.println("protected "+Dispatcher.class.getSimpleName()+" dispatcher;");
		sw.println();
		sw.println("public "+className+"() {");
		sw.indent();
		sw.println("final DelayingDispatcher d = new DelayingDispatcher();");
		sw.println("this.dispatcher = d;");
		sw.println("Injection.create("+dispatcher.getSimpleName()+".class, new AsyncCallback<"+dispatcher.getSimpleName()+">() {");
		sw.indent();
		sw.println("@Override");
		sw.println("public void onFailure(Throwable caught) {");
		sw.println("	GWT.log(\"Failed to load dispatcher for "+escape(classType.getSimpleSourceName())+"\");");
		sw.println("}");
		sw.println("");
		sw.println("@Override");
		sw.println("public void onSuccess("+dispatcher.getSimpleName()+" result) {");
		sw.println("	d.setDispatcher(result);");
		sw.println("	dispatcher = result;");
		sw.println("}");
		sw.outdent();
		sw.println("});");
		sw.outdent();
		sw.println("}");
	}

	private void writeMethods(SourceWriter sw) {
		JClassType[] implementedInterfaces = classType.getImplementedInterfaces();
		JMethod[] methods = classType.getMethods();
		int i = 0;
		for(JMethod method : methods){
			writeMethod(sw, classType, method,i++);
		}
		for (JClassType interfaze:implementedInterfaces){
			methods = interfaze.getMethods();
			for(JMethod method : methods){
				writeMethod(sw, interfaze, method,i++);
			}
		}
	}

	private void writeMethod(SourceWriter sw, JClassType interfaze,
			JMethod method, int methodnr) {
		JParameter[] parameters = method.getParameters();
		sw.println();
		sw.print("private Class<?>[] RETVAL_"+methodnr);
		sw.print(" = new Class<?>[]{");
		writeTypeInfo(sw,parameters[parameters.length-1].getType().isParameterized().getTypeArgs()[0]);
		sw.println("};");
		sw.println("@Override");
		sw.print("public void "+method.getName()+"(");
		for(int i=0,c=parameters.length;i<c;i++){
			if(i > 0 ){
				sw.print(",");
			}
			sw.print(parameters[i].getType().getParameterizedQualifiedSourceName());
			sw.print(" ");
			sw.print(parameters[i].getName());
		}
		sw.println(") {");
		sw.indent();
		String callbackname = parameters[parameters.length-1].getName();
		sw.print(Request.class.getCanonicalName() + " r = new " + Request.class.getCanonicalName() + "(" + callbackname
				+ ", RETVAL_" + methodnr + ","
				+ interfaze.getQualifiedSourceName() + ".class,\""
				+ escape(method.getName()) + "\"");
		for(int i=0,c=parameters.length-1;i<c;i++){
			sw.print(",\"");
			sw.print(escape(parameters[i].getName()));
			sw.print("\",");
			sw.print(parameters[i].getName());
		}
		sw.println(");");
		//sw.print("r.setParamNames(");
		//for(int i=0,c=parameters.length-1;i<c;i++){
		//	if(i>0) sw.print(",");
		//	sw.print("\"");
		//	sw.print(escape(parameters[i].getName()));
		//	sw.print("\"");
		//}
		//sw.println(");");
		if(method.getReturnType().isPrimitive() == JPrimitiveType.VOID){
			sw.println("dispatcher.execute(r);");
		} else {
			sw.println("return r;");
		}
		sw.outdent();
		sw.println("}");
	}

	private void writeTypeInfo(SourceWriter sw, JType type){
		JWildcardType w = type.isWildcard();
		if(w != null){
			type = w.getUpperBound();
		}
		sw.print(type.getQualifiedSourceName()+ ".class");
		sw.print(",");
		JParameterizedType x = type.isParameterized();
		if(x != null){
			for(JClassType nestedType : x.getTypeArgs()){
				writeTypeInfo(sw, nestedType);
			}
		}
	}

}