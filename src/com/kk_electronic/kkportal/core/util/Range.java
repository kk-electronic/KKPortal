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

package com.kk_electronic.kkportal.core.util;

/**
 * @author Jes Andersen
 * 
 */
public class Range<T extends Comparable<T>> implements Cloneable {
	public T begin;
	public T end;

	public Range() {
	}

	public Range(T begin, T end) {
		this.begin = begin;
		this.end = end;
	}

	public boolean isBounded() {
		return begin != null && end != null;
	}
	
	public boolean contains(Range<T> range){
		if (!isBounded()){
			return false;
		}
		if (range.begin.compareTo(begin) < 0){
			return false;
		}
		if (range.end.compareTo(end) > 0){
			return false;
		}
		return true;
	}

	public Range<T> clone() {
		return new Range<T>(begin, end);
	}

	/**
	 * @param range
	 */
	public void extend(Range<T> range) {
		if(range.begin != null && (begin == null || range.begin.compareTo(begin) < 0)){
			begin = range.begin;
		}
		if(range.end != null && (end == null || range.end.compareTo(end) > 0)){
			end = range.end;
		}
	}
}
