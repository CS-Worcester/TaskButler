/* TaskAlarm.java
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

import edu.worcester.cs499summer2012.database.TasksDataSource;
import edu.worcester.cs499summer2012.task.Task;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


/**
 * Wrapper Class for setRepeatingAlarm(), cancelAlarm(), setOnetimeAlarm()
 * @author Dhimitraq Jorgji
 */
public class TaskAlarm {
	/**
	 * Cancel alarm using the task id, PendingIntent is created using the Task id
	 * @param context
	 * @param intent
	 */
    public void cancelAlarm(Context context, int id)
    {
    	PendingIntent pi = getPendingIntent(context, id);
    	AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    	alarmManager.cancel(pi);
    	pi.cancel();
    }
    
    /**
     * Set a One Time Alarm using the taskID
     * @param context
     * @param id id of task to retrieve task from SQLite database
     */
    public void setOnetimeAlarm(Context context, int id){
		TasksDataSource db = TasksDataSource.getInstance(context);
		Task task = db.getTask(id);
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, task.getDateDue(), getPendingIntent(context, id));
    }
    
    //get a PendingIntent 
	PendingIntent getPendingIntent(Context context, int id) {
		Intent intent =  new Intent(context, OnAlarmReceiver.class)
			.putExtra(Task.EXTRA_TASK_ID, id);
		return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
}
