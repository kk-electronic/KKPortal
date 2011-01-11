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
package com.kk_electronic.kkportal.core.rpc.jsonformat;

import java.util.List;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.kk_electronic.kkportal.core.rpc.FrameEncoder;
import com.kk_electronic.kkportal.core.rpc.RpcEnvelope;
import com.kk_electronic.kkportal.core.rpc.RpcError;
import com.kk_electronic.kkportal.core.rpc.RpcRequest;
import com.kk_electronic.kkportal.core.rpc.RpcResponse;
import com.kk_electronic.kkportal.core.rpc.SimpleEncoder;

public class JsonRpcEnvelope implements JsonValue<RpcEnvelope> {

	@Override
	public void toJson(StringBuilder response, RpcEnvelope object,
			FrameEncoder<JSONValue> encoder) throws UnableToSerialize {
		if(object instanceof RpcRequest){
			toJson(response,(RpcRequest) object,encoder);
		}
		else if(object instanceof RpcResponse<?>){
			toJson(response,(RpcResponse<?>) object,encoder);
		}
	}
	
	public void toJson(StringBuilder response, RpcRequest object,
			FrameEncoder<JSONValue> encoder) throws UnableToSerialize {
		response.append("{");
		
		encoder.encode("method", response);
		response.append(":");
		encoder.encode(object.getFeatureName() + "." + object.getMethod(), response);
		
		response.append(",");
		
		encoder.encode("id", response);
		response.append(":");
		encoder.encode(object.getId(), response);
		
		response.append(",");
		
		encoder.encode("params", response);
		response.append(":");
		encoder.encode(object.getParams(), response);
		
		response.append("}");
	}

	public void toJson(StringBuilder response, RpcResponse<?> object,
			FrameEncoder<JSONValue> encoder) throws UnableToSerialize {
		throw new UnableToSerialize("not implemented yet");
	}

	@Override
	public RpcEnvelope fromJson(JSONValue jsonValue, List<Class<?>> subtypes,
			SimpleEncoder simpleEncoder) throws UnableToDeserialize {
		JSONObject jsonObject = jsonValue.isObject();
		if(jsonObject == null) throw new UnableToDeserialize("Identity must be an Json Object");
		if(jsonObject.containsKey("result")) return responseFromJson(jsonObject,simpleEncoder);
		if(jsonObject.containsKey("method")) return requestFromJson(jsonObject,simpleEncoder);
		if(jsonObject.containsKey("error")) return errorFromJson(jsonObject,simpleEncoder);
		throw new UnableToDeserialize("Json Rpc Envelope must contain either result,error or method");
	}

	private RpcError errorFromJson(JSONObject jsonValue, SimpleEncoder simpleEncoder) throws UnableToDeserialize {
		if(! ( jsonValue.containsKey("error") && jsonValue.containsKey("id"))) throw new UnableToDeserialize("Json Rpc Error must contain both id and error");
		Integer id = null;
		id = simpleEncoder.validate(jsonValue.get("id"),id,new Class<?>[]{Integer.class});
		JSONObject error = jsonValue.get("error").isObject();
		if(error == null || ! (error.containsKey("message") && error.containsKey("code"))) throw new UnableToDeserialize("Json Rpc Error element must be an object that contains both message and code");
		String message = null;
		message = simpleEncoder.validate(error.get("message"), message, new Class<?>[]{String.class});
		Integer code = null;
		code = simpleEncoder.validate(error.get("code"), code, new Class<?>[]{Integer.class});
		return new RpcError(id,code,message,error.get("data")); 
	}

	public RpcResponse<?> responseFromJson(JSONObject jsonValue, SimpleEncoder simpleEncoder) throws UnableToDeserialize {
		if(! ( jsonValue.containsKey("id") && jsonValue.containsKey("result"))) throw new UnableToDeserialize("Json Rpc Response must contain both id and result");
		Integer id = null;
		id = simpleEncoder.validate(jsonValue.get("id"),id,new Class<?>[]{Integer.class});
		JSONValue result = jsonValue.get("result");
		return new RpcResponse<Object>(id, result); 
	}
	
	public RpcRequest requestFromJson(JSONObject jsonValue, SimpleEncoder simpleEncoder) throws UnableToDeserialize {
		if(! ( jsonValue.containsKey("method") && jsonValue.containsKey("params"))) throw new UnableToDeserialize("Json Rpc Response must contain both method and params");
		String method = null;
		method = simpleEncoder.validate(jsonValue.get("method"),method,new Class<?>[]{String.class});
		JSONValue params = jsonValue.get("params");
		return new RpcRequest(null,method,params); 
	}
}
