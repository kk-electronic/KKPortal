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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.activity.Activity;
import com.kk_electronic.kkportal.core.services.ModuleTypeInfo;
import com.kk_electronic.kkportal.core.tabs.ModuleTypeInfoProvider;
import com.kk_electronic.kkportal.core.tabs.TabInfo;
import com.kk_electronic.kkportal.core.tabs.TabsModel;

public class AddModule implements Activity {
	protected String error;
	protected List<ModuleTypeInfo> moduleTypes;
	private final Display display;
	private final TabsModel tabsModel;

	public static interface UIBinder extends UiBinder<Panel, Display> {
	}

	public static class Display {
		private final UIBinder binder;

		@UiField(provided = true)
		CellList<ModuleTypeInfo> list;

		SingleSelectionModel<ModuleTypeInfo> selectionModel = new SingleSelectionModel<ModuleTypeInfo>();

		@Inject
		public Display(UIBinder binder, ModuleTypeInfoProvider infoProvider) {
			list = new CellList<ModuleTypeInfo>(new ModuleTypeCell());
			list.setSelectionModel(selectionModel);
			infoProvider.addDisplay(list);
			this.binder = binder;
		}

		AddModule handler;

		public void setHandler(AddModule handler) {
			this.handler = handler;
		}

		@UiHandler("add")
		public void onAddClick(ClickEvent event) {
			ModuleTypeInfo selectedObject = selectionModel.getSelectedObject();
			if (handler != null && selectedObject != null) {
				handler.addNewModule(selectedObject);
			}
		}

		public Widget asWidget() {
			return binder.createAndBindUi(this);
		}
	}

	@Inject
	public AddModule(Display display, TabsModel tabsModel) {
		this.display = display;
		this.tabsModel = tabsModel;
		display.setHandler(this);
	}

	public void addNewModule(ModuleTypeInfo selectedObject) {
		TabInfo tabInfo = tabsModel.getSelectedTab();
		if (tabInfo == null)
			return;
		tabsModel.addNewModule(tabInfo, selectedObject);
		History.newItem("View$" + tabInfo.getId());
	}

	@Override
	public Widget asWidget() {
		return display.asWidget();
	}
}
