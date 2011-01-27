package com.kk_electronic.kkportal.core.persistence;

public class SessionStorage implements Storage {

	@Override
	public native String get(String key)/*-{
		return sessionStorage.getItem(key);
	}-*/;

	@Override
	public native void put(String key, String value)/*-{
		sessionStorage.setItem(key,value);
	}-*/;

	@Override
	public native void remove(String key) /*-{
		sessionStorage.removeItem(key);
	}-*/;

	@Override
	public native void clear() /*-{
		sessionStorage.clear();
	}-*/;
}
