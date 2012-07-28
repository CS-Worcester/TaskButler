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

package edu.worcester.cs499summer2012;

import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable {
	
	// Class methods
	
	public Task() {
		task_name = "Untitled task";
	}

	public Task(String task_name) {
		this.task_name = task_name;
	}
	
	public String toString() {
		return task_name;
	}
	
	public void setTaskName(String task_name) {
		this.task_name = task_name;
	}
	
	public String getTaskName() {
		return task_name;
	}
	
	private String task_name;

	// Methods for implementing Parcelable
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(task_name);		
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
		task_name = in.readString();
	}
}
