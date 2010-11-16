package com.kk_electronic.kkportal.core.rpc.jsonformat;

import java.util.List;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.kk_electronic.kkportal.core.rpc.FrameEncoder;
import com.kk_electronic.kkportal.core.rpc.SimpleEncoder;

public class JsonString implements JsonValue<String> {

	public JsonString() {
	}
	@Override
	public void toJson(StringBuilder response, String object,
			FrameEncoder<JSONValue> encoder) throws UnableToSerialize {
		response.append(JsonUtils.escapeValue(object));
	}
	@Override
	public String fromJson(JSONValue jsonValue, List<Class<?>> subtypes,
			SimpleEncoder simpleEncoder) throws UnableToDeserialize {
		if(jsonValue.isNull() != null) return null;
		JSONString jsonString = jsonValue.isString();
		if(jsonString == null) throw new UnableToDeserialize("Expected json String");
		return jsonString.stringValue();
	}
}
