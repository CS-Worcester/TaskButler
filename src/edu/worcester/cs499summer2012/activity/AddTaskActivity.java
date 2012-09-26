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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.task.DeprecatedTask;

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
        
        // Allow Action bar icon to act as a button
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    
    /**************************************************************************
     * Private fields                                                         *
     **************************************************************************/
    
    private Intent intent;
    
    /**************************************************************************
	 * Class methods                                                          *
	 **************************************************************************/
    
    /**
     * This method is called when the user clicks the OK button. A new task is
     * created based on user input. The task is added as an extra parcel to a 
     * return intent, and the activity finishes.
     * @param view The view from which the user called this method
     */
    public boolean addTask() {
    	// Get task name
    	EditText task_name = (EditText) findViewById(R.id.edit_add_task_name);
    	String name = task_name.getText().toString();
    	
    	// If there is no task name, don't create the task
    	if (name.equals(""))
    	{
    		Toast.makeText(this, "Task needs a name!", Toast.LENGTH_SHORT).show();
    		return false;
    	}
    	
    	// Get task priority
    	RadioGroup task_priority = (RadioGroup) findViewById(R.id.radiogroup_add_task_priority);
    	int priority;
    	
    	switch (task_priority.getCheckedRadioButtonId()) {
    	case R.id.radio_add_task_urgent:
    		priority = DeprecatedTask.URGENT;
    		break;
    	case R.id.radio_add_task_trivial:
    		priority = DeprecatedTask.TRIVIAL;
    		break;
    	case R.id.radio_add_task_normal:
    	default:
    		priority = DeprecatedTask.NORMAL;
    		break;    		
    	}
    	
    	// Get task creation date & task due date
    	DatePicker task_date = (DatePicker) findViewById(R.id.date_add_task_due);
    	TimePicker task_time = (TimePicker) findViewById(R.id.time_add_task_due);
    	Calendar date_created = new GregorianCalendar();
    	Calendar date_due = new GregorianCalendar();
    	date_due.set(task_date.getYear(), task_date.getMonth(), 
    			task_date.getDayOfMonth(), task_time.getCurrentHour(), 
    			task_time.getCurrentMinute());
    	
    	// Get task notes
    	EditText task_notes = (EditText) findViewById(R.id.edit_add_task_notes);
    	String notes = task_notes.getText().toString();
    	if (notes.equals(""))
    		notes = null;
    	    	
    	// Create the task
    	DeprecatedTask deprecatedTask = new DeprecatedTask();
    	deprecatedTask.setName(name).setIsCompleted(false).setPriority(priority)
    	    .setCreationDate(date_created).setDueDate(date_due)
    	    .setNotes(notes);
    	
    	// Create the return intent and add the task
    	intent = new Intent(this, MainActivity.class);    	
    	intent.putExtra(EXTRA_TASK, deprecatedTask);
    	
    	return true;
    }
    
	/**************************************************************************
	 * Overridden parent methods                                              *
	 **************************************************************************/
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getSupportMenuInflater();
    	inflater.inflate(R.menu.activity_add_task, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case android.R.id.home:
    	case R.id.menu_add_task_cancel:
    		setResult(RESULT_CANCELED);
    		finish();
    		return true;
    	
    	case R.id.menu_add_task_confirm:
    		if (addTask())
    		{
    	    	// Set the return result to OK and finish the activity
    	    	setResult(RESULT_OK, intent);
    		    finish();
    		}
    		return true;
    		
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
}
