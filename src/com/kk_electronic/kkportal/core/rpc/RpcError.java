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
package com.kk_electronic.kkportal.core.rpc;

import com.google.gwt.json.client.JSONValue;


@SuppressWarnings("serial")
public class RpcError extends Exception implements RpcEnvelope {
	private final int code;
	private final String message;
	private final JSONValue data;
	private final int id;
	
	
	public int getId() {
		return id;
	}


	public RpcError(int id, int code, String message, JSONValue data) {
		this.id = id;
		this.code = code;
		this.message = message;
		this.data = data;
	}


	public int getCode() {
		return code;
	}


	public String getMessage() {
		return message;
	}


	public JSONValue getData() {
		return data;
	}
	
}
