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
package com.kk_electronic.kkportal.core.rpc;

import com.google.gwt.event.shared.HandlerRegistration;
import com.kk_electronic.kkportal.core.event.FrameReceivedEvent;
import com.kk_electronic.kkportal.core.event.FrameSentEvent;
import com.kk_electronic.kkportal.core.event.ServerConnectEvent;
import com.kk_electronic.kkportal.core.event.ServerDisconnectEvent;

public interface WebSocket {
	void send(String s);
	void connect(String url, String subprotocol);
	void close();
	boolean isConnected();
	boolean isTxBusy();
	HandlerRegistration addFrameSentHandler(FrameSentEvent.Handler handler);
	HandlerRegistration addFrameReceivedHandler(FrameReceivedEvent.Handler handler);
	HandlerRegistration addServerConnectHandler(ServerConnectEvent.Handler handler);
	HandlerRegistration addServerDisconnectHandler(ServerDisconnectEvent.Handler handler);
}
