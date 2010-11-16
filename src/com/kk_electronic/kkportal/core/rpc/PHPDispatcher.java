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


public class PHPDispatcher implements Dispatcher {

	@Override
	public <T> void execute(AsyncCallback<T> callback,
			Class<?>[] returnValueType,
			Class<? extends RemoteService> serverinterface, String method,
			Object... params) {
		// TODO Auto-generated method stub
		
	}
//
//	private final SimpleEncoder encoder;
//
//	@Inject
//	public PHPDispatcher(SimpleEncoder encoder) {
//		this.encoder = encoder;
//	}
//	
//	@Override
//	public <T> void execute(final AsyncCallback<T> callback,
//			final Class<?>[] returnValueType,
//			Class<? extends RemoteService> serverinterface, String method,
//			Object... params) {
//		String url = "http://1.2.3.4/php" + serverinterface.getName() + "/" + method + ".php";
//		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,url);
//		String data = JsonValueHelper.makeJSONValue(params).toString();
//		try {
//			builder.sendRequest(data,new RequestCallback() {
//				
//				@Override
//				public void onResponseReceived(com.google.gwt.http.client.Request request,
//						Response response) {
////					T result = encoder.decodeResult(returnValueType, response.getText());
////					callback.onSuccess(result);
//				}
//				
//				@Override
//				public void onError(com.google.gwt.http.client.Request request,
//						Throwable exception) {
//					callback.onFailure(exception);
//				}
//			});
//		} catch (RequestException e) {
//			callback.onFailure(e);
//		}
//	}
}
