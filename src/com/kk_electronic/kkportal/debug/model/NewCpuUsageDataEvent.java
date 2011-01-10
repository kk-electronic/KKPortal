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
package com.kk_electronic.kkportal.debug.model;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.kk_electronic.kkportal.core.event.ServerEvent;

/**
 * This class is created and fired on the eventBus each time a NewCpuUsageDataEvent comes from the server.
 * The parameters are decodes and comes from the server as well.
 * @author Jes Andersen
 */
public class NewCpuUsageDataEvent extends GwtEvent<NewCpuUsageDataEvent.Handler> implements ServerEvent {

	/*
	 * Defines an interface that matches the event. This enables a Class to have many handlers defined
	 * for different events.
	 */
	public interface Handler extends EventHandler {
		void onNewCpuUsage(NewCpuUsageDataEvent event);
	}

	/**
	 * Handler type.
	 * Used when subscribing to the event on the eventbus.
	 */
	public static final Type<Handler> TYPE = new Type<Handler>();
	
	/*
	 * Parameters from the server
	 */
	private final String cpuname;
	private final Double cpuload;

	public NewCpuUsageDataEvent(String cpuname,Double cpuload) {
		this.cpuname = cpuname;
		this.cpuload = cpuload;
	}

	public String getCpuname() {
		return cpuname;
	}

	public Double getCpuload() {
		return cpuload;
	}

	/*
	 * The next two functions makes the class work with the GWT eventbus system.
	 */
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onNewCpuUsage(this);
	}
}
