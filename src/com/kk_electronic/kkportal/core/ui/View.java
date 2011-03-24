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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.LoginIndicator;
import com.kk_electronic.kkportal.core.activity.Activity;
import com.kk_electronic.kkportal.res.Resources;

public class View implements Activity {
	private final Canvas canvas;
	private final TabDisplay tabDisplay;
	private Resources resources;

	@Inject
	public View(Resources resources, Canvas canvas,TabDisplay tabDisplay,LoginIndicator login) {
		this.canvas = canvas;
		this.tabDisplay = tabDisplay;
		this.resources = resources;
	}
	
	DockLayoutPanel panel;
	
	@Override
	public Widget asWidget() {
		if(panel != null) return panel;
		panel = new DockLayoutPanel(Unit.EM);
		panel.addNorth(tabDisplay.asWidget(), 1.4);
		Widget w = canvas.asWidget();
		panel.add(w);
		Element e = panel.getWidgetContainerElement(w);
		e.getStyle().setBackgroundColor(resources.palette().colour2());
		e.getStyle().setOverflow(Overflow.AUTO);
		e.getStyle().setPropertyPx("borderTopLeftRadius", 10);
		e.getStyle().setPropertyPx("WebkitBorderTopLeftRadius", 10);
		e.getStyle().setPropertyPx("MozBorderRadiusTopleft", 10);

		return panel;
	}
}
