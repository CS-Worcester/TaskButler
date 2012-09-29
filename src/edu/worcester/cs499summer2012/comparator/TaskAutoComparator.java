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

import edu.worcester.cs499summer2012.task.Task;

/**
 * Comparator for auto-sorting tasks.
 * @author Jonathan Hasenzahl
 */
public class TaskAutoComparator implements Comparator<Task> {

	/**
	 * Compares two tasks, first by completion status. An uncompleted task is
	 * ranked before a completed task. If completion statuses are the same, the
	 * tasks are next ranked by priority. Urgent tasks are ranked before
	 * normal tasks which are ranked before trivial tasks. If priorities are the
	 * same, tasks are next ranked by due date. A task with an earlier due date 
	 * is ranked before a task with a later due date. If due dates are the same,
	 * the tasks are ranked by category. Categories have no hierarchy and are
	 * ranked by their ID. If categories are the same, tasks are finally ranked 
	 * by the alphabetic order of their names.
	 * @param lhs the first task
	 * @param rhs the second task
	 * @return A negative value if the first task if the first task is ordered 
	 *         before the second task, 0 if they are ordered the same, or a 
	 *         positive value if the second task is ordered before the first 
	 *         task
	 */
	public int compare(Task lhs, Task rhs) {
		// Step 1: Compare by completion status
		if (!lhs.isCompleted() && rhs.isCompleted())
			return -1;
		if (lhs.isCompleted() && !rhs.isCompleted())
			return 1;
		
		// Step 2: Compare by priority
		// Ex: LHS -> priority 2 (urgent)
		//     RHS -> priority 1 (normal)
		//     RHS - LHS = 1 - 2 = -1 = LHS ordered first
		if (lhs.getPriority() != rhs.getPriority())
			return rhs.getPriority() - lhs.getPriority();		
		
		// Step 3: Compare by due date
		if (lhs.getDateDue() == 0) {
			// Case 1: lhs has no due date
			if (rhs.getDateDue() > 0) {
				// Case 1a: rhs has a due date and is ordered first
				return 1;
			}
		}
		else {
			// Case 2: lhs has a due date
			if (rhs.getDateDue() == 0) {
				// Case 2a: rhs has no due date and is ordered second
				return -1;
			}
			else {
				// Case 2b: rhs has a due date and the due dates must be
				// checked for equality
				if (lhs.getDateDue() != rhs.getDateDue()) {
					// Due dates are not equal, now we can compare them
					// Ex. LHS -> date 5000ms (earlier)
					//     RHS -> date 6000ms (later)
					//     LHS - RHS = 5000 - 6000 = -1000 = LHS ordered first
					return (int) (lhs.getDateDue() - rhs.getDateDue());
				}
			}
		}
		
		// Step 4: Compare by category
		if (lhs.getCategory() != rhs.getCategory())
			return lhs.getCategory() - rhs.getCategory();
				
		// Step 5: Compare by name
		return lhs.getName().compareToIgnoreCase(rhs.getName());
	}

}
