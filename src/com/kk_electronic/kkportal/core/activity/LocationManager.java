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

package com.kk_electronic.kkportal.core.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.kk_electronic.kkportal.core.event.LocationChangedEvent;

/**
 * Class responsible for monitoring the History of the browser 
 * and firing event when it happens.
 * 
 * @author Rasmus Carlsen
 * @author Jes Andersen
 *
 */
@Singleton
public class LocationManager implements ValueChangeHandler<String> {
	private final LocationInfo locationInfo;
	/**
	 * @return the locationInfo
	 */
	public LocationInfo getLocationInfo() {
		return locationInfo;
	}

	private final EventBus eventBus;
	private final String defaultplace;
	private static final Map<String, String> tokenMap = new HashMap<String, String>();
	private ArrayList<AbstractLink> linkList = new ArrayList<AbstractLink>();
	
	@Inject
	public LocationManager(@Named("DefaultHistoryToken") String defaultplace, EventBus eventBus, LocationInfo locationInfo) {
		this.locationInfo = locationInfo;
		this.eventBus = eventBus;
		this.defaultplace = defaultplace;
		
		History.addValueChangeHandler(this);
		
		/*
		 * If we do not have anything in the history stack go to the default
		 * view TODO: Decide if we should redirect like here or just show it
		 */
		if ("".equals(History.getToken())) {
			goHome();
		} else {
			/*
			 * If we have a history token we fire the change event which handles
			 * setup of the activity
			 */
			History.fireCurrentHistoryState();
		}

	}
	
	public String getToken(String token) {
		return tokenMap.get(token);
	}

	public String addTokens(String partialLink) {
		tokenMap.entrySet();
		return partialLink;
	}
	
	public void refreshLinks() {
		for (AbstractLink alink : linkList) {
			alink.updateLink(tokenMap);
		}
	}
	
