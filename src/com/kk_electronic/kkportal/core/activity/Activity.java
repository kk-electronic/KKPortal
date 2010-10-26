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

import com.google.gwt.user.client.ui.Widget;
import com.kk_electronic.kkportal.core.inject.ConstructFromLiteral;

/**
 * Classed implementing this interface is intended to provide an entrypoint
 * to the application and to be bookmarkable  
 * @author Jes Andersen
 */
@ConstructFromLiteral
public interface Activity {
	/**
	 * @return The widget representing this activity
	 */
	public Widget asWidget();
}
