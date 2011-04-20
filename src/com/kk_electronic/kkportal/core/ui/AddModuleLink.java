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

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.event.TabSelectedEvent;
import com.kk_electronic.kkportal.core.tabs.TabInfo;
import com.kk_electronic.kkportal.core.tabs.TabsModel;

public class AddModuleLink extends Composite implements TabSelectedEvent.Handler {
	
	Anchor link = new Anchor();
	Integer id;
	
	@Inject
	public AddModuleLink(TabsModel tabsModel) {
		initWidget(link);
		updateLink(tabsModel.getSelectedTab());
		tabsModel.addTabSelectedHandler(this);
		link.addClickHandler(handler);
	}

	public void updateLink(TabInfo tabInfo) {
		if(tabInfo != null) {
			id = tabInfo.getId();
			link.setText("Add Module");
			link.setHref("#View$" + id + ";sidepanel=AddModule");
		}
	}

	@Override
	public void onTabSelected(TabSelectedEvent event) {
		updateLink(event.getTabInfo());
	}
}
