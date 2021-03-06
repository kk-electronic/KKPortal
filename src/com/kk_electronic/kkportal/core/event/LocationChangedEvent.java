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
package com.kk_electronic.kkportal.core.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.kk_electronic.kkportal.core.activity.LocationInfo;

/**
 * @author Jes Andersen
 */
public class LocationChangedEvent extends GwtEvent<LocationChangedEvent.Handler> {

	public interface Handler extends EventHandler {
		void onLocationChanged(LocationChangedEvent event);
	}

	/**
	 * Handler type.
	 */
	public static final Type<Handler> TYPE = new Type<Handler>();
	private final LocationInfo locationInfo;

	public LocationInfo getLocationInfo() {
		return locationInfo;
	}

	public LocationChangedEvent(LocationInfo locationInfo) {
		this.locationInfo = locationInfo;
	}

	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onLocationChanged(this);
	}
}
