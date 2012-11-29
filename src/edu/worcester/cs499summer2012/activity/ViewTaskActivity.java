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

import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
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
import edu.worcester.cs499summer2012.task.Category;
import edu.worcester.cs499summer2012.task.Task;

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

	/**************************************************************************
	 * Class methods                                                          *
	 **************************************************************************/

	private void displayTask() {
		// Set name
		((TextView) findViewById(R.id.text_view_task_name)).setText(task.getName());

		// Set completion checkbox
		CheckBox checkbox = (CheckBox) findViewById(R.id.checkbox_complete_task);
		checkbox.setChecked(task.isCompleted());
		checkbox.setOnClickListener(this);

		// Set priority
		((TextView) findViewById(R.id.text_priority)).setText(Task.PRIORITY_LABELS[task.getPriority()]);

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

		// Set category (if category ID is not 1, i.e. no category)
		View v_category_color = (View) findViewById(R.id.view_category);
		TextView tv_category_name = (TextView) findViewById(R.id.text_category);
		if (task.getCategory() != 1) {
			v_category_color.setVisibility(View.VISIBLE);
			tv_category_name.setVisibility(View.VISIBLE);
			Category category = data_source.getCategory(task.getCategory());
			v_category_color.setBackgroundColor(category.getColor());
			tv_category_name.setText(category.getName());
		} else {
			v_category_color.setVisibility(View.GONE);
			tv_category_name.setVisibility(View.GONE);
		}

		// Set due date
		if (task.hasDateDue()) {
			((ImageView) findViewById(R.id.image_date)).setVisibility(View.VISIBLE);
			TextView date_due = ((TextView) findViewById(R.id.text_date_due));
			date_due.setVisibility(View.VISIBLE);
			date_due.setText(DateFormat.format("'Due' MM/dd/yy 'at' h:mm AA", task.getDateDueCal()));

			if (task.isPastDue())
				date_due.setTextColor(Color.RED);
			else
				date_due.setTextColor(Color.LTGRAY);
		} else {
			((TextView) findViewById(R.id.text_date_due)).setVisibility(View.GONE);
			((ImageView) findViewById(R.id.image_date)).setVisibility(View.GONE);
		}

		// Set final due date
		if (task.hasFinalDateDue())
			((ImageView) findViewById(R.id.image_alarm)).setVisibility(View.VISIBLE);
		else
			((ImageView) findViewById(R.id.image_alarm)).setVisibility(View.GONE);

		// Set repetition
		if (task.isRepeating()) {
			((ImageView) findViewById(R.id.image_repeat)).setVisibility(View.VISIBLE);
			TextView repeat_text = (TextView) findViewById(R.id.text_repeat);
			repeat_text.setVisibility(View.VISIBLE);
			
			repeat_text.setText("Repeat every " + task.getRepeatInterval() + ' ' + Task.REPEAT_LABELS[task.getRepeatType()]);
		} else {
			((TextView) findViewById(R.id.text_repeat)).setVisibility(View.GONE);
			((ImageView) findViewById(R.id.image_repeat)).setVisibility(View.GONE);
		}

		// Set notes
		String notes = task.getNotes();
		if (!notes.equals("")) {
			((ImageView) findViewById(R.id.image_notes)).setVisibility(View.VISIBLE);
			TextView notes_text = (TextView) findViewById(R.id.text_notes);
			notes_text.setVisibility(View.VISIBLE);
			notes_text.setText(notes);
		} else {
			((TextView) findViewById(R.id.text_notes)).setVisibility(View.GONE);
			((ImageView) findViewById(R.id.image_notes)).setVisibility(View.GONE);
		}
	}

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
		ActionBar action_bar = getSupportActionBar();
		action_bar.setHomeButtonEnabled(true);
		action_bar.setDisplayHomeAsUpEnabled(true);

		// Get instance of the db
		data_source = TasksDataSource.getInstance(this);

		// Get the task from the intent
		task = data_source.getTask(getIntent().getIntExtra(Task.EXTRA_TASK_ID, 0));

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
		if (v.getId() == R.id.checkbox_complete_task) {
			task.toggleIsCompleted();
			task.setDateModified(GregorianCalendar.getInstance().getTimeInMillis());
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

	/**************************************************************************
	 * Methods implementing DialogInterface.OnClickListener interface         *
	 **************************************************************************/

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			data_source.deleteTask(task);
			if (task.hasDateDue()) {
				TaskAlarm alarm = new TaskAlarm();
				alarm.cancelAlarm(getApplicationContext(), task.getID());
			}
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
		if (request_code == MainActivity.EDIT_TASK_REQUEST && result_code == MainActivity.RESULT_OK) {
			task = data_source.getTask(intent.getIntExtra(Task.EXTRA_TASK_ID, 0));
			displayTask();
			if (!task.isCompleted() && task.hasDateDue() &&
					(task.getDateDue() >= System.currentTimeMillis())) {
				TaskAlarm alarm = new TaskAlarm();
				alarm.setAlarm(this, task.getID());
			}
		}
	}
}
