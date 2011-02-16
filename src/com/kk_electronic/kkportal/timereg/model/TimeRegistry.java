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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.util.Range;
import com.kk_electronic.kkportal.timereg.model.RangeUpdatedEvent.Handler;
import com.kk_electronic.kkportal.timereg.ui.TimeView;

/**
 * @author Jes Andersen
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
		final TimeEntry entry = new TimeEntry(now.getTime()/1000, null, null);
		addNewEntry(entry);
	}

	private void updatedisplays() {
		for (TimeView display : displays) {
			display.update(findInRange(display.getRange()));
		}
	}

	public TimeEntry getLast() {
		if (entries.size() == 0)
			return null;
		return entries.get(entries.size() - 1);
	}
	
	private List<TimeEntry> findInRange(Range<Long> range){
		List<TimeEntry> result = new ArrayList<TimeEntry>();
		for(TimeEntry entry:entries){
			if(entry.getCheckout() != null && entry.getCheckout() < range.begin){
				continue;
			}
			if(entry.getCheckin() != null && entry.getCheckin() > range.end){
				continue;
			}
			result.add(entry);
		}
		//TODO: Sort
		return result;
	}
	
	AsyncCallback<Object> ignore = new AsyncCallback<Object>(){

		@Override
		public void onFailure(Throwable caught) {
			GWT.log("TimeRegistry - Failed to update",caught);
		}

		@Override
		public void onSuccess(Object result) {
			GWT.log("TimeRegistry - Updated");			
		}
	};

	private Handler rangeHandler = new RangeUpdatedEvent.Handler() {
		@Override
		public void onRangeUpdate(RangeUpdatedEvent event) {
			if(fetched.contains(event.getRange())){
				updatedisplays();
			} else {
				requestrange(event.getRange());
			}
		}
	};

	public void checkout() {
		Date now = new Date();
		TimeEntry entry = getLast();
		if (entry != null && entry.getCheckoutDate() == null) {
			entry.setCheckoutDate(now);
			timeService.update(entry, ignore);
			updatedisplays();
		} else {
			addNewEntry(new TimeEntry(null, now.getTime()/1000, null));
		}
	}

	/**
	 * @param entry
	 */
	private void addNewEntry(final TimeEntry entry) {
		entries.add(entry);
		timeService.add(entry, new AsyncCallback<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
				entry.setId(result);
				seen.add(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				entries.remove(entry);
			}
		});
		updatedisplays();
	}

	/**
	 * @param timeView
	 */
	public void addDisplay(TimeView timeView) {
		displays.add(timeView);
		timeView.addRangeUpdatedHandler(rangeHandler);
		Range<Long> viewrange = timeView.getRange();
		if (viewrange == null || !viewrange.isBounded()) {
			return;
		}
		if (fetched.contains(viewrange)) {
			timeView.update(findInRange(timeView.getRange()));
		} else {
			requestrange(viewrange);
		}
	}

	/**
	 * @param viewrange
	 */
	private void requestrange(Range<Long> viewrange) {
		if (requested.contains(viewrange)) {
			return;
		} else {
			if (requested.isBounded()){
				requested.begin = Math.min(requested.begin, viewrange.begin);
				requested.end = Math.max(requested.end, viewrange.end);
			} else {
				requested = viewrange.clone();
			}
			if (fetched.isBounded()) {
				Range<Long> upperrange = requested.clone();
				upperrange.begin = fetched.end;
				Range<Long> lowerrange = requested.clone();
				lowerrange.end = fetched.begin;
				request(lowerrange);
				request(upperrange);
			} else {
				viewrange.clone();
				request(viewrange);
			}
		}
	}

	/**
	 * @param viewrange
	 */
	private void request(final Range<Long> range) {
		if (range.begin >= range.end){
			return;
		}
		timeService.get(range.begin, range.end,
				new AsyncCallback<List<TimeEntry>>() {

					@Override
					public void onSuccess(List<TimeEntry> result) {
						update(result,range);
						GWT.log("TimeRegistry - Got entries:" + result.size());
					}

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("TimeRegistry - Failed fetching entries",
								caught);
					}
				});
	}

	private Set<Integer> seen = new HashSet<Integer>();
	/**
	 * @param result
	 * @param range 
	 */
	protected void update(List<TimeEntry> result, Range<Long> range) {
		fetched.extend(range);
		for(TimeEntry entry: result){
			if(seen.add(entry.getId())){
				entries.add(entry);
			}
		}
		updatedisplays();
	}
}
