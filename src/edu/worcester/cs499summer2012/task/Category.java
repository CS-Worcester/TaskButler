/*
 * Category.java
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

import android.graphics.Color;

public class Category {

	public static final int NEW_CATEGORY = 0;

	private int id;
	private String name;
	private int color;
	private long updated;
	private String gID;

	public Category() {}

	public Category(String name, int color, long updated) {
		this.name = name;
		this.color = color;
		this.updated = updated;
	}

	public Category(int id, String name, int color, long updated) {
		this(name, color, updated);
		this.id = id;
	}

	public Category(Category c) {
		this(c.id, c.name, c.color, c.updated);
	}
	
	public Category(int id, String name, int color, long updated, String gID) {
		this(id,name,color,updated);
		this.gID = gID;

	}

	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Categories are equal if they have the same ID.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null)
			return false;
		if (o.getClass() != this.getClass())
			return false;
		
		return ((Category) o).id == this.id;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public int getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = Color.parseColor(color);
	}

	public void setColor(int color) {
		this.color = color;
	}

	public long getUpdated() {
		return updated;
	}

	public void setUpdated(long updated) {
		this.updated = updated;
	}

	public String getgID() {
		return gID;
	}

	public void setgID(String gID) {
		this.gID = gID;
	}
}
