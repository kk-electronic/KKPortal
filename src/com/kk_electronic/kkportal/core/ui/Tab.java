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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.tabs.TabInfo;

/**
 * @author Rasmus Carlsen
 *
 */
public class Tab extends Composite{
	private TabInfo info;
	
	public static interface UIBinder extends UiBinder<Widget, Tab> {
		
	}
	
	static interface Style extends CssResource {
		String selected();
	}
	
	@UiField
	Style style;
	
	@UiField
	Anchor name;
	
	@UiField
	TogglePanel toggle;

	@UiField
	TextBox editname;
	
	@UiField
	Widget container;

	private TabDisplay handler;
	
	@Inject
	public Tab(UIBinder binder) {
		initWidget(binder.createAndBindUi(this));
	}
	
	public String getName() {
		if (info != null) {
			return info.getName();
		} else {
			return null;
		}
	}
	
	public void setInfo(TabInfo tabInfo) {
		this.info = tabInfo;
		name.setText(this.info.getName());
		name.setHref("#View$" + this.info.getId());
	}
	
	public TabInfo getInfo() { 
		return info;
	}
	
	public void setSelected() {
		this.addStyleName(style.selected());
	}
	
	/**
	 * @param b
	 */
	public void setEdit(boolean b) {
		if(b){
			toggle.setWidget(1);
			editname.setText(info.getName());
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				
				@Override
				public void execute() {
					editname.setFocus(true);					
				}
			});
		} else {
			toggle.setWidget(0);
		}
	}
	
	@UiHandler("editname")
	public void onBlur(BlurEvent event){
		commit();
	}

	@UiHandler("editname")
	public void onKeyPress(KeyPressEvent event){
		if(event.getCharCode() == KeyCodes.KEY_ENTER){
			commit();
		}
	}

	@UiHandler("name")
	public void onDoubleClick(DoubleClickEvent event){
		setEdit(true);
	}

	@UiHandler("delete")
	public void onDoubleClick(ClickEvent event){
		if(handler != null){
			handler.deletetab(this);
		}		
	}

	/**
	 * 
	 */
	private void commit() {
		GWT.log("Tab-edit done");
		setEdit(false);
		name.setText(editname.getText());
		if(handler != null){
			handler.onEditTabNameClick(this,editname.getText());
		}
	}

	/**
	 * @param tabDisplay
	 */
	public void setHandler(TabDisplay handler) {
		this.handler = handler;
	}
}