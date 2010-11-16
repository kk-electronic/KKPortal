package com.kk_electronic.kkportal.core.util.wait;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;

public class WaitForComponents {
	public WaitForComponents(EventBus bus,Command command,Component...components) {
		for(Component component:components){
			if(!component.isReady()) addToWaitList(component);
		}
	}

	private void addToWaitList(Component component) {
	}
}
