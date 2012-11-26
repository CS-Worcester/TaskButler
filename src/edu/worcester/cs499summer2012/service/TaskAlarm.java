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

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import edu.worcester.cs499summer2012.database.TasksDataSource;
import edu.worcester.cs499summer2012.task.Task;


/**
 * Wrapper Class for setRepeatingAlarm(), cancelAlarm(), setOnetimeAlarm()
 * @author Dhimitraq Jorgji
 */
public class TaskAlarm {

	public static final String ALARM_EXTRA ="edu.worcester.cs499summer2012.TaskAlarm";
	public static final int REPEATING_ALARM = 1;
	public static final int PROCRASTINATOR_ALARM =2;

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
	public void setAlarm(Context context, int id){
		TasksDataSource db = TasksDataSource.getInstance(context);
		Task task = db.getTask(id);
		AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, task.getDateDue(), getPendingIntent(context, id));
	}

	/**
	 * Sets DateDue field to the next repeat cycle, you still need to call setAlarm()
	 * @param context
	 * @param id
	 */
	public Task setRepeatingAlarm(Context context, int id){
		TasksDataSource db = TasksDataSource.getInstance(context);
		Task task = db.getTask(id);    	
		Calendar cal = Calendar.getInstance();    	

		long newDateDue;
		switch(task.getRepeatType()){
		case Task.MINUTES:
			cal.add(Calendar.MINUTE, task.getRepeatInterval());
			newDateDue = cal.getTimeInMillis();
			Log.d("MINUTES",""+newDateDue);
			task.setDateDue(newDateDue);
			task.setIsCompleted(false); //this allows user to mark task complete until next time
			db.updateTask(task);
			return task;
		case Task.HOURS:
			cal.add(Calendar.HOUR, task.getRepeatInterval());
			newDateDue = cal.getTimeInMillis();
			Log.d("HOURS",""+newDateDue);
			task.setDateDue(newDateDue);
			task.setIsCompleted(false); //this allows user to mark task complete until next time
			db.updateTask(task);
			return task;
		case Task.DAYS:
			cal.add(Calendar.DAY_OF_YEAR, task.getRepeatInterval());
			newDateDue = cal.getTimeInMillis();
			Log.d("DAYS",""+newDateDue);
			task.setDateDue(newDateDue);
			task.setIsCompleted(false); //this allows user to mark task complete until next time
			db.updateTask(task);
			return task;
		case Task.WEEKS:
			cal.add(Calendar.WEEK_OF_YEAR, task.getRepeatInterval());
			newDateDue = cal.getTimeInMillis();
			Log.d("WEEKS",""+newDateDue);
			task.setDateDue(newDateDue);
			task.setIsCompleted(false); //this allows user to mark task complete until next time
			db.updateTask(task);
			return task;
		case Task.MONTHS:
			cal.add(Calendar.MONTH, task.getRepeatInterval());
			newDateDue = cal.getTimeInMillis();
			Log.d("MONTHS",""+newDateDue);
			task.setDateDue(newDateDue);
			task.setIsCompleted(false); //this allows user to mark task complete until next time
			db.updateTask(task);
			return task;
		case Task.YEARS:
			cal.add(Calendar.YEAR, task.getRepeatInterval());
			newDateDue = cal.getTimeInMillis();
			Log.d("YEARS",""+newDateDue);
			task.setDateDue(newDateDue);
			task.setIsCompleted(false); //this allows user to mark task complete until next time
			db.updateTask(task);
			return task;
		default:
			return task;
		}

	}

	public void setProcrastinatorAlarm(Context context, int id){
		TasksDataSource db = TasksDataSource.getInstance(context);
		Task task = db.getTask(id);
		if(task.getDateDue() >= System.currentTimeMillis() && task.getFinalDateDue() <= System.currentTimeMillis())
			return;
		
		long alarm = (task.getFinalDateDue() - task.getDateDue()) / 4; //fit 4 reminders in the time between alarms
		alarm += System.currentTimeMillis();

		AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, alarm, getPendingIntent(context, id));
	}

	//get a PendingIntent 
	PendingIntent getPendingIntent(Context context, int id) {
		Intent intent =  new Intent(context, OnAlarmReceiver.class)
		.putExtra(Task.EXTRA_TASK_ID, id);
		return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
}