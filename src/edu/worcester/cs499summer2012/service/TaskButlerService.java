/*
 * TasksBatlerService.java
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

import java.util.List;

import android.content.Intent;

import edu.worcester.cs499summer2012.database.TasksDataSource;
import edu.worcester.cs499summer2012.task.Task;

/**
 * An IntentService that takes care of setting up alarms for Task Butler
 * to remind the user of upcoming events
 * @author Dhimitraq Jorgji
 *
 */
public class TaskButlerService extends WakefulIntentService{

	public TaskButlerService() {
		super("TaskButlerService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		TasksDataSource db = TasksDataSource.getInstance(this); //get access to the instance of TasksDataSource
		TaskAlarm alarm = new TaskAlarm();


		List<Task> tasks = db.getAllTasks(); //Get a list of all the tasks there
		for (Task task : tasks) {
			//handle edge case of phone being off during an alarm going off.
			if(task.isRepeating()){
				if(task.hasStopRepeatingDate() && task.getStopRepeatingDate() <= System.currentTimeMillis()){
					/*Indented do nothing*/
				} else {
					while(task.getDateDue() <= System.currentTimeMillis())
						task = alarm.setRepeatingAlarm(this, task.getID());
						//TODO: maybe set a different flag for when a task is permanently complete
				}
			}
			//set procrastinator alarm if the task has a finalDateDue
			if(!task.isCompleted() && task.hasFinalDateDue())
				alarm.setPrograstinatorAlarm(this, task.getID());

			if(!task.isCompleted() && (task.getDateDue() >= System.currentTimeMillis())){
				alarm.setAlarm(this, task.getID());	}
		}
		super.onHandleIntent(intent);
	}
}