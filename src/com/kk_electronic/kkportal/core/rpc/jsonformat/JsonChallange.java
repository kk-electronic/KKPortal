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

import java.math.BigInteger;
import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.kk_electronic.kkportal.core.rpc.FrameEncoder;
import com.kk_electronic.kkportal.core.security.Challange;

public class JsonChallange implements JsonValue<Challange> {

	@Override
	public Challange fromJson(JSONValue jsonValue, List<Class<?>> subtypes,
			FrameEncoder<JSONValue> encoder) throws UnableToDeserialize {
		if(jsonValue == null || jsonValue.isArray() == null){
			throw new UnableToDeserialize("Challange must be array");
		}
		JSONArray array = jsonValue.isArray();
		if (array.size() < 4 ) {
			throw new UnableToDeserialize("Challange must have at least 4 elements");
		}
		Challange c = new Challange();
		String s = null;
		s = encoder.validate(array.get(0), s , new Class<?>[]{String.class});
		if(s != null) c.N = new BigInteger(s,16);
		s = encoder.validate(array.get(1), s , new Class<?>[]{String.class});
		if(s != null) c.g = new BigInteger(s,16);
		c.method = encoder.validate(array.get(2), s , new Class<?>[]{String.class});
		s = encoder.validate(array.get(3), s , new Class<?>[]{String.class});
		if(s != null) c.s = new BigInteger(s,16);
		s = encoder.validate(array.get(4), s , new Class<?>[]{String.class});
		if(s != null) c.B = new BigInteger(s,16);
		c.sessionId = encoder.validate(array.get(5), s , new Class<?>[]{String.class});
		return c;
	}

	@Override
	public void toJson(StringBuilder response, Challange object,
			FrameEncoder<JSONValue> encoder) throws UnableToSerialize {
		throw new UnableToSerialize("Not supported");
	}
}
