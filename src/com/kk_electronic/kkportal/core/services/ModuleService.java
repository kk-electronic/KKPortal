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
import com.kk_electronic.kkportal.core.security.Identity;
import com.kk_electronic.kkportal.core.tabs.TabInfo;

public interface ModuleService extends RemoteService {
	public interface ModuleInfo  {
		public int getId();
		public int getHeight();
		public void setHeight(int height);
		public int getType();
		public void setType(int code);
	}

	void getTabs(Identity user,AsyncCallback<List<TabInfo>> asyncCallback);
	
	void getModuleInfo(List<Integer> ids,AsyncCallback<List<ModuleInfo>> callback);
		
	void createModule(Identity identity, Integer typeId, AsyncCallback<Integer> asyncCallback);

	void setModulesIdsOnTab(Integer id, List<List<Integer>> moduleIds,AsyncCallback<?> callback);
	void setModuleHeight(Integer moduleId, Integer height, AsyncCallback<?> callback);
}
