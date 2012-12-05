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

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.database.TasksDataSource;
import edu.worcester.cs499summer2012.service.TaskAlarm;
import edu.worcester.cs499summer2012.service.TaskButlerWidgetProvider;
import edu.worcester.cs499summer2012.task.Category;
import edu.worcester.cs499summer2012.task.Task;
import edu.worcester.cs499summer2012.task.ToastMaker;

/**
 * Activity for adding a new task.
 * @author Jonathan Hasenzahl
 */
public class ViewTaskActivity extends SherlockActivity implements OnClickListener, 
DialogInterface.OnClickListener {

	/**************************************************************************
	 * Static fields and methods                                              *
	 **************************************************************************/

	/**************************************************************************
	 * Private fields                                                         *
	 **************************************************************************/

	private TasksDataSource data_source;
	private Task task;
	private Intent intent;
	private ActionBar action_bar;
	
	private CheckedTextView name;

	/**************************************************************************
	 * Class methods                                                          *
	 **************************************************************************/

	private void displayTask() {
		// Set name
		name = (CheckedTextView) findViewById(R.id.text_view_task_name);
		name.setText(task.getName());
		name.setChecked(task.isCompleted());
		name.setOnClickListener(this);
		name.setTextColor(name.isChecked() ? Color.GRAY : Color.WHITE);

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
		if (task.getCategory() != Category.NO_CATEGORY) {
			Category category = data_source.getCategory(task.getCategory());
			action_bar.setTitle(category.getName());
			((View) findViewById(R.id.view_category)).setBackgroundColor(category.getColor());
		} else {
			action_bar.setTitle(R.string.title_activity_view_task);
			((View) findViewById(R.id.view_category)).setBackgroundColor(Color.parseColor("#33B5E5"));
		}

		// Set due date
		if (task.hasDateDue()) {
			((LinearLayout) findViewById(R.id.due_date_bar)).setVisibility(View.VISIBLE);
			
			TextView due_date = (TextView) findViewById(R.id.text_date_due);
			
			if (task.isPastDue())
				due_date.setTextColor(Color.RED);
			else
				due_date.setTextColor(Color.LTGRAY);
			
			Calendar current_cal = GregorianCalendar.getInstance();
			Calendar due_cal = task.getDateDueCal();
			if (due_cal.getTimeInMillis() >= current_cal.getTimeInMillis()) {
				// Due date is in the future
				// Same year?
				if (due_cal.get(Calendar.YEAR) == current_cal.get(Calendar.YEAR))
					due_date.setText(DateFormat.format("'Due' MMMM d 'at' h:mmaa", due_cal));
				else
					due_date.setText(DateFormat.format("'Due' MMMM d, yyyy 'at' h:mmaa", due_cal));
			} else {
				// Due date is in the past
				// Same year?
				if (due_cal.get(Calendar.YEAR) == current_cal.get(Calendar.YEAR))
					due_date.setText(DateFormat.format("'Was due' MMMM d 'at' h:mmaa", due_cal));
				else
					due_date.setText(DateFormat.format("'Was due' MMMM d, yyyy 'at' h:mmaa", due_cal));
			}
			
			// Set repetition
			if (task.isRepeating()) {
				((ImageView) findViewById(R.id.image_repeat)).setVisibility(View.VISIBLE);
			} else {
				((ImageView) findViewById(R.id.image_repeat)).setVisibility(View.GONE);
			}
			
			// Set procrastinator alarm
			if (task.hasFinalDateDue()) {
				((ImageView) findViewById(R.id.image_alarm)).setVisibility(View.VISIBLE);
			} else {
				((ImageView) findViewById(R.id.image_alarm)).setVisibility(View.GONE);
			}

		} else
			((LinearLayout) findViewById(R.id.due_date_bar)).setVisibility(View.GONE);

		// Set notes
		((TextView) findViewById(R.id.text_notes)).setText(task.getNotes());
	}

	/**
	 * Displays a message in a Toast notification for a short duration.
	 */
	private void toast(String message)
	{
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Displays a message in a Toast notification for a short duration.
	 */
	private void toast(int message)
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
		action_bar = getSupportActionBar();
		action_bar.setHomeButtonEnabled(true);
		action_bar.setDisplayHomeAsUpEnabled(true);

		// Get instance of the db
		data_source = TasksDataSource.getInstance(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		// Get the task from the intent
		int id = getIntent().getIntExtra(Task.EXTRA_TASK_ID, 0);
		
		if (id == 0)
			finish();
		
		task = data_source.getTask(id);
		
		// Exit the task if it no longer exists (has been deleted)
		if (task == null) {
			toast("This task has been deleted!");
			finish();
			return;
		}

		// Display the task
		displayTask();
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
			setResult(RESULT_CANCELED);
			finish();
			return true;

		case R.id.menu_view_task_edit:
			Intent intent = new Intent(this, EditTaskActivity.class);
			intent.putExtra(Task.EXTRA_TASK_ID, task.getID());
			startActivityForResult(intent, MainActivity.EDIT_TASK_REQUEST);
			return true;

		case R.id.menu_view_task_delete:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to delete this task?");
			builder.setCancelable(true);
			builder.setPositiveButton("Yes", this);
			builder.setNegativeButton("No", this);
			builder.create().show();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**************************************************************************
	 * Methods implementing OnClickListener interface                         *
	 **************************************************************************/

	@Override
	public void onClick(View v) {	
		if (v.getId() == R.id.text_view_task_name) {
			task.toggleIsCompleted();
			name.setChecked(task.isCompleted());
			task.setDateModified(System.currentTimeMillis());
			data_source.updateTask(task);
			
			// Alarm logic: Complete/Uncomplete a task (ViewTaskActivity)
			// * Don't forget to update date modified!
			// * Task must be updated in database first
			// * Cancel alarm first to be safe
			// * Cancel an existing notification
			// * If user completed the task:
			// *	If is repeating:
			// *		Set repeating alarm to get new due date (possibly uncompletes the task)
			// *		Notify user that repeated task has been rescheduled
			// *		Set alarm if task was uncompleted
			// *	 	(Future repeating due date will be handled by the service after alarm rings)
			// * Else user uncompleted the task:
			// *	If has due date and is not past due:
			// *		Set alarm
			TaskAlarm alarm = new TaskAlarm();
			alarm.cancelAlarm(this, task.getID());
			alarm.cancelNotification(this, task.getID());
			if (task.isCompleted()) {
				toast(R.string.toast_task_completed);
				if (task.isRepeating()) {
					task = alarm.setRepeatingAlarm(this, task.getID());
										
					if (!task.isCompleted()) {
						alarm.setAlarm(this, task);
						toast(ToastMaker.getRepeatMessage(this, 
								R.string.toast_task_repeated, 
								task.getDateDueCal()));
					} else {
						toast(ToastMaker.getRepeatMessage(this, 
								R.string.toast_task_repeat_delayed, 
								task.getDateDueCal()));
					}
				}
			} else {
				if (task.hasDateDue() && !task.isPastDue())
					alarm.setAlarm(this, task);
			}
			
			// Update homescreen widget (after change has been saved to DB)
			TaskButlerWidgetProvider.updateWidget(this);
		}

		intent = new Intent(this, MainActivity.class);
		intent.putExtra(Task.EXTRA_TASK_ID, task.getID());
		setResult(RESULT_OK, intent);
		finish();
	}

	/**************************************************************************
	 * Methods implementing DialogInterface.OnClickListener interface         *
	 **************************************************************************/

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			// Alarm logic: Delete a task (ViewTaskActivity)
			// * Task must not be deleted from database yet!
			// * Cancel alarm
			// * Cancel an existing notification
			TaskAlarm alarm = new TaskAlarm();
			alarm.cancelAlarm(this, task.getID());
			alarm.cancelNotification(this, task.getID());
			
			data_source.deleteTask(task);
			
			// Update homescreen widget (after change has been saved to DB)
			TaskButlerWidgetProvider.updateWidget(this);
			
			toast("Task deleted");
			finish();
			break;

		case DialogInterface.BUTTON_NEGATIVE:
			dialog.cancel();
			break;
		}
	}

	@Override
	public void onActivityResult(int request_code, int result_code, Intent intent) {		
		// There is currently no special handling for activity results
		/*if (request_code == MainActivity.EDIT_TASK_REQUEST && result_code == MainActivity.RESULT_OK) {
			
		}*/
	}
}