	/**
	 * This function is called when ever the history of the browser is changed.
	 * 
	 * @see ValueChangeHandler#onValueChange(ValueChangeEvent)
	 */
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		parseToken(event.getValue());
		if (!tokenMap.containsKey(null)) {
			GWT.log("History Token has no location - returning to home page");
			goHome();
		} else {
			updateLocationInfo();
		}
	}

	/**
	 * Decode the map like described in {@link ActivityManager}
	 * 
	 * If an invalid rawString is provided it returns to the home activity
	 * 
	 * @param rawHistoryString
	 *            the anchor part of the current url
	 * @return a map of parameters
	 */
	private void parseToken(String rawHistoryString) {
		tokenMap.clear();
		String[] values = rawHistoryString.split(";");
		for (String placestring : values) {
			String[] l = placestring.split("[=]", 2);
			if (l.length > 1) {
				assert (!tokenMap.containsKey(l[0]));
				tokenMap.put(l[0], l[1]);
			} else {
				assert (!tokenMap.containsKey(null));
				tokenMap.put(null, l[0]);
			}
		}
	}
	
	/**
	 * Return the application to the default home activity
	 */
	public void goHome() {
		History.newItem(defaultplace);
	}
	
	/**
	 * Change location to the new place
	 * 
	 * @param place
	 *            new location to go to
	 */
	public void go(String placename) {
		assert(placename != null && !placename.equals(""));
		History.newItem(placename);
	}
	
	/**
	 * This one trims the place name a bit before using activitymap to lookup
	 * the name. It is done to provide some nice looking urls. Everything after
	 * $ or / is cut off
	 * 
	 * @param tokens
	 *            a map if input tokens, where the null key is the main
	 *            navigation element
	 * @return the class matching the token set
	 */
	private void updateLocationInfo() {
		assert (tokenMap.containsKey(null));
		
		String location = tokenMap.get(null);
		
		String[] matches = location.split("[/$]", 2);
		
		// Reset Location Info and start filling in new information
		locationInfo.setActivity(matches[0]);
		locationInfo.setSubint(null);
		locationInfo.setSubpath(null);
		GWT.log("mainplace: " + matches[0]);
		
		if (matches.length > 1) {
			switch (location.charAt(matches[0].length())) {
			case '/':
				locationInfo.setSubpath(matches[1]);
				break;
			case '$':
				locationInfo.setSubint(Integer.valueOf(matches[1]));
				break;
			default:
				GWT.log("LocationManager:Problems with history token" + location);
			}
		} else {
			GWT.log("LocationManager:No Tab selected");
		}
		eventBus.fireEvent(new LocationChangedEvent(locationInfo));
		refreshLinks();
	}
	
	public AbstractLink registerLink(Hyperlink link, String... values) {
		AbstractLink al = new AbstractHyperLink(link, generateMap(values));
		linkList.add(al);
		return al;
	}
	
	public AbstractLink registerLink(Anchor link, String... values) {
		AbstractLink al = new AbstractAnchor(link, generateMap(values)); 
		linkList.add(al);
		al.updateLink(tokenMap);
		return al;
	}
	
	private Map<String, String> generateMap(String[] values) {
		assert(values.length % 2 == 0);

		HashMap<String, String> map = new HashMap<String, String>();

		for (int i = 0; i < values.length - 1; i = i + 2) {
			map.put(values[i], values[i + 1]);
		}
		return map;
	}

	public static String combineMaps(Map<String, String> baseValues, Map<String, String> tokenMap) {
		HashMap<String, String> tik = new HashMap<String, String>();
		if (tokenMap != null) {
			tik.putAll(tokenMap);
		}
		if (baseValues != null) {
			tik.putAll(baseValues);
		}
		StringBuffer sb = new StringBuffer();
		if (tik.containsKey(null)) {
			sb.append("#");
			sb.append(tik.get(null));
		}
		for (Entry<String,String> token : tik.entrySet()) {
			if (token.getKey() != null) {
				sb.append(";");
				sb.append(token.getKey());
				sb.append("=");
				sb.append(token.getValue());
			}
		}
		return sb.toString();
	}
	
	public interface AbstractLink {
		public void updateLink(Map<String, String> tokenMap);
		public void changeValues(String... baseValues);
	}
	
	private class AbstractHyperLink implements AbstractLink {
		private Hyperlink link;
		private Map<String, String> baseValues;
		
		/**
		 * @param link
		 * @param map
		 */
		public AbstractHyperLink(Hyperlink link, Map<String, String> map) {
			this.link = link;
			this.baseValues = map;
		}

		/* (non-Javadoc)
		 * @see com.kk_electronic.kkportal.core.activity.LocationManager.AbstractLink#updateLink(java.util.Map)
		 */
		@Override
		public void updateLink(Map<String, String> tokenMap) {
			String linkText = LocationManager.combineMaps(tokenMap, baseValues);
			link.setTargetHistoryToken(linkText);
		}

		/* (non-Javadoc)
		 * @see com.kk_electronic.kkportal.core.activity.LocationManager.AbstractLink#changeLink(java.util.Map)
		 */
		@Override
		public void changeValues(String... baseValues) {
			this.baseValues = generateMap(baseValues);
		}		
	}

	private class AbstractAnchor implements AbstractLink {

		private final Anchor link;
		private Map<String, String> baseValues;

		/**
		 * @param link
		 * @param generateMap
		 */
		public AbstractAnchor(Anchor link, Map<String, String> generateMap) {
			this.link = link;
			this.baseValues = generateMap;
		}

		/* (non-Javadoc)
		 * @see com.kk_electronic.kkportal.core.activity.LocationManager.AbstractLink#updateLink(java.util.Map)
		 */
		@Override
		public void updateLink(Map<String, String> tokenMap) {
			String linkText = LocationManager.combineMaps(tokenMap, baseValues);
			link.setHref(linkText);
		}

		/* (non-Javadoc)
		 * @see com.kk_electronic.kkportal.core.activity.LocationManager.AbstractLink#changeLink(java.util.Map)
		 */
		@Override
		public void changeValues(String... baseValues) {
			this.baseValues = generateMap(baseValues);
			this.updateLink(LocationManager.tokenMap);
		}		
	}
}