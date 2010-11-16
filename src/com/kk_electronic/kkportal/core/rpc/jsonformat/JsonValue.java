package com.kk_electronic.kkportal.core.rpc.jsonformat;

import java.util.List;

import com.google.gwt.json.client.JSONValue;
import com.kk_electronic.kkportal.core.rpc.FrameEncoder;
import com.kk_electronic.kkportal.core.rpc.SimpleEncoder;

public interface JsonValue<T> {
	public T fromJson(JSONValue jsonValue,List<Class<?>> subtypes, SimpleEncoder simpleEncoder) throws UnableToDeserialize;
	public void toJson(StringBuilder response,T object,FrameEncoder<JSONValue> encoder) throws UnableToSerialize;
}
