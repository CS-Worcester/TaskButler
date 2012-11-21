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
	
	private final long MINUTES = 60000;
	private final long HOURS = MINUTES * 60;
	private final long DAYS = HOURS * 24;
	private final long WEEKS =  DAYS * 7;

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
    	long months = DAYS * cal.getActualMaximum(Calendar.DAY_OF_MONTH); //TODO: fix and add the field in a loop
    	long years = DAYS * cal.getActualMaximum(Calendar.DAY_OF_YEAR);
    	
    	long newDateDue;
    	switch(task.getRepeatType()){
		case Task.MINUTES:
			newDateDue = task.getDateDue() + MINUTES*task.getRepeatInterval();
			Log.d("MINUTES",""+newDateDue);
			task.setDateDue(newDateDue);
			task.setIsCompleted(false); //this allows user to mark task complete until next time
			db.updateTask(task);
			return task;
		case Task.HOURS:
			newDateDue = task.getDateDue() + HOURS*task.getRepeatInterval();
			Log.d("HOURS",""+newDateDue);
			task.setDateDue(newDateDue);
			task.setIsCompleted(false); //this allows user to mark task complete until next time
			db.updateTask(task);
			return task;
		case Task.DAYS:
			newDateDue = task.getDateDue() + DAYS*task.getRepeatInterval();
			Log.d("DAYS",""+newDateDue);
			task.setDateDue(newDateDue);
			task.setIsCompleted(false); //this allows user to mark task complete until next time
			db.updateTask(task);
			return task;
		case Task.WEEKS:
			newDateDue = task.getDateDue() + WEEKS*task.getRepeatInterval();
			Log.d("WEEKS",""+newDateDue);
			task.setDateDue(newDateDue);
			task.setIsCompleted(false); //this allows user to mark task complete until next time
			db.updateTask(task);
			return task;
		case Task.MONTHS:
			newDateDue = task.getDateDue() + months * task.getRepeatInterval();
			Log.d("MONTHS",""+newDateDue);
			task.setDateDue(newDateDue);
			task.setIsCompleted(false); //this allows user to mark task complete until next time
			db.updateTask(task);
			return task;
		case Task.YEARS:
			newDateDue = task.getDateDue() + years *task.getRepeatInterval();
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
    	long alarm = System.currentTimeMillis() + (1 * MINUTES);
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
