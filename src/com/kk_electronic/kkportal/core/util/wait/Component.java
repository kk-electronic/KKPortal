package com.kk_electronic.kkportal.core.util.wait;

public interface Component {
	public <H extends LoadEvent.Handler & LoadFailedEvent.Handler> void addLoadHandler(H handler);
	public void load();
	public boolean isReady();
	public boolean canLoad();
}
