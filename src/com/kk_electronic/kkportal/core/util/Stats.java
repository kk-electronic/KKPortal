package com.kk_electronic.kkportal.core.util;
import java.util.Date;

import com.google.gwt.core.client.GWT;

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
