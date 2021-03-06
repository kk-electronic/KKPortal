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
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kk_electronic.kkportal.core.activity.LocationInfo;
import com.kk_electronic.kkportal.core.event.LocationChangedEvent;
import com.kk_electronic.kkportal.core.event.NewContentEvent;
import com.kk_electronic.kkportal.core.event.TabSelectedEvent;
import com.kk_electronic.kkportal.core.security.IdentityProvider;
import com.kk_electronic.kkportal.core.security.NewPrimaryIdentityEvent;
import com.kk_electronic.kkportal.core.services.ModuleService;
import com.kk_electronic.kkportal.core.services.ModuleTypeInfo;
import com.kk_electronic.kkportal.core.services.TabService;
import com.kk_electronic.kkportal.core.util.Pair;

@Singleton
public class TabsModel implements NewPrimaryIdentityEvent.Handler, LocationChangedEvent.Handler{
	List<HasData<TabInfo>> displays = new LinkedList<HasData<TabInfo>>();
	private final LocationInfo locationInfo;
	
	private final ModuleService moduleService;
	private final TabService tabService;
	private final SingleSelectionModel<TabInfo> selectionModel;
	private List<TabInfo> tabInfos;
	private final EventBus eventBus;
	private Integer localTabs = -1; // First Local tab this counter lowers itself on use.
	
	@Inject
	public TabsModel(IdentityProvider identityProvider, ModuleService moduleService,LocationInfo locationInfo,EventBus eventBus, TabService tabService) {
		this.moduleService = moduleService;
		this.tabService = tabService;
		this.locationInfo = locationInfo;
		this.eventBus = eventBus;
		eventBus.addHandler(LocationChangedEvent.TYPE, this);
		selectionModel = new SingleSelectionModel<TabInfo>();
		identityProvider.addNewPrimaryIdentityEventHandler(this);
		if(identityProvider.getPrimaryIdentity() != null){
			getTabs();
		}
	}

	public void addDataDisplay(HasData<TabInfo> newDisplay){
		if(displays != null) GWT.log("display overridden");

		displays.add(newDisplay);
		updateDisplays();
	}

	private TabInfo findSelectedTabInfo(List<TabInfo> tabInfos){
		if(locationInfo.getSubint() == null) return null;
		return findTabInfo(tabInfos, locationInfo.getSubint());
	}

	private TabInfo findTabInfo(List<TabInfo> tabInfos, Integer id) {
		if (id == null) return null;
		for(TabInfo tabInfo : tabInfos){
			if(id.equals(tabInfo.getId())){
				return tabInfo;
			}
		}
		return null;
	}
	
	public int getMaxWidth() {
		//TODO:iterate and find
		return 120;
	}

