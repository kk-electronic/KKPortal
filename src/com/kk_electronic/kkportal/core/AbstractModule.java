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
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.event.ContentChangedEvent;
import com.kk_electronic.kkportal.core.moduleview.Module;
import com.kk_electronic.kkportal.core.services.ModuleService.ModuleInfo;

public abstract class AbstractModule implements Module {

	private ModuleInfo info;
	@Inject private EventBus eventBus;  
	
	@Override
	public void setModuleInfo(ModuleInfo info) {
		this.info = info;
	}

	@Override
	public String getTitle() {
		return null;
	}
	
	protected void contentChanged() {
		eventBus.fireEventFromSource(new ContentChangedEvent(info.getId(),info.getHeight()), this);
	}
	
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}
}
