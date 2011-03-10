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
package com.kk_electronic.kkportal.core.tabs;

import java.util.List;

public class TabInfo {
	public final int id;
	public String name;
	private List<List<Integer>> moduleIds;
	
	public TabInfo(int id, String name, List<List<Integer>> moduleIds) {
		this.name = name;
		this.moduleIds = moduleIds;
		this.id = id;
	}
	
	/**
	 * @param name
	 * @param moduleIds
	 */
	public TabInfo(String name, List<List<Integer>> moduleIds) {
		this(0, name, moduleIds);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<List<Integer>> getModuleIds() {
		return moduleIds;
	}
	public void setModuleIds(List<List<Integer>> moduleIds) {
		this.moduleIds = moduleIds;
	}
}