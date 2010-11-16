package com.kk_electronic.kkportal.core.rpc.jsonformat;

import java.util.List;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.kk_electronic.kkportal.core.rpc.FrameEncoder;
import com.kk_electronic.kkportal.core.rpc.SimpleEncoder;
import com.kk_electronic.kkportal.core.tabs.TabInfo;

public class JsonTabInfo implements JsonValue<TabInfo> {

	public static class TabInfoDTO implements TabInfo{
		public final int id;
		public final String name;
		private final List<List<Integer>> moduleIds;
		public List<List<Integer>> getModuleIds() {
			return moduleIds;
		}
		public TabInfoDTO(int id, String name, List<List<Integer>> moduleIds) {
			super();
			this.id = id;
			this.name = name;
			this.moduleIds = moduleIds;
		}
		public int getId() {
			return id;
		}
		public String getName() {
			return name;
		}
	}
	
	@Override
	public TabInfo fromJson(JSONValue jsonValue, List<Class<?>> subtypes,
			SimpleEncoder simpleEncoder) throws UnableToDeserialize {
		JSONObject jsonObject = jsonValue.isObject();
		if(jsonObject == null) throw new UnableToDeserialize("Expected json Object");
		Integer id = null;
		String name = null;
		String moduleidstring = null;
		id = simpleEncoder.validate(jsonObject.get("tab_id"), id,new Class<?>[]{Integer.class});
		name = simpleEncoder.validate(jsonObject.get("name"), name,new Class<?>[]{String.class});
		moduleidstring = simpleEncoder.validate(jsonObject.get("module_ids"), moduleidstring,new Class<?>[]{String.class});
		JSONValue mi = JSONParser.parseStrict(moduleidstring);
		List<List<Integer>> moduleIds = null;
		moduleIds = simpleEncoder.validate(mi, moduleIds, new Class<?>[]{List.class,List.class,Integer.class});
		return new TabInfoDTO(id, name, moduleIds);
	}

	@Override
	public void toJson(StringBuilder response, TabInfo object,
			FrameEncoder<JSONValue> encoder) throws UnableToSerialize {
		// TODO Auto-generated method stub
		
	}
}
