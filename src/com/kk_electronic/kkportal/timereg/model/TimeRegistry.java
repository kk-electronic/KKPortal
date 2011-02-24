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
import com.kk_electronic.kkportal.timereg.ui.TimeView;

/**
 * @author Jes Andersen
 * 
 */
public class TimeRegistry {

	//Range that is ready and received from the server
	Range<Long> fetched = new Range<Long>();
	//Range that we ave requested from the server
	//If fetched.equals(requested) we do not have any outstanding requests
	Range<Long> requested = new Range<Long>();

	//The actual data
	//For simplicity we use use a plain list
	private final List<TimeEntry> entries = new LinkedList<TimeEntry>();
	//List of displays interested in updates to the data
	private List<TimeView> displays = new LinkedList<TimeView>();
	private final TimeService timeService;

	//Constructor takes a TimeService so we can actually fetch items from the backend
	@Inject
	public TimeRegistry(TimeService timeService) {
		this.timeService = timeService;
	}
	
	//Checkin should add a new TimeEntry
	public void checkin() {
		if(!canCheckin()){
			return;
		}
		//Without arguments creates the current local time
		Date now = new Date();
		//getTime is in milliseconds and TimeEntry uses seconds
		final TimeEntry entry = new TimeEntry(now.getTime()/1000, null, null);
		//This will call the service, fill out the id, and update displays
		addNewEntry(entry);
	}

	//Checking is possible if we have fetched something from the server and the last
	//entry is checked out
	public boolean canCheckin() {
		if (!fetched.isBounded()){
			return false;
		}
		//Refuse to do a double checkin
		TimeEntry e = getLast();
		if(e != null && e.getCheckout() == null){
			return false;
		}
		return true;
	}	

	//Checkout is possible if the last entry is not checked out
	public boolean canCheckout() {
		TimeEntry e = getLast();
		if(e != null && e.getCheckout() == null){
			return true;
		}
		return false;
	}	

	//Checkout should either update a entry if there is one to close or create a new one
	public void checkout() {
		if(!canCheckout()){
			return;
		}
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
	//This is used by checkout to find an potential update entry
	public TimeEntry getLast() {
		if (entries.size() == 0)
			return null;
		return entries.get(entries.size() - 1);
	}

	//Add a new View to be called when the data changes
	public void addDisplay(TimeView timeView) {
		displays.add(timeView);
		timeView.addRangeUpdatedHandler(rangeHandler);
		Range<Long> viewrange = timeView.getRange();
		if (viewrange == null || !viewrange.isBounded()) {
			return;
		}
		if (fetched.contains(viewrange)) {
			timeView.update(filterEntries(timeView.getRange()));
		} else {
			ensureRange(viewrange);
		}
	}
	//Called when a TimeView updates the time range
	private RangeUpdatedEvent.Handler rangeHandler = new RangeUpdatedEvent.Handler() {
		@Override
		public void onRangeUpdate(RangeUpdatedEvent event) {
			if(fetched.contains(event.getRange())){
				updatedisplays();
			} else {
				ensureRange(event.getRange());
			}
		}
	};
	
	//Function to update all displays added with addDisplay();
	private void updatedisplays() {
		//For each display we update it with a list fitting it's range
		for (TimeView display : displays) {
			display.update(filterEntries(display.getRange()));
		}
	}
	//Filter the entries and return a list of entries that overlaps visible for a range
	//User by updatedisplays() to send the appropriate data
	private List<TimeEntry> filterEntries(Range<Long> range){
		List<TimeEntry> result = new ArrayList<TimeEntry>();
		for(TimeEntry entry:entries){
			//If the entry ends before our range, we skip it
			if(entry.getCheckout() != null && entry.getCheckout() < range.begin){
				continue;
			}
			//If the entry begins after our range, we skip it
			if(entry.getCheckin() != null && entry.getCheckin() > range.end){
				continue;
			}
			//When we don't for sure it not there we add it
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

	//Call the service to add a new entry and update the id when the call returns
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

	//Called to ensure a given range is in the data.
	//In case of missing data callsrequestRangeFromService()
	private void ensureRange(Range<Long> viewrange) {
		//If the range is inside what is already requested we don't do anything
		if (requested.contains(viewrange)) {
			return;
		} else {
			//The requested range is expanded with the new range
			if (requested.isBounded()){
				requested.begin = Math.min(requested.begin, viewrange.begin);
				requested.end = Math.max(requested.end, viewrange.end);
			} else {
				//if requested is null is copy the range
				requested = viewrange.clone();
			}
			//if we have some data we need to construct the two new ranges we need
			if (fetched.isBounded()) {
				Range<Long> upperrange = requested.clone();
				upperrange.begin = fetched.end;
				Range<Long> lowerrange = requested.clone();
				lowerrange.end = fetched.begin;
				requestRangeFromService(lowerrange);
				requestRangeFromService(upperrange);
			} else {
				//If we do not have any entries simply request the entire range
				viewrange.clone();
				requestRangeFromService(viewrange);
			}
		}
	}
	//Makes a call the the service to fetch new data
	private void requestRangeFromService(final Range<Long> range) {
		//Do a sanity check to prevent asking the server for senseless ranges
		if (range.begin >= range.end){
			return;
		}
		timeService.get(range.begin, range.end,
				new AsyncCallback<List<TimeEntry>>() {

					@Override
					public void onSuccess(List<TimeEntry> result) {
						//Add the new entries and update the fetched range
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

	//Set of seen id's
	private Set<Integer> seen = new HashSet<Integer>();
	//Adds a list of new data entries to the internal data structure
	//It has a guard to protect against entries getting added multiple times.
	protected void update(List<TimeEntry> result, Range<Long> range) {
		fetched.extend(range);
		for(TimeEntry entry: result){
			//This prevents entries that spans from the requested into fetches
			//from getting added multiple times 
			if(seen.add(entry.getId())){
				//This part is only called if entry id was not seen before
				entries.add(entry);
			}
		}
		//Since we have changed the entries data, we update 
		updatedisplays();
	}
}
