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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author albatros
 *
 */
public class Request<T> implements AsyncCallback<T>{
	private AsyncCallback<T> callback;
	private Class<?>[] returnValueType;
	private Class<? extends RemoteService> serverinterface;
	private String method;
	private Object[] params;
	
	public Request() {
	}

	public Request(AsyncCallback<T> callback, Class<?>[] returnValueType,
			Class<? extends RemoteService> serverinterface, String method,
			Object... params) {
		super();
		this.callback = callback;
		this.returnValueType = returnValueType;
		this.serverinterface = serverinterface;
		this.method = method;
		this.params = params;
	}

	public AsyncCallback<T> getCallback() {
		return callback;
	}

	public void setCallback(AsyncCallback<T> callback) {
		this.callback = callback;
	}

	public Class<?>[] getReturnValueType() {
		return returnValueType;
	}

	public void setReturnValueType(Class<?>[] returnValueType) {
		this.returnValueType = returnValueType;
	}

	public Class<? extends RemoteService> getServerinterface() {
		return serverinterface;
	}

	public void setServerinterface(Class<? extends RemoteService> serverinterface) {
		this.serverinterface = serverinterface;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	@Override
	public void onFailure(Throwable caught) {
		if(callback != null){
			callback.onFailure(caught);
		}
	}

	@Override
	public void onSuccess(T result) {
		if(callback != null){
			callback.onSuccess(result);
		}
	}
	
}
