package com.kk_electronic.kkportal.core;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.inject.FlexInjector;
import com.kk_electronic.kkportal.core.moduleview.Module;
import com.kk_electronic.kkportal.core.reflection.ModuleMap;
import com.kk_electronic.kkportal.core.services.ModuleTypeInfo;
import com.kk_electronic.kkportal.core.services.ModuleService.ModuleInfo;
import com.kk_electronic.kkportal.core.tabs.ModuleTypeInfoProvider;
import com.kk_electronic.kkportal.core.ui.ModuleWindow;

//TODO: Make some stop mechanism
public class ModuleWindowFactory {
	HashMap<ModuleInfo, ModuleWindow> map = new HashMap<ModuleInfo, ModuleWindow>();
	private final ModuleTypeInfoProvider typeInfoProvider;
	private final FlexInjector flexInjector;
	private final ModuleMap moduleMap;

	@Inject
	public ModuleWindowFactory(ModuleTypeInfoProvider typeInfoProvider,
			FlexInjector flexInjector,ModuleMap moduleMap) {
		this.typeInfoProvider = typeInfoProvider;
		this.flexInjector = flexInjector;
		this.moduleMap = moduleMap;
	}

	public ModuleWindow get(final ModuleInfo info) {
		if (map.containsKey(info))
			return map.get(info);
		final ModuleWindow moduleWindow = new ModuleWindow(info);
		map.put(info, moduleWindow);
		typeInfoProvider.get(info.getType(),
				new AsyncCallback<ModuleTypeInfo>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed to run module", caught);
					}

					@Override
					public void onSuccess(ModuleTypeInfo result) {
						flexInjector.create(moduleMap.getClassFromKey(result.getCode()), new AsyncCallback<Module>() {

							@Override
							public void onFailure(Throwable caught) {
								GWT.log("Failed to load code",caught);
							}

							@Override
							public void onSuccess(Module result) {
								result.setModuleInfo(info);
								String title = result.getTitle();
								if(title == null){
									title = getDefaultName(result.getClass());
								}
								moduleWindow.setTitle(title);
								moduleWindow.setContent(result.asWidget());
							}
						});
					}
				});
		return moduleWindow;
	}

	private String getDefaultName(
			Class<? extends Module> class1) {
		String s = class1.getName();
		StringBuilder sb = new StringBuilder();
		int start = s.lastIndexOf('.')+1;
		int end = s.length();
		for(int i=start;i<end;i++){
			if(i!=start && Character.isUpperCase(s.charAt(i))){
				sb.append(' ');
			}
			sb.append(s.charAt(i));
		}
		return sb.toString();
	}
}
