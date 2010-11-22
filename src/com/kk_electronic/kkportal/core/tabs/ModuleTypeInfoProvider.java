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
import com.kk_electronic.kkportal.core.reflection.ModuleMap;
import com.kk_electronic.kkportal.core.services.ModuleTypeInfo;
import com.kk_electronic.kkportal.debug.modules.UsageGraph;
import com.kk_electronic.kkportal.examples.modules.HelloWorld;
import com.kk_electronic.kkportal.examples.modules.Inotify;
import com.kk_electronic.kkportal.examples.modules.Wall;

//TODO: Generate this class
public class ModuleTypeInfoProvider {
	HashMap<Integer, ModuleTypeInfo> map = new HashMap<Integer, ModuleTypeInfo>();

	@Inject
	public ModuleTypeInfoProvider(ModuleMap moduleMap) {
		map.put(0, new ModuleTypeInfo(0, moduleMap
				.getKeyFromClass(HelloWorld.class), "Hello World"));
		map.put(1, new ModuleTypeInfo(1, moduleMap
				.getKeyFromClass(Inotify.class), "Inotify"));
		map.put(2, new ModuleTypeInfo(2, moduleMap
				.getKeyFromClass(Wall.class), "Wall"));
		map.put(3, new ModuleTypeInfo(3, moduleMap
				.getKeyFromClass(UsageGraph.class), "Cpu Usage"));
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
