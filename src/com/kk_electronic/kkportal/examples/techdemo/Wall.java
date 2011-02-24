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
package com.kk_electronic.kkportal.examples.techdemo;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.AbstractModule;
import com.kk_electronic.kkportal.core.moduleview.Module;
import com.kk_electronic.kkportal.core.services.TechDemo;

/**
 * Implementation of a wall to show ability to 
 * communicate across browsers in a timely fashion
 * 
 * <p>Using UIBinder for defining how the module looks like see: <a href="http://code.google.com/webtoolkit/doc/latest/DevGuideUiBinder.html">UI Binder</a></p>
 * @author Jes Andersen
 */
public class Wall extends AbstractModule implements Module, NewWallMessageEvent.Handler {
	private final Display display;
	private final TechDemo service;
	List<String> lines = new ArrayList<String>();
	private final EventBus eventBus;

	public static interface UIBinder extends UiBinder<Widget, Display>{};
	
	/**
	 * Display inner class used to bind with and control the UI.
	 * 
	 * @author Jes Andersen
	 */
	public static class Display{
		Widget widget;
		@UiField 
		Element lineList;
		@UiField
		ValueBoxBase<String> message;
		private Wall wall;

		@Inject
		public Display(UIBinder binder) {
			widget = binder.createAndBindUi(this);
		}
		
		public Widget asWidget(){
			return widget;
		}
		
		public void setHandler(Wall wall){
			this.wall = wall;
		}

		public void setLines(List<String> lines) {
			StringBuilder sb = new StringBuilder();
			for(String line:lines){
				if(sb.length() != 0){
					sb.append("<br />");
				}
				sb.append(line);
			}
			lineList.setInnerHTML(sb.toString());
		}

		@UiHandler("post")
		public void onPost(ClickEvent event){
			postTextBox();
		}
		
		@UiHandler("message")
		public void onPost(KeyDownEvent event){
			if (KeyCodes.KEY_ENTER != event.getNativeKeyCode()) return;
			postTextBox();
		}

		private void postTextBox() {
			if(message.getValue().isEmpty()) return;
			if(wall != null){
				wall.postMessage(message.getValue());
			}
			message.setValue("");
		}
	}
	
	@Inject
	public Wall(Display display,TechDemo service,EventBus eventBus) {
		this.service = service;
		this.eventBus = eventBus;
		display.setHandler(this);
		service.getWall(new AsyncCallback<List<String>>() {
			
			@Override
			public void onSuccess(List<String> result) {
				for(String s:result){
					addMessage(s);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("WALL-Could not subscribe");
			}
		});
		this.display = display;
		this.eventBus.addHandler(NewWallMessageEvent.TYPE, this);
	}
	
	public void addMessage(String s){
		lines.add(s);
		while(lines.size() > 8){
			lines.remove(0);
		}
		display.setLines(lines);
		contentChanged();
	}
	
	public void postMessage(final String s){
		service.postToWall(s, new AsyncCallback<Object>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("WALL-Post failed",caught);
			}

			@Override
			public void onSuccess(Object result) {
				GWT.log("WALL-Posted "+s);
			}
		});
	}
	
	@Override
	public Widget asWidget() {
		return display.asWidget();
	}

	@Override
	public void onNewWallMessage(NewWallMessageEvent event) {
		addMessage(event.getMessage());
	}
}
