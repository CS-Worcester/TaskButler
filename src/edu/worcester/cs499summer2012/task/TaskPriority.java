/**
 * TaskPriority.java
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

public class TaskPriority implements Comparator<Task> {
	
	public static final int TRIVIAL = 0;
	public static final int NORMAL = 1;
	public static final int URGENT = 2;
	
	public int compare(Task lhs, Task rhs) {
		if (lhs.getPriority() == rhs.getPriority())
			return 0;
		else if (lhs.getPriority() < rhs.getPriority())
			return -1;
		else
			return 1;
	}
}
