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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.task.Task;

/**
 * Activity for adding a new task.
 * @author Jonathan Hasenzahl
 * @author James Celona
 */
public class AddTaskActivity extends SherlockActivity implements OnCheckedChangeListener {

	/**************************************************************************
	 * Static fields and methods                                              *
	 **************************************************************************/
	
	/**
	 * Label for the extra task parcel which will be added to the returned intent
	 */
	public final static String EXTRA_TASK = "edu.worcester.cs499summer2012.TASK";
    
    /**************************************************************************
     * Private fields                                                         *
     **************************************************************************/
    
    private Intent intent;
    private CheckBox has_due_date;
    private Button edit_due_date;
    private TextView due_date;
    private CheckBox has_final_due_date;
    private Button edit_final_due_date;
    private TextView final_due_date;
    private CheckBox has_repetition;   
    private TextView repeats;
    private EditText repeat_interval;
    private Spinner repeat_type;
    
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
    	
    	// Get completion status
    	CheckBox task_completion = (CheckBox) findViewById(R.id.checkbox_already_completed);
    	boolean is_completed = task_completion.isChecked();
    	
    	// Get task priority
    	RadioGroup task_priority = (RadioGroup) findViewById(R.id.radiogroup_add_task_priority);
    	int priority;
    	
    	switch (task_priority.getCheckedRadioButtonId()) {
    	case R.id.radio_add_task_urgent:
    		priority = Task.URGENT;
    		break;
    	case R.id.radio_add_task_trivial:
    		priority = Task.TRIVIAL;
    		break;
    	case R.id.radio_add_task_normal:
    	default:
    		priority = Task.NORMAL;
    		break;    		
    	}
    	
    	// Get task category
    	// TODO: Implement this
    	
    	// Get has due date
    	CheckBox task_has_due_date = (CheckBox) findViewById(R.id.checkbox_has_due_date);
    	boolean has_due_date = task_has_due_date.isChecked();
    	
    	// Get has final due date
    	CheckBox task_has_final_due_date = (CheckBox) findViewById(R.id.checkbox_has_final_due_date);
    	boolean has_final_due_date = task_has_final_due_date.isChecked();
    	
    	// Get task creation date
    	Calendar date_created = GregorianCalendar.getInstance();
    	
    	// Get task due date
    	/*DatePicker task_date = (DatePicker) findViewById(R.id.date_add_task_due);
    	TimePicker task_time = (TimePicker) findViewById(R.id.time_add_task_due);
    	Calendar date_due = new GregorianCalendar();
    	date_due.set(task_date.getYear(), task_date.getMonth(), 
    			task_date.getDayOfMonth(), task_time.getCurrentHour(), 
    			task_time.getCurrentMinute());
    	date_due.set(GregorianCalendar.SECOND, 0);
    	date_due.set(GregorianCalendar.MILLISECOND, 0);*/
    	
    	// Get task notes
    	EditText task_notes = (EditText) findViewById(R.id.edit_add_task_notes);
    	String notes = task_notes.getText().toString();
    	    	
    	// Create the task
    	Task task = new Task(
    			name, 
    			is_completed, 
    			priority, 
    			0,
    			has_due_date,
    			has_final_due_date,
    			false,
    			false,
    			0,
    			0,
    			date_created.getTimeInMillis(), 
    			0, 
    			0,
    			0,
    			notes);
    	
    	// Create the return intent and add the task
    	intent = new Intent(this, MainActivity.class);    	
    	intent.putExtra(EXTRA_TASK, task);
    	
    	return true;
    }
    
	/**************************************************************************
	 * Overridden parent methods                                              *
	 **************************************************************************/
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        
        // Initialize the fields that can be enabled/disabled or listened to
        has_due_date = (CheckBox) findViewById(R.id.checkbox_has_due_date);
        edit_due_date = (Button) findViewById(R.id.button_edit_due_date);
        due_date = (TextView) findViewById(R.id.text_add_task_due_date);
        has_final_due_date = (CheckBox) findViewById(R.id.checkbox_has_final_due_date);
        edit_final_due_date = (Button) findViewById(R.id.button_edit_final_due_date);
        final_due_date = (TextView) findViewById(R.id.text_add_task_final_due_date);
        has_repetition = (CheckBox) findViewById(R.id.checkbox_has_repetition);   
        repeats = (TextView) findViewById(R.id.text_add_task_repeats);
        repeat_interval = (EditText) findViewById(R.id.edit_add_task_repeat_interval);
        repeat_type = (Spinner) findViewById(R.id.spinner_add_task_repeat_type);
        has_due_date.setOnCheckedChangeListener(this);
        
        // Allow Action bar icon to act as a button
        getSupportActionBar().setHomeButtonEnabled(true);
        
        // Populate the repeat type spinner
        ArrayAdapter<CharSequence> repeat_type_adapter = 
        		ArrayAdapter.createFromResource(this, 
        				R.array.spinner_repeat_types, 
        				android.R.layout.simple_spinner_item);
        repeat_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeat_type.setAdapter(repeat_type_adapter);
        repeat_type.setEnabled(false);
    }
    
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

	/**************************************************************************
	 * Methods implementing OnCheckedChangedListener interface                *
	 **************************************************************************/

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.checkbox_has_due_date:
			if (isChecked) {
				edit_due_date.setEnabled(true);
				due_date.setEnabled(true);
				has_final_due_date.setEnabled(true);
				has_repetition.setEnabled(true);
			} else {
				edit_due_date.setEnabled(false);
				due_date.setEnabled(false);
				has_final_due_date.setEnabled(false);
				has_repetition.setEnabled(false);
			}
		}
	}
}
