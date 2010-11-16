package com.kk_electronic.kkportal.core;

import com.kk_electronic.kkportal.core.moduleview.Module;
import com.kk_electronic.kkportal.core.services.ModuleService.ModuleInfo;

public abstract class AbstractModule implements Module {

	@SuppressWarnings("unused")
	private ModuleInfo info;

	@Override
	public void setModuleInfo(ModuleInfo info) {
		this.info = info;
	}

	@Override
	public String getTitle() {
		return null;
	}
}
