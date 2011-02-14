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
package com.kk_electronic.kkportal.examples.modules;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.AbstractModule;
import com.kk_electronic.kkportal.core.moduleview.Module;

/**
 * A Simple Hello World module.
 * 
 * <p>
 * This module demonstrates how to use UI binder to show something. <br>
 * All the content of this module is in the XML for this file "HelloWorld.ui.xml"
 * </p>
 * 
 * <p>For more infomation on UIBinder see KKPortal tutorials or <a href="http://code.google.com/webtoolkit/doc/latest/DevGuideUiBinder.html">UI Binder</a></p>
 */
public class HelloWorld extends AbstractModule implements Module {
	private final Display display;

	public static interface UIBinder extends UiBinder<Widget, Display>{};

	/**
	 * Inner Display Class used for binding the UI.
	 */
	public static class Display{
		Widget widget;
		
		@Inject
		public Display(UIBinder binder) {
			widget = binder.createAndBindUi(this);
		}
		
		public Widget asWidget(){
			return widget;
		}
	}
	
	@Inject
	public HelloWorld(Display display) {
		this.display = display;
	}
	
	@Override
	public Widget asWidget() {
		return display.asWidget();
	}
}
