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

import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.dnd.DND;
import com.kk_electronic.kkportal.core.dnd.DND.DropSink;
import com.kk_electronic.kkportal.core.inject.FlexInjector;
import com.kk_electronic.kkportal.core.moduleview.Module;
import com.kk_electronic.kkportal.core.reflection.ModuleMap;
import com.kk_electronic.kkportal.core.services.ModuleTypeInfo;
import com.kk_electronic.kkportal.core.services.ModuleService.ModuleInfo;
import com.kk_electronic.kkportal.core.tabs.ModuleTypeInfoProvider;
import com.kk_electronic.kkportal.res.Resources;
import com.kk_electronic.kkportal.res.Resources.ColourPalette;

public class ModuleDisplay implements DropSink<ModuleWindow> {
	LayoutPanel canvas = new LayoutPanel();
	private final ProvidesKey<ModuleInfo> keyprovider;
	private final ModuleTypeInfoProvider typeInfoProvider;
	private final ModuleMap moduleMap;
	private final FlexInjector injector;
	private final DND<ModuleWindow> dnd;
	private final ColourPalette palette;

	@Inject
	public ModuleDisplay(ProvidesKey<ModuleInfo> keyprovider,
			ModuleTypeInfoProvider typeInfoProvider, ModuleMap moduleMap,
			FlexInjector injector, DND<ModuleWindow> dnd, Resources resources) {
		this.keyprovider = keyprovider;
		this.typeInfoProvider = typeInfoProvider;
		this.moduleMap = moduleMap;
		this.injector = injector;
		this.dnd = dnd;
		this.palette = resources.palette();
		canvas.getElement().getStyle().setBackgroundColor(palette.colour2());
		dnd.registerDropSink(this);
	}

	public Widget asWidget() {
		return canvas;
	}

	public int getWidth() {
		return canvas.getOffsetWidth();
	}

	public List<ModuleWindow> selectFromMouse(List<List<ModuleWindow>> widgets,
			MouseUpEvent event) {
		int colsize = canvas.getOffsetWidth() / widgets.size();
		int i = 0;
		int x = event.getRelativeX(canvas.getElement());
		for (List<ModuleWindow> column : widgets) {
			i += colsize;
			if (x < i)
				return column;
		}
		return null;
	}

	public int selectIndexFromMouse(List<ModuleWindow> column,
			MouseUpEvent event) {
		int logicY = 0;
		int index = 0;
		int mouseY = event.getRelativeY(canvas.getElement());
		for (ModuleWindow window : column) {
			int height = window.getHeight();
			if (mouseY < logicY + height / 2)
				return index;
			logicY += height;
			index++;
		}
		return index;
	}

	/**
	 * 
	 * @param modules
	 * @param animate
	 */
	public void setModuleList(List<List<ModuleInfo>> modules, boolean animate) {
		HashMap<Object, ModuleWindow> displayed = getModuleWindowMap();
		// TODO: Add weights to column sizes
		double left = 0;
		for (int i=0,l=modules.size();i<l;i++) {
			List<ModuleInfo> moduleInfos = modules.get(i);
			double top = 0;
			double width = 100.0 / modules.size();
			for (ModuleInfo moduleInfo : moduleInfos) {
				ModuleWindow widget = displayed.remove(keyprovider
						.getKey(moduleInfo));
				if (widget == null) {
					widget = new ModuleWindow(moduleInfo);
					final ModuleWindow drag = widget;
					widget.addMouseDownHandler(new MouseDownHandler() {

						@Override
						public void onMouseDown(MouseDownEvent event) {
							/*
							 * Drag before removing so the draghandler can copy
							 * the height and width of the object
							 */
							dnd.startDrag(event, drag);
							if (viewModules != null) {
								viewModules.remove(drag.getModule());
							}
						}
					});
					widget.addDeleteHandler(new MouseDownHandler() {

						@Override
						public void onMouseDown(MouseDownEvent event) {
							viewModules.remove(drag.getModule());
							viewModules.save();
						}
					});
					loadModuleContents(widget);
					canvas.add(widget);
				}
				widget.setFirstColumn(i == 0);
				widget.setLastColumn((i + 1) == l);
				//TODO: Calculate from style
				int height = moduleInfo.getHeight() + 30;
				canvas
						.setWidgetTopHeight(widget, top, Unit.PX, height,
								Unit.PX);
				canvas.setWidgetLeftWidth(widget, left, Unit.PCT, width,
						Unit.PCT);
				top += height;
			}
			left += width;
		}
		/*
		 * Anything still in the hashmap must be widgets not should be deleted
		 * since they are not in the module list
		 */
		for (ModuleWindow window : displayed.values()) {
			canvas.remove(window);
		}
		if (animate) {
			canvas.animate(200);
		} else {
			canvas.forceLayout();
		}
	}

