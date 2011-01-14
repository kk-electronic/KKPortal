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
import com.kk_electronic.kkportal.core.rpc.SimpleEncoder;
import com.kk_electronic.kkportal.core.services.ModuleService.ModuleInfo;

public class JsonModuleInfo implements JsonValue<ModuleInfo> {

	public static class ModuleInfoDTO implements ModuleInfo{
		public final int id;
		public int type;
		public int height;
		public void setType(int type) {
			this.type = type;
		}
		public void setHeight(int height) {
			this.height = height;
		}
		public ModuleInfoDTO(int id, int type, int height) {
			super();
			this.id = id;
			this.type = type;
			this.height = height;
		}
		public int getId() {
			return id;
		}
		public int getType() {
			return type;
		}
		public int getHeight() {
			return height;
		}
	}
	
	@Override
	public ModuleInfo fromJson(JSONValue jsonValue, List<Class<?>> subtypes,
			SimpleEncoder simpleEncoder) throws UnableToDeserialize {
		JSONObject jsonObject = jsonValue.isObject();
		if(jsonObject == null) throw new UnableToDeserialize("Expected json Object");
		Integer id = null;
		id = simpleEncoder.validate(jsonObject.get("module_id"), id,new Class<?>[]{Integer.class});
		Integer type = null;
		type = simpleEncoder.validate(jsonObject.get("type_id"), type,new Class<?>[]{Integer.class});
		Integer height = null;
		height = simpleEncoder.validate(jsonObject.get("height"), height,new Class<?>[]{Integer.class});
		return new ModuleInfoDTO(id, type,height);
	}

	@Override
	public void toJson(StringBuilder response, ModuleInfo object,
			FrameEncoder<JSONValue> encoder) throws UnableToSerialize {
		// TODO Auto-generated method stub
		
	}

}
