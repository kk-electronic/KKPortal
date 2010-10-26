package com.kk_electronic.kkportal.core.ui;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.model.TabInfoProvider;
import com.kk_electronic.kkportal.core.services.ModuleService.TabInfo;

public class TabDisplay {
	
	CellList<TabInfo> display;
	
	@Inject
	public TabDisplay(TabInfoProvider tabInfoProvider) {
		display = new CellList<TabInfo>(new ConverterCell<TabInfo, String>(new HasCell<TabInfo, String>() {
			TextCell cell = new TextCell();
			@Override
			public Cell<String> getCell() {
				return cell;
			}

			@Override
			public FieldUpdater<TabInfo, String> getFieldUpdater() {
				return null;
			}

			@Override
			public String getValue(TabInfo object) {
				return object.getName();
			}
		}));
		tabInfoProvider.addDataDisplay(display);
	}
	
	public Widget asWidget() {
		return display;
	}
}
