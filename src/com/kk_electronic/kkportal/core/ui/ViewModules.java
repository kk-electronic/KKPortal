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
package com.kk_electronic.kkportal.core.ui;


import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.activity.Activity;
import com.kk_electronic.kkportal.core.event.ContentChangedEvent;
import com.kk_electronic.kkportal.core.services.ModuleService.ModuleInfo;
import com.kk_electronic.kkportal.core.tabs.CanvasModel;
import com.kk_electronic.kkportal.core.tabs.TabsModel;
import com.kk_electronic.kkportal.core.util.Sink;

/**
 * Note this is not a good example yet. Does not have View-Presenter separation
 * 
 * @author Jes Andersen type filter text
 */
public class ViewModules implements Activity, ContentChangedEvent.Handler, Sink<List<List<ModuleInfo>>> {
	private final ModuleDisplay moduleDisplay;
	protected List<List<ModuleInfo>> moduleList;
	private final TabDisplay tabDisplay;

	@Inject
	public ViewModules(ModuleDisplay moduleDisplay,TabDisplay tabDisplay, EventBus eventBus,TabsModel tabsModel,CanvasModel canvasModel) {
		this.moduleDisplay = moduleDisplay;
		this.tabDisplay = tabDisplay;
		eventBus.addHandler(ContentChangedEvent.TYPE, this);
		moduleDisplay.setViewModules(this);
		canvasModel.addCurrentCanvasDisplay(this);
	}
	
	protected boolean removeWindow(ModuleWindow window){
		for(List<ModuleInfo> column : moduleList){
			if(column.remove(window)) {
				moduleDisplay.setModuleList(moduleList);
				return true;
			}
		}
		return false;
	}

	DockLayoutPanel dockLayoutPanel;
	
	@Override
	public Widget asWidget() {
		if(dockLayoutPanel != null) return dockLayoutPanel;
		dockLayoutPanel = new DockLayoutPanel(Unit.EM);
		dockLayoutPanel.addNorth(tabDisplay.asWidget(), 2);
		dockLayoutPanel.add(moduleDisplay.asWidget());
		return dockLayoutPanel;
	}
	
	public boolean remove(ModuleInfo module){
		for (List<ModuleInfo> moduleInfos : moduleList){
			if(moduleInfos.remove(module)){
				updateDisplays();
				return true;
			}
		}
		return false;
	}

	private void updateDisplays() {
		moduleDisplay.setModuleList(moduleList);
	}
	
	public void add(ModuleInfo module, double d, int j) {
		int columnIndex = (int) ((d / 100.0) * moduleList.size());
		List<ModuleInfo> column = moduleList.get(columnIndex);
		int moduleindex=0;
		while(moduleindex<column.size()){
			int height = 20 + column.get(moduleindex).getHeight();
			if(j < height / 2)
				break;
			j -= height;
			moduleindex++;
		}
		column.add(moduleindex,module);
		updateDisplays();
	}

	Timer delayedSave = new Timer() {
		
		@Override
		public void run() {
			//TODO: Save Modules
		}
	};
	
	public void save() {
		delayedSave.schedule(1300);
	}

	@Override
	public void onContentSizeChanged(ContentChangedEvent event) {
		moduleDisplay.checkForResizes();
	}

	public void updateHeight(ModuleInfo module, int currentHeight) {
		module.setHeight(currentHeight + 5);
		moduleDisplay.setModuleList(moduleList);
	}

	@Override
	public void setSinkValue(List<List<ModuleInfo>> value) {
		moduleList = value;
		moduleDisplay.setModuleList(moduleList);
	}
}
