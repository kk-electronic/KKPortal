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
package com.kk_electronic.kkportal.core.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kk_electronic.kkportal.core.rpc.RemoteService;
import com.kk_electronic.kkportal.core.tabs.TabInfo;

/**
 * This Service is responsible for the communication about Modules and Tabs
 * 
 * @author Jes Andersen
 */
public interface ModuleService extends RemoteService {
	
	/**
	 * Bean class for Module Information
	 * 
	 * @author Jes Andersen
	 */
	public interface ModuleInfo  {
		public int getId();
		public int getHeight();
		public void setHeight(int height);
		public int getType();
		public void setType(int code);
	}

	/**
	 * Gets the tabs for the given user.
	 * 
	 * @param asyncCallback returns a list of {@link TabInfo} to onSucces if successful
	 * @deprecated Moved and renamed in TabService
	 */
	void getTabs(AsyncCallback<List<TabInfo>> asyncCallback);
	
	/**
	 * Get the information related to the specific Module Instances
	 * 
	 * @param ids Module Instance id's
	 */
	void getModuleInfo(List<Integer> ids,AsyncCallback<List<ModuleInfo>> callback);
		
	/**
	 * Create a new Module instance based on a specific typeId
	 * 
	 * @param typeId the type identifier of a Module.
	 */
	void createModule(Integer typeId, AsyncCallback<Integer> asyncCallback);

	/**
	 * Updates a Tab's module list.
	 * 
	 * @param id the Tab's identity
	 * @param moduleIds a List of Module Instances on the tab.
	 * @deprecated Moved and renamed in TabService
	 */
	void setModulesIdsOnTab(Integer id, List<List<Integer>> moduleIds,AsyncCallback<?> callback);

	/**
	 * Saves the height of a Module Instance
	 * 
	 * @param moduleId Module Instance id's
	 * @param height height in pixels
	 */
	void setModuleHeight(Integer moduleId, Integer height, AsyncCallback<?> callback);
}