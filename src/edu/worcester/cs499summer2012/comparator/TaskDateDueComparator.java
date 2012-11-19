/*
 * TaskDateDueComparator.java
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
 * Comparator for sorting tasks by due date.
 * @author Jonathan Hasenzahl
 */
public class TaskDateDueComparator implements Comparator<Task> {
	
	/**
	 * Compares two tasks by their due dates. Tasks with no due date are ordered
	 * after those with a due date.
	 * @param lhs the first task
	 * @param rhs the second task
	 * @return A negative value if the first task is due first, a positive
	 *         value if the second task is due first, or 0 if they are due at
	 *         the same time
	 */	
	public int compare(Task lhs, Task rhs) {
		if (!lhs.hasDateDue()) {
			// Case 1: lhs has no due date
			if (rhs.hasDateDue()) {
				// Case 1a: rhs has a due date and is ordered first
				return 1;
			} else {
				// Case 2a: rhs has no due date and they are equal
				return 0;
			}
		}
		else {
			// Case 2: lhs has a due date
			if (!rhs.hasDateDue()) {
				// Case 2a: rhs has no due date and is ordered second
				return -1;
			}
			else {
				// Case 2b: rhs has a due date
				// Ex. LHS -> date 5000ms (earlier)
				//     RHS -> date 6000ms (later)
				//     LHS - RHS = 5000 - 6000 = -1000 = LHS ordered first
				return (int) (lhs.getDateDue() - rhs.getDateDue());
			}
		}
	}
}