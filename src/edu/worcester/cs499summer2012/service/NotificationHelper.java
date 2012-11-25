/*
 * NotificationHelper.java
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

package edu.worcester.cs499summer2012.service;

import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.activity.ViewTaskActivity;
import edu.worcester.cs499summer2012.database.TasksDataSource;
import edu.worcester.cs499summer2012.task.Task;

import android.app.Notification;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Creates Notifications using NotificationCompat to allow for
 * comparability though different API levels
 * 
 * @author Dhimitraq Jorgji
 *
 */
public class NotificationHelper{
	/**
	 * Basic Text Notification for Task Butler, using NotificationCompat
	 * @param context 
	 * @param id id of task, call task.getID() and pass it to this parameter
	 */
	public void sendBasicNotification(Context context, int id) {
		TasksDataSource db = TasksDataSource.getInstance(context);
		Task task = db.getTask(id);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
			.setContentText(task.getName())
			.setContentTitle("Task Butler")
			.setSmallIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? 
					R.drawable.ic_notification : R.drawable.ic_notification_deprecated)
			.setAutoCancel(true)
			.setContentIntent(getPendingIntent(context, id))
			.setWhen(System.currentTimeMillis())
			.setDefaults(Notification.DEFAULT_ALL);
		Notification notification = builder.getNotification();
		NotificationManager notificationManager = getNotificationManager(context);
		notificationManager.notify(task.getID(), notification);
	}
	
	public void sendPersistentNotification(Context context, int id) {
		TasksDataSource db = TasksDataSource.getInstance(context);
		Task task = db.getTask(id);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
			.setContentText(task.getName())
			.setContentTitle("Task Butler")
			.setSmallIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? 
					R.drawable.ic_notification : R.drawable.ic_notification_deprecated)
			.setAutoCancel(true)
			.setContentIntent(getPendingIntent(context, id))
			.setWhen(System.currentTimeMillis())
			.setOngoing(true)
			.setDefaults(Notification.DEFAULT_ALL);
		Notification notification = builder.getNotification();
		NotificationManager notificationManager = getNotificationManager(context);
		notificationManager.notify(task.getID(), notification);
	}
	//get a PendingIntent
	PendingIntent getPendingIntent(Context context, int id) {
		Intent intent =  new Intent(context, ViewTaskActivity.class)
			.putExtra(Task.EXTRA_TASK_ID, id);
		return PendingIntent.getActivity(context, id, intent, 0);
	}
	//get a NotificationManager
	NotificationManager getNotificationManager(Context context) {
		return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

}
