package com.kk_electronic.kkportal.examples.modules;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kk_electronic.kkportal.core.rpc.RemoteService;

public interface MotDService extends RemoteService{
	public void getMessageOfTheDay(AsyncCallback<String> callback);
}
