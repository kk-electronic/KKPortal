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
package com.kk_electronic.kkportal.core.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.rpc.FrameEncoder;
import com.kk_electronic.kkportal.core.rpc.RemoteServer;
import com.kk_electronic.kkportal.core.rpc.RpcRequest;
import com.kk_electronic.kkportal.core.security.NewPrimaryIdentityEvent.Handler;
import com.kk_electronic.kkportal.core.ui.InputDialog;
import com.kk_electronic.kkportal.core.util.Pair;

/**
 * 
 * @author Jes Andersen
 */

public class Digest implements SecurityMethod, Handler, com.kk_electronic.kkportal.core.ui.InputDialog.Handler {

	private final Hasher hasher;
	private Identity identity;
	private String secret;
	private final FrameEncoder<JSONValue> encoder;
	private final InputDialog dialog;
	@SuppressWarnings("unused")
	private final IdentityProvider identityProvider;
	private final RemoteServer remoteServer;
	private final SecurityContext context;
	private final SRP srp;

	@Inject
	public Digest(RemoteServer remoteServer, Hasher hasher,FrameEncoder<JSONValue> encoder,InputDialog dialog,IdentityProvider identityProvider,SecurityContext context,SRP srp) {
		this.remoteServer = remoteServer;
		this.hasher = hasher;
		this.encoder = encoder;
		this.dialog = dialog;
		this.identityProvider = identityProvider;
		this.context = context;
		this.srp = srp;
		identityProvider.addNewPrimaryIdentityEventHandler(this);
		setIdentity(identityProvider.getPrimaryIdentity());
		dialog.setHandler(this);
	}
	
	List<Pair<RpcRequest, AsyncCallback<RpcRequest>>> queue;
	private AsyncCallback<String>  encryptedKeyCallback = new AsyncCallback<String>() {
		
		@Override
		public void onSuccess(String result) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub
			
		}
	};
	private List<String> methods = Arrays.asList(new String[]{"password"});
	
	@Override
	public void sign(RpcRequest request, AsyncCallback<RpcRequest> asyncCallback) {
		if(secret == null){
			queue(request,asyncCallback);
		} else {
			signInternal(request, asyncCallback);
		}
	}

	private void queue(RpcRequest request,
			AsyncCallback<RpcRequest> asyncCallback) {
		if(queue == null) queue = new ArrayList<Pair<RpcRequest,AsyncCallback<RpcRequest>>>();
		queue.add(new Pair<RpcRequest, AsyncCallback<RpcRequest>>(request, asyncCallback));
		GWT.log("DIGEST-Queued call for signing");
	}

	private void signInternal(RpcRequest request,
			AsyncCallback<RpcRequest> asyncCallback) {
		String requestSignature = request.getSignature(encoder);
		GWT.log("DIGEST-Signing: " + requestSignature);
		String digestinput = secret+':'+requestSignature;
		request.setParams(new Object[]{
				hasher.hash(digestinput),
				request.getParams()
		});
		asyncCallback.onSuccess(request);
	}
		
	public void setIdentity(Identity identity){
		if(identity == null){
			this.identity = null;
			secret = null;
			return;
		} else {
			GWT.log("DIGEST-New identity " + identity);
			Identity newidentity = identity;
			if(newidentity.equals(this.identity)) return;
			dialog.setText("Need password for " + newidentity.toString());
			dialog.setValue("");
			this.identity = newidentity;
			secret = null;
			context.setIdentity(identity.toString());
			if(queue != null && (!queue.isEmpty())) dialog.show();
		}
	}

	@Override
	public void onNewPrimaryIdentity(NewPrimaryIdentityEvent event) {
		setIdentity(event.getIdentity());
	}

	@Override
	public void onInput(String input) {
		context.setPassword(input);
		GWT.log("DIGEST-Got password");
		srp.requestChallange(null, context.getIdentity(), methods , new AsyncCallback<Challange>() {
			
			@Override
			public void onSuccess(Challange result) {
				sendAnswer(context.calc_answer(result));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Login failed", caught);	
			}
		});
	}
	
	

	protected void sendAnswer(Answer answer) {
		srp.answerChallange(answer.a.toString(16), answer.m1, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				secret = hasher.hash(context.getSecret());
				GWT.log("DIGEST-Has shared secret with server");
				sendQueue();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Login failed", caught);
				identityProvider.invalidate(identity,"Wrong password");
			}
		});
	}

	private void sendQueue() {
		if(queue != null && !queue.isEmpty()){
			for(Pair<RpcRequest, AsyncCallback<RpcRequest>> pair : queue){
				signInternal(pair.getA(), pair.getB());
			}
		}
	}
}
