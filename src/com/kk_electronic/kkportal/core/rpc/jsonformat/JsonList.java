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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.kk_electronic.kkportal.core.rpc.FrameEncoder;
import com.kk_electronic.kkportal.core.rpc.JsonEncoder;

public class JsonList<T> implements JsonValue<List<T>> {

	public JsonList() {
	}

	@Override
	public void toJson(StringBuilder response, List<T> object,
			FrameEncoder<JSONValue> encoder) throws UnableToSerialize {
		response.append("[");
		for (int i = 0, l = object.size(); i < l; i++) {
			if (i != 0)
				response.append(",");
			encoder.encode(object.get(i), response);
		}
		response.append("]");
	}

	@Override
	public List<T> fromJson(JSONValue jsonValue, List<Class<?>> subtypes,
			JsonEncoder simpleEncoder) throws UnableToDeserialize {
		JSONArray jsonArray = jsonValue.isArray();
		if (jsonArray == null)
			throw new UnableToDeserialize("Expected Json Array");
		List<T> list = new ArrayList<T>();
		for (int i = 0, l = jsonArray.size(); i < l; i++) {
			T result = null;
			result = simpleEncoder.test(jsonArray.get(i),subtypes,result);
			list.add(result);
		}
		return list;
	}
}
