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
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.dnd.DND.Dragsource;
import com.kk_electronic.kkportal.core.services.ModuleService.ModuleInfo;

public class ModuleWindow extends Composite implements Dragsource<ModuleWindow> {
	public static interface UIBinder extends UiBinder<Panel, ModuleWindow> {
	}

	public UIBinder binder = GWT.create(UIBinder.class);

	static interface Style extends CssResource {
		String first();
		String last();
	}
	
	@UiField
	Style style;

	@UiField
	Widget titlebar;

	@UiField
	Hyperlink title;

	@UiField
	SimplePanel content;

	@UiField
	Element container;

	private Widget masked;

	private final ModuleInfo module;

	@UiField
	Widget delete;

	@Inject
	public ModuleWindow(ModuleInfo module) {
		this.module = module;
		masked = binder.createAndBindUi(this);
		super.initWidget(masked);
		title.setTargetHistoryToken("SingleRender$" + module.getId());
	}

	public void setTitle(String title) {
		this.title.setText(title);
	}

	public int getHeight() {
		return container.getOffsetHeight();
	}

	public void setHeight(int height) {
		module.setHeight(height);
	}

	@Override
	public ModuleWindow get() {
		return this;
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return titlebar.addDomHandler(handler, MouseDownEvent.getType());
	}

	public HandlerRegistration addDeleteHandler(MouseDownHandler handler) {
		return delete.addDomHandler(handler, MouseDownEvent.getType());
	}

	public ModuleInfo getModule() {
		return module;
	}

	public void setContent(Widget content) {
		this.content.setWidget(content);
	}

	public Widget getContent() {
		return this.content;
	}

	public void setFirstColumn(boolean b) {
		if(b){
			this.addStyleName(style.first());
		} else {
			this.removeStyleName(style.first());
		}
	}

	public void setLastColumn(boolean b) {
		if(b){
			this.addStyleName(style.last());
		} else {
			this.removeStyleName(style.last());
		}
	}
}