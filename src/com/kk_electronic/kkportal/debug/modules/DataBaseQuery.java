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
package com.kk_electronic.kkportal.debug.modules;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.AbstractModule;
import com.kk_electronic.kkportal.core.moduleview.Module;
import com.kk_electronic.kkportal.core.services.TechDemo;

public class DataBaseQuery extends AbstractModule implements Module {
	private final TechDemo debug;
	private final UIBinder display;
	
	public static interface UIBinder extends UiBinder<Widget, DataBaseQuery>{};
	
	@Inject
	public DataBaseQuery(TechDemo debug,UIBinder display) {
		this.debug = debug;
		this.display = display;
	}
	
	@Override
	public Widget asWidget() {
		return display.createAndBindUi(this);
	}
	
	public void submit(String query){
		resultDisplay.setWidget(new Label("Fetching"));
		//TODO Run query and display it
	}
		
	
	@UiField
	SimplePanel resultDisplay;

	HasEnabled resultDisplayEnabled;
	
	@UiField
	HasValue<String> query;
		
	@UiHandler("query")
	public void onEnter(KeyPressEvent event){
		if(event.getCharCode() == '\n'){
			submit(query.getValue());
		}
	}
	
	@UiHandler("submit")
	public void onSubmit(ClickEvent event){
		submit(query.getValue());
	}
}
