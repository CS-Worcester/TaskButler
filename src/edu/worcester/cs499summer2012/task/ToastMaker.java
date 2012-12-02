/*
 * ToastMaker.java
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

package edu.worcester.cs499summer2012.task;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.text.format.DateFormat;

/**
 * Helper class that provides methods to build Strings used in Toast notifications
 * in various activities.
 * @author Jon
 *
 */
public class ToastMaker {
	
	/**
	 * Generates a string that can be used to inform the user the next time a
	 * repeating task is due.
	 * @param context The context of the calling activity.
	 * @param resId The resource ID of the prefix string
	 * @param cal A Calendar set to the time of the next due date.
	 * @return a String for a Toast notification
	 */
	public static String getRepeatMessage(Context context, int resId, Calendar cal) {
		Calendar current_date = GregorianCalendar.getInstance();
		StringBuilder message = new StringBuilder();
		message.append(context.getString(resId));
		
		if (cal.get(Calendar.YEAR) > current_date.get(Calendar.YEAR)) {
			// Due date is in a future year
			message.append(DateFormat.format(" MMMM d, yyyy'.'", cal));
		} else if (cal.get(Calendar.DAY_OF_YEAR) - current_date.get(Calendar.DAY_OF_YEAR) > 6) {
			// Due date is more than a week away
			message.append(DateFormat.format(" MMMM d'.'", cal));
		} else if (cal.get(Calendar.DAY_OF_YEAR) > current_date.get(Calendar.DAY_OF_YEAR)) {
			// Due date is after today
			message.append(DateFormat.format(" EEEE 'at' h:mmaa'.'", cal));
		} else {
			// Due date is today
			message.append(DateFormat.format(" 'today at' h:mmaa'.'", cal));
		}
		
		return message.toString();
	}

}
