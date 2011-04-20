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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kk_electronic.kkportal.core.activity.Activity;
import com.kk_electronic.kkportal.core.activity.ActivityContainer;

/**
 * @author Rasmus Carlsen
 *
 */
@Singleton
public class SideBar extends Composite implements ActivityContainer {
	StackLayoutPanel slp = new StackLayoutPanel(Unit.EM);
	private Widget activityWidget;
	
	@Inject
	public SideBar() {
		super.initWidget(slp);
	}
	
	/* (non-Javadoc)
	 * @see com.kk_electronic.kkportal.core.ui.ActivityContainer#displayActivity(com.kk_electronic.kkportal.core.activity.Activity)
	 */
	@Override
	public void displayActivity(Activity activity) {
		if(activityWidget == activity){
			return;
		}
		slp.clear();
		if(activity == null){
			activityWidget = null;
		} else {
			activityWidget = activity.asWidget();
			slp.add(activityWidget, "Add Module", 2.0);
		}
		
	}

}
