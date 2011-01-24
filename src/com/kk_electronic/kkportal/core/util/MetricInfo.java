/**
 * 
 */
package com.kk_electronic.kkportal.core.util;

import com.google.gwt.core.client.JavaScriptObject;

public final class MetricInfo extends JavaScriptObject{
	protected MetricInfo() {}
	
	public native double getMillis() /*-{
		return this.millis;
	}-*/;
	public native String getModuleName() /*-{
	return this.moduleName;
}-*/;
	public native String getSubSystem() /*-{
	return this.subSystem;
}-*/;
	public native String getEvtGroup() /*-{
	return this.evtGroup;
}-*/;
	public native String getType() /*-{
	return this.type;
}-*/;
}