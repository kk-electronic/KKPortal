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
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.event.ContentChangedEvent;
import com.kk_electronic.kkportal.core.moduleview.Module;
import com.kk_electronic.kkportal.core.services.ModuleService.ModuleInfo;
import com.kk_electronic.kkportal.core.util.Callback;
import com.kk_electronic.kkportal.core.util.DualCallback;

public abstract class AbstractModule implements Module {

	private ModuleInfo info;
	@Inject protected EventBus eventBus;
	
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
	
	protected void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	
	/**
	 * Function used to avoid doing error handling in AsyncCallbacks,<br>
	 * This is instead done centrally.
	 * 
	 * @param <T> the return type of data.
	 * @param callback A simpler version of Callback with only onSucces
	 * @return a AsyncCallback with central error handling.
	 */
	public <T> AsyncCallback<T> call(final Callback<T> callback){
		return call(callback, "");
	}
	
	/**
	 * Function used to avoid doing error handling in AsyncCallbacks,<br>
	 * This is instead done centrally.
	 * 
	 * @param <T> the return type of data.
	 * @param callback A simpler version of Callback with only onSucces
	 * @param failmessage message to be printed to the log along with the class name of the source.
	 * @return a AsyncCallback with central error handling.
	 */
	public <T> AsyncCallback<T> call(final Callback<T> callback, final String failmessage){
		final Class<? extends AbstractModule> clazz = this.getClass();
		return new DualCallback<T>() {
			@Override
			public void onFailure(Throwable caught) {
				if(!GWT.isScript()){
					String s = clazz.getName();
					s = s.substring(s.lastIndexOf('.')+1).toUpperCase();
					if(failmessage != null && !failmessage.isEmpty()){
						s = s + "-" + failmessage;
					}
					GWT.log(s,caught);
				}
			}

			@Override
			public void onSuccess(T result) {
				callback.onSuccess(result);
			}
		};
	}
}
