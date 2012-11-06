/* 
 * ViewTaskActivity.java
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
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.database.TasksDataSource;
import edu.worcester.cs499summer2012.task.Task;

/**
 * Activity for adding a new task.
 * @author Jonathan Hasenzahl
 */
public class ViewTaskActivity extends SherlockActivity implements OnClickListener {

	/**************************************************************************
	 * Static fields and methods                                              *
	 **************************************************************************/
    
    /**************************************************************************
     * Private fields                                                         *
     **************************************************************************/
    
	private TasksDataSource data_source;
	private Task task;
	private Intent intent;
	
    /**************************************************************************
	 * Class methods                                                          *
	 **************************************************************************/
    
	/**
     * Displays a message in a Toast notification for a short duration.
     */
    private void toast(String message)
    {
    	Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
	/**************************************************************************
	 * Overridden parent methods                                              *
	 **************************************************************************/
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);
        
        // Allow Action bar icon to act as a button
        getSupportActionBar().setHomeButtonEnabled(true);
        
        // Get instance of the db
        data_source = TasksDataSource.getInstance(this);
        
        // Get the task from the intent
        task = data_source.getTask(getIntent().getIntExtra(Task.EXTRA_TASK_ID, 0));
        
        // Set name
        ((TextView) findViewById(R.id.text_view_task_name)).setText(task.getName());
        
        // Set completion button
        Button button = (Button) findViewById(R.id.button_complete_task);
        if (!task.isCompleted())
        	button.setText(R.string.button_not_completed);
        else
        	button.setText(R.string.button_completed);
        button.setOnClickListener(this);
        
        // Set priority
        ((TextView) findViewById(R.id.text_priority)).setText(Task.LABELS[task.getPriority()]);
        
        // Set priority icon
        switch (task.getPriority()) {
        case Task.URGENT:
        	((ImageView) findViewById(R.id.image_priority)).setImageResource(R.drawable.ic_urgent);
        	break;
        case Task.NORMAL:
        	((ImageView) findViewById(R.id.image_priority)).setImageResource(R.drawable.ic_normal);
        	break;
        case Task.TRIVIAL:
        	((ImageView) findViewById(R.id.image_priority)).setImageResource(R.drawable.ic_trivial);
        	break;
        }
        
        // Set category
        // TODO Implement this
        
        // Set due date
        if (task.hasDateDue())
        	((TextView) findViewById(R.id.text_date_due)).setText(DateFormat.format("MM/dd/yy 'at' h:mm AA", task.getDateDueCal()));
        else
        	((TextView) findViewById(R.id.text_date_due)).setText(R.string.text_no_due_date);
        
        // Set final due date
        if (task.hasFinalDateDue())
        	((TextView) findViewById(R.id.text_alarm)).setText(DateFormat.format("MM/dd/yy 'at' h:mm AA", task.getFinalDateDueCal()));
        else
        	((TextView) findViewById(R.id.text_alarm)).setText(R.string.text_no_final_due_date);
        
        // Set repetition
        if (task.isRepeating()) {
        	((TextView) findViewById(R.id.text_repeat)).setText("Repeat every " + task.getRepeatInterval() + ' ' + Task.REPEAT_LABELS[task.getRepeatInterval()]);
        	
        	if (task.hasStopRepeatingDate())
        		((TextView) findViewById(R.id.text_repeat_2)).setText(DateFormat.format("'until' MM/dd/yy 'at' h:mm AA", task.getStopRepeatingDateCal()));
        	else
        		((TextView) findViewById(R.id.text_repeat_2)).setText(R.string.text_no_stop_repeating_date);
        } else
        	((TextView) findViewById(R.id.text_repeat)).setText(R.string.text_no_repetition);
        
        // Set notes
        ((TextView) findViewById(R.id.text_notes)).setText(task.getNotes());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getSupportMenuInflater();
    	inflater.inflate(R.menu.activity_view_task, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case android.R.id.home:
    	case R.id.menu_view_task_back:
    		setResult(RESULT_CANCELED);
    		finish();
    		return true;
    		
    	case R.id.menu_view_task_edit:
    		
    	case R.id.menu_view_task_delete:
    		
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }

	/**************************************************************************
	 * Methods implementing OnClickListener interface                         *
	 **************************************************************************/
	
	@Override
	public void onClick(View v) {	
		if (v.getId() == R.id.button_complete_task) {
			task.toggleIsCompleted();
			if (task.isCompleted())
				toast("Task completed!");
			else
				toast("Task not completed");
		}
		
		data_source.updateTask(task);
		
		intent = new Intent(this, MainActivity.class);
		intent.putExtra(Task.EXTRA_TASK_ID, task.getID());
		setResult(RESULT_OK, intent);
		finish();
	}
	
}
