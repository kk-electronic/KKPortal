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
package com.kk_electronic.kkportal.core.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.persistence.Storage;
import com.kk_electronic.kkportal.core.security.Identity;
import com.kk_electronic.kkportal.core.security.IdentityProvider;
import com.kk_electronic.kkportal.core.security.NewPrimaryIdentityEvent;

public class LogoutLink extends Composite implements NewPrimaryIdentityEvent.Handler, ClickHandler {
	Anchor anchor = new Anchor("Logout");
	private final IdentityProvider identityProvider;
	private final Storage storage;
	
	@Inject
	public LogoutLink(IdentityProvider identityProvider,Storage storage) {
		this.identityProvider = identityProvider;
		this.storage = storage;
		identityProvider.addNewPrimaryIdentityEventHandler(this);
		initWidget(anchor);
		setIdentity(identityProvider.getPrimaryIdentity());
		anchor.addClickHandler(this);
	}

	@Override
	public void onNewPrimaryIdentity(NewPrimaryIdentityEvent event) {
		setIdentity(event.getIdentity());
	}

	private void setIdentity(Identity identity) {
		SafeHtmlBuilder html = new SafeHtmlBuilder();
		html.appendHtmlConstant("Logout");
		if(identity != null){
			html.appendHtmlConstant(" ");
			html.appendEscaped(identity.toString());
		}
		anchor.setHTML(html.toSafeHtml());
	}

	@Override
	public void onClick(ClickEvent event) {
		identityProvider.invalidate(identityProvider.getPrimaryIdentity());
		storage.clear();
		Location.reload();
	}
}
