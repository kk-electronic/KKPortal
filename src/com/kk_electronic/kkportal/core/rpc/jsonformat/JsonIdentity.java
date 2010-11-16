package com.kk_electronic.kkportal.core.rpc.jsonformat;

import java.util.List;

import com.google.gwt.json.client.JSONValue;
import com.kk_electronic.kkportal.core.rpc.FrameEncoder;
import com.kk_electronic.kkportal.core.rpc.SimpleEncoder;
import com.kk_electronic.kkportal.core.security.Identity;

public class JsonIdentity implements JsonValue<Identity> {

	@Override
	public void toJson(StringBuilder response, Identity object,
			FrameEncoder<JSONValue> encoder) throws UnableToSerialize {
		encoder.encode(object.getUsername() + "@" + object.getServer(), response);
	}
//	@Override
//	public Identity fromJson(JSONValue jsonValue, List<Class<?>> subTypes)
//			throws UnableToDeserialize {
//		JSONString jsonString = jsonValue.isString();
//		if(jsonString == null) throw new UnableToDeserialize("Identity must be an Json String");
//		String[] t = jsonString.stringValue().split("@",1);
//		if(t.length != 2) throw new UnableToDeserialize("Identity must be on the form username@server");
//		return new Identity(t[0], t[1]);
//	}
//

	@Override
	public Identity fromJson(JSONValue jsonValue, List<Class<?>> subtypes,
			SimpleEncoder simpleEncoder) throws UnableToDeserialize {
		// TODO Auto-generated method stub
		return null;
	}
}
