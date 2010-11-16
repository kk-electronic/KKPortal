package com.kk_electronic.kkportal.core.rpc.jsonformat;

import java.util.List;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONValue;
import com.kk_electronic.kkportal.core.rpc.FrameEncoder;
import com.kk_electronic.kkportal.core.rpc.SimpleEncoder;

public class JsonInteger implements JsonValue<Integer> {

	public JsonInteger() {
	}

	@Override
	public void toJson(StringBuilder response, Integer object,
			FrameEncoder<JSONValue> encoder) throws UnableToSerialize {
		response.append(object.toString());
	}

	@Override
	public Integer fromJson(JSONValue jsonValue, List<Class<?>> subtypes,
			SimpleEncoder simpleEncoder) throws UnableToDeserialize {
		JSONNumber number = jsonValue.isNumber();
		if(number == null) throw new UnableToDeserialize("Expected json number");
		return (int) number.doubleValue();
	}
}
