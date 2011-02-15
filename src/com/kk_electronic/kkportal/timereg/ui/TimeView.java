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

package com.kk_electronic.kkportal.timereg.ui;

import java.util.Date;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasData;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.AbstractModule;
import com.kk_electronic.kkportal.core.util.Range;
import com.kk_electronic.kkportal.core.util.TimeColumn;
import com.kk_electronic.kkportal.timereg.model.TimeEntry;
import com.kk_electronic.kkportal.timereg.model.TimeRegistry;

/**
 * @author albatros
 *
 */
public class TimeView extends AbstractModule {

	public static interface UIBinder extends UiBinder<Widget, TimeView>{}

	private Widget display;
	
	@UiField
	Label dateLabel;
	private final DateTimeFormat dateTimeFormat;
	private Range<Long> date;
	
	@UiHandler("checkin")
	protected void onCheckin(ClickEvent event){
		timeRegistry.checkin();
	}

	@UiHandler("checkout")
	protected void onCheckout(ClickEvent event){
		timeRegistry.checkout();
	}
	
	@UiField
	Button checkout;
	
	@UiField
	Button checkin;
	
	@UiField
	CellTable<TimeEntry> entries;

	private final TimeRegistry timeRegistry;

	@Inject
	public TimeView(UIBinder binder,DateTimeFormat dateTimeFormat,TimeRegistry timeRegistry) {
		this.display = binder.createAndBindUi(this);
		this.timeRegistry = timeRegistry;
		this.dateTimeFormat = dateTimeFormat;
//		this.dateTimeFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_FULL);
		entries.addColumn(new TimeColumn<TimeEntry>() {
			@Override
			public Date getValue(TimeEntry object) {
				return object.getCheckinDate();
			}
		}, "in");
		entries.addColumn(new TimeColumn<TimeEntry>() {
			@Override
			public Date getValue(TimeEntry object) {
				return object.getCheckoutDate();
			}
		}, "out");
		entries.addColumn(new TimeColumn<TimeEntry>() {
			@SuppressWarnings("deprecation")
			@Override
			public Date getValue(TimeEntry object) {
				if(object.getCheckoutDate() == null || object.getCheckinDate() == null){
					return null;
				} else {
					long i = (object.getCheckoutDate().getTime() - object.getCheckinDate().getTime());
					Date now = new Date();
					return new Date(i + now.getTimezoneOffset()*60000);
				}
			}
		}, "total");
		entries.addColumn(new TextColumn<TimeEntry>() {

			@Override
			public String getValue(TimeEntry object) {
				Integer i = object.getTaskId();
				return i!=null?i.toString():"";
			}
		},"Task");
		setToday();
		timeRegistry.addDisplay(this);
	}
	
	@SuppressWarnings("deprecation")
	private void setToday() {
		Date now = new Date();
		Long timestamp = now.getTime();
		Long tz = Long.valueOf(now.getTimezoneOffset())*60000;
		Long low = (timestamp - ((timestamp - tz) % 86400000))/1000;
		Long high = (low + 86400);
		date = new Range<Long>(low,high);
		updateDateLabel();
	}
	
	private void updateDateLabel() {
		dateLabel.setText(
				dateTimeFormat.format(new Date(date.begin*1000))
				);
	}

	@Override
	public Widget asWidget() {
		return display.asWidget();
	}

	/**
	 * @return
	 */
	public HasData<TimeEntry> getHasData() {
		return entries;
	}

	/**
	 * @param values
	 */
	public void update(List<TimeEntry> values) {
		entries.setRowData(values);
		entries.setRowCount(values.size());
		contentChanged();
	}

	/**
	 * @return
	 */
	public Range<Long> getRange() {
		return date;
	}
}
