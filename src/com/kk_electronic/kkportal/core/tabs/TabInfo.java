/**
 * 
 */
package com.kk_electronic.kkportal.core.tabs;

import java.util.List;

public interface TabInfo {
	public int getId();
	public String getName();
	public List<List<Integer>> getModuleIds();
}