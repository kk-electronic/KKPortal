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

package com.kk_electronic.kkportal.core;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.event.ServerConnectEvent;
import com.kk_electronic.kkportal.core.event.ServerDisconnectEvent;
import com.kk_electronic.kkportal.core.event.TabSelectedEvent;
import com.kk_electronic.kkportal.core.event.TabSelectedEvent.Handler;
import com.kk_electronic.kkportal.core.rpc.RpcDispatcher;
import com.kk_electronic.kkportal.core.rpc.WebSocket;
import com.kk_electronic.kkportal.core.security.Digest;
import com.kk_electronic.kkportal.core.security.IdentityProvider;
import com.kk_electronic.kkportal.core.security.LoginEvent;
import com.kk_electronic.kkportal.core.security.NewPrimaryIdentityEvent;
import com.kk_electronic.kkportal.core.tabs.TabsModel;
import com.kk_electronic.kkportal.core.ui.GlassPanel;

/**
 * This class i statemachine that displays how far in the login process the user is
 * Since some of the login commands is computationally heavy, the indicator is there
 * to provide a smooth user experience.
 * 
 * This class assumes that parts of the system send proper events out when significant
 * changes happen during the login phase.
 * @author albatros
 *
 */
public class LoginIndicator implements ServerConnectEvent.Handler,
		NewPrimaryIdentityEvent.Handler, LoginEvent.Handler, Handler, com.kk_electronic.kkportal.core.event.ServerDisconnectEvent.Handler {
	HTML x = new HTML();

	public static enum State {
		NOT_CONNECTED, CONNECTED, IDENTITY, PRELOGIN, LOGGED_IN, HASTAB
	}
	
	public static String[] messages = new String[]{
		"Connecting to Portalserver",
		"Connected to Portalserver",
		"Connected to Portalserver",
		"Estabilishing secure connection",
		"Fetching initial data",
		"System is ready",
		};

	State state = State.NOT_CONNECTED;
	private boolean showing;
	
	private final IdentityProvider identityProvider;
	private final Digest digest;
	private final GlassPanel glassPanel;

	@Inject
	public LoginIndicator(RpcDispatcher dispatcher, IdentityProvider identityProvider,
			Digest digest, TabsModel tabsModel,GlassPanel glassPanel) {
		this.identityProvider = identityProvider;
		this.digest = digest;
		this.glassPanel = glassPanel;
		glassPanel.addWidget(x);
		x.getElement().getStyle().setFontSize(150, Unit.PCT);
		tabsModel.addTabSelectedHandler(this);
		WebSocket socket = dispatcher.getSocket();
		socket.addServerConnectHandler(this);
		socket.addServerDisconnectHandler(this);
		digest.addLoginEventHandler(this);
		identityProvider.addNewPrimaryIdentityEventHandler(this);
		if(socket.isConnected()){
			trasition(State.NOT_CONNECTED, State.CONNECTED);
		}
	}

	private void trasition(State oldstate, State newstate) {
		if (oldstate != null && state != oldstate) {
			addLog(oldstate.toString() + " !=> " + newstate.toString());
			return;
		}
		show();
		addLog(state + " => " + newstate.toString());
		state = newstate;
		updateWidgetText();
		switch (state) {
		case CONNECTED:
			if (identityProvider.getPrimaryIdentity() != null) {
				trasition(State.CONNECTED, State.IDENTITY);
			}
			break;
		case IDENTITY:
			if (digest.hasSecret()) {
				trasition(State.IDENTITY, State.LOGGED_IN);
			}
			break;
		case LOGGED_IN:
			if (identityProvider.getPrimaryIdentity() != null) {
				trasition(State.LOGGED_IN, State.HASTAB);
			}
			break;
		case HASTAB:
			hide();
			break;
		}
	}
	
	private void show() {
		if(showing) return;
		showing = true;
		glassPanel.addWidget(x);
	}

	private void hide() {
		if(!showing) return;
		showing = false;
		glassPanel.remove(x);
	}

	private void updateWidgetText() {
		x.setHTML(messages[state.ordinal()]);
	}

	private void addLog(String string) {
		GWT.log(string);
	}

	@Override
	public void onServerConnect(ServerConnectEvent event) {
		trasition(State.NOT_CONNECTED, State.CONNECTED);
	}

	@Override
	public void onNewPrimaryIdentity(NewPrimaryIdentityEvent event) {
		if(event.getIdentity() != null){
			trasition(State.CONNECTED, State.IDENTITY);
		} else {
			trasition(null, State.CONNECTED);
		}
	}

	@Override
	public void onNewLoginEvent(LoginEvent event) {
		if(event.getIdentity() != null){
			trasition(null, State.LOGGED_IN);
		} else {
			trasition(State.IDENTITY, State.PRELOGIN);
		}
	}

	@Override
	public void onTabSelected(TabSelectedEvent event) {
		if(event.getTabInfo() != null){
			trasition(State.LOGGED_IN, State.HASTAB);
		}
	}

	/* (non-Javadoc)
	 * @see com.kk_electronic.kkportal.core.event.ServerDisconnectEvent.Handler#onServerDisconnect(com.kk_electronic.kkportal.core.event.ServerDisconnectEvent)
	 */
	@Override
	public void onServerDisconnect(ServerDisconnectEvent event) {
		trasition(null, State.NOT_CONNECTED);
	}
}
