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
import edu.worcester.cs499summer2012.activity.HandleNotificationActivity;
import edu.worcester.cs499summer2012.activity.ViewTaskActivity;
import edu.worcester.cs499summer2012.database.TasksDataSource;
import edu.worcester.cs499summer2012.task.Task;

import android.app.Notification;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
		Log.d("my notification method","beginning of sendBasicNotification");
		TasksDataSource db = TasksDataSource.getInstance(context);
		Task task = db.getTask(id);
		Intent intent =  new Intent(context, ViewTaskActivity.class);
		intent.putExtra("edu.worcester.cs499summer2012.TASK", task);
		
		PendingIntent pi = PendingIntent.getActivity(context, task.getID(), intent, 0);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
			.setContentText(task.getName())
			.setContentTitle("Task Butler")
			.setSmallIcon(R.drawable.ic_launcher)
			.setAutoCancel(true)
			.setContentIntent(pi)
			.setWhen(System.currentTimeMillis())
			.setDefaults(Notification.DEFAULT_ALL);
		Notification notification = builder.getNotification();

		NotificationManager notificationManager = getNotificationManager(context);
		notificationManager.notify(task.getID(), notification);

		Log.d("my notification method","after building notification"+Notification.DEFAULT_ALL);
	}
	//use this to make pending intents so that they match 100% 
	PendingIntent getPendingIntent(Context context) {
		return PendingIntent.getActivity(context, 0, new Intent(context,
				ViewTaskActivity.class), 0);
	}
	//get a NotificationManager
	NotificationManager getNotificationManager(Context context) {
		return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

}
