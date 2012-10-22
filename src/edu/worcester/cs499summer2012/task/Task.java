/*
 * Task.java
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

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Defines a Task object (name, completion, priority, date created, due date, notes), 
 * provides multiple constructors as well as mutators, it has its own defined toString() and equals()
 * @author Dhimitraq Jorgji
 * @author Jonathan Hasenzahl
 * @author James Celona
 */


public class Task implements Parcelable {

	/**************************************************************************
	 * Static fields and methods                                              *
	 **************************************************************************/
	
	// Priority constants
	public static final String[] LABELS = {"Trivial", "Normal", "Urgent"};
	public static final int TRIVIAL = 0;
	public static final int NORMAL = 1;
	public static final int URGENT = 2;
	
	// Repeat constants
	public static final int MINUTES = 0;
	public static final int HOURS = 1;
	public static final int DAYS = 2;
	public static final int WEEKS = 3;
	public static final int MONTHS = 4;
	public static final int YEARS = 5;
	
	/**************************************************************************
	 * Private fields                                                         *
	 **************************************************************************/
	
	private int id;
	private String name;
	private boolean isCompleted;
	private int priority;
	private int category;
	private boolean hasDateDue;
	private boolean hasFinalDateDue;
	private boolean isRepeating;
	private boolean hasStopRepeatingDate;
	private int repeatType;
	private int repeatInterval;
	private long dateCreated;
	private long dateModified;
	private long dateDue;
	private long finalDateDue;
	private long stopRepeatingDate;
	private String notes;
	private Calendar dateCreatedCal;
	private Calendar dateModifiedCal;
	private Calendar dateDueCal;
	private Calendar finalDateDueCal;
	private Calendar stopRepeatingDateCal;


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
		// ID is not copied and kept unique so the copy will not replace the
		// original task
		name = task.name;
		isCompleted = task.isCompleted;
		priority = task.priority;
		category = task.category;
		hasDateDue = task.hasDateDue;
		hasFinalDateDue = task.hasFinalDateDue;
		isRepeating = task.isRepeating;
		hasStopRepeatingDate = task.hasStopRepeatingDate;
		repeatType = task.repeatType;
		repeatInterval = task.repeatInterval;
		dateCreated = task.dateCreated;
		dateModified = task.dateModified;
		dateDue = task.dateDue;
		finalDateDue = task.finalDateDue;
		stopRepeatingDate = task.stopRepeatingDate;
		notes = task.notes;
		
