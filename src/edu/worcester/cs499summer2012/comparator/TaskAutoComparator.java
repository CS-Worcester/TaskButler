/*
 * TaskAutoComparator.java
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

package edu.worcester.cs499summer2012.comparator;

import java.util.Comparator;

import edu.worcester.cs499summer2012.task.DeprecatedTask;

/**
 * Comparator for auto-sorting tasks.
 * @author Jonathan Hasenzahl
 */
public class TaskAutoComparator implements Comparator<DeprecatedTask> {

	/**
	 * Compares two tasks, first by completion status. An uncompleted task is
	 * ranked before a completed task. If completion statuses are the same, the
	 * tasks are next ranked by due date. A task with an earlier due date is
	 * ranked before a task with a later due date. If due dates are the same,
	 * the tasks are next ranked by priority. Urgent tasks are ranked before
	 * normal tasks which are ranked before trivial tasks. If priorities are the
	 * same, tasks are finally ranked by the lexographical order of their
	 * names.
	 * @param lhs the first task
	 * @param rhs the second task
	 * @return -1 if the first task if the first task is ordered before the
	 * 		   second task, 0 if they are ordered the same, or 1 if the second
	 *         task is ordered before the first task
	 */
	public int compare(DeprecatedTask lhs, DeprecatedTask rhs) {
		// Compare by completion status
		if (!lhs.getIsCompleted() && rhs.getIsCompleted())
			return -1;
		if (lhs.getIsCompleted() && !rhs.getIsCompleted())
			return 1;
		
		// Compare by due date
		if (lhs.getDateDue() == null) {
			// Case 1: lhs is null
			if (rhs.getDateDue() != null) {
				// Case 1a: rhs is not null and is ordered first
				return 1;
			}
		}
		else {
			// Case 2: lhs is not null
			if (rhs.getDateDue() == null) {
				// Case 2a: rhs is null and is ordered second
				return -1;
			}
			else {
				// Case 2b: rhs is not null and the due dates must be
				// checked for equality
				if (!lhs.getDateDue().equals(rhs.getDateDue())) {
					// Due dates are not equal, now we can compare them
					return lhs.getDateDue().compareTo(rhs.getDateDue());
				}
			}
		}
		
		// Compare by priority
		if (lhs.getPriority() > rhs.getPriority())
			return -1;
		if (lhs.getPriority() < rhs.getPriority())
			return 1;
		
		// Compare by name
		return lhs.getName().compareToIgnoreCase(rhs.getName());
	}

}
