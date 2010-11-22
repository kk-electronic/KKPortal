package com.kk_electronic.kkportal.debug.model;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.HasData;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.services.Debug;

public class CpuUsage implements AsyncCallback<List<Double>>,
		NewCpuUsageDataEvent.Handler {
	List<Double> l = new LinkedList<Double>();
	List<HasData<Double>> displays = new LinkedList<HasData<Double>>();

	@Inject
	public CpuUsage(Debug debug, EventBus eventBus) {
		debug.getCpuHistory(this);
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
		l = result;
		updateDisplays(l);
	}

	@Override
	public void onNewCpuUsage(NewCpuUsageDataEvent event) {
		if ("cpu".equals(event.getCpuname())) {
			l.add(event.getCpuload());
			if(l.size()>60){
				l.remove(0);
			}
			updateDisplays(l);
		}
	}
}
