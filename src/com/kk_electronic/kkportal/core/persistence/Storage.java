package com.kk_electronic.kkportal.core.persistence;

public interface Storage {
	public void put(String key,String value);
	public String get(String key);
	public void remove(String string);
	public void clear();
}
