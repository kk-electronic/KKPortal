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
package com.kk_electronic.kkportal.core.util;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.inject.ConstructFromLiteral;

@ConstructFromLiteral
public class DebugPanel {
	static HTML panel;
	static boolean showing = false;

	private NativePreviewHandler handler = new NativePreviewHandler() {
		
		@Override
		public void onPreviewNativeEvent(NativePreviewEvent event) {
			if(event.getTypeInt() == Event.ONKEYDOWN){
				NativeEvent e = event.getNativeEvent();
				if (e.getAltKey() && e.getShiftKey()){
					toggleDebugWindow();
				}
			}
		}
	};
	private final LayoutPanel p;

	@Inject
	public DebugPanel() {
		p = RootLayoutPanel.get();
		Event.addNativePreviewHandler(handler);
		refire();
		attachListener();
	}

	protected void toggleDebugWindow() {
		ensureWidget();
		showing = ! showing;
		p.setWidgetTopHeight(panel, 0, Unit.PCT, showing?100:0, Unit.PCT);
		p.animate(250);
	}

	private void ensureWidget() {
		if(panel != null) return;
		panel = new HTML();
		panel.getElement().getStyle().setZIndex(200);
		panel.getElement().getStyle().setBackgroundColor("black");
		panel.getElement().getStyle().setColor("green");
		panel.getElement().getStyle().setOverflow(Overflow.SCROLL);
		RootLayoutPanel.get().add(panel);
		updatelog();
	}
	
	private static void updatelog(){
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.appendEscaped("Raw log");
		builder.appendHtmlConstant("<table><tr><th>Time</th><th>Sub System</th><th>Group Key</th><th>Type</th></tr>");
		for (MetricInfo i : infos){
			builder.appendHtmlConstant("<tr><td>");
			builder.append((long)(i.getMillis()-start));
			builder.appendHtmlConstant("</td><td>");
			builder.appendEscaped(i.getSubSystem());
			builder.appendHtmlConstant("</td><td>");
			builder.appendEscaped(i.getEvtGroup());
			builder.appendHtmlConstant("</td><td>");
			builder.appendEscaped(i.getType());
			builder.appendHtmlConstant("</td></tr>");
		}
		builder.appendHtmlConstant("</table>");
		panel.setHTML(builder.toSafeHtml());
	}
		
	static List<MetricInfo> infos = new LinkedList<MetricInfo>();
	
	static Double start;
	
	@SuppressWarnings("unused")
	private static void onMetric(MetricInfo info){
		if(start == null){
			start = info.getMillis();
		}
		infos.add(info);
		if(panel != null){
			updatelog();
		}
	}
	
	private native void refire() /*-{
		for (key in $wnd.__stats){
			@com.kk_electronic.kkportal.core.util.DebugPanel::onMetric(Lcom/kk_electronic/kkportal/core/util/MetricInfo;)($wnd.__stats[key]);
		}
	}-*/;
	
	private native void attachListener() /*-{
		$wnd.__stats_listener = @com.kk_electronic.kkportal.core.util.DebugPanel::onMetric(Lcom/kk_electronic/kkportal/core/util/MetricInfo;);
	}-*/;
}
