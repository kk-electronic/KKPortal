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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kk_electronic.kkportal.core.activity.ActivityManager;
import com.kk_electronic.kkportal.core.reflection.Injection;
import com.kk_electronic.kkportal.core.services.Debug;

/**
 * This is the main entry point for the KKPortal project.
 * It's only responsibility is to start the injection, which is done by
 * requesting the Injection framework to create the base class.  
 * @author Jes Andersen
 */

public class KKPortal implements EntryPoint {
	
	@Override
	public void onModuleLoad() {
		startActivityManager();
		if(!GWT.isScript()){
			reloadServer();
		}
	}

	private void reloadServer() {
		Injection.create(Debug.class, new AsyncCallback<Debug>() {
			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(Debug result) {
				result.reload(null);
			}
		});
	}

	private void startActivityManager() {
		Injection.create(ActivityManager.class, new AsyncCallback<ActivityManager>() {

			@Override
			public void onFailure(Throwable caught) {
				/*
				 * This is a really fatal error which means that an error occurred during
				 * the initial creation of the portal.  
				 */
				GWT.log("Load failed",caught);
			}

			@Override
			public void onSuccess(ActivityManager result) {
				/*
				 * We inform that the base class and all dependencies of it has been loaded
				 */
				GWT.log("Application Start");
			}
		});
	}
}
