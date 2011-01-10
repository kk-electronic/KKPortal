package com.kk_electronic.kkportal.debug.model;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.HasData;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.services.TechDemo;

/**
 * I hold the data for Cpu Usage on the server, and manage a list
 * of any class that is interested in data.
 * I listen for any {@link NewCpuUsageDataEvent} and update my data
 * accordingly.
 * Whenever I update my data I make sure to call setRowData on any
 * display that is connected to this data. 
 * @author Jes Andersen
 *
 */
public class CpuUsage implements AsyncCallback<List<Double>>,
		NewCpuUsageDataEvent.Handler {
	List<Double> cachedData = new LinkedList<Double>();
	List<HasData<Double>> displays = new LinkedList<HasData<Double>>();

	@Inject
	public CpuUsage(TechDemo techDemo, EventBus eventBus) {
		/*
		 * the getCpuHistory has the side effect that the server
		 * will send events about updates to the data
		 */
		techDemo.getCpuHistory(this);
		eventBus.addHandler(NewCpuUsageDataEvent.TYPE, this);
	}

	public void addDisplay(HasData<Double> display) {
		displays.add(display);
	}

	private void updateDisplays(List<Double> values) {
		for (HasData<Double> display : displays) {
			display.setRowData(0, values);
		}
	}

	@Override
	public void onFailure(Throwable caught) {
		GWT.log("CpuUsage-Could not get usage", caught);
	}

	@Override
	public void onSuccess(List<Double> result) {
		cachedData = result;
		updateDisplays(cachedData);
	}

	@Override
	public void onNewCpuUsage(NewCpuUsageDataEvent event) {
		/*
		 * We get data for all cpus in the system and a single
		 * combined one.
		 * The names are "cpu" for the total and "cpuX" where
		 * X is a zero based index of the cpu/core
		 * The class only takes care of the combined data currently.
		 */
		if ("cpu".equals(event.getCpuname())) {
			cachedData.add(event.getCpuload());
			if(cachedData.size()>60){
				cachedData.remove(0);
			}
			updateDisplays(cachedData);
		}
	}
}
