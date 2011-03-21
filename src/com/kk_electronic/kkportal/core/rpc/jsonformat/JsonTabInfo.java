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

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.kk_electronic.kkportal.core.rpc.FrameEncoder;
import com.kk_electronic.kkportal.core.rpc.JsonEncoder;
import com.kk_electronic.kkportal.core.tabs.TabInfo;

public class JsonTabInfo implements JsonValue<TabInfo> {

	@Override
	public TabInfo fromJson(JSONValue jsonValue, List<Class<?>> subtypes,
			JsonEncoder simpleEncoder) throws UnableToDeserialize {
		JSONObject jsonObject = jsonValue.isObject();
		if(jsonObject == null) throw new UnableToDeserialize("Expected json Object");

		Integer id = null;
		String name = null;
		List<List<Integer>> moduleIds = null;

		id = simpleEncoder.validate(jsonObject.get("tab_id"), id,new Class<?>[]{Integer.class});
		name = simpleEncoder.validate(jsonObject.get("name"), name,new Class<?>[]{String.class});
		moduleIds = simpleEncoder.validate(jsonObject.get("module_ids"), moduleIds, new Class<?>[]{List.class,List.class,Integer.class});
		
		return new TabInfo(id, name, moduleIds);
	}

	@Override
	public void toJson(StringBuilder response, TabInfo object,
			FrameEncoder<JSONValue> encoder) throws UnableToSerialize {
		response.append("{");
		
		encoder.encode("moduleIds", response);
		response.append(":");
		encoder.encode(object.getModuleIds(), response);
		response.append(",");
		
		encoder.encode("name", response);
		response.append(":");
		encoder.encode(object.getName(), response);
		response.append(",");		

		encoder.encode("tab_id", response);
		response.append(":");
		encoder.encode(object.getId(), response);
		response.append("}");
	}
}
