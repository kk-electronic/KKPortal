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

package com.kk_electronic.kkportal.scada;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.rpc.DelayingDispatcher;
import com.kk_electronic.kkportal.core.rpc.Dispatcher;
import com.kk_electronic.kkportal.core.rpc.Request;
import com.kk_electronic.kkportal.scada.dto.LoginResult;
import com.kk_electronic.kkportal.scada.dto.Result;

/**
 * @author Jes Andersen
 *         <p>
 *         This dispatcher allows us to add the UserKey authentication in
 *         central place. Most scada service except the pure public services
 *         should probably use this one
 *         </p>
 *         <p>
 *         Since the constructor only gets called if we get injected as a
 *         transitive dependency we can expect to be called. Therefore we login
 *         immediately.
 *         </p>
 *         <p>
 *         The actual handling of the call is not the responsibility of this
 *         dispatcher and it is simply forwarded to {@link ScadaJsonCaller} when
 *         the UserKey has been successfully appended.
 *         </p>
 */
public class UserKeyAppender implements Dispatcher {

	/**
	 * The state we use to keep track of if we should delay,forward or fail the
	 * calls. The only possible transitions is when the login call fails or
	 * succeeds.
	 */
	private static enum State {
		DELAY, FORWARD, DENY
	}

	/**
	 * The {@link DelayingDispatcher} is used as a queue for the request while
	 * we wait for a login.
	 */
	private DelayingDispatcher queue;
	/**
	 * This is the UserKey value that should get appended to every call.
	 */
	private String key;
	private State state = State.DELAY;

	/**
	 * This is the dispatcher that we forward to
	 */
	private final Dispatcher forwardDispatcher;

	@Inject
	public UserKeyAppender(ScadaJsonCaller nextDispatcher,
			ISecurityService securityService) {
		this.forwardDispatcher = nextDispatcher;
		queue = new DelayingDispatcher();
		securityService.Login("kk_system", "disk7glas3",
				new AsyncCallback<Result<LoginResult>>() {

					@Override
					public void onSuccess(Result<LoginResult> result) {
						if (result.Result.Errors == null) {
							// If the login was successful we save the userkey
							// and switch to forward mode
							key = result.Result.UserKey;
							setState(State.FORWARD);
						} else {
							// If not we can only report it as a failure
							// backwards
							setState(State.DENY);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						// Something happened to the call so we deny further
						// calls
						setState(State.DENY);
					}
				});
	}

	private void setState(State newstate) {
		// We assume only one transition happens since login reloads the portal
		assert (state == State.DELAY && state != newstate);
		state = newstate;
		// Setting the dispatcher makes our execute function get called with
		// requests again
		queue.setDispatcher(UserKeyAppender.this);
		//When done we delete the queue
		queue = null;
	}

	@Override
	public <T> void execute(Request<T> request) {
		switch (state) {
		case FORWARD:
			request.addParam("UserKey", key);
			forwardDispatcher.execute(request);
			break;
		case DELAY:
			queue.execute(request);
			break;
		case DENY:
			request.onFailure(new Exception("Cannot login"));
			break;
		}
	}
}
