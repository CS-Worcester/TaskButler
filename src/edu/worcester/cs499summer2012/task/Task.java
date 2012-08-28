/**
 * Task.java
 * 
 * @file
 * The class that actually constructs the task itself
 * 
 * @author Jonathan Hasenzahl
 * @author James Celona
 * @param  
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
	 * Static fields and methods
	 */
	
	public static final int NAME = 0;
	public static final int IS_COMPLETED = 1;
	public static final int PRIORITY = 2;
	public static final int NOTES = 0;
	private static final String DIV = "%%";  //assuming no one ever has %% in their task
	public static Task parseTask(String string) {
		String[] tokens = string.split(DIV);
		return new Task(tokens[NAME], 
				Boolean.parseBoolean(tokens[IS_COMPLETED]),
    			Integer.parseInt(tokens[PRIORITY]),
    			tokens[NOTES]);	
	}	
	
	/*
	 * Class fields
	 */
	
	private String name;
	private boolean is_completed;
	private int priority;
	private String notes;
	
	/**
	 * Constructors 
	 */ 
	
	public Task() {
		this("Untitled task", false, TaskPriority.NORMAL, "No Description Available");
	}

	public Task(String task_name) {
		this(task_name, false, TaskPriority.NORMAL, null);
	}
	
	public Task(String task_name, boolean is_completed, int priority, String notes) {
		this.name = task_name;
		this.is_completed = is_completed;
		this.priority = priority;
		this.notes = notes;
	}
	
	public Task(Task task) {
		this(task.getName(), task.getIsCompleted(), task.getPriority(), task.getNotes());
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
		return new String(name + DIV + Boolean.toString(is_completed) + DIV + 
				Integer.toString(priority)+ notes);
	}

	/*
	 * Parcelable methods
	 */
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(name);
		out.writeString(Boolean.toString(is_completed));
		out.writeInt(priority);
		out.writeString(notes);
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
		is_completed = Boolean.parseBoolean(in.readString());
		priority = in.readInt();
		notes = in.readString();
	}
	
	
	/**
	 * 
	 * @return the name of the task.
	 */
	public String getName() {
		return name;
	}
	/**
	 * 
	 * @param task_name the name of the task.
	 */
	
	public void setName(String task_name) {
		this.name = task_name;
	}
	
	/**
	 * @return the priority of the task.
	 */
	public int getPriority() {
		return priority;
	}
	/**
	 * 
	 * @param priority the priority of the task.
	 */
	public void setPriority(int priority) {
		if (priority >= TaskPriority.TRIVIAL && priority <= TaskPriority.URGENT)
			this.priority = priority;
	}
	/**
	 * 
	 * @return weather the task is completed or not
	 */
	
	public boolean getIsCompleted() {
		return is_completed;
	}
	/**
	 * 
	 * @param is_completed boolean value representing the completion of a task.
	 */
	public void setIsCompleted(boolean is_completed) {
		this.is_completed = is_completed;
	}
	
	/**
	 * Toggle the task as completed if not already.
	 */
	
	public void toggleIsCompleted() {
		is_completed = is_completed ? false : true;
	}

	/**
	 * accessor methods for notes
	 * @return the notes from the app
	 */
	
	public String getNotes() {
		return notes;
	}
	/**
	 * setter for notes.
	 * @param notes a string that describes the task that is going to be attempted.
	 */
	public void setNotes(String notes) {
			this.notes = notes;
		
	}
}

