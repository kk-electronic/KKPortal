package com.kk_electronic.kkportal.core.security;

public class Identity {
	public String getUsername() {
		return username;
	}
	public String getServer() {
		return server;
	}
	private final String username;
	private final String server;
	public Identity(String username,String server){
		this.username = username;
		this.server = server;
	}
	@Override
	public String toString() {
		return username + "@" + server;
	}
}
