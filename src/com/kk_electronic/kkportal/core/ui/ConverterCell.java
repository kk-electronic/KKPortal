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
