package com.kk_electronic.kkportal.core.tabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kk_electronic.kkportal.core.services.ModuleService;
import com.kk_electronic.kkportal.core.services.ModuleService.ModuleInfo;

@Singleton
public class ModuleInfoProvider {

	HashMap<Integer, ModuleInfo> map = new HashMap<Integer, ModuleInfo>();
	private final ModuleService moduleService;
	List<AsyncCallback<Map<Integer, ModuleInfo>>> asyncCallbacks;
	
	private AsyncCallback<List<ModuleInfo>> infoCallback = new AsyncCallback<List<ModuleInfo>>(){
		@Override
		public void onFailure(Throwable caught) {
			for(AsyncCallback<Map<Integer, ModuleInfo>> callback:asyncCallbacks){
				callback.onFailure(caught);
			}
			asyncCallbacks = null;
		}

		@Override
		public void onSuccess(List<ModuleInfo> result) {
			for(ModuleInfo moduleInfo:result){
				if(missing.remove(moduleInfo.getId())){
					map.put(moduleInfo.getId(), moduleInfo);
				} else {
					continue;
				}
			}
			if(missing.isEmpty()){
				for(AsyncCallback<Map<Integer, ModuleInfo>> callback:asyncCallbacks){
					callback.onSuccess(map);
				}
				asyncCallbacks = null;
			}
		}
	};
	
	@Inject
	public ModuleInfoProvider(ModuleService moduleService) {
		this.moduleService = moduleService;
	}
	
	public void translate(HashSet<Integer> mustHave,
			AsyncCallback<Map<Integer, ModuleInfo>> asyncCallback) {
		for(int x:mustHave){
			if(!map.containsKey(x)){
				addMissing(x);
			}
		}
		if(missing != null && !missing.isEmpty()){
			addCallback(asyncCallback);
			//We defer so multiple call in the same cycle gets bundled
			DeferredCommand.addCommand(new Command() {
				
				@Override
				public void execute() {
					requestMissing();
				}
			});
		} else {
			asyncCallback.onSuccess(map);
		}
	}

	private void addCallback(
			AsyncCallback<Map<Integer, ModuleInfo>> asyncCallback) {
		if(asyncCallbacks == null) asyncCallbacks = new ArrayList<AsyncCallback<Map<Integer,ModuleInfo>>>();
		asyncCallbacks.add(asyncCallback);
	}
	
	private void requestMissing() {
		moduleService.getModuleInfo(new ArrayList<Integer>(missing), infoCallback);
	}

	HashSet<Integer> missing;

	private void addMissing(int x) {
		if(missing == null)	missing = new HashSet<Integer>();
		missing.add(x);
	}
}
