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

import java.util.List;

import com.google.gwt.json.client.JSONValue;
import com.kk_electronic.kkportal.core.rpc.FrameEncoder;
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
			FrameEncoder<JSONValue> encoder) throws UnableToDeserialize {
		// FIXME Auto-generated method stub
		return null;
	}
}
