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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.kk_electronic.kkportal.core.rpc.jsonformat.UnableToSerialize;

public final class RpcRequest implements IsSerializable,RpcEnvelope {
	
	private final String featureName;
	private final String method;
	private List<?> params;
	private Integer id;

	public String getFeatureName() {
		return featureName;
	}

	public String getMethod() {
		return method;
	}

	public List<?> getParams() {
		return params;
	}

	public Integer getId() {
		return id;
	}

	public RpcRequest(
			String featureName, String method,
			Object... params) {
				this.featureName = featureName;
				this.method = method;
				this.setParams(Arrays.asList(params));
	}
	
	public RpcRequest(
			String featureName, String method) {
				this.featureName = featureName;
				this.method = method;
	}
	
	public String getSignature(FrameEncoder<JSONValue> encoder){
		StringBuilder sb = new StringBuilder();
		sb.append(getId());
		sb.append(',');
		sb.append(getFeatureName());
		sb.append('.');
		sb.append(getMethod());
		sb.append(',');
		try {
			encoder.encode(params, sb);
		} catch (UnableToSerialize e) {
			GWT.log("Could bnot create signature",e);
			return null;
		}
		return sb.toString();
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setParams(List<?> params) {
		this.params = params;
	}

	public void setParams(Object[] objects) {
		this.params = Arrays.asList(objects);
	}
}