	private HashMap<Object, ModuleWindow> getModuleWindowMap() {
		/*
		 * To avoid unnecessary reload we ...
		 */
		HashMap<Object, ModuleWindow> displayed = new HashMap<Object, ModuleWindow>();
		for (int i = 0, l = canvas.getWidgetCount(); i < l; i++) {
			Widget widget = canvas.getWidget(i);
			if (widget instanceof ModuleWindow) {
				ModuleWindow window = (ModuleWindow) widget;
				displayed.put(keyprovider.getKey(window.getModule()), window);
			}
		}
		return displayed;
	}

	private void loadModuleContents(final ModuleWindow widget) {
		typeInfoProvider.get(widget.getModule().getType(),
				new AsyncCallback<ModuleTypeInfo>() {

					@Override
					public void onFailure(Throwable caught) {
						onLoadError(widget, caught);
					}

					@Override
					public void onSuccess(ModuleTypeInfo result) {
						widget.setTitle(result.getTitle());
						injector.create(moduleMap.getClassFromKey(result
								.getCode()), new AsyncCallback<Module>() {

							@Override
							public void onFailure(Throwable caught) {
								onLoadError(widget, caught);
							}

							@Override
							public void onSuccess(Module result) {
								widget.setContent(result.asWidget());
							}
						});
					}
				});
	}

	protected void onLoadError(ModuleWindow widget, Throwable caught) {
		GWT.log("Failed to load module", caught);
		widget.setContent(new Label("Failed to load:" + caught));
	}

	public ViewModules viewModules;

	public void setViewModules(ViewModules viewModules) {
		this.viewModules = viewModules;
	}

	@Override
	public boolean Drop(MouseUpEvent event, final ModuleWindow t) {
		int height = t.getOffsetHeight();
		event.setRelativeElement(canvas.getElement());
		final int x = event.getX();
		final int y = event.getY();
		/*
		 * Note must be 100.0 and not 100 since the later would result in
		 * integer division which most likely would be 0
		 */
		double width = (100.0 * t.getOffsetWidth()) / canvas.getOffsetWidth();
		int top = t.getAbsoluteTop() - canvas.getAbsoluteTop();
		double left = (100.0 * (t.getAbsoluteLeft() - canvas.getAbsoluteLeft()))
				/ canvas.getOffsetWidth();
		canvas.add(t);
		canvas.setWidgetTopHeight(t, top, Unit.PX, height, Unit.PX);
		canvas.setWidgetLeftWidth(t, left, Unit.PCT, width, Unit.PCT);
		if (viewModules != null) {
			Scheduler.get().scheduleDeferred(new Command() {

				@Override
				public void execute() {
					viewModules.add(t.getModule(), 100.0 * x
							/ canvas.getOffsetWidth(), y);
					viewModules.save();
				}
			});
			return true;
		}
		return false;
	}

	public void setModuleList(List<List<ModuleInfo>> moduleList) {
		setModuleList(moduleList, true);
	}

	public void checkForResizes() {
		if (viewModules == null)
			return;
		for (int i = 0, l = canvas.getWidgetCount(); i < l; i++) {
			Widget widget = canvas.getWidget(i);
			if (widget instanceof ModuleWindow) {
				ModuleWindow window = (ModuleWindow) widget;
				int currentHeight = window.getContent().getElement()
						.getScrollHeight();
				if (currentHeight != window.getHeight()) {
					viewModules.updateHeight(window.getModule(), currentHeight);
				}
			}
		}
	}
}