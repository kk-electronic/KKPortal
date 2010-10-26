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
package com.kk_electronic.kkportal.core.model;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.HasData;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.security.User;
import com.kk_electronic.kkportal.core.services.ModuleService;
import com.kk_electronic.kkportal.core.services.ModuleService.TabInfo;

public class TabInfoProvider{
	@SuppressWarnings("unused")
	private final ModuleService moduleService;
	private List<TabInfo> tabInfos;
	
	private AsyncCallback<List<TabInfo>> tabscallback = new AsyncCallback<List<TabInfo>>() {
		
		@Override
		public void onSuccess(List<TabInfo> tabInfos) {
			TabInfoProvider.this.tabInfos = tabInfos;
			updateDisplay();
		}	
		
		@Override
		public void onFailure(Throwable caught) {
			GWT.log("ERROR-Could not fetch tabs: " + caught);
		}
	};

	@Inject
	public TabInfoProvider(User user, ModuleService moduleService) {
		this.moduleService = moduleService;
		moduleService.getTabs(user, tabscallback );
	}
	
	HasData<TabInfo> display;
	public void addDataDisplay(HasData<TabInfo> newDisplay){
		if(display != null) GWT.log("display overridden");

		display = newDisplay;
		updateDisplay();
	}
	
	private void updateDisplay() {
		if(display == null || tabInfos == null) return;
		display.setRowData(0, tabInfos);
	}
}
