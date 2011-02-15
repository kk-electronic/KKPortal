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
package com.kk_electronic.kkportal.examples.modules;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.AbstractModule;
import com.kk_electronic.kkportal.core.services.MotDService;
import com.kk_electronic.kkportal.core.util.Callback;

/**
 * Message of the Day module,
 * displaying what the current message of the day is <p> 
 * 
 * using the {@link MotDService}
 * 
 * @author Jes Andersen
 *
 */
public class MotD extends AbstractModule implements Callback<String> {
	HTML widget = new HTML("Fetching MotD");
	
	@Override
	public Widget asWidget() {
		return widget;
	}

	@Inject
	public MotD(MotDService service){
		/*
		 *  Uses the call method in AbstractModule to 
		 *	convert a Callback to a Asynccallback for the service.
		/**/
		service.getMessageOfTheDay(call(this));
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
}