	protected void getTabs() {
		tabService.retrieve(new AsyncCallback<List<TabInfo>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("ERROR - Could not fetch tabs: " + caught);
			}	
			
			@Override
			public void onSuccess(List<TabInfo> tabInfos) {
				setTabInfos(tabInfos);
			}
		});
	}
	
	protected void setTabInfos(List<TabInfo> tabInfos) {
		this.tabInfos = tabInfos;
		TabInfo selectedInfo = findSelectedTabInfo(tabInfos);
		if(selectedInfo == null && tabInfos.size()>0){
			selectedInfo = tabInfos.get(0);
		}
		setSelectedWithoutCheck(selectedInfo);
	}
	
	public void setSelectedTab(TabInfo tabInfo){
		if(!tabInfos.contains(tabInfo)){
			return;
		}
		setSelectedWithoutCheck(tabInfo);
	}
	
	public TabInfo getSelectedTab(){
		return selectionModel.getSelectedObject();
	}
	
	private void setSelectedWithoutCheck(TabInfo tabInfo){
		selectionModel.setSelected(tabInfo, true);
		eventBus.fireEventFromSource(new TabSelectedEvent(tabInfo), this);
		updateDisplays();
	}
	
	
	private void updateDisplays() {
		if(tabInfos == null) return;
		for(HasData<TabInfo> display:displays){
			display.setRowData(0, tabInfos);
		}
	}

	@Override
	public void onNewPrimaryIdentity(NewPrimaryIdentityEvent event) {
		if(event.getIdentity() == null){
			setTabInfos(new ArrayList<TabInfo>());
			return;
		}
		getTabs();
	}

	public HandlerRegistration addTabSelectedHandler(TabSelectedEvent.Handler handler) {
		return eventBus.addHandlerToSource(TabSelectedEvent.TYPE, this, handler); 
	}
	
	public HandlerRegistration addNewContentEventHandler(NewContentEvent.Handler handler) {
		return eventBus.addHandlerToSource(NewContentEvent.TYPE, this, handler);
	}

	public SelectionModel<? super TabInfo> getSelectionModel() {
		return selectionModel;
	}

	public void addNewModule(final TabInfo tabInfo, final ModuleTypeInfo selectedObject) {
		if(tabInfo == null){
			Window.alert("The blue sock found");
		}
		moduleService.createModule(selectedObject.getType_id(),new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("TabsModel-Could not create module",caught);
			}

			@Override
			public void onSuccess(Integer result) {
				addToCorrectColumn(tabInfo,result);
				eventBus.fireEventFromSource(new NewContentEvent(selectedObject, tabInfo), this);
			}
		});
	}

	protected void addToCorrectColumn(final TabInfo tabInfo, Integer result) {
		List<Integer> minColumn = null;
		if(tabInfo.getModuleIds() != null && !tabInfo.getModuleIds().isEmpty()){
			for(List<Integer> search:tabInfo.getModuleIds()){
				if(minColumn == null || minColumn.size()>search.size()){
					minColumn = search;
				}
			}
		} else {
			List<List<Integer>> newList = new ArrayList<List<Integer>>();
			tabInfo.setModuleIds(newList);
			minColumn = new ArrayList<Integer>();
			newList.add(minColumn);
		}
		minColumn.add(result);
		setModuleIds(tabInfo, tabInfo.getModuleIds());
	}

	/**
	 * <p>
	 * Called when the address of the page is changed by the browser, user or external factors,
	 * it checks whether a new tab is selected based on the address.
	 * </p>
	 * <p>
	 * This allows bookmarking of specific tabs.
	 * </p> 
	 */
	@Override
	public void onLocationChanged(LocationChangedEvent event) {
		if(tabInfos != null){
			TabInfo tabInfo = findSelectedTabInfo(tabInfos);
			setSelectedWithoutCheck(tabInfo);
		}
	}

	public void setModuleIds(final TabInfo tabInfo,
			List<List<Integer>> newIds) {
		if(!tabInfos.contains(tabInfo)){
			return;
		}
		final TabInfo newTabInfo = new TabInfo(tabInfo.getId(),tabInfo.getName(),newIds);
		tabInfos.set(tabInfos.indexOf(tabInfo), newTabInfo);
		setSelectedWithoutCheck(newTabInfo);
		Scheduler.get().scheduleDeferred(new Command() {
			
			@Override
			public void execute() {
				saveTabInfo(tabInfo, newTabInfo);				
			}
		});
	}

	private void saveTabInfo(final TabInfo tabInfo, final TabInfo newTabInfo) {
		if(newTabInfo == null){
			Window.alert("Missing sock found");
		}
		tabService.update(newTabInfo, new AsyncCallback<Object>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("TabsModel-Save failed",caught);
				// roll back changes in the local data storage
				tabInfos.set(tabInfos.indexOf(newTabInfo), tabInfo);
				// exchanged the selected tab if it's the new tab
				if(selectionModel.isSelected(newTabInfo)) {
					setSelectedWithoutCheck(tabInfo);
				}
			}

			@Override
			public void onSuccess(Object result) {
				updateDisplays();
			}
		});
	}
	
	public void setModuleHeight(int moduleId, int height) {
		moduleService.setModuleHeight(moduleId, height, new AsyncCallback<Object>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("ModuleHeight-Save failed",caught);
			}

			@Override
			public void onSuccess(Object result) {
			}
		});
		
	}
	
	/**
	 * @param name
	 * @param moduleIds 
	 * @return
	 */
	public TabInfo createTab(final String name, final List<List<Integer>> moduleIds) {
		final TabInfo t = new TabInfo(localTabs--, name, moduleIds);
		tabInfos.add(t);
		setSelectedWithoutCheck(t);
		createTab(t);
		return t;
	}
	
	public void createTab(final TabInfo info) {
		tabService.create(info, new AsyncCallback<Integer>(){
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Creatation of new Tab failed: ", caught);
			}

			@Override
			public void onSuccess(Integer result) {
				info.setId(result);
				updateDisplays();
			}
		});
	}
	
	public void deleteTab(final TabInfo tabInfo) {
		if(selectionModel.getSelectedObject() == tabInfo){
			int i = tabInfos.indexOf(tabInfo);
			if (i > 0){
				selectionModel.setSelected(tabInfos.get(i-1), true);
			} else if ( !tabInfos.isEmpty() ){
				selectionModel.setSelected(tabInfos.get(0), true);
			}
		}
		// Remove Local
		tabInfos.remove(tabInfo);
		
		if (tabInfo.getId() == null ) {
			localTabs++;
			return;
		}
		// Remove remote if tabInfo has an id.
		tabService.delete(tabInfo.getId(), new AsyncCallback<Object>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Deletion of tab id:" + tabInfo.getId() + " failed", caught);
			}

			@Override
			public void onSuccess(Object result) {
			}
		});
		updateDisplays();
	}
	
	public void updateTab(final TabInfo tabInfo) {
		if (tabInfo.getId() == null || tabInfo.getId() < 0) {
			createTab(tabInfo);
			return;
		}
		TabInfo oldTab = findTabInfo(this.tabInfos, tabInfo.getId());
		saveTabInfo(oldTab, tabInfo);
	}
	
	public TabInfo createEmptyLocalTab() {
		TabInfo tabInfo = new TabInfo(localTabs--, "New Tab", new ArrayList<List<Integer>>());
		// Create local tab
		tabInfos.add(tabInfo);
		// Select new Tab
		setSelectedWithoutCheck(tabInfo);
		return tabInfo;
	}

	/**
	 * @param list
	 */
	public void setModuleHeight(List<Pair<Integer, Integer>> list) {
		moduleService.setModuleHeights(list, new AsyncCallback<Object>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("ModuleHeight-Save failed",caught);
			}

			@Override
			public void onSuccess(Object result) {
			}
		});
		
	}
}
