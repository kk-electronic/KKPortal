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
package com.kk_electronic.kkportal.core.rpc;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;

public class DelayingDispatcher implements Dispatcher {

	private boolean isReady = false;
	private Dispatcher dispatcher;

	public ArrayList<Request<?>> delayedExecutions;

	public <T> void execute(Request<T> request) {
		if (isReady()) {
			dispatcher.execute(request);
		} else {
			if (delayedExecutions == null) {
				delayedExecutions = new ArrayList<Request<?>>();
			}
			delayedExecutions.add(request);
		}
	}

	public void setDispatcher(Dispatcher dispatcher) {
		if (dispatcher == null) {
			GWT.log("Dispatcher was set to null in DelayingDispatcher");
			return;
		}
		this.dispatcher = dispatcher;
		setReady(true);
	}

	public Dispatcher getDispatcher() {
		return dispatcher;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
		if (isReady) {
			runDelayed();
		}
	}

	private void runDelayed() {
		if (delayedExecutions != null) {
			for (Request<?> d : delayedExecutions) {
				dispatcher.execute(d);
			}
		}
		delayedExecutions = null;
	}

	public boolean isReady() {
		return isReady;
	}
}
