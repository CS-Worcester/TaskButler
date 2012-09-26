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


/**
 * Defines a Task object (name, complition, priority, date created, due date, notes), 
 * provides multiple constructors as well as mutators, it has its own defined toString() and equals()
 * @author Dhimitraq Jorgji
 * @author Jonathan Hasenzahl
 * @author James Celona
 */


public class Task {

	//private variables
	private int id;
	private String name;
	private boolean isCompleted;
	private int priority;
	private int dateCreated;
	private int dueDate;
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
		isCompleted = task.isCompleted;
		priority = task.priority;
		dateCreated = task.dateCreated;
		dueDate = task.dueDate;
		notes = task.notes;
	}

	/**
	 * Designated constructor
	 * @param name
	 * @param isCompleted
	 * @param priority
	 * @param date_created
	 * @param date_due
	 * @param notes
	 */
	public Task(String name, boolean isCompleted, int priority, 
			int date_created, int date_due, String notes){
		this.name = name;
		this.isCompleted = isCompleted;
		this.priority = priority;
		this.dateCreated = date_created;
		this.dueDate = date_due;
		this.notes = notes;
	}

	/**
	 * Designated constructor
	 * @param id
	 * @param name
	 * @param isCompleted
	 * @param priority
	 * @param date_created
	 * @param date_due
	 * @param notes
	 */
	public Task(int id, String name, boolean isCompleted, int priority, 
			int date_created, int date_due, String notes){
		this.id = id;
		this.name = name;
		this.isCompleted = isCompleted;
		this.priority = priority;
		this.dateCreated = date_created;
		this.dueDate = date_due;
		this.notes = notes;
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
		if (this.isCompleted != t.isCompleted)
			return false;
		if (this.priority != t.priority)
			return false;
		if (this.dateCreated != t.dateCreated)
			return false;
		if (this.dueDate != t.dueDate)
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
		return name + " " +  Boolean.toString(isCompleted)+ " "
				+Integer.toString(priority) + " "
				+ dateCreated + " " + dueDate +  " "
				+ notes;
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

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getDate_created() {
		return dateCreated;
	}

	public void setDate_created(int date_created) {
		this.dateCreated = date_created;
	}

	public int getDate_due() {
		return dueDate;
	}

	public void setDate_due(int date_due) {
		this.dueDate = date_due;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Task toggleIsCompleted() {
		isCompleted = isCompleted ? false : true;
		return this;
	}
}
