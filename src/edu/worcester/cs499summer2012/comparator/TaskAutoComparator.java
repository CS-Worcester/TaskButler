/*
 * TaskAutoComparator.java
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

package edu.worcester.cs499summer2012.comparator;

import java.util.Comparator;

import edu.worcester.cs499summer2012.task.Task;

/**
 * Comparator for auto-sorting tasks.
 * @author Jonathan Hasenzahl
 */
public class TaskAutoComparator implements Comparator<Task> {

	/**
	 * This comparator will sort a task by the following method:
	 *     1. Compare by completion status. An uncompleted task is ranked 
	 *        before a completed task. If completion statuses are the same:
	 *     2. Compare by due date. A task with an earlier due date 
	 *        is ranked before a task with a later due date. A task with a due
	 *        date is ranked before a task without a due date. If due dates are
	 *        the same:
	 *     3. Compare by priority. Urgent tasks are ranked before normal tasks 
	 *        which are ranked before trivial tasks. If priorities are the
	 *        same:
	 *     4. Compare by creation date. Tasks with an earlier creation date are
	 *        ranked before tasks with a later creation date.
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
		
		// Step 2: Compare by due date
		if (!lhs.hasDateDue()) {
			// Case 1: lhs has no due date
			if (rhs.hasDateDue()) {
				// Case 1a: rhs has a due date and is ordered first
				return 1;
			}
		}
		else {
			// Case 2: lhs has a due date
			if (!rhs.hasDateDue()) {
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
					long diff = lhs.getDateDue() - rhs.getDateDue();
					
					return diff < 0 ? -1 : 1;
				}
			}
		}
		
		// Step 3: Compare by priority
		// Ex: LHS -> priority 2 (urgent)
		//     RHS -> priority 1 (normal)
		//     RHS - LHS = 1 - 2 = -1 = LHS ordered first
		if (lhs.getPriority() != rhs.getPriority())
			return rhs.getPriority() - lhs.getPriority();		
		
		// Step 4: Compare by creation date
		// Ex. LHS -> date 5000ms (earlier)
		//     RHS -> date 6000ms (later)
		//     LHS - RHS = 5000 - 6000 = -1000 = RHS ordered first
		long diff = lhs.getDateCreated() - rhs.getDateCreated();
		
		if (diff < 0)
			return 1;
		
		if (diff > 0)
			return -1;
		
		return 0;
	}

}
