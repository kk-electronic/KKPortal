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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.kk_electronic.kkportal.core.rpc.FrameEncoder;

public class JsonMap<V> implements JsonValue<Map<String,V>> {

	public JsonMap() {
	}

	@Override
	public void toJson(StringBuilder response, Map<String,V> object,
			FrameEncoder<JSONValue> encoder) throws UnableToSerialize {
		throw new UnableToSerialize("not implemented yet");
	}

	@Override
	public Map<String,V> fromJson(JSONValue jsonValue, List<Class<?>> subtypes,
			FrameEncoder<JSONValue> encoder) throws UnableToDeserialize {
		JSONObject jsonObject = jsonValue.isObject();
		if (jsonObject == null)
			throw new UnableToDeserialize("Expected Json Object");
		if (subtypes.get(0) != String.class)
			throw new UnableToDeserialize("Json Maps only supports Strings as keys");
		subtypes = subtypes.subList(1, subtypes.size());
		Map<String,V> map = new HashMap<String, V>();
		for (String key :jsonObject.keySet()){
			V result = null;
			result = encoder.validate(jsonObject.get(key), result, subtypes.toArray(new Class<?>[subtypes.size()]));
			map.put(key, result);
		}
		return map;
	}
}
