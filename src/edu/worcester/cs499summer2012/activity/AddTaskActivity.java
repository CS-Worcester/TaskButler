/* 
 * AddTaskActivity.java
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

package edu.worcester.cs499summer2012.activity;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.task.Task;

/**
 * Activity for adding a new task.
 * @author Jonathan Hasenzahl
 * @author James Celona
 */
public class AddTaskActivity extends SherlockActivity {

	/**************************************************************************
	 * Static fields and methods                                              *
	 **************************************************************************/
	
	/**
	 * Label for the extra task parcel which will be added to the returned intent
	 */
	public final static String EXTRA_TASK = "edu.worcester.cs499summer2012.TASK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
    }
    
    /**************************************************************************
	 * Class methods                                                          *
	 **************************************************************************/
    
    /**
     * This method is called when the user clicks the OK button. A new task is
     * created based on user input. The task is added as an extra parcel to a 
     * return intent, and the activity finishes.
     * @param view The view from which the user called this method
     */
    public void addTask(View view) {
    	// Get task name
    	EditText task_name = (EditText) findViewById(R.id.edit_task_name);
    	String name = task_name.getText().toString();
    	
    	// If there is no task name, don't create the task
    	if (name.equals(""))
    	{
    		Toast.makeText(this, "Task needs a name!", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	// Get task priority
    	RadioGroup task_priority = (RadioGroup) findViewById(R.id.radiogroup_task_priority);
    	int priority;
    	
    	switch (task_priority.getCheckedRadioButtonId()) {
    	case R.id.radio_urgent:
    		priority = Task.URGENT;
    		break;
    	case R.id.radio_trivial:
    		priority = Task.TRIVIAL;
    		break;
    	case R.id.radio_normal:
    	default:
    		priority = Task.NORMAL;
    		break;    		
    	}
    	
    	// Get task creation date & task due date
    	DatePicker task_date = (DatePicker) findViewById(R.id.date_task_due);
    	TimePicker task_time = (TimePicker) findViewById(R.id.time_task_due);
    	Calendar date_created = new GregorianCalendar();
    	Calendar date_due = new GregorianCalendar();
    	date_due.set(task_date.getYear(), task_date.getMonth(), 
    			task_date.getDayOfMonth(), task_time.getCurrentHour(), 
    			task_time.getCurrentMinute());
    	
    	// Get task notes
    	EditText task_notes = (EditText) findViewById(R.id.edit_task_notes);
    	String notes = task_notes.getText().toString();
    	if (notes.equals(""))
    		notes = null;
    	    	
    	// Create the task
    	Task task = new Task();
    	task.setName(name).setIsCompleted(false).setPriority(priority)
    	    .setCreationDate(date_created).setDueDate(date_due)
    	    .setNotes(notes);
    	
    	// Create the return intent and add the task
    	Intent intent = new Intent(this, MainActivity.class);    	
    	intent.putExtra(EXTRA_TASK, task);
	    
    	// Set the return result to OK and finish the activity
    	setResult(RESULT_OK, intent);
	    finish();
    }
    
    /**
     * This method is called when the user clicks the cancel button. The 
     * activity finishes without adding a new task.
     * @param view The view from which the user called this method
     */
    public void cancel(View view) {
    	setResult(RESULT_CANCELED);
    	finish();
    }
}
