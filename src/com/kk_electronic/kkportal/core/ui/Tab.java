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

import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.tabs.TabInfo;

/**
 * @author Rasmus Carlsen
 *
 */
public class Tab extends Composite implements HasDoubleClickHandlers{
	private TabInfo info;
	
	public static interface UIBinder extends UiBinder<Widget, Tab> {
		
	}
	
	static interface Style extends CssResource {
		String selected();
	}
	
	@UiField
	Style style;
	
	@UiField
	Anchor name;
	
	@UiField
	LayoutPanel container;
	
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
		name.setHref("#View$" + this.info.getId());
	}
	
	public TabInfo getInfo() { 
		return info;
	}
	
	public void setSelected() {
		this.addStyleName(style.selected());
	}
	
	public void editTabName(final AsyncCallback<String> callback) {
		// insert a text edit field in place of title
		final PopupPanel pp = new PopupPanel(true, true);
		final TextBox tb = new TextBox();
		pp.add(tb);

		tb.setText(getName());
		pp.setAnimationEnabled(true);
		pp.showRelativeTo(name);
		pp.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				String text = tb.getText();
				if (!text.equals(name)) {
					callback.onSuccess(text);
				}	
			}
		});
		tb.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getCharCode() == KeyCodes.KEY_ENTER) {
					pp.hide();
				}
			}
		});
	}

	@Override
	public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
		return name.addDoubleClickHandler(handler);
	}
}