package com.kk_electronic.kkportal.core.rpc.jsonformat;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.kk_electronic.kkportal.core.rpc.FrameEncoder;
import com.kk_electronic.kkportal.core.rpc.SimpleEncoder;

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
			SimpleEncoder simpleEncoder) throws UnableToDeserialize {
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
