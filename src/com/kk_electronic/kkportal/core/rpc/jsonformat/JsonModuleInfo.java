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
