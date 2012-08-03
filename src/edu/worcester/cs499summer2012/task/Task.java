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
	
	/*
	 * Static fields
	 */
	
	public static final int NAME = 0;
	public static final int IS_COMPLETED = 1;
	public static final int PRIORITY = 2;
	
	public static Task getTaskFromString(String string) {
		String[] tokens = string.split(",");
		return new Task(tokens[NAME], 
				Boolean.parseBoolean(tokens[IS_COMPLETED]),
    			Integer.parseInt(tokens[PRIORITY]));
	}
	
	/*
	 * Class fields
	 */
	
	private String name;
	private boolean is_completed;
	private int priority;
	
	/*
	 * Constructors 
	 */ 
	
	public Task() {
		this("Untitled task", false, TaskPriority.NORMAL);
	}

	public Task(String task_name) {
		this(task_name, false, TaskPriority.NORMAL);
	}
	
	public Task(String task_name, boolean is_completed, int priority) {
		this.name = task_name;
		this.is_completed = is_completed;
		this.priority = priority;
	}
	
	public Task(Task task) {
		this(task.getName(), task.getIsCompleted(), task.getPriority());
	}
	
	/*
	 * Class methods 
	 */ 	

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
	
	@Override
	public String toString() {
		return new String(name + ',' + Boolean.toString(is_completed) + ',' + 
				Integer.toString(priority) + ',');
	}

	/*
	 * Parcelable methods
	 */
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(toString());
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
		this(getTaskFromString(in.readString()));
	}
	
	/*
	 * Getters and setters
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
