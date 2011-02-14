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

/**
 * The {@link RemoteService} interface is a marker interface that
 * enables the framework to generate the implementation of this
 * interface, which links it to the rest of the system.
 */
public interface TechDemo extends RemoteService {
	/**
	 * The interface must use {@link AsyncCallback} instead of
	 * a return value, since java script is single threaded and
	 * we cannot make blocking calls. 
	 */
	void getCpuHistory(AsyncCallback<List<Double>> callback);

	/**
	 * Attempts to reload the python code on the server, <br>
	 * so that new instances are of the newest code on the server.
	 */
	void reloadPythonCode(AsyncCallback<?> callback);
	
	/**
	 * Sets the service to observe changes on the specific path, <br>
	 * and notify the callback of any changes in a timely fashion.
	 * 
	 * @param path Location on file system to be observed
	 */
	void inotify(String path, AsyncCallback<?> callback);
	
	/**
	 * Get the current Wall messages. 
	 */
	void getWall(AsyncCallback<List<String>> callback);
	
	/**
	 * Transmit a message to the wall. 
	 * 
	 * <p>Callback is for successful or failed attempts</p>
	 * 
	 * @param message String message to post on the wall
	 */
	void postToWall(String message, AsyncCallback<?> callback);
}