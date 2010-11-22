package com.kk_electronic.kkportal.core.rpc.jsonformat;

import java.util.List;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONValue;
import com.kk_electronic.kkportal.core.rpc.FrameEncoder;
import com.kk_electronic.kkportal.core.rpc.SimpleEncoder;

public class JsonDouble implements JsonValue<Double> {

	public JsonDouble() {
	}

	@Override
	public void toJson(StringBuilder response, Double object,
			FrameEncoder<JSONValue> encoder) throws UnableToSerialize {
		response.append(object.toString());
	}

	@Override
	public Double fromJson(JSONValue jsonValue, List<Class<?>> subtypes,
			SimpleEncoder simpleEncoder) throws UnableToDeserialize {
		JSONNumber number = jsonValue.isNumber();
		if(number == null) throw new UnableToDeserialize("Expected json number");
		return number.doubleValue();
	}
}
