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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kk_electronic.kkportal.core.activity.Activity;

@Singleton
public class WebPageLayout extends Composite implements ApplicationLayout {
	public static interface UIBinder extends UiBinder<Panel, WebPageLayout>{}
		
	@UiField
	DockLayoutPanel main;
	@UiField
	LayoutPanel activityContainer;
	private Widget activityWidget;
	
	@Inject
	public WebPageLayout(UIBinder binder) {
		Panel e = binder.createAndBindUi(this);
		super.initWidget(e);
	}

	@Override
	public void go() {
		RootLayoutPanel.get().add(this);
	}

	@Override
	public void displayActivity(Activity activity) {
		if(activityWidget == activity){
			return;
		}
		activityContainer.clear();
		if(activity == null){
			activityWidget = null;
		} else {
			activityWidget = activity.asWidget();
			activityContainer.add(activityWidget);
		}
	}
	
	boolean isSidebarHidden = true;
	@UiField
	Widget sidebar;
	
	public void toggleSidebar() {
		if(isSidebarHidden){
			showSidebar();
		} else {
			hideSidebar();
		}
	}
	
	public void hideSidebar() {
		main.setWidgetSize(sidebar, 0);
		main.animate(200);
		isSidebarHidden = true;
	}

	public void showSidebar() {
		main.setWidgetSize(sidebar, 160);
		main.animate(200);
		isSidebarHidden = false;
	}

	@UiHandler("thumb")
	public void thumbClick(ClickEvent event){
		toggleSidebar();
	}
	
}
