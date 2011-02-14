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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.ModuleWindowFactory;
import com.kk_electronic.kkportal.core.activity.Activity;
import com.kk_electronic.kkportal.core.activity.LocationInfo;
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
	private ModuleWindowFactory factory;
	private ModuleWindow window;

	@Inject
	public SingleRender(final LocationInfo location,
			ModuleService moduleService, Display display2,
			ModuleTypeInfoProvider typeInfoProvider, ModuleWindowFactory moduleWindowFactory) {
		this.location = location;
		this.display = display2;
		this.typeInfoProvider = typeInfoProvider;
		this.factory = moduleWindowFactory;

		moduleService.getModuleInfo(Arrays.asList(new Integer[]{location.getSubint()}),
				new AsyncCallback<List<ModuleInfo>>() {
					@Override
					public void onFailure(Throwable caught) {
						display.showError("Failed to load module "
								+ location.getSubint() + ":" + caught);
					}

					@Override
					public void onSuccess(List<ModuleInfo> result) {
						if (result != null && !result.isEmpty()) {
							loadModule(result.get(0));
						} else {
							onFailure(new RuntimeException("Empty Result"));
						}
					}
				});
	}

	protected void loadModule(ModuleInfo moduleInfo) {
		window = factory.get(moduleInfo);
		display.showWidget(window.asWidget());

		typeInfoProvider.get(moduleInfo.getType(),
				new AsyncCallback<ModuleTypeInfo>() {

					@Override
					public void onFailure(Throwable caught) {
						display.showError("Failed to load module type "
								+ location.getSubint() + ":" + caught);
					}

					@Override
					public void onSuccess(ModuleTypeInfo result) {
						Window.setTitle(window.title.getText());
					}
				});
	}

	@Override
	public Widget asWidget() {
		return display.asWidget();
	}
}
