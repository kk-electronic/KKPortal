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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.view.client.ProvidesKey;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.kk_electronic.kkportal.core.inject.FlexInjector;
import com.kk_electronic.kkportal.core.reflection.Injection;
import com.kk_electronic.kkportal.core.rpc.Comet;
import com.kk_electronic.kkportal.core.rpc.FrameEncoder;
import com.kk_electronic.kkportal.core.rpc.IdCreator;
import com.kk_electronic.kkportal.core.rpc.IdCreatorSequence;
import com.kk_electronic.kkportal.core.rpc.SimpleEncoder;
import com.kk_electronic.kkportal.core.rpc.WebSocket;
import com.kk_electronic.kkportal.core.security.Hasher;
import com.kk_electronic.kkportal.core.security.SHA256;
import com.kk_electronic.kkportal.core.security.User;
import com.kk_electronic.kkportal.core.security.UserProvider;
import com.kk_electronic.kkportal.core.services.ModuleInfoKeyProvider;
import com.kk_electronic.kkportal.core.services.ModuleService.ModuleInfo;
import com.kk_electronic.kkportal.core.ui.ApplicationLayout;
import com.kk_electronic.kkportal.core.ui.WebPageLayout;

/**
 * This class defines which implementations is used for different interfaces
 * Look at the documentation for google-gin and/or google-guice for more
 * information.
 * 
 * @author Jes Andersen
 */
public class WebPageInjectConfig extends AbstractGinModule {

	@Override
	protected void configure() {
		bind(ApplicationLayout.class).to(WebPageLayout.class);
		/* 
		 * When binding a class with generics this construct has to be used to
		 * keep the information about the parameterised types.
		 */
		bind(new TypeLiteral<IdCreator<Integer>>() {}).to(IdCreatorSequence.class);
		bind(new TypeLiteral<ProvidesKey<ModuleInfo>>() {}).to(ModuleInfoKeyProvider.class);
		bind(WebSocket.class).to(Comet.class).in(Singleton.class);
		bind(ActivityManager.class).in(Singleton.class);
		bind(User.class).toProvider(UserProvider.class);
		bind(Hasher.class).to(SHA256.class);
		bind(FrameEncoder.class).to(SimpleEncoder.class);
		bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
		bindConstant().annotatedWith(Names.named("DefaultHistoryToken")).to("ViewModules");
	}
	
	@Provides @Singleton
	FlexInjector provideFlexInjector(){
		return Injection.getInjector();
	}
}
