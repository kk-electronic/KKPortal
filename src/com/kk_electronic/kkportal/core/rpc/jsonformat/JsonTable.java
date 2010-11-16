package com.kk_electronic.kkportal.core.rpc.jsonformat;

import java.util.List;

import com.google.gwt.json.client.JSONValue;
import com.kk_electronic.kkportal.core.rpc.FrameEncoder;
import com.kk_electronic.kkportal.core.rpc.SimpleEncoder;
import com.kk_electronic.kkportal.core.rpc.Table;

public class JsonTable<T> implements JsonValue<Table<T>> {

	@Override
	public Table<T> fromJson(JSONValue jsonValue, List<Class<?>> subtypes,
			SimpleEncoder simpleEncoder) throws UnableToDeserialize {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void toJson(StringBuilder response, Table<T> object,
			FrameEncoder<JSONValue> encoder) throws UnableToSerialize {
		// TODO Auto-generated method stub
		
	}

}
