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
package com.kk_electronic.kkportal.timereg.model;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.kk_electronic.kkportal.core.util.Range;

/**
 * Event is fired when the range of a Time View Module is updated.
 * 
 * @author Rasmus Carlsen
 */
public class RangeUpdatedEvent extends GwtEvent<RangeUpdatedEvent.Handler> {

	public interface Handler extends EventHandler {
		void onRangeUpdate(RangeUpdatedEvent event);
	}

	/**
	 * Handler type.
	 */
	public static final Type<Handler> TYPE = new Type<Handler>();
	private final Range<Long> range;

	public RangeUpdatedEvent(Range<Long> range) {
		this.range = range;
	}

	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onRangeUpdate(this);
	}

	/**
	 * @return
	 */
	public Range<Long> getRange() {
		return this.range;
	}
}
