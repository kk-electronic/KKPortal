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
package com.kk_electronic.kkportal.core.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kk_electronic.kkportal.core.rpc.RemoteService;
import com.kk_electronic.kkportal.core.tabs.TabInfo;

/**
 * This Service is responsible for the communication about Tabs
 * 
 * @author Rasmus Carlsen
 */
public interface TabService extends RemoteService {
	
	/**
	 * Create a new Tab based on the tab information given.
	 * 
	 * @param tab TabInfo to be used 
	 * @param callback Returns the resulting id of the tab
	 */
	void create(TabInfo tab, AsyncCallback<Integer> callback);
	
	/**
	 * Updates the given tab.
	 * 
	 * @param tab TabInfo to be used
	 * @param callback Generic callback to indicate successful completion of task
	 */
	void update(TabInfo tab, AsyncCallback<?> callback);

	/**
	 * Gets the tabs for the given user.
	 * 
	 * @param asyncCallback returns a list of {@link TabInfo} to onSucces if successful
	 */
	void retrieve(AsyncCallback<List<TabInfo>> asyncCallback);
	
	/**
	 * Delete the given tab from the users tabs
	 * 
	 * @param id Id for the targeted tab
	 * @param callback Generic callback to indicate successful completion of task
	 */
	void delete(int id, AsyncCallback<?> callback);
}