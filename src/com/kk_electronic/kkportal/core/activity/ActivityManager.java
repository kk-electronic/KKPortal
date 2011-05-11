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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kk_electronic.kkportal.core.event.LocationChangedEvent;
import com.kk_electronic.kkportal.core.inject.ConstructFromLiteral;
import com.kk_electronic.kkportal.core.inject.FlexInjector;
import com.kk_electronic.kkportal.core.reflection.ActivityMap;
import com.kk_electronic.kkportal.core.reflection.Injection;
import com.kk_electronic.kkportal.core.ui.ApplicationLayout;
import com.kk_electronic.kkportal.core.ui.SideBar;
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
public class ActivityManager implements LocationChangedEvent.Handler {
	
	private final ApplicationLayout display;
	private final SideBar secondaryDisplay;
	private final FlexInjector injector;
	private final ActivityMap activityMap;
	private final Stats stats;
	private final LocationManager locationManager;
	
	HashMap<Class<? extends Activity>, Activity> running = new HashMap<Class<? extends Activity>, Activity>();
	private LocationInfo locationInfo;
	
	@Inject
	public ActivityManager(ApplicationLayout layout, FlexInjector injector,
			ActivityMap activityMap, LocationInfo locationInfo,
			EventBus eventBus, Stats stats, LocationManager locationManager,
			SideBar sideBar) {

		this.injector = injector;
		this.activityMap = activityMap;
		this.display = layout;
		this.locationInfo = locationInfo;
		this.stats = stats;
		this.locationManager = locationManager;
		this.secondaryDisplay = sideBar;
		
		/*
		 * We create the initial GUI elements needed for displaying activities.
		 */
		layout.go();
		
		/*
		 * Start Listening to LocationChange events.
		 */
		eventBus.addHandler(LocationChangedEvent.TYPE, this);
		
		/*
		 * Do first location check
		 */
		onLocationChanged(null);
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
		locationManager.go(placename);
	}
		
	private void displayActivityClass(final Class<? extends Activity> activity, final ActivityContainer target) {
		if(running.containsKey(activity)){
			displayActivity(running.get(activity), target);
			return;
		}
		stats.sendStats(Injection.class, activity, "begin");
		injector.create(activity, new AsyncCallback<Activity>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to start requested Presenter", caught);
			}

			@Override
			public void onSuccess(Activity result) {
				stats.sendStats(Injection.class, activity, "end");
				running.put(activity, result);
				displayActivity(result, target);
			}
		});		
	}

	private void displayActivity(Activity result, ActivityContainer target) {
		target.displayActivity(result);
	}
	
	/**
	 * 
	 * @return the class matching the token set
	 */
	private Class<? extends Activity> getActityFromLocation(String activityToken) {
		return activityMap.getClassFromKey(activityToken);
	}

	/**
	 * This is called either when the user loads the page for the first time or
	 * when the user switches places.
	 * 
	 * It is not meant to be called directly, but implicit via
	 * {@link #go(Class)}
	 * @see LocationChangedEvent.Handler#onLocationChanged(LocationChangedEvent)
	 */
	@Override
	public void onLocationChanged(LocationChangedEvent event) {
		String activity = locationInfo.getActivity();

		if (activity == null || activity.equals("")) {
			GWT.log("History Token has no location - returning to home page");
			locationManager.goHome();
		} else {
			Class<? extends Activity> mainActivity = getActityFromLocation(activity);
			if (mainActivity != null) {
				displayActivityClass(mainActivity, display);
			} else {
				GWT.log("Unknown location - returning to home page");
				locationManager.goHome();
				return;
			}
		}		

		/*
		 * Check for sidebar activities 
		 */
		String panelToken = locationManager.getToken("sidepanelOpen");
		if (panelToken != null) {
			Boolean b = new Boolean(panelToken);			
			if (b) {
				// TODO show sidebar
			} else {
				// TODO hide sidebar
			}
		}
		
		String sideToken = locationManager.getToken("sidepanel");
		secondaryDisplay.clear();
		if (sideToken != null && !sideToken.equals("")) {
			String[] matches = sideToken.split("[,]");
			for (String match : matches) {
				Class<? extends Activity> secActivity = getActityFromLocation(match);
				if(secActivity != null) {
					displayActivityClass(secActivity, secondaryDisplay);
				}				
			}
		}
	}
}
