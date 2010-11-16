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
package com.kk_electronic.kkportal.core.dnd;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * 
 * TODO: support multiple drop targets
 * TODO: support dragpreview
 * @author Jes Andersen
 *
 * @param <T>
 */
public class DND<T extends Widget> implements MouseMoveHandler,MouseUpHandler {
	T widgetDragging;
	
	public static interface DragOver<T> {

	}

	public static interface DropSink<T> {
		public boolean Drop(MouseUpEvent event,T t);
	}

	public static interface Dragsource<T> extends HasMouseDownHandlers {
		public T get();
	}

	private final MousePanel mousePanel;
	private DropSink<T> dropSink;

	@Inject
	public DND(MousePanel mousePanel) {
		this.mousePanel = mousePanel;
	}
	
	public HandlerRegistration registerDragSource(final Dragsource<T> dragsource){
		return dragsource.addMouseDownHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				startDrag(event,dragsource.get());
			}
		});
	}

	public void startDrag(MouseDownEvent event, T widgetToDrag) {
		assert(widgetDragging == null);
		mousePanel.attachWidgetToMouse(event, widgetToDrag,this,this);
		widgetDragging = widgetToDrag;
		GWT.log("DND-dragging started");
		
	}

	public void registerDropSink(DropSink<T> dropSink){
		this.dropSink = dropSink;
	}
	
	public void registerDragOver(DragOver<T> dragOver){
		
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		dropSink.Drop(event, widgetDragging);
		mousePanel.clearWidgetFromMouse();
		widgetDragging = null;
		GWT.log("DND-dragging ended");
	}
}
