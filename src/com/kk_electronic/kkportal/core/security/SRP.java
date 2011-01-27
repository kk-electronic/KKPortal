package com.kk_electronic.kkportal.core.security;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kk_electronic.kkportal.core.rpc.RemoteService;

public interface SRP extends RemoteService {
	public void requestChallange(String server, String username,
			List<String> methods,String sessionId, AsyncCallback<Challange> callback);

	public void answerChallange(String A, String m1,AsyncCallback<String> callback);
}
