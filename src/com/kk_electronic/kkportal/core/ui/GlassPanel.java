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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class GlassPanel implements IsWidget {

	public static interface UIBinder extends UiBinder<Widget, GlassPanel>{};
		
	private Widget widget;
	@UiField
	VerticalPanel panel;
	private boolean showing = false;
	
	@Inject
	public GlassPanel(UIBinder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
	
	public void hide() {
		if(!showing) return;
		RootLayoutPanel.get().remove(widget);
		showing = false;
	}

	public void show() {
		if(showing ) return;
		showing = true;
		final RootLayoutPanel r = RootLayoutPanel.get();
		Scheduler.get().scheduleDeferred(new Command() {
			
			@Override
			public void execute() {
				if(r.getWidgetIndex(widget) == -1){
					r.add(widget);
				}
			}
		});
	}

	public void addWidget(Widget x) {
		panel.add(x);
		show();
	}

	public void addWidget(IsWidget x) {
		addWidget(x.asWidget());
	}

	public void remove(Widget x) {
		if(panel.getWidgetCount() == 1){
			hide();
		}
		panel.remove(x);
	}
	public void remove(IsWidget x) {
		remove(x.asWidget());
	}

	/**
	 * @param x
	 * @param i
	 */
	public void addWidget(IsWidget x, int i) {
		addWidget(x.asWidget(),i);
	}
	/**
	 * @param x
	 * @param i
	 */
	public void addWidget(Widget x, int i) {
		panel.insert(x, i);
		show();
	}
}
