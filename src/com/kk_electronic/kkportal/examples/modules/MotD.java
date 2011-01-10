package com.kk_electronic.kkportal.examples.modules;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.AbstractModule;

public class MotD extends AbstractModule implements AsyncCallback<String> {
	HTML widget = new HTML("Fetching MotD");
	
	@Override
	public Widget asWidget() {
		return widget;
	}

	@Inject
	public MotD(MotDService service){
		service.getMessageOfTheDay(this);
	}

	@Override
	public void onSuccess(String result) {
		if(result != null){
			SafeHtmlBuilder sb = new SafeHtmlBuilder();
			sb.appendEscapedLines(result);
			widget.setHTML(sb.toSafeHtml());
		} else {
			widget.setHTML("No message of the day");
		}
	}
	
	@Override
	public void onFailure(Throwable caught) {
		widget.setText(caught.toString());
	}
}
