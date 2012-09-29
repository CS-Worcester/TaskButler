/*
 * TaskDateModifiedComparator.java
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
 * Comparator for sorting tasks by date modified.
 * @author Jonathan Hasenzahl
 */
public class TaskDateModifiedComparator implements Comparator<Task> {
	
	/**
	 * Compares two tasks by their date modified
	 * @param lhs the first task
	 * @param rhs the second task
	 * @return A negative value if the first task was modified more recently, a 
	 *         positive value if the second task was modified more recently, or 
	 *         0 if they were last modified at the same time
	 */	
	public int compare(Task lhs, Task rhs) {
		// Compare by date
		// Ex. LHS -> date 5000ms (earlier)
		//     RHS -> date 6000ms (later)
		//     LHS - RHS = 5000 - 6000 = -1000 = LHS ordered first
		return (int) (lhs.getDateModified() - rhs.getDateModified());
	}
}