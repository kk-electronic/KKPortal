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
import com.kk_electronic.gwt.rebind.Dispatch;
import com.kk_electronic.kkportal.core.rpc.RemoteService;
import com.kk_electronic.kkportal.scada.dto.LoginResult;
import com.kk_electronic.kkportal.scada.dto.Result;

/**
 * @author albatros
 *
 */
@Dispatch(ScadaJsonCaller.class)
public interface ISecurityService extends RemoteService {
	public void Login(String userName, String password,AsyncCallback<Result<LoginResult>> callback);
	public void LoginMD5(String base64OfMd5OfUserColonPassword,AsyncCallback<Result<LoginResult>> callback);
}
