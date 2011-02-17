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

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TimeServiceMock implements TimeService {
	int nextid = 1;
	
	List<TimeEntry> entries = new LinkedList<TimeEntry>();
	public TimeServiceMock() {
		long now = new Date().getTime()/1000;
		TimeEntry t = new TimeEntry(now, now+60, null);
		t.setId(nextid++);
		entries.add(t);
		t = new TimeEntry(now+120, now+180, null);
		t.setId(nextid++);
		entries.add(t);
		t = new TimeEntry(now+200, null, null);
		t.setId(nextid++);
		entries.add(t);
	}
	
	@Override
	public void add(TimeEntry entry, AsyncCallback<Integer> callback) {
		callback.onFailure(new UnsupportedOperationException());
	}

	@Override
	public void get(Long begin, Long end,
			final AsyncCallback<List<TimeEntry>> callback) {
		Timer t = new Timer() {
			
			@Override
			public void run() {
				callback.onSuccess(entries);
			}
		};
		t.schedule(1000);
	}

	@Override
	public void remove(int id, AsyncCallback<?> callback) {
		callback.onFailure(new UnsupportedOperationException());
	}

	@Override
	public void update(TimeEntry entry, AsyncCallback<?> callback) {
		callback.onFailure(new UnsupportedOperationException());
	}
}
