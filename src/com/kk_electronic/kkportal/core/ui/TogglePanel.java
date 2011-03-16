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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author albatros
 *
 */
public class TogglePanel extends Composite implements IndexedPanel.ForIsWidget, HasWidgets.ForIsWidget {
	LayoutPanel holder = new LayoutPanel();
	List<Widget> widgets = new ArrayList<Widget>(); 
	
	public TogglePanel() {
		initWidget(holder);
	}
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.IndexedPanel.ForIsWidget#getWidgetIndex(com.google.gwt.user.client.ui.IsWidget)
	 */
	@Override
	public int getWidgetIndex(IsWidget child) {
		return getWidgetIndex(child.asWidget());
	}
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.IndexedPanel#getWidget(int)
	 */
	@Override
	public Widget getWidget(int index) {
		return widgets.get(index);
	}
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.IndexedPanel#getWidgetCount()
	 */
	@Override
	public int getWidgetCount() {
		return widgets.size();
	}
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.IndexedPanel#getWidgetIndex(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public int getWidgetIndex(Widget child) {
		return widgets.indexOf(child);
	}
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.IndexedPanel#remove(int)
	 */
	@Override
	public boolean remove(int index) {
		if(index < 0 || index >= widgets.size()){
			return false;
		}
		Widget w = widgets.remove(index);
		if(getWidget() == w){
			holder.clear();
		}
		return true;
	}
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasWidgets#add(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public void add(Widget w) {
		if(getWidget() == null){
			holder.add(w);
		}
		widgets.add(w);
	}
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasWidgets#clear()
	 */
	@Override
	public void clear() {
		widgets.clear();
		holder.clear();
	}
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasWidgets#iterator()
	 */
	@Override
	public Iterator<Widget> iterator() {
		return widgets.iterator();
	}
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasWidgets#remove(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public boolean remove(Widget w) {
		if(getWidget() == w){
			holder.clear();
		}
		return widgets.remove(w);
	}
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasWidgets.ForIsWidget#add(com.google.gwt.user.client.ui.IsWidget)
	 */
	@Override
	public void add(IsWidget w) {
		add(w.asWidget());
	}
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasWidgets.ForIsWidget#remove(com.google.gwt.user.client.ui.IsWidget)
	 */
	@Override
	public boolean remove(IsWidget w) {
		return remove(w.asWidget());
	}
	
	public void setWidget(Widget widget) {
		if(!widgets.contains(widget)){
			widgets.add(widget);
		}
		showWidget(widget);
	}
	private void showWidget(Widget widget) {
		if(getWidget() == widget) {
			return;
		}
		if(holder.getWidgetCount() > 0){
			holder.clear();
		}
		holder.add(widget);
	}
	
	public Widget getWidget(){
		if(holder.getWidgetCount() == 0) return null;
		return holder.getWidget(0);
	}
	/**
	 * @param i
	 */
	public Widget setWidget(int index) {
		if(index < 0 || index >= widgets.size()){
			return null;
		}
		Widget w = widgets.get(index);
		showWidget(w);
		return w;
	}
}
