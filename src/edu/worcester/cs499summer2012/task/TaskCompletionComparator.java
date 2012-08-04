/**
 * TaskCompletionComparator.java
 * 
 * @file
 * Simple class to confirm completion of a task.
 * @author Jonathan Hasenzahl
 * @author James Celona
 * @verion 1.0 dev
 * 
 * 
 * Copyright 2012 Jonathan Hasenzahl
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

import java.util.Comparator;


public class TaskCompletionComparator implements Comparator<Task> {

	/**
	 * @param lhs The left hand side boolean value of a task
	 * @param rhs the left hand side boolean value of a task
	 * 
	 */
	public int compare(Task lhs, Task rhs) {
		if (!lhs.getIsCompleted() && rhs.getIsCompleted())
			return -1;
		else if (lhs.getIsCompleted() && !rhs.getIsCompleted())
			return 1;
		else
			return 0;
	}

}
