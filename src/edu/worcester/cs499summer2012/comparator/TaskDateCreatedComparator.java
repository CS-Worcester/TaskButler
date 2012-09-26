/*
 * TaskDateCreatedComparator.java
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
 * Comparator for sorting tasks by creation date.
 * @author Jonathan Hasenzahl
 */
public class TaskDateCreatedComparator implements Comparator<DeprecatedTask> {
	
	/**
	 * Compares two tasks by their creation date
	 * @param lhs the first task
	 * @param rhs the second task
	 * @return 0 if the creation dates are the same, -1 if the first task was
	 *         created first, or 1 if the second task was created first
	 */	
	public int compare(DeprecatedTask lhs, DeprecatedTask rhs) {
		return lhs.getDateCreated().compareTo(rhs.getDateCreated());
	}
}