package com.kk_electronic.kkportal.core.rpc.jsonformat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.kk_electronic.kkportal.core.rpc.FrameEncoder;
import com.kk_electronic.kkportal.core.rpc.SimpleEncoder;

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
			SimpleEncoder simpleEncoder) throws UnableToDeserialize {
		JSONObject jsonObject = jsonValue.isObject();
		if (jsonObject == null)
			throw new UnableToDeserialize("Expected Json Object");
		if (subtypes.get(0) != String.class)
			throw new UnableToDeserialize("Json Maps only supports Strings as keys");
		subtypes = subtypes.subList(1, subtypes.size());
		Map<String,V> map = new HashMap<String, V>();
		for (String key :jsonObject.keySet()){
			V result = null;
			result = simpleEncoder.test(jsonObject.get(key), subtypes, result);
			map.put(key, result);
		}
		return map;
	}
}
