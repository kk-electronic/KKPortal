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
package com.kk_electronic.kkportal.core.tabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.HasData;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.moduleview.Module;
import com.kk_electronic.kkportal.core.reflection.ModuleMap;
import com.kk_electronic.kkportal.core.services.ModuleTypeInfo;

public class ModuleTypeInfoProvider implements hasAddModuleTypeInfo {
	HashMap<Integer, ModuleTypeInfo> map = new HashMap<Integer, ModuleTypeInfo>();
	private final ModuleMap moduleMap;

	@Inject
	public ModuleTypeInfoProvider(ModuleMap moduleMap, ModuleTypeInfoHelper data) {
		this.moduleMap = moduleMap;
		data.insertData(this);
	}
	
	/**
	 * Used for adding modules to the internal Data structure
	 * 
	 * @see com.kk_electronic.kkportal.core.tabs.hasAddModuleTypeInfo#add(java.lang.Class, java.lang.Integer, java.lang.String)
	 */
	public void add(Class<? extends Module> clazz,Integer id,String name){
		if (name == null){
			name = getNameFromClass(clazz);
		}
		if (id == null){
			id = getKetFromClass(clazz);
		}
		map.put(id, new ModuleTypeInfo(id, moduleMap
				.getKeyFromClass(clazz), name));
	}
		
	private Integer getKetFromClass(Class<? extends Module> clazz) {
		return clazz.getName().hashCode();
	}

	private String getNameFromClass(Class<? extends Module> clazz) {
		String s = clazz.getName();
		s = s.substring(s.lastIndexOf('.')+1);
		StringBuilder sb = new StringBuilder();
		boolean canAdd = false;
		for(int i=0,l=s.length();i<l;i++){
			char c = s.charAt(i);
			if(canAdd && Character.isUpperCase(c)){
				canAdd = false;
				sb.append(' ');
			}
			sb.append(c);
			if(Character.isLowerCase(c)){
				canAdd = true;
			}
		}
		return sb.toString();
	}

	public void get(Integer typeid, AsyncCallback<ModuleTypeInfo> callback) {
		if (map.containsKey(typeid)) {
			callback.onSuccess(map.get(typeid));
		} else {
			callback.onFailure(new Exception(
					"Could not find ModuleTypeInfo for type:" + typeid));
		}
	}

	public void getAll(AsyncCallback<List<ModuleTypeInfo>> callback) {
		callback.onSuccess(new ArrayList<ModuleTypeInfo>(map.values()));
	}

	public void addDisplay(HasData<ModuleTypeInfo> list) {
		list.setRowData(0, new ArrayList<ModuleTypeInfo>(map.values()));
	}	
}
