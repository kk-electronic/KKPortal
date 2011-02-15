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
package com.kk_electronic.kkportal.examples.rpc;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.rpc.Dispatcher;
import com.kk_electronic.kkportal.core.rpc.RemoteService;
import com.kk_electronic.kkportal.core.rpc.JsonEncoder;
import com.kk_electronic.kkportal.core.rpc.jsonformat.UnableToDeserialize;
import com.kk_electronic.kkportal.core.rpc.jsonformat.UnableToSerialize;

/**
 * An Example of how a dispatcher for service requests would look written to use PHP
 * 
 * TODO: Write a more extensive comment on this dispatcher
 * 
 * It's server side file is located in "war-src/php" and is called dispatch.php
 * 
 * @author Jes Andersen
 *
 */
public class PHPDispatcher implements Dispatcher {

	private final JsonEncoder encoder;

	@Inject
	public PHPDispatcher(JsonEncoder encoder) {
		this.encoder = encoder;
	}
	
	@Override
	public <T> void execute(final AsyncCallback<T> callback,
			final Class<?>[] returnValueType,
			Class<? extends RemoteService> serverinterface, String method,
			Object... params) {
		String url = "php/dispatch.php?i="+serverinterface.getName()+"&m="+method;
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,url);
		StringBuilder sb = new StringBuilder();
		try {
			encoder.encode(params, sb);
		} catch (UnableToSerialize e) {
			callback.onFailure(e);
		}
		try {
			builder.sendRequest(sb.toString(),new RequestCallback() {
							
				@Override
				public void onError(com.google.gwt.http.client.Request request,
						Throwable exception) {
					callback.onFailure(exception);
				}

				@Override
				public void onResponseReceived(Request request,
						Response response) {
					JSONValue result;
					try {
						result = encoder.decode(response.getText());
					} catch (UnableToDeserialize e) {
						callback.onFailure(e);
						return;
					}
					T decodedResult = null;
					try {
						decodedResult = encoder.validate(result, decodedResult, returnValueType);
					} catch (UnableToDeserialize e) {
						callback.onFailure(e);
						return;
					}
					callback.onSuccess(decodedResult);
				}
			});
		} catch (RequestException e) {
			callback.onFailure(e);
		}
	}
}
