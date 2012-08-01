/**
 * Task.java
 * 
 * Copyright 2012 Jonathan Hasenzahl
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

import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable {
	
	private String name;
	private int priority;
	private boolean is_completed;
	
	/* Class methods */
	
	public Task() {
		name = "Untitled task";
		priority = TaskPriority.NORMAL;
		is_completed = false;
	}

	public Task(String task_name) {
		this.name = task_name;
		priority = TaskPriority.NORMAL;
		is_completed = false;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null)
			return false;
		if (o.getClass() != this.getClass())
			return false;
		else {
			Task t = (Task) o;
			return this.name.equals(t.name);
		}
	}

	/*
	 * Parcelable Methods
	 */
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(name);		
	}
	
	public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
		public Task createFromParcel(Parcel in) {
			return new Task(in);
		}
		
		public Task[] newArray(int size) {
			return new Task[size];
		}
	};
	
	private Task(Parcel in) {
		name = in.readString();
	}
	
	/*
	 * Getters and Setters
	 */
	
	public String getName() {
		return name;
	}
	
	public void setName(String task_name) {
		this.name = task_name;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		if (priority >= TaskPriority.TRIVIAL && priority <= TaskPriority.URGENT)
			this.priority = priority;
	}
	
	public boolean getIsCompleted() {
		return is_completed;
	}
	
	public void setIsCompleted(boolean is_completed) {
		this.is_completed = is_completed;
	}
	
	public void toggleIsCompleted() {
		is_completed = is_completed ? false : true;
	}
}
