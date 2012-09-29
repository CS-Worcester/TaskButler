/*
 * TaskCompletionComparator.java
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
 * Comparator for sorting tasks by completion status.
 * @author Jonathan Hasenzahl
 */
public class TaskCompletionComparator implements Comparator<Task> {

	/**
	 * Compares two tasks by their completion status. Tasks that are
	 * unfinished come before tasks that are finished.
	 * @param lhs the first task
	 * @param rhs the second task
	 * @return -1 if the first task is unfinished and the second task is 
	 *         finished; 1 if the first task is finished and the second task is
	 *         unfinished; 0 otherwise 
	 */
	public int compare(Task lhs, Task rhs) {
		if (!lhs.isCompleted() && rhs.isCompleted())
			return -1;
		else if (lhs.isCompleted() && !rhs.isCompleted())
			return 1;
		else
			return 0;
	}

}
