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
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.LoginIndicator;
import com.kk_electronic.kkportal.core.activity.Activity;

public class View implements Activity {
	private final Canvas canvas;
	private final TabDisplay tabDisplay;

	@Inject
	public View(Canvas canvas,TabDisplay tabDisplay,LoginIndicator login) {
		this.canvas = canvas;
		this.tabDisplay = tabDisplay;
	}
	
	DockLayoutPanel panel;
	
	@Override
	public Widget asWidget() {
		if(panel != null) return panel;
		panel = new DockLayoutPanel(Unit.EM);
		panel.addNorth(tabDisplay.asWidget(), 2);
		panel.add(canvas.asWidget());
		return panel;
	}
}
