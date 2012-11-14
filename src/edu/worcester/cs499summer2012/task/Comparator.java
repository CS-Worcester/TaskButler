/*
 * Comparator.java
 * 
 * Copyright 2012 Jonathan Hasenzahl, James Celona, Dhimitraq Jorgji
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.worcester.cs499summer2012.task;

public class Comparator {

	public final static int NAME = 1;
	public final static int COMPLETION = 2;
	public final static int PRIORITY = 3;
	public final static int CATEGORY = 4;
	public final static int DATE_DUE = 5;
	public final static int FINAL_DATE_DUE = 6;
	public final static int DATE_CREATED = 7;
	public final static int DATE_MODIFIED = 8;
	public final static int NUM_COMPARATORS = 8;
	
	private int id;
	private String name;
	private boolean isEnabled;
	private int order;
	
	public Comparator(int id, String name) {
		this.id = id;
		this.name = name;
		isEnabled = false;
		order = id;
	}
	
	public Comparator(int id, String name, boolean isEnabled, int order) {
		this.id = id;
		this.name = name;
		this.isEnabled = isEnabled;
		this.order = order;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}
	
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	public void toggleEnabled() {
		isEnabled = !isEnabled;
	}
	
	public int getOrder() {
		return order;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}
}
