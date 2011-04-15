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

package com.kk_electronic.kkportal.scada;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.AbstractModule;
import com.kk_electronic.kkportal.scada.IDataElementService.DataElementValue;
import com.kk_electronic.kkportal.scada.IDataElementService.Result;

/**
 * @author Jes Andersen
 * 
 *         This class is an example on how you can access a Scada service using
 *         the new rpc system. The system consists of three parts. The
 *         underlying framework, some interfaces and some dispatchers.
 * 
 *         Most Developers should only care about the interfaces.
 * 
 *         In this example {@link IDataElementService} is used to fetch the
 *         current values and simply displayed as simple text.
 * 
 *         This example violates MVP isolation and calls the back-end directly,
 *         but this is done to make the example smaller. If you need an example
 *         for how this is done feel free to contact me.
 */
public class ScadaDataElementExample extends AbstractModule {

	/**
	 * The {@link HTML} is where we place our load text, and put the answer when
	 * we eventually get it.
	 */
	HTML label = new HTML("Fetching values");

	@Inject
	public ScadaDataElementExample(IDataElementService scada) {
		/*
		 * All rpc calls are asynchronous since we must not halt the ui while
		 * fetching data.
		 */
		AsyncCallback<Result> callback = new AsyncCallback<Result>() {

			@Override
			public void onSuccess(Result result) {
				/**
				 * We create a SafeHtmlBuilder to create the new HTML of label
				 * The result is similar to this:
				 * 
				 * <pre>
				 * Current W=48.8[A]
				 * Frequence=49.928[Hz]
				 * Gearoil temperature=46.5[C]
				 * </pre>
				 */
				SafeHtmlBuilder sb = new SafeHtmlBuilder();

				/**
				 * This is an example of the json that gets transferred from
				 * scada. While not nessesary to know it helps to understand the
				 * structure
				 * 
				 * <pre>
				 * {
				 *   "Errors": null, 
				 *   "TotalResults": 37, 
				 *   "Result": [
				 *     {
				 *       "StationGUID": "b64af73e-5f2c-464b-addf-7912542ccdf0", 
				 *       "ElementName": null, 
				 *       "RepresentationGUID": "cfd108bf-557f-4103-9176-1895bacda8b5", 
				 *       "TimeStamp": "0001-01-01 00:00:00.0000000", 
				 *       "Value": "N/M", 
				 *       "MappingName": "DCLink voltage", 
				 *       "ElementFullName": null, 
				 *       "MappingGUID": "fd8e1338-e210-428d-9c54-1d3baca9426c", 
				 *       "Units": null, 
				 *       "ElementGUID": "00000000-0000-0000-0000-000000000000", 
				 *       "EnumValueName": null
				 *     },
				 *     ...
				 * }
				 * </pre>
				 * 
				 * The {@link com.kk_electronic.kkportal.scada.dto.Result<T>}
				 * object corresponds to the outermost element in the json, and
				 * the Result element is a List<DataElementValue>
				 */
				
				// Iteration is done over the results
				for (DataElementValue i : result.Result) {
					//If we have both a name and a value we add it as name=value
					if (i.MappingName != null && i.Value != null) {
						sb.appendEscaped(i.MappingName);
						sb.append('=');
						sb.appendEscaped(i.Value);
						//We optionally appends the unit if defined as [unit]
						if (i.Units != null) {
							sb.append('[').appendEscaped(i.Units).append(']');
						}
						sb.appendHtmlConstant("<br />");
					}
				}
				//and finally we update the label to contain the result
				label.setHTML(sb.toSafeHtml());
			}

			@Override
			public void onFailure(Throwable caught) {
				// If the call failed we simply notify the user
				label.setText("Call failed");
			}
		};
		/*
		 * When calling the server, we add some parameters it expect, and use our previous callback.
		 * as a special note here we have stripped UserKey from the interface. That responsibility is delegated to another class.
		 * For more info read the documentation on the interface
		 */
		scada
				.GetDataElementValue(
						"b64af73e-5f2c-464b-addf-7912542ccdf0",
						"0",
						"EBABF07A-99A7-41ec-8E90-8C65D79CDB67,A250597F-5768-445f-A668-0D97C054A32A,4E50A845-A86F-486a-901A-E6FC2AF3C2B1,F9C1415D-BDDC-4343-B421-9F9BAE96A781,6E5FFB7E-31BB-4561-A57D-652E2805C50D,A3DED774-054D-40ef-BD5D-EBC668F5E3E4,1BBC05FD-EBD6-4738-995D-DC41947636D0,DC8BC9EF-91FA-4c39-9469-CA69FA7ACB3E,625F01B9-7302-4cc1-8010-A3446C343848,FD8E1338-E210-428d-9C54-1D3BACA9426C,EA65A1AB-2952-43d1-A5AD-F06B95E49EFC,1C855DE9-75D7-425c-A8EF-3974385A2358,65cbd39b-37d5-471e-8bb4-147bbe9ec332,E27B096C-CD76-44ad-9A4E-A190F998F0E2,03C237CF-C1C4-413f-B951-938A58AA78F8,9DFC4A73-29CB-47bb-8F9C-2DD625F390C6,6D51F747-365D-46b1-AD01-9CB9FE2670AF,6720db90-f5d6-4df6-9f85-de3420adeafc,A52D2DB1-8848-40ec-9C79-3880B0AF1171,2EF819D3-88E0-468d-97D4-21AACF2FC996,8DAA0769-7E3D-4d24-B724-C3BBD3BD3A5B,A7DFBF40-466A-4df9-ABE1-2C2C8BF0EFD0,FB086AFF-A4D5-4675-B97F-EF16BCC2B552,bb00d11f-1546-4324-995d-5cf4a19a1a06,7d217e48-2eb0-4ea8-bd08-9c4cd1e0deb3,4C6B8768-D939-4225-A427-5873C4F637F8,FC10A518-DD88-425c-88C0-5598E1EEAEE1,9a9258af-e920-4bf4-a97f-fee506aa86e7,474cf253-92c5-4692-8f01-008c642b15e8,9A1B8AF3-1812-4e78-A6E2-EA2E5ED6B89B,A1F846F2-F6C4-42a1-9B6E-88859F9EE3B9,00b34192-7885-4b9f-a50f-fa96a4a05f42,9A0FC960-8794-437f-9B7F-AFCDC5A402C3,4E5E3FA9-A341-440f-B9DF-6DAF85000CE3,b987d0d8-b0b8-4658-9a34-747c9a74fa99,c1c2bbc0-becf-4efc-b1c1-5003d5c92864,073f1e92-081b-41d7-b061-0b67b1763870",
						null, null, false, callback);
	}

	@Override
	public Widget asWidget() {
		return label;
	}

}
