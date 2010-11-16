package com.kk_electronic.kkportal.core.security;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kk_electronic.kkportal.core.ui.InputDialog;
import com.kk_electronic.kkportal.core.ui.InputDialog.Handler;

@Singleton
public class IdentityProvider implements Handler{
	private final InputDialog display;
	private final EventBus eventBus;
	
	public static interface Display {
		void show();
		void setPresenter(IdentityProvider identityProvider);
	}

	@Inject
	public IdentityProvider(InputDialog display,EventBus eventBus) {
		this.display = display;
		this.eventBus = eventBus;
		display.setHandler(this);
	}
	
	List<Identity> identities = new LinkedList<Identity>();
	public void invalidate(Identity identity){
		identities.remove(identity);
		if(identities.size() == 0){
			eventBus.fireEventFromSource(new NewPrimaryIdentityEvent(null),this);
		}
	}
	public Identity getPrimaryIdentity(){
		if(identities.size() > 0) return identities.get(0);
		display.show();
		return null;
	}

	public void addIdentity(Identity identity) {
		identities.add(identity);
		if(identities.size() == 1){
			eventBus.fireEventFromSource(new NewPrimaryIdentityEvent(identity),this);
		}
	}
	
	public HandlerRegistration addNewPrimaryIdentityEventHandler(NewPrimaryIdentityEvent.Handler handler){
		return eventBus.addHandler(NewPrimaryIdentityEvent.TYPE, handler);
	}
	@Override
	public void onInput(String input) {
		String[] c = input.split("@",2);
		addIdentity(new Identity(c[0],c[1]));
	}
}
