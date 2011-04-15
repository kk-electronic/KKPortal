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

package com.kk_electronic.kkportal.scada;

import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.rpc.Dispatcher;
import com.kk_electronic.kkportal.core.rpc.JsonEncoder;
import com.kk_electronic.kkportal.core.rpc.Request;
import com.kk_electronic.kkportal.core.rpc.jsonformat.UnableToDeserialize;
import com.kk_electronic.kkportal.core.services.HttpProxy;
import com.kk_electronic.kkportal.core.services.HttpProxy.HttpResult;

/**
 * @author Jes Andersen
 * 
 *         This is the class that actually calls scada. On KKPortal it uses
 *         {@link HttpProxy} but could easily be fetchJSON on KKPilot.
 * 
 *         It uses {@link JsonEncoder} from the framework to decode the json
 *         response. but just uses toString() when passing the parameters in.
 */
public class ScadaJsonCaller implements Dispatcher {

	private final HttpProxy proxy;
	private final JsonEncoder encoder;

	@Inject
	public ScadaJsonCaller(HttpProxy proxy, JsonEncoder encoder) {
		this.proxy = proxy;
		this.encoder = encoder;
	}

	/**
	 * Using {@link ISecurityService#Login(String, String, AsyncCallback)} as an
	 * example the result should be:
	 * 
	 * <pre>
	 * http://77.243.35.41:8732/ParkServices/ParkService/JSON/Login?userName=******&password=******
	 * </pre>
	 * 
	 */
	@Override
	public <T> void execute(final Request<T> request) {
		// First we add the semi static part
		StringBuilder sb = new StringBuilder("http://");
		sb.append("77.243.35.41");
		sb.append(":");
		sb.append(8732);
		sb.append("/ParkServices/ParkService/JSON/");
		// The last part of the url is made from the method name and the
		// parameters
		sb.append(request.getMethod());
		String[] names = request.getNames();
		Object[] values = request.getParams();
		// The first key-value pair must be separated by a "?"
		char sep = '?';
		// For every pair
		for (int i = 0, l = Math.min(names.length, values.length); i < l; i++) {
			// We skip the parameter name if the value is null
			if (values[i] != null) {
				sb.append(sep);
				sep = '&';
				// We urlencode both the key
				sb.append(URL.encodePathSegment(names[i]));
				sb.append('=');
				// and the value
				sb.append(URL.encodePathSegment(values[i].toString()));
			}
		}
		// We call the proxy to fetch the result
		proxy.fetch(sb.toString(), null, new AsyncCallback<HttpResult>() {

			@Override
			public void onSuccess(HttpResult result) {
				// We have a holder for the decoded value
				T r = null;
				JSONValue s;
				try {
					// Using information in the request object we can decode the
					// json
					// Why? To allow the same json to be decoded to different
					// java beans, so metadata such a class name does not need
					// to be transfered.
					s = encoder.decode(result.body);
					r = encoder.validate(s, r, request.getReturnValueType());
					request.onSuccess(r);
				} catch (UnableToDeserialize e) {
					request.onFailure(e);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				request.onFailure(caught);
			}
		});
	}
}
