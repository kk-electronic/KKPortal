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

package com.kk_electronic.kkportal.timereg.model;

import java.util.Date;


/**
 * @author albatros
 *
 */
public class TimeEntry {

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getCheckinDate() {
		if(checkin == null){
			return null;
		}
		return new Date(checkin*1000);
	}
	public void setCheckinDate(Date checkin) {
		this.checkin = checkin.getTime()/1000;
	}
	public Date getCheckoutDate() {
		if(checkout == null){
			return null;
		}
		return new Date(checkout*1000);
	}
	public void setCheckoutDate(Date checkout) {
		this.checkout = checkout.getTime()/1000;
	}
	public Long getCheckin() {
		return checkin;
	}
	public Long getCheckout() {
		return checkout;
	}
	public Integer getTaskId() {
		return taskId;
	}
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}
	public TimeEntry(Long checkin,Long checkout, Integer taskId) {
		this.checkin = checkin;
		this.checkout = checkout;
		this.taskId = taskId;
		this.id = null;
	}
	private Integer id;
	private Long checkin;
	private Long checkout;
	private Integer taskId;
}