		updateDateCreatedCal();
		updateDateModifiedCal();
		updateDateDueCal();
		updateFinalDateDueCal();
		updateStopRepeatingDateCal();
	}

	/**
	 * Constructor, without ID and without modification date (new task)
	 * @param name
	 * @param isCompleted
	 * @param priority
	 * @param category
	 * @param hasDateDue
	 * @param hasFinalDateDue
	 * @param isRepeating
	 * @param hasStopRepeatingDate
	 * @param repeatType
	 * @param repeatInterval
	 * @param dateCreated
	 * @param dateDue
	 * @param finalDateDue
	 * @param stopRepeatingDate
	 * @param notes
	 */
	public Task(String name, boolean isCompleted, int priority, int category,
			boolean hasDateDue, boolean hasFinalDateDue, boolean isRepeating, 
			boolean hasStopRepeatingDate, int repeatType, int repeatInterval,
			long dateCreated, long dateDue, long finalDateDue, 
			long stopRepeatingDate, String notes) {
		this.name = name;
		this.isCompleted = isCompleted;
		this.priority = priority;
		this.category = category;
		this.hasDateDue = hasDateDue;
		this.hasFinalDateDue = hasFinalDateDue;
		this.isRepeating = isRepeating;
		this.hasStopRepeatingDate = hasStopRepeatingDate;
		this.repeatType = repeatType;
		this.repeatInterval = repeatInterval;
		this.dateCreated = dateCreated;
		this.dateModified = dateCreated; // New task has not been modified yet
		this.dateDue = dateDue;
		this.finalDateDue = finalDateDue;
		this.stopRepeatingDate = stopRepeatingDate;
		this.notes = notes;
		
		updateDateCreatedCal();
		updateDateModifiedCal();
		updateDateDueCal();
		updateFinalDateDueCal();
		updateStopRepeatingDateCal();
	}
	
	/**
	 * Constructor, with ID and with modification date (existing task)
	 * @param id
	 * @param name
	 * @param isCompleted
	 * @param priority
	 * @param category
	 * @param hasDateDue
	 * @param hasFinalDateDue
	 * @param isRepeating
	 * @param hasStopRepeatingDate
	 * @param repeatType
	 * @param repeatInterval
	 * @param dateCreated
	 * @param dateModified
	 * @param dateDue
	 * @param finalDateDue
	 * @param stopRepeatingDate
	 * @param notes
	 */
	public Task(int id, String name, boolean isCompleted, int priority, 
			int category, boolean hasDateDue, boolean hasFinalDateDue, 
			boolean isRepeating, boolean hasStopRepeatingDate, int repeatType, 
			int repeatInterval, long dateCreated, long dateModified, 
			long dateDue, long finalDateDue, long stopRepeatingDate, 
			String notes) {
		this(name, isCompleted, priority, category, hasDateDue,
				hasFinalDateDue, isRepeating, hasStopRepeatingDate, repeatType,
				repeatInterval, dateCreated, dateDue, finalDateDue, 
				stopRepeatingDate, notes);
		this.id = id;
		this.dateModified = dateModified;
	}

	/**************************************************************************
	 * Class methods                                                          *
	 **************************************************************************/ 
	
	private void updateDateCreatedCal() {
		if (dateCreatedCal == null)
			dateCreatedCal = new GregorianCalendar();
		
		dateCreatedCal.setTimeInMillis((long) dateCreated);
	}
	
	private void updateDateModifiedCal() {
		if (dateModifiedCal == null)
			dateModifiedCal = new GregorianCalendar();
		
		dateModifiedCal.setTimeInMillis((long) dateModified);
	}
	
	private void updateDateDueCal() {
		if (!hasDateDue)
		{
			dateDueCal = null;
			return;
		}
		
		if (dateDueCal == null)
			dateDueCal = new GregorianCalendar();
		
		dateDueCal.setTimeInMillis((long) dateDue);
	}
	
	private void updateFinalDateDueCal() {
		if (!hasFinalDateDue)
		{
			finalDateDueCal = null;
			return;
		}
		
		if (finalDateDueCal == null)
			finalDateDueCal = new GregorianCalendar();
		
		finalDateDueCal.setTimeInMillis((long) finalDateDue);
	}
	
	private void updateStopRepeatingDateCal() {
		if (!hasStopRepeatingDate)
		{
			stopRepeatingDateCal = null;
			return;
		}
		
		if (stopRepeatingDateCal == null)
			stopRepeatingDateCal = new GregorianCalendar();
		
		stopRepeatingDateCal.setTimeInMillis((long) stopRepeatingDate);
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
		// TODO: Needs to be updated
		if (o == this)
			return true;
		if (o == null)
			return false;
		if (o.getClass() != this.getClass())
			return false;

		Task t = (Task) o;
		if (!this.name.equals(t.name))
			return false;
		if (this.isCompleted != t.isCompleted)
			return false;
		if (this.priority != t.priority)
			return false;
		if (this.category != t.category)
			return false;
		if (this.dateCreated != t.dateCreated)
			return false;
		if (this.dateModified != t.dateModified)
			return false;
		if (this.dateDue != t.dateDue)
			return false;
		if (this.finalDateDue != t.finalDateDue)
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
		// TODO: (Jon) Reimplement this method in a more useful form
		return name;
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
		return 0;
	}
	
	/**
	 * Converts task to a parcel.
	 * @param out the parcel the task will be written to
	 * @param flags unused
	 * @see Parcelable
	 */
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(id);
		out.writeString(name);
		out.writeString(Boolean.toString(isCompleted));
		out.writeInt(priority);
		out.writeInt(category);
		out.writeString(Boolean.toString(hasDateDue));
		out.writeString(Boolean.toString(hasFinalDateDue));
		out.writeString(Boolean.toString(isRepeating));
		out.writeString(Boolean.toString(hasStopRepeatingDate));
		out.writeInt(repeatType);
		out.writeInt(repeatInterval);
		out.writeLong(dateCreated);
		out.writeLong(dateModified);
		out.writeLong(dateDue);
		out.writeLong(finalDateDue);
		out.writeLong(stopRepeatingDate);
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
		id = in.readInt();
		name = in.readString();
		isCompleted = Boolean.parseBoolean(in.readString());
		priority = in.readInt();
		category = in.readInt();
		hasDateDue = Boolean.parseBoolean(in.readString());
		hasFinalDateDue = Boolean.parseBoolean(in.readString());
		isRepeating = Boolean.parseBoolean(in.readString());
		hasStopRepeatingDate = Boolean.parseBoolean(in.readString());
		repeatType = in.readInt();
		repeatInterval = in.readInt();
		dateCreated = in.readLong();
		dateModified = in.readLong();
		dateDue = in.readLong();
		finalDateDue = in.readLong();
		stopRepeatingDate = in.readLong();
		notes = in.readString();
		
		updateDateCreatedCal();
		updateDateModifiedCal();
		updateDateDueCal();
		updateFinalDateDueCal();
		updateStopRepeatingDateCal();
	}
	
	/**************************************************************************
	 * Getters and setters                                                    *
	 **************************************************************************/	

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

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(boolean is_completed) {
		this.isCompleted = is_completed;
	}

	public void toggleIsCompleted() {
		isCompleted = isCompleted ? false : true;
	}
	
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public int getCategory() {
		return category;
	}
	
	public void setCategory(int category) {
		this.category = category;
	}
	
	public boolean hasDateDue() {
		return hasDateDue;
	}
	
	public void setHasDateDue(boolean hasDateDue) {
		this.hasDateDue = hasDateDue;
	}
	
	public boolean hasFinalDateDue() {
		return hasFinalDateDue;
	}
	
	public void setHasFinalDateDue(boolean hasFinalDateDue) {
		this.hasFinalDateDue = hasFinalDateDue;
	}
	
	public boolean isRepeating() {
		return isRepeating;
	}
	
	public void setIsRepeating(boolean isRepeating) {
		this.isRepeating = isRepeating;
	}
	
	public boolean hasStopRepeatingDate() {
		return hasStopRepeatingDate;
	}
	
	public void setHasStopRepeatingDate(boolean hasStopRepeatingDate) {
		this.hasStopRepeatingDate = hasStopRepeatingDate;
	}
	
	public int getRepeatType() {
		return repeatType;
	}
	
	public void setRepeatType(int repeatType) {
		if (repeatType >= 0 && repeatType <= 5)
			this.repeatType = repeatType;
	}
	
	public int getRepeatInterval() {
		return repeatInterval;
	}
	
	public void setRepeatInterval(int repeatInterval) {
		this.repeatInterval = repeatInterval;
	}
	
	public long getDateCreated() {
		return dateCreated;
	}
	
	public Calendar getDateCreatedCal() {
		return dateCreatedCal;
	}

	public void setDateCreated(long date_created) {
		this.dateCreated = date_created;
		updateDateCreatedCal();
	}
	
	public long getDateModified() {
		return dateModified;
	}
	
	public Calendar getDateModifiedCal() {
		return dateModifiedCal;
	}

	public void setDateModified(long date_modified) {
		this.dateModified = date_modified;
		updateDateModifiedCal();
	}

	public long getDateDue() {
		return dateDue;
	}
	
	public Calendar getDateDueCal() {
		return dateDueCal;
	}

	public void setDateDue(long date_due) {
		this.dateDue = date_due;
		updateDateDueCal();
	}
	
	public long getFinalDateDue() {
		return finalDateDue;
	}
	
	public Calendar getFinalDateDueCal() {
		return finalDateDueCal;
	}

	public void setFinalDateDue(long final_date_due) {
		this.finalDateDue = final_date_due;
		updateFinalDateDueCal();
	}
	
	public long getStopRepeatingDate() {
		return stopRepeatingDate;
	}
	
	public Calendar getStopRepeatingDateCal() {
		return stopRepeatingDateCal;
	}

	public void setStopRepeatingDate(long stopRepeatingDate) {
		this.stopRepeatingDate = stopRepeatingDate;
		updateStopRepeatingDateCal();
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
