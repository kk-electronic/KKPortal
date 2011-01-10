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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.AbstractModule;
import com.kk_electronic.kkportal.core.event.ContentChangedEvent;
import com.kk_electronic.kkportal.core.moduleview.Module;
import com.kk_electronic.kkportal.core.services.TechDemo;

public class Inotify extends AbstractModule implements Module,INotificationEvent.Handler {
	private final Display display;

	public static interface UIBinder extends UiBinder<Widget, Display>{};
	public static class Display{
		Widget w;
		@UiField
		Element lineList;
		private final EventBus eventBus;
		@Inject
		public Display(UIBinder binder,EventBus eventBus) {
			this.eventBus = eventBus;
			w = binder.createAndBindUi(this);
		}
		public Widget asWidget(){
			return w;
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
			eventBus.fireEventFromSource(new ContentChangedEvent(), this);
		}
	}
	
	@Inject
	public Inotify(Display display,TechDemo debug,EventBus eventBus) {
		eventBus.addHandler(INotificationEvent.TYPE, this);
		debug.inotify("server", new AsyncCallback<Object>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("INOTIFY-Could not listen",caught);
			}

			@Override
			public void onSuccess(Object result) {
				GWT.log("INOTIFY-Listening");
			}
		});
		this.display = display;
	}
	
	List<String> lines = new ArrayList<String>();
	
	public void addMessage(String s){
		lines.add(s);
		while(lines.size() > 8){
			lines.remove(0);
		}
		display.setLines(lines);
	}
	
	@Override
	public Widget asWidget() {
		return display.asWidget();
	}

	@Override
	public void onINotification(INotificationEvent event) {
		addMessage(event.getNotifytype() + ":" +event.getFileName());
	}
}
