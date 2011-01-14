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
package com.kk_electronic.kkportal.core.reflection;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.event.ServerEvent;
import com.kk_electronic.kkportal.core.rpc.FrameEncoder;
import com.kk_electronic.kkportal.core.rpc.jsonformat.UnableToDeserialize;
import com.kk_electronic.kkportal.debug.model.NewCpuUsageDataEvent;
import com.kk_electronic.kkportal.examples.modules.NewWallMessageEvent;

/* TODO: Destroy this class */
public class EventFromJsonCreator {
	
	private final FrameEncoder<JSONValue> encoder;
	private final EventBus eventBus;

	@Inject
	public EventFromJsonCreator(FrameEncoder<JSONValue> encoder,EventBus eventBus) {
		this.encoder = encoder;
		this.eventBus = eventBus;
	}
	
	public <T> void create(Class<? extends ServerEvent> clazz, List<?> list, AsyncCallback<ServerEvent> asyncCallback){
		if(NewWallMessageEvent.class.equals(clazz)){
			String message = null;
			JSONValue value = (JSONValue)list.get(0);
			try {
				message = encoder.validate(value.isArray().get(0), message, new Class<?>[]{String.class});
			} catch (UnableToDeserialize e) {
				asyncCallback.onFailure(e);
				return;
			}
			NewWallMessageEvent event = new NewWallMessageEvent(message);
			eventBus.fireEvent(event);
			asyncCallback.onSuccess(new NewWallMessageEvent(message));
			return;
		}
		if(NewCpuUsageDataEvent.class.equals(clazz)){
			String message = null;
			Double load = null;
			JSONValue value = (JSONValue)list.get(0);
			try {
				message = encoder.validate(value.isArray().get(0), message, new Class<?>[]{String.class});
				load = encoder.validate(value.isArray().get(1), load, new Class<?>[]{Double.class});
			} catch (UnableToDeserialize e) {
				asyncCallback.onFailure(e);
				return;
			}
			NewCpuUsageDataEvent event = new NewCpuUsageDataEvent(message,load);
			eventBus.fireEvent(event);
			asyncCallback.onSuccess(event);
			return;
		}
		asyncCallback.onFailure(new Exception("Class not found"));
	}
}
