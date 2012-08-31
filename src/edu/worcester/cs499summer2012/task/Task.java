/*
 * Task.java
 * 
 * Copyright 2012 Jonathan Hasenzahl, James Celona
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

import java.util.Calendar;
import java.util.GregorianCalendar;

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
	
	/**************************************************************************
	 * Static fields and methods                                              *
	 **************************************************************************/
	
	// Priority labels and indexes
	public static final String[] LABELS = {"Trivial", "Normal", "Urgent"};
	public static final int TRIVIAL = 0;
	public static final int NORMAL = 1;
	public static final int URGENT = 2;	
	
	// Token indexes
	public static final int NAME = 0;
	public static final int IS_COMPLETED = 1;
	public static final int PRIORITY = 2;
	public static final int DATE_CREATED = 3;
	public static final int DATE_DUE = 4;
	public static final int NOTES = 5;
	public static final int YEAR = 0;
	public static final int MONTH = 1;
	public static final int DAY = 2;
	public static final int HOUR = 3;
	public static final int MINUTE = 4;

	// Token dividers for file i/o
	private static final String DIV = "%D%";
	private static final String DATE_DIV = "%C%";
	private static final String NO_DATA = "%N%";
	
	/**
	 * Creates a new task from a string. Used for reading tasks from a file.
	 * @param string the string to be parsed
	 * @return a new task derived from the parsed string
	 */
	public static Task taskFromString(String string) {
		String[] tokens = string.split(DIV);
		Task task = new Task();
		
		task.name = tokens[NAME];
		task.is_completed = Boolean.parseBoolean(tokens[IS_COMPLETED]);
		task.priority = Integer.parseInt(tokens[PRIORITY]);
		task.date_created = calendarFromString(tokens[DATE_CREATED]);
		task.date_due = calendarFromString(tokens[DATE_DUE]);
		task.notes = notesFromString(tokens[NOTES]);
		
		return task;
	}
	
	/**
	 * Creates a new calendar object from a string. Used for reading tasks from
	 * a file.
	 * @param string the string to be parsed
	 * @return a new calendar object derived from the parsed string, or null if
	 *         there was no data
	 */
	private static Calendar calendarFromString(String string)
	{
		if (string.equals(NO_DATA))
			return null;
		
		String[] tokens = string.split(DATE_DIV);
		Calendar calendar = new GregorianCalendar();
		calendar.set(Integer.parseInt(tokens[YEAR]), 
				Integer.parseInt(tokens[MONTH]), 
				Integer.parseInt(tokens[DAY]), 
				Integer.parseInt(tokens[HOUR]), 
				Integer.parseInt(tokens[MINUTE]));
		
		return calendar;
	}
	
	private static String notesFromString(String string) {
		if (string.equals(NO_DATA))
			return null;
		else
			return string;
	}
	
	private static String calendarToString(Calendar calendar) {
		if (calendar == null)
			return NO_DATA;
		
		return calendar.get(Calendar.YEAR) + DATE_DIV + 
				calendar.get(Calendar.MONTH) + DATE_DIV +
				calendar.get(Calendar.DAY_OF_MONTH) + DATE_DIV +
				calendar.get(Calendar.HOUR) + DATE_DIV +
				calendar.get(Calendar.MINUTE);
	}
	
	private static String notesToString(String string) {
		if (string == null)
			return NO_DATA;
		else
			return string;
	}
	
	/**************************************************************************
	 * Private fields                                                         *
	 **************************************************************************/
	
	private String name;
	private boolean is_completed;
	private int priority;
	private Calendar date_created;
	private Calendar date_due;
	private String notes;
	
	/**************************************************************************
	 * Constructors                                                           *
	 **************************************************************************/

	/**
	 * Default constructor. Creates an empty task.
	 */
	public Task() {}
	
	/**
	 * Copy constructor.
	 * @param task the Task to be copied
	 */
	public Task(Task task) {
		name = task.name;
		is_completed = task.is_completed;
		priority = task.priority;
		date_created = task.date_created;
		date_due = task.date_due;
		notes = task.notes;
	}
	
	/**************************************************************************
	 * Overridden parent methods                                              *
	 **************************************************************************/ 	
	
	/**
	 * Compares this object to another. To return true, the compared object must
	 * have the same class and identical private fields.
	 * @param o the object to be compared with
	 * @return true if the objects are equal, false otherwise
	 */
	@Override
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
		if (this.date_created != t.date_created)
			return false;
		if (this.date_due != t.date_due)
			return false;
		if (!this.notes.equals(t.notes))
			return false;
		
		return true;
	}
	
	/**
	 * Returns a string representation of the class. Used for writing to file.
	 * The order of the fields in the string is:
	 *     Name
	 *     Is completed
	 *     Priority
	 *     Date created
	 *     Date due
	 *     Notes
	 * @return a string representation of the class
	 */
	@Override
	public String toString() {
		return name + DIV + Boolean.toString(is_completed) + DIV + 
				Integer.toString(priority) + DIV + 
				calendarToString(date_created) + DIV +
				calendarToString(date_due) + DIV + notesToString(notes);
	}
	


	/**************************************************************************
	 * Methods implementing Parcelable interface                              *
	 **************************************************************************/
	
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
		out.writeString(calendarToString(date_created));
		out.writeString(calendarToString(date_due));
		out.writeString(notesToString(notes));
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
		date_created = calendarFromString(in.readString());
		date_due = calendarFromString(in.readString());
		notes = notesFromString(in.readString());
	}
	
	/**************************************************************************
	 * Getters and setters                                                    *
	 **************************************************************************/
	
	public String getName() {
		return name;
	}
	
	public boolean getIsCompleted() {
		return is_completed;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public Calendar getDateCreated() {
		return date_created;
	}
	
	public Calendar getDateDue() {
		return date_due;
	}
	
	public String getNotes() {
		return notes;
	}
	
	public Task setName(String task_name) {
		this.name = task_name;
		return this;
	}
	
	public Task setIsCompleted(boolean is_completed) {
		this.is_completed = is_completed;
		return this;
	}
	
	public Task toggleIsCompleted() {
		is_completed = is_completed ? false : true;
		return this;
	}
	
	public Task setPriority(int priority) {
		if (priority >= TRIVIAL && priority <= URGENT)
			this.priority = priority;
		return this;
	}
	
	public Task setCreationDate(Calendar date_created)
	{
		this.date_created = date_created;
		return this;
	}
	
	public Task setDueDate(Calendar date_due)
	{
		this.date_due = date_due;
		return this;
	}

	public Task setNotes(String notes) {
		this.notes = notes;
		return this;
	}
}

