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

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kk_electronic.gwt.rebind.Dispatch;
import com.kk_electronic.kkportal.core.rpc.RemoteService;
import com.kk_electronic.kkportal.core.rpc.Rename;

/**
 * @author Jes Andersen
 * 
 *         This class is not feature complete yet, it only contains one of the
 *         functions from the IDataElementService region of the
 *         IParkServiceJson.cs interface (C# code from the park server, not
 *         included here)
 * 
 *         What is possible to send and receive:
 *         <ul>
 *         <li>Boxed basic Java Types (String, Integer, Boolean, Double)</li>
 *         <li>Map&lt;K,V&gt;</li>
 *         <li>List&lt;V&gt;</li>
 *         <li>Any class with fields containing only any of the above
 *         (Recursively)</li>
 *         </ul>
 * 
 *         <pre>
 * class A {
 * 	String someValue;
 * }
 * 
 * class B {
 * 	List&lt;A&gt; someList;
 * }
 * </pre>
 * 
 *         Would map to this json:
 * 
 *         <pre>
 * {
 *     "someList" : [
 *         {"someValue":"value1"}, 
 *         {"someValue":"value2"}, 
 *         {"someValue":"value3"} 
 *     ]
 * }
 * </pre>
 * 
 *         It is possible to have different field names in json and java using
 *         the {@link Rename} annotation, but it is not used in this example
 */
@Dispatch(UserKeyAppender.class)
public interface IDataElementService extends RemoteService {
	// We create the basic return type here
	public static class DataElementValue {
		String StationGUID;
		String ElementName;
		String RepresentationGUID;
		String TimeStamp;
		String Value;
		String MappingName;
		String ElementFullName;
		String MappingGUID;
		String Units;
		String ElementGUID;
		String EnumValueName;
	}

	public static class Result {
		List<DataElementValue> Result;
	}

	/*
	 * And then we use the generic result wrapper that all scada services must
	 * implement and return a List of the basic return type.
	 */
	public void GetDataElementValue(String stationGUID,
			String hierarchyTypeNumber, String mappingGUID,
			String dataElementGUID, String aggregation,
			Boolean returnFromStation, AsyncCallback<Result> callback);
}
