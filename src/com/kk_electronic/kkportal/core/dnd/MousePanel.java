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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class MousePanel implements MouseMoveHandler,MouseUpHandler{
	private HandlerRegistration handlerRegistration;
	private HandlerRegistration handlerRegistration2;
	private LayoutPanel parent;
	private Widget widget;
	private int height;
	private int width;
	private int offsetX;
	private int offsetY;
	private MouseUpHandler mouseUpHandler;
	private MouseMoveHandler mouseMoveHandler;
	private String oldmargin;

	public MousePanel() {
		parent = RootLayoutPanel.get();
	}
	
	public void attachWidgetToMouse(MouseDownEvent event, Widget w,MouseMoveHandler mouseMoveHandler,MouseUpHandler mouseUpHandler) {
		this.mouseMoveHandler = mouseMoveHandler;
		this.mouseUpHandler = mouseUpHandler;
		if (widget == w) return;
		
		clearWidgetFromMouse();
		widget = w;

		if(widget != null){
			height = w.getOffsetHeight();
			width = w.getOffsetWidth();
			offsetX = event.getRelativeX(w.getElement());
			offsetY = event.getRelativeY(w.getElement());
			oldmargin = w.getElement().getStyle().getMargin();
			w.getElement().getStyle().setMargin(0, Unit.PX);
			parent.add(widget);
			parent.setWidgetTopHeight(widget, event.getClientY() - offsetY, Unit.PX, height, Unit.PX);
			parent.setWidgetLeftWidth(widget, event.getClientX() - offsetX, Unit.PX, width, Unit.PX);
			attachHandlers();
		}
	}
	
	
	public void clearWidgetFromMouse() {
		if(widget != null){
			if("".equals(oldmargin)){
				widget.getElement().getStyle().clearMargin();
			} else {
				widget.getElement().getStyle().setProperty("margin", oldmargin);
			}
			detachHandlers();
			/*
			 * Do not use widget.removeFromParent() since the parent might not
			 * be "parent" if the widget has been attached to another parent
			 * meanwhile (which is very likely)
			 */
			parent.remove(widget);
			widget = null;
		}
	}

	public void attachHandlers(){
		if(handlerRegistration != null) return;
		handlerRegistration=parent.addDomHandler(this,MouseMoveEvent.getType());
		handlerRegistration2=parent.addDomHandler(this, MouseUpEvent.getType());
		DOM.setCapture(parent.getElement());
	}
	
	public void detachHandlers(){
		/*
		 * We remove the handlers to make sure unnecessary processing is not done
		 */
		if(handlerRegistration == null) return;
		handlerRegistration.removeHandler();
		handlerRegistration2.removeHandler();
		handlerRegistration = null;
		handlerRegistration2 = null;
		DOM.releaseCapture(parent.getElement());
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		parent.setWidgetTopHeight(widget, event.getClientY() - offsetY, Unit.PX, height, Unit.PX);
		parent.setWidgetLeftWidth(widget, event.getClientX() - offsetX, Unit.PX, width, Unit.PX);
		parent.forceLayout();
		if(mouseMoveHandler != null){
			mouseMoveHandler.onMouseMove(event);
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if(mouseUpHandler != null){
			mouseUpHandler.onMouseUp(event);
		}
	}
}
