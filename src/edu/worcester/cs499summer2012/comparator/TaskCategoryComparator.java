/*
 * TaskCategoryComparator.java
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
public class TaskCategoryComparator implements Comparator<Task> {

	/**
	 * Compares two tasks by category. Categories have no hierarchy and are
	 * ranked by category ID.
	 * @param lhs the first task
	 * @param rhs the second task
	 * @return A negative value if the first task if the first task is ordered 
	 *         before the second task, 0 if they are ordered the same, or a 
	 *         positive value if the second task is ordered before the first 
	 *         task
	 */
	public int compare(Task lhs, Task rhs) {
		return lhs.getCategory() - rhs.getCategory();
	}

}
