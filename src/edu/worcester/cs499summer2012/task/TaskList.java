/*
 * TaskList.java
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

import java.util.ArrayList;
import java.util.Comparator;

import edu.worcester.cs499summer2012.comparator.TaskCompletionComparator;
import edu.worcester.cs499summer2012.comparator.TaskDateCreatedComparator;
import edu.worcester.cs499summer2012.comparator.TaskDateDueComparator;
import edu.worcester.cs499summer2012.comparator.TaskNameComparator;
import edu.worcester.cs499summer2012.comparator.TaskPriorityComparator;

/**
 * Wrapper class for ArrayList&#60;Task&#62;
 * @author Jonathan Hasenzahl
 */
public class TaskList extends ArrayList<Task> {

	/**************************************************************************
	 * Static fields and methods                                              *
	 **************************************************************************/
	
	private final Comparator<Task> name_comparator = new TaskNameComparator();
	private final Comparator<Task> completion_comparator = new TaskCompletionComparator();
	private final Comparator<Task> priority_comparator = new TaskPriorityComparator();
	private final Comparator<Task> date_created_comparator = new TaskDateCreatedComparator();
	private final Comparator<Task> date_due_comparator = new TaskDateDueComparator();
	private static final long serialVersionUID = 1L; // Not used
	
	/**************************************************************************
	 * Constructors                                                           *
	 **************************************************************************/
	
	/**
	 * Default constructor. Creates an empty TaskList with 0 elements.
	 */
	public TaskList() {
		super(0);
	}
	
	/**
	 * Copy constructor.
	 * @param task_list the TaskList to be copied
	 */
	public TaskList(TaskList task_list) {
		this();
		for (Task task : task_list) {
			super.add(new Task(task));
		}
		
	}
	
	/**************************************************************************
	 * Class methods                                                          *
	 **************************************************************************/
	
	public Comparator<Task> nameComparator() {
		return name_comparator;
	}
	
	public Comparator<Task> completionComparator() {
		return completion_comparator;
	}
	
	public Comparator<Task> priorityComparator() {
		return priority_comparator;
	}
	
	public Comparator<Task> dateCreatedComparator() {
		return date_created_comparator;
	}
	
	public Comparator<Task> dateDueComparator() {
		return date_due_comparator;
	}

}
