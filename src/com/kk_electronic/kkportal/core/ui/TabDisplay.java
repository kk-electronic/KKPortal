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
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RowCountChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.RangeChangeEvent.Handler;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.kk_electronic.kkportal.core.tabs.TabInfo;
import com.kk_electronic.kkportal.core.tabs.TabsModel;
import com.kk_electronic.kkportal.res.Resources;

public class TabDisplay implements HasData<TabInfo> {

	@UiField
	LayoutPanel panel;
	private SelectionModel<? super TabInfo> selectionModel;
	private final EventBus eventBus;
	private Range range;
	private int rowCount;
	private boolean isRowCountExact;
	private final TabsModel tabInfoProvider;
	private final Provider<Tab> tabProvider;
	private Widget widget;

	public static interface UIBinder extends UiBinder<Widget, TabDisplay> { }
	
	@Inject
	public TabDisplay(TabsModel tabInfoProvider, EventBus eventBus,
			Resources resources, Provider<Tab> tabProvider, UIBinder binder) {
		this.tabInfoProvider = tabInfoProvider;
		this.eventBus = eventBus;
		this.tabProvider = tabProvider;
		this.widget = binder.createAndBindUi(this);
		selectionModel = tabInfoProvider.getSelectionModel();
		tabInfoProvider.addDataDisplay(this);
	}
	
	public Widget asWidget() {
		return widget;
	}

	@Override
	public SelectionModel<? super TabInfo> getSelectionModel() {
		return selectionModel;
	}

	@Override
	public void setRowData(int start, List<? extends TabInfo> values) {
		assert start == 0 : "TabDisplay does not support partial viewing";
		panel.clear();
		int margin = 10;
		int totalWidth = margin;
		int tabWidth = tabInfoProvider.getMaxWidth();
		int i = values.size(); 
		for (TabInfo info : values) {
			Tab t = tabProvider.get();
			t.setInfo(info);
			panel.add(t);
			if (selectionModel != null && selectionModel.isSelected(info)) {
				t.setSelected();
			}
			t.getElement().getStyle().setZIndex(i--);
			panel.setWidgetBottomHeight(t, 0, Unit.EM, 1, Unit.EM);
			panel.setWidgetLeftWidth(t, totalWidth, Unit.PX, tabWidth, Unit.PX);
			totalWidth += tabWidth + margin;
		}
		totalWidth -= margin;
	}

	@Override
	public void setSelectionModel(SelectionModel<? super TabInfo> selectionModel) {
		this.selectionModel = selectionModel;
	}

	@Override
	public void setVisibleRangeAndClearData(Range range,
			boolean forceRangeChangeEvent) {
		Range oldrange = this.range;
		this.range = range;
		if (forceRangeChangeEvent || (!range.equals(oldrange))) {
			RangeChangeEvent.fire(this, range);
		}
	}

	@Override
	public HandlerRegistration addRangeChangeHandler(Handler handler) {
		return eventBus.addHandlerToSource(RangeChangeEvent.getType(), this,
				handler);
	}

	@Override
	public HandlerRegistration addRowCountChangeHandler(
			com.google.gwt.view.client.RowCountChangeEvent.Handler handler) {
		return eventBus.addHandlerToSource(RowCountChangeEvent.getType(), this,
				handler);
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	@Override
	public Range getVisibleRange() {
		return range;
	}

	@Override
	public boolean isRowCountExact() {
		return isRowCountExact;
	}

	@Override
	public void setRowCount(int count) {
		setRowCount(count, true);
	}

	@Override
	public void setRowCount(int count, boolean isExact) {
		this.rowCount = count;
		this.isRowCountExact = isExact;
	}

	@Override
	public void setVisibleRange(Range range) {
		this.range = range;
	}

	@Override
	public void setVisibleRange(int start, int length) {
		this.range = new Range(start, length);
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		eventBus.fireEventFromSource(event, this);
	}

	@Override
	public TabInfo getVisibleItem(int indexOnPage) {
		return null;
	}

	@Override
	public int getVisibleItemCount() {
		return 0;
	}

	@Override
	public Iterable<TabInfo> getVisibleItems() {
		return null;
	}

	@Override
	public HandlerRegistration addCellPreviewHandler(
			com.google.gwt.view.client.CellPreviewEvent.Handler<TabInfo> handler) {
		return null;
	}
}
