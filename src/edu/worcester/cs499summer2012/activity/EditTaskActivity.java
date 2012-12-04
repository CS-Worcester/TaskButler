/* 

 * EditTaskActivity.java
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

package edu.worcester.cs499summer2012.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import edu.worcester.cs499summer2012.service.TaskAlarm;
import edu.worcester.cs499summer2012.task.Category;
import edu.worcester.cs499summer2012.task.Task;

public class EditTaskActivity extends BaseTaskActivity {

	private Task task;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get the task from the intent
		int id = getIntent().getIntExtra(Task.EXTRA_TASK_ID, 0);
		if (id == 0) {
			toast("Error retrieving task");
			finish();
		}
        task = data_source.getTask(id);
        
        // Set task name
        et_name.setText(task.getName());
        
        // Set priority
        s_priority.setSelection(task.getPriority());
        
        // Set category
        default_category = data_source.getCategory(task.getCategory());
        s_category.setSelection(category_adapter.getPosition(default_category));
		
        // Set due date
        if (task.hasDateDue()) {
        	due_date_cal = task.getDateDueCal();
        	prevent_initial_due_date_popup = true;
        	cb_due_date.setChecked(true);
        }
        
        // Set final due date
        if (task.hasFinalDateDue())
        	cb_final_due_date.setChecked(true);
        
        // Set repeating
        if (task.isRepeating()) {
        	cb_repeating.setChecked(true);
        	repeat_interval_string = String.valueOf(task.getRepeatInterval());
        	et_repeat_interval.setText(repeat_interval_string);
        	s_repeat_type.setSelection(task.getRepeatType());
        }
          
        // Set notes
        et_notes.setText(task.getNotes());
	}
	
	protected boolean addTask() {
		// 1. ID (not modified)
		
    	// 2. Task name
    	String name = et_name.getText().toString().trim();
    	// If there is no task name, don't create the task
    	if (name.equals(""))
    	{
    		Toast.makeText(this, "Task needs a name!", Toast.LENGTH_SHORT).show();
    		return false;
    	}
    	task.setName(name);
    	
    	// 3. Is completed (not modified)
    	
    	// 4. Task priority
    	task.setPriority(s_priority.getSelectedItemPosition());
    	
    	// 5. Task category
    	task.setCategory(((Category) s_category.getSelectedItem()).getID());
    	
    	// 6. Has date due
    	task.setHasDateDue(cb_due_date.isChecked());
    	
    	// 7. Has final date due
    	task.setHasFinalDateDue(cb_final_due_date.isChecked());
    	
    	// 8. Is repeating
		task.setIsRepeating(cb_repeating.isChecked());
    	
    	// 9. Repeat type
		// 10. Repeat interval
		if (task.isRepeating())	{
			task.setRepeatType(s_repeat_type.getSelectedItemPosition());

	    	
	    	int interval = 1;
	    	String interval_string = et_repeat_interval.getText().toString();
	    	if (!interval_string.equals("")) {
	    		interval =  Integer.parseInt(interval_string);
	    		if (interval == 0)
	    			interval = 1;
	    	}
	    	if (task.isRepeating())
	    		task.setRepeatInterval(interval);
		}
    	
    	// 11. Date created (not modified)
    	
    	// 12. Date modified
    	task.setDateModified(System.currentTimeMillis());
    	
    	// 13. Task due date
    	if (task.hasDateDue())
    		task.setDateDue(due_date_cal.getTimeInMillis());
    	
    	// 14. gID (not modified)
    	
    	// 15. Task notes
    	task.setNotes(et_notes.getText().toString());
    	
    	// Update the task in the database
    	data_source.updateTask(task);
    	
    	// Alarm logic: Edit a task [non-completion] (EditTaskActivity)
    	// * Don't forget to update date modified!
    	// * Task must be updated in database first
    	// * User could have changed any setting, so checking booleans is not reliable
    	// * Cancel alarm first to be safe
    	// * Cancel existing notification
    	// * If has due date:
    	// *	Set alarm
    	// * 	(Repeating due date will be handled by the service after alarm rings)
    	TaskAlarm alarm = new TaskAlarm();
    	alarm.cancelAlarm(this, task.getID());
    	alarm.cancelNotification(this, task.getID());
    	if (task.hasDateDue() && !task.isPastDue())
    		alarm.setAlarm(this, task);
    	
    	// Create the return intent and add the task ID
    	intent = new Intent(this, MainActivity.class);    	
    	intent.putExtra(Task.EXTRA_TASK_ID, task.getID());
    	
    	return true;
    }
}
