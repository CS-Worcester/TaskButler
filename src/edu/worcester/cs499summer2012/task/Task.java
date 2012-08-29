/**
 * Task.java
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

/**
 * Represents a single task. A task contains information such as a name,
 * completion status, and due date. Tasks are parcelable so they can be
 * bundled with intents and passed between activities.
 * @author Jonathan Hasenzahl
 * @author James Celona
 */
public class Task implements Parcelable {
	
	/*
	 * Static fields and methods
	 */

	// Token indexes
	public static final int NAME = 0;
	public static final int IS_COMPLETED = 1;
	public static final int PRIORITY = 2;
	public static final int NOTES = 3;
	
	// Priority labels and indexes
	public static final String[] LABELS = {"Trivial", "Normal", "Urgent"};
	public static final int TRIVIAL = 0;
	public static final int NORMAL = 1;
	public static final int URGENT = 2;	

	// Token divider for file i/o
	private static final String DIV = "%DIV%";
	
	/**
	 * Creates a new task from a string. Used for reading tasks from a file.
	 * @param string the string to be parsed
	 * @return a new task derived from the parsed string
	 */
	public static Task parseTask(String string) {
		String[] tokens = string.split(DIV);
		
		String name = tokens[NAME];
		boolean is_completed = Boolean.parseBoolean(tokens[IS_COMPLETED]);
		int priority = Integer.parseInt(tokens[PRIORITY]);
		String notes;
		
		// Notes are optional, so there may not be any notes to parse
		if (tokens.length < 4)
			notes = null;
		else
			notes = tokens[NOTES];
		
		return new Task(name, is_completed, priority, notes);
	}	
	
	/*
	 * Class fields
	 */
	
	private String name;
	private boolean is_completed;
	private int priority;
	private String notes;
	

	/**
	 * Default constructor. Creates a new task based on the supplied arguments.
	 * @param task_name the name of the task
	 * @param is_completed whether or not the task is completed
	 * @param priority the priority of the task
	 * @param notes task notes
	 */
	public Task(String task_name, boolean is_completed, int priority, String notes) {
		this.name = task_name;
		this.is_completed = is_completed;
		this.priority = priority;
		this.notes = notes;
	}
	
	/**
	 * Copy constructor.
	 * @param task the Task to be copied
	 */
	public Task(Task task) {
		this(task.getName(), task.getIsCompleted(), task.getPriority(), task.getNotes());
	}
	
	/*
	 * Class methods 
	 */ 	
	
	@Override
	/**
	 * Compares this object to another. To return true, the compared object must
	 * have the same class and identical private fields.
	 * @param o the object to be compared with
	 * @return true if the objects are equal, false otherwise
	 */
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null)
			return false;
		if (o.getClass() != this.getClass())
			return false;
		
		Task t = (Task) o;
		if (!this.name.equals(t.name))
			return false;
		if (this.is_completed != t.is_completed)
			return false;
		if (this.priority != t.priority)
			return false;
		if (!this.notes.equals(t.notes))
			return false;
		
		return true;
	}
	
	@Override
	/**
	 * Returns a string representation of the class. Used for writing to file.
	 * return a string representation of the class
	 */
	public String toString() {
		String to_string = name + DIV + Boolean.toString(is_completed) + DIV + 
				Integer.toString(priority);
		
		// Notes are optional, so only write to file if they exist
		if (notes != null)
			to_string += DIV + notes;
		
		return to_string;
	}

	/*
	 * Parcelable methods
	 */
	/**
	 * Empty & unused method. Required for implementing Parcelable.
	 * @return 0
	 * @see Parcelable
	 */
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * Converts task to a parcel.
	 * @param out the parcel the task will be written to
	 * @param flags unused
	 * @see Parcelable
	 */
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
		if (priority >= TRIVIAL && priority <= URGENT)
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
	
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;		
	}
}

