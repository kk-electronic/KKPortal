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

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.rpc.jsonformat.JsonChallange;
import com.kk_electronic.kkportal.core.rpc.jsonformat.JsonDate;
import com.kk_electronic.kkportal.core.rpc.jsonformat.JsonDouble;
import com.kk_electronic.kkportal.core.rpc.jsonformat.JsonIdentity;
import com.kk_electronic.kkportal.core.rpc.jsonformat.JsonInteger;
import com.kk_electronic.kkportal.core.rpc.jsonformat.JsonList;
import com.kk_electronic.kkportal.core.rpc.jsonformat.JsonMap;
import com.kk_electronic.kkportal.core.rpc.jsonformat.JsonModuleInfo;
import com.kk_electronic.kkportal.core.rpc.jsonformat.JsonRpcEnvelope;
import com.kk_electronic.kkportal.core.rpc.jsonformat.JsonString;
import com.kk_electronic.kkportal.core.rpc.jsonformat.JsonTabInfo;
import com.kk_electronic.kkportal.core.rpc.jsonformat.JsonValue;
import com.kk_electronic.kkportal.core.rpc.jsonformat.UnableToDeserialize;
import com.kk_electronic.kkportal.core.rpc.jsonformat.UnableToSerialize;
import com.kk_electronic.kkportal.core.security.Challange;
import com.kk_electronic.kkportal.core.security.Identity;
import com.kk_electronic.kkportal.core.services.ModuleService.ModuleInfo;
import com.kk_electronic.kkportal.core.tabs.TabInfo;

public class SimpleEncoder implements FrameEncoder<JSONValue> {

	HashMap<Class<?>, JsonValue<?>> types = new HashMap<Class<?>, JsonValue<?>>();

	@Inject
	public SimpleEncoder() {
		JsonValue<?> t;
		types.put(Identity.class, new JsonIdentity());
		t = new JsonRpcEnvelope();
		types.put(RpcRequest.class, t);
		types.put(RpcResponse.class, t);
		t = new JsonList<Object>();
		types.put(AbstractList.class, t);
		types.put(List.class, t);
		types.put(String.class, new JsonString());
		types.put(Integer.class, new JsonInteger());
		t = new JsonMap<Object>();
		types.put(Map.class, t);
		types.put(HashMap.class, t);
		types.put(TabInfo.class, new JsonTabInfo());
		types.put(ModuleInfo.class, new JsonModuleInfo());
		types.put(Double.class, new JsonDouble());
		types.put(Challange.class, new JsonChallange());
		types.put(Date.class, new JsonDate());
	}

	@SuppressWarnings("unchecked")
	public <T> JsonValue<T> get(Class<T> basetype) {
		return (JsonValue<T>) types.get(basetype);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void encode(T object, StringBuilder json)
			throws UnableToSerialize {
		if( object == null){
			json.append("null");
			return;
		}
		if( object instanceof Object[] ){
			T[] ts = (T[]) object;
			encode(Arrays.asList(ts), json);
			return;
		}
		JsonValue<T> jsonValue = find(object.getClass(), object);
		if (jsonValue == null)
			throw new UnableToSerialize("Could not find serializer for "
					+ object.getClass());
		jsonValue.toJson(json, object, this);
	}

	@SuppressWarnings("unchecked")
	private <T> JsonValue<T> find(Class<?> clazz, T ihatejava) {
		if (types.containsKey(clazz))
			return (JsonValue<T>) types.get(clazz);
		if (clazz.getSuperclass() != null)
			return find(clazz.getSuperclass(), ihatejava);
		return null;
	}

	@Override
	public <T> T validate(JSONValue result, T resultType, Class<?>[] subtypes)
			throws UnableToDeserialize {
		if (!(result instanceof JSONValue))
			throw new UnableToDeserialize("result must not be null");
		if (subtypes == null || subtypes.length == 0)
			throw new UnableToDeserialize("Subtypes must not be null or empty");
		return test(result,Arrays.asList(subtypes),resultType);
	}

	public <T> T test(JSONValue result, List<Class<?>> subtypes,T resultType) throws UnableToDeserialize {
		if(result.isNull() != null) return null;
		JsonValue<T> jsonValue = find(subtypes.get(0), resultType);
		if (jsonValue == null){
			throw new UnableToDeserialize("Could not find deserializer for "
					+ subtypes.get(0));
		}
		return jsonValue.fromJson(result, subtypes.subList(1, subtypes.size()), this);
	}

	@Override
	public JSONValue decode(String string) throws UnableToDeserialize {
		return JSONParser.parseLenient(string);
	}
}
