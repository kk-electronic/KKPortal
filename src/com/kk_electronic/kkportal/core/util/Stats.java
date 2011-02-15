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

import java.util.Date;

import com.google.gwt.core.client.GWT;

/**
 * Stats is used to send logEvents
 * 
 * @author Jes Andersen
 */
public class Stats {
	
	public void sendStats(Object module, Object key,String type) {
		logEvent(GWT.getModuleName(), clip(module.toString()), clip(key.toString()), new Date().getTime(), type);
	}
	
	private String clip(String s){
		return s.substring(s.lastIndexOf(".") +1);
	}

	private static native void logEvent(String moduleName, String subSystem,
			String eventGroup, double millis, String type) /*-{
		$wnd.__gwtStatsEvent({
		  'moduleName' : moduleName,
		  'subSystem' : subSystem,
		  'evtGroup' : eventGroup,
		  'millis' : millis,
		  'type' : type
		});
	}-*/;
}
