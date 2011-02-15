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
import com.kk_electronic.kkportal.timereg.model.TimeEntry;

/**
 * @author Jes Andersen
 *
 */
public class JsonTimeEntry implements JsonValue<TimeEntry> {
	@Override
	public TimeEntry fromJson(JSONValue jsonValue, List<Class<?>> subtypes,
			JsonEncoder jsonEncoder) throws UnableToDeserialize {
		if (jsonValue.isNull() != null)
			return null;
		if (jsonValue.isObject() == null)
			throw new UnableToDeserialize("Expected Json Object");
		JSONObject o = jsonValue.isObject();
		
		Long checkin = null;
		Long checkout = null;
		Integer taskid = null;
		Integer id = null;
		checkin = jsonEncoder.validate(o.get("checkin"), checkin, new Class<?>[]{Long.class});
		checkout = jsonEncoder.validate(o.get("checkout"), checkout, new Class<?>[]{Long.class});
		taskid = jsonEncoder.validate(o.get("taskId"), taskid, new Class<?>[]{Integer.class});
		id = jsonEncoder.validate(o.get("id"), id, new Class<?>[]{Integer.class});
		TimeEntry t = new TimeEntry(checkin, checkout, taskid);
		t.setId(id);
		return t;
	}

	@Override
	public void toJson(StringBuilder response, TimeEntry object,
			FrameEncoder<JSONValue> encoder) throws UnableToSerialize {
		response.append("{");
		
		encoder.encode("checkin", response);
		response.append(":");
		encoder.encode(object.getCheckin(), response);
		response.append(",");
		
		encoder.encode("checkout", response);
		response.append(":");
		encoder.encode(object.getCheckout(), response);
		response.append(",");
		
		encoder.encode("id", response);
		response.append(":");
		encoder.encode(object.getId(), response);
		response.append(",");
		
		encoder.encode("taskid", response);
		response.append(":");
		encoder.encode(object.getTaskId(), response);		
		response.append("}");
	}
}
