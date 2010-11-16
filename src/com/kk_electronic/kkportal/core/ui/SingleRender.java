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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.activity.Activity;
import com.kk_electronic.kkportal.core.activity.LocationInfo;
import com.kk_electronic.kkportal.core.inject.FlexInjector;
import com.kk_electronic.kkportal.core.moduleview.Module;
import com.kk_electronic.kkportal.core.reflection.ModuleMap;
import com.kk_electronic.kkportal.core.services.ModuleService;
import com.kk_electronic.kkportal.core.services.ModuleTypeInfo;
import com.kk_electronic.kkportal.core.services.ModuleService.ModuleInfo;
import com.kk_electronic.kkportal.core.tabs.ModuleTypeInfoProvider;

public class SingleRender implements Activity {

	public static class Display {
		LayoutPanel container = new LayoutPanel();

		public Widget asWidget() {
			return container;
		}

		public void showError(String errorMessage) {
			showWidget(new Label(errorMessage));
		}

		public void showWidget(Widget widget) {
			if(container.getWidgetCount() > 0 && container.getWidget(0) == widget){
				return;
			}
			container.clear();
			container.add(widget);
		}
	}

	private final Display display;
	private final ModuleTypeInfoProvider typeInfoProvider;
	private final LocationInfo location;
	private final ModuleMap moduleMap;
	private final FlexInjector injector;
	private ModuleInfo moduleInfo;
	private String title;

	@Inject
	public SingleRender(final LocationInfo location,
			ModuleService moduleService, Display display2,
			ModuleTypeInfoProvider typeInfoProvider, ModuleMap moduleMap,
			FlexInjector injector) {
		this.location = location;
		this.display = display2;
		this.typeInfoProvider = typeInfoProvider;
		this.moduleMap = moduleMap;
		this.injector = injector;

		moduleService.getModule(location.getSubint(),
				new AsyncCallback<ModuleInfo>() {
					@Override
					public void onFailure(Throwable caught) {
						display.showError("Failed to load module "
								+ location.getSubint() + ":" + caught);
					}

					@Override
					public void onSuccess(ModuleInfo result) {
						loadModule(result);
					}
				});
	}

	protected void loadModule(ModuleInfo moduleInfo) {

		this.moduleInfo = moduleInfo;
		typeInfoProvider.get(moduleInfo.getType(),
				new AsyncCallback<ModuleTypeInfo>() {

					@Override
					public void onFailure(Throwable caught) {
						display.showError("Failed to load module "
								+ location.getSubint() + ":" + caught);
					}

					@Override
					public void onSuccess(ModuleTypeInfo result) {
						title = result.getTitle();
						Window.setTitle(title);
						loadCode(result);
					}
				});
	}

	protected void loadCode(ModuleTypeInfo moduleTypeInfo) {
		Class<? extends Module> moduleClass = moduleMap.getClassFromKey(moduleTypeInfo
				.getCode());
		if (moduleClass == null) {
			display.showError("Unknown module type code" + moduleTypeInfo.getCode());
		} else {
			injector.create(moduleClass, new AsyncCallback<Module>() {

				@Override
				public void onFailure(Throwable caught) {
					display.showError("Could not load code:" + caught);
				}

				@Override
				public void onSuccess(Module result) {
					ModuleWindow window = new ModuleWindow(moduleInfo);
					window.setTitle(title);
					window.setContent(result.asWidget());
					display.showWidget(window);
				}
			});
		}
	}

	@Override
	public Widget asWidget() {
		return display.asWidget();
	}
}
