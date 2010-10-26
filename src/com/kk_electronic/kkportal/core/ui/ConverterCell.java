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
package com.kk_electronic.kkportal.core.ui;

import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class ConverterCell<C, V> extends AbstractCell<C> {

	private final HasCell<C, V> wrapped;

	public ConverterCell(HasCell<C, V> wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public boolean dependsOnSelection() {
		return wrapped.getCell().dependsOnSelection();
	}

	@Override
	public Set<String> getConsumedEvents() {
		return wrapped.getCell().getConsumedEvents();
	}

	@Override
	public boolean handlesSelection() {
		return wrapped.getCell().handlesSelection();
	}

	@Override
	public void render(C value, Object key, SafeHtmlBuilder sb) {
		Cell<V> cell = wrapped.getCell();
	    cell.render(wrapped.getValue(value), key, sb);
	}
}
