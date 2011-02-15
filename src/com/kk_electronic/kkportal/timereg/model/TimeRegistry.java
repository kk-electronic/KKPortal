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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.util.Range;
import com.kk_electronic.kkportal.timereg.ui.TimeView;

/**
 * @author albatros
 * 
 */
public class TimeRegistry {

	Range<Long> fetched = new Range<Long>();
	Range<Long> requested = new Range<Long>();

	private final List<TimeEntry> entries = new LinkedList<TimeEntry>();
	private List<TimeView> displays = new LinkedList<TimeView>();
	private final TimeService timeService;

	@Inject
	public TimeRegistry(TimeService timeService) {
		this.timeService = timeService;
	}

	public void checkin() {
		Date now = new Date();
		entries.add(new TimeEntry(now, null, null));
		updatedisplays();
	}

	private void updatedisplays() {
		for (TimeView display : displays) {
			display.update(entries);
		}
	}

	public TimeEntry getLast() {
		if (entries.size() == 0)
			return null;
		return entries.get(entries.size() - 1);
	}

	public void checkout() {
		Date now = new Date();
		TimeEntry entry = getLast();
		if (entry != null && entry.getCheckout() == null) {
			entry.setCheckout(now);
		} else {
			entries.add(new TimeEntry(null, now, null));
		}
		updatedisplays();
	}

	/**
	 * @param timeView
	 */
	public void addDisplay(TimeView timeView) {
		displays.add(timeView);
		Range<Long> viewrange = timeView.getRange();
		if (viewrange == null || !viewrange.isBounded()) {
			return;
		}
		if (fetched.contains(viewrange)) {
			timeView.getHasData().setRowData(0, entries);
		} else {
			requestrange(viewrange);
		}
	}

	/**
	 * @param viewrange
	 */
	private void requestrange(Range<Long> viewrange) {
		if(requested.contains(viewrange)){
			return;
		} else {
			Range<Long> upperrange = viewrange.clone();
			upperrange.begin = fetched.end;
			Range<Long> lowerrange = viewrange.clone();
			lowerrange.end = fetched.begin;
		}
	}
}
