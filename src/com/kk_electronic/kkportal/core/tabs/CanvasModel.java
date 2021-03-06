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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.event.TabSelectedEvent;
import com.kk_electronic.kkportal.core.services.ModuleService;
import com.kk_electronic.kkportal.core.services.ModuleService.ModuleInfo;
import com.kk_electronic.kkportal.core.util.Sink;

/**
 * This class is responsible for running the modules on the current page, and stop them
 * when the user navigates away.
 * @author albatros
 *
 */
public class CanvasModel implements TabSelectedEvent.Handler {

	HashMap<Integer, ModuleInfo> map = new HashMap<Integer, ModuleInfo>();
	List<Sink<List<List<ModuleInfo>>>> displays = new LinkedList<Sink<List<List<ModuleInfo>>>>();
	private final ModuleService moduleService;
	private AsyncCallback<List<ModuleInfo>> infoCallback = new AsyncCallback<List<ModuleInfo>>(){

		@Override
		public void onFailure(Throwable caught) {
			GWT.log("ModuleRunner could not get info for requested types",caught);
		}

		@Override
		public void onSuccess(List<ModuleInfo> result) {
			for(ModuleInfo moduleInfo:result){
				if(missing.remove(moduleInfo.getId())){
					map.put(moduleInfo.getId(), moduleInfo);
				} else {
					assert false:"Server returned a nonrequested moduleInfo";
				}
			}
			if(missing.isEmpty()){
				missing = null;
				updateFromSelection();
			}
		}
	};
	private final TabsModel tabsModel;
	
	@Inject
	public CanvasModel(TabsModel tabsModel,ModuleService moduleService) {
		this.tabsModel = tabsModel;
		this.moduleService = moduleService;
		tabsModel.addTabSelectedHandler(this);
		updateFromSelection();
	}
	
	public void addCurrentCanvasDisplay(Sink<List<List<ModuleInfo>>> sink) {
		displays.add(sink);
	}

	private void updateFromSelection() {
		TabInfo tabInfo = tabsModel.getSelectedTab();
		if(tabInfo == null){
			updateDisplays(new ArrayList<List<ModuleInfo>>());
			return;
		}
		addMissing(tabInfo.getModuleIds());
		if(missing != null){
			requestMissing();
			return;
		}
		updateDisplays(getModuleIds(tabInfo.getModuleIds()));
	}

	private List<List<ModuleInfo>> getModuleIds(List<List<Integer>> moduleIds) {
		List<List<ModuleInfo>> ret = new ArrayList<List<ModuleInfo>>();
		if (moduleIds != null) {
			for(List<Integer> column:moduleIds){
				List<ModuleInfo> columnModule = new ArrayList<ModuleInfo>();
				for(int id : column){
					columnModule.add(map.get(id));
				}
				ret.add(columnModule);
			}			
		}
		return ret;
	}

	private void requestMissing() {
		ArrayList<Integer> list = new ArrayList<Integer>(missing);
		moduleService.getModuleInfo(list, infoCallback );
		GWT.log("CANVASMODEL-Need module info on " + missing);
	}

	private void updateDisplays(List<List<ModuleInfo>> newvalue) {
		for(Sink<List<List<ModuleInfo>>> sink:displays){
			sink.setSinkValue(newvalue);
		}
	}
	
	private void addMissing(List<List<Integer>> moduleIds) 
	{
		if(moduleIds == null){
			return;
		}
		for(List<Integer> column:moduleIds){
			for(Integer id:column){
				if(! map.containsKey(id)) addRequest(id);
			}
		}
	}

	HashSet<Integer> missing;
	private void addRequest(int i){
		if(missing == null) missing = new HashSet<Integer>();
		missing.add(i);
	}

	@Override
	public void onTabSelected(TabSelectedEvent event) {
		updateFromSelection();
	}
}
