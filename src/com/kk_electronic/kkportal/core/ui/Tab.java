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

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.tabs.TabInfo;

/**
 * @author Rasmus Carlsen
 *
 */
public class Tab extends Composite {
	private TabInfo info;
	
	public static interface UIBinder extends UiBinder<Widget, Tab> {
		
	}
	
	static interface Style extends CssResource {
		String selected();
	}
	
	@UiField
	Style style;
	
	@UiField
	Hyperlink name;
	
	@UiField
	Widget container;
	
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
		name.setTargetHistoryToken("View$" + this.info.getId());
	}
	
	public void setSelected() {
		this.addStyleName(style.selected());
	}
}