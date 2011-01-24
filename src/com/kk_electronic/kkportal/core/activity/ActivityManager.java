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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.kk_electronic.kkportal.core.event.LocationChangedEvent;
import com.kk_electronic.kkportal.core.inject.ConstructFromLiteral;
import com.kk_electronic.kkportal.core.inject.FlexInjector;
import com.kk_electronic.kkportal.core.reflection.ActivityMap;
import com.kk_electronic.kkportal.core.reflection.Injection;
import com.kk_electronic.kkportal.core.ui.ApplicationLayout;
import com.kk_electronic.kkportal.core.util.Stats;

/**
 * This class is responsible for history management in a browser and for
 * selection the entry point class.
 * 
 * The History token is encoded like this:
 * value;key=value;key=value;key=value... where key is unique and null if not
 * specified.
 * 
 * The null key value (that is the value without a key) is used the the
 * selecting main navigation.
 * 
 * This is done using the ActivityMap which maps strings to the Activity class
 * 
 * @author Jes Andersen
 */
@ConstructFromLiteral
/* We make this class can be created using the injection framework */
@Singleton
/* And hint that only one such class such exist */
public class ActivityManager implements ValueChangeHandler<String> {
	private final ApplicationLayout display;
	private final FlexInjector injector;
	private final ActivityMap activityMap;
	private final String defaultplace;
	private final LocationInfo locationInfo;
	private final EventBus eventBus;
	private final Stats stats;

	@Inject
	public ActivityManager(ApplicationLayout layout, FlexInjector injector,
			ActivityMap activityMap,
			@Named("DefaultHistoryToken") String defaultplace,LocationInfo locationInfo
			, EventBus eventBus,Stats stats) {

		this.injector = injector;
		this.activityMap = activityMap;
		this.defaultplace = defaultplace;
		this.display = layout;
		this.locationInfo = locationInfo;
		this.eventBus = eventBus;
		this.stats = stats;
		/*
		 * We create the initial GUI elements needed for displaying activities.
		 */
		layout.go();
		History.addValueChangeHandler(this);

		/*
		 * If we do not have anything in the history stack de go to the default
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
	public void go(Class<? extends Activity> place) {
		String placename = activityMap.getKeyFromClass(place);
		if (placename == null) {
			GWT.log("Place not found for activity: " + place.getName());
		}
		History.newItem(placename);
	}
		
	/**
	 * This is called either when the user loads the page for the first time or
	 * when the user switches places.
	 * 
	 * It is not meant to be called directly, but implicit via
	 * {@link #go(Class)}
	 */
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		Map<String, String> tokens = parseToken(event.getValue());
		if (!tokens.containsKey(null)) {
			GWT.log("History Token has no location - returning to home page");
			goHome();
		} else {
			Class<? extends Activity> mainActivity = getActityFromLocation(tokens);
			if (mainActivity != null) {
				displayActivityClass(mainActivity);
			} else {
				GWT.log("Unknown location - returning to home page");
				goHome();
				return;
			}
		}
	}

	HashMap<Class<? extends Activity>, Activity> running = new HashMap<Class<? extends Activity>, Activity>();

	private void displayActivityClass(final Class<? extends Activity> mainActivity) {
		if(running.containsKey(mainActivity)){
			displayActivity(running.get(mainActivity));
			return;
		}
		stats.sendStats(Injection.class, mainActivity, "begin");
		injector.create(mainActivity, new AsyncCallback<Activity>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to start requested Presenter", caught);
			}

			@Override
			public void onSuccess(Activity result) {
				stats.sendStats(Injection.class, mainActivity, "end");
				running.put(mainActivity, result);
				displayActivity(result);
			}
		});		
	}

	private void displayActivity(Activity result) {
		display.displayActivity(result);
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
	private Class<? extends Activity> getActityFromLocation(
			Map<String, String> tokens) {
		String location = tokens.get(null);
		String[] matches = location.split("[/$]", 2);
		GWT.log("mainplace: " + matches[0]);
		if (matches.length > 1) {
			switch (location.charAt(matches[0].length())) {
			case '/':
				locationInfo.setSubint(null);
				locationInfo.setSubpath(matches[1]);
				break;
			case '$':
				locationInfo.setSubpath(null);
				locationInfo.setSubint(Integer.valueOf(matches[1]));
				break;
			default:
				GWT.log("ActivityManager:Problems with history token"
						+ location);
			}
		}
		eventBus.fireEvent(new LocationChangedEvent(locationInfo));
		return activityMap.getClassFromKey(matches[0]);
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
	private Map<String, String> parseToken(String rawHistoryString) {
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.clear();
		String[] values = rawHistoryString.split(";");
		for (String placestring : values) {
			String[] l = placestring.split("[=]", 2);
			if (l.length > 1) {
				assert (!parameterMap.containsKey(l[0]));
				parameterMap.put(l[0], l[1]);
			} else {
				if (!parameterMap.containsKey(null)) {
					parameterMap.put(null, l[0]);
				} else {
					if (!parameterMap.containsKey(l[0])) {
						GWT
								.log("History Contains Duplicate items - returning to home page");
						goHome();
					}
					parameterMap.put(l[0], "");
				}
			}
		}
		return parameterMap;
	}
}
