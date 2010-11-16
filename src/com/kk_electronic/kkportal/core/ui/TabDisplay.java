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
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RowCountChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.RangeChangeEvent.Handler;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.tabs.TabInfo;
import com.kk_electronic.kkportal.core.tabs.TabsModel;
import com.kk_electronic.kkportal.res.Resources;
import com.kk_electronic.kkportal.res.Resources.ColourPalette;

public class TabDisplay implements HasData<TabInfo> {

	LayoutPanel panel = new LayoutPanel();
	private SelectionModel<? super TabInfo> selectionModel;
	private final EventBus eventBus;
	private Range range;
	private int rowCount;
	private boolean isRowCountExact;
	private final TabsModel tabInfoProvider;
	private ColourPalette palette;

	@Inject
	public TabDisplay(TabsModel tabInfoProvider, EventBus eventBus,
			Resources resources) {
		this.tabInfoProvider = tabInfoProvider;
		this.eventBus = eventBus;
		palette = resources.palette();
		selectionModel = tabInfoProvider.getSelectionModel();
		tabInfoProvider.addDataDisplay(this);
	}

	public Widget asWidget() {
		return panel;
	}

	@Override
	public SelectionModel<? super TabInfo> getSelectionModel() {
		return selectionModel;
	}

	@Override
	public void setRowData(int start, List<TabInfo> values) {
		assert start == 0 : "TabDisplay does not support partial viewing";
		panel.clear();
		int margin = 10;
		int totalWidth = margin;
		int tabWidth = tabInfoProvider.getMaxWidth();
		for (TabInfo info : values) {
			Hyperlink l = new Hyperlink(SafeHtmlUtils.fromString(info.getName()),"View$" + info.getId());
			if (selectionModel != null && selectionModel.isSelected(info)) {
				l.getElement().getStyle().setBackgroundColor(palette.colour2());
			} else {
				l.getElement().getStyle().setBackgroundColor(palette.colour1());
			}
			panel.add(l);
			panel.setWidgetBottomHeight(l, 0, Unit.EM, 1, Unit.EM);
			panel.setWidgetLeftWidth(l, totalWidth, Unit.PX, tabWidth, Unit.PX);
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
}
