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
import com.kk_electronic.kkportal.core.activity.LocationManager;
import com.kk_electronic.kkportal.core.activity.LocationManager.AbstractLink;
import com.kk_electronic.kkportal.core.event.TabSelectedEvent;
import com.kk_electronic.kkportal.core.tabs.TabInfo;
import com.kk_electronic.kkportal.core.tabs.TabsModel;

public class AddModuleLink extends Composite implements TabSelectedEvent.Handler {
	
	Anchor link = new Anchor();
	Integer id;
	private AbstractLink al;
	
	@Inject
	public AddModuleLink(TabsModel tabsModel, LocationManager lm) {
		initWidget(link);
		updateLink(tabsModel.getSelectedTab());
		tabsModel.addTabSelectedHandler(this);
		link.setText("Add Module");
		this.al = lm.registerLink(link);
	}

	public void updateLink(TabInfo tabInfo) {
		if(tabInfo != null) {
			id = tabInfo.getId();
			al.changeValues(null, "View$" + id, "sidepanel", "AddModule");
		}
	}

	@Override
	public void onTabSelected(TabSelectedEvent event) {
		updateLink(event.getTabInfo());
	}
}
