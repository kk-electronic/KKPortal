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
import java.util.Vector;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.kk_electronic.kkportal.core.moduleview.Module;
import com.kk_electronic.kkportal.core.moduleview.ModuleHash;
import com.kk_electronic.kkportal.core.moduleview.ModuleName;
import com.kk_electronic.kkportal.core.tabs.ModuleTypeInfoHelper;

public class ModuleTypeInfoHelperGenerator extends Generator {

	private String packageName;
	private String className;
	private TypeOracle typeOracle;
	private JClassType classType;
	private TreeLogger logger;
	
	@Override
	public String generate(TreeLogger logger, GeneratorContext context,
			String typeName) throws UnableToCompleteException {
		this.logger = logger;
		try {
	    	typeOracle = context.getTypeOracle();
	    	classType = typeOracle.getType(typeName);
	    	packageName = classType.getPackage().getName();
	    	className = classType.getSimpleSourceName() + "Impl";
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
		
		composer.addImplementedInterface(ModuleTypeInfoHelper.class.getCanonicalName());
		
		composer.addImport(ModuleTypeInfoHelper.class.getCanonicalName());

		SourceWriter sourceWriter = composer.createSourceWriter(context,printWriter);
		
		writeClass(sourceWriter);
		
		sourceWriter.outdent();
		sourceWriter.println("}");
		
		context.commit(logger, printWriter);
	}

	private void writeClass(SourceWriter sw) {
		sw.println("@Override");
		sw.println("public void insertData(hasAddModuleTypeInfo provider) {");
		sw.indent();
		for(JClassType j : getClasses()) {
			Integer id = getModuleID(j);
			String name = getModuleName(j);
			sw.println("provider.add(" + j.getQualifiedSourceName() + ".class," + id + "," + name + ");");
		}
		sw.outdent();
		sw.println("}");
	}

	
	private String getModuleName(JClassType j) {
		assert(j != null);
		ModuleName moduleNameAnnotation = j.getAnnotation(ModuleName.class);
		if (moduleNameAnnotation != null) {
			return moduleNameAnnotation.value();
		}
		return null;
	}

	private Integer getModuleID(JClassType j) {
		assert(j != null);
		ModuleHash moduleIDAnnotation = j.getAnnotation(ModuleHash.class);
		if (moduleIDAnnotation != null) {
			return moduleIDAnnotation.value();
		}
		return null;
	}

	private JClassType[] getClasses() {
		Vector<JClassType> vector = new Vector<JClassType>();
		JClassType desiredInterface;
		try {
			desiredInterface = typeOracle.getType(Module.class.getCanonicalName());
		} catch (NotFoundException e) {
			logger.log(TreeLogger.ERROR, "Can't find marker interface", e);
			return new JClassType[]{};
		}
		for(JClassType j : typeOracle.getTypes()){
			if(j.isAssignableTo(desiredInterface) && !j.isAbstract()){
				vector.add(j);
			}
		}
		JClassType[] classArray = new JClassType[vector.size()];
		vector.copyInto(classArray);
		return classArray;
	}
		
}