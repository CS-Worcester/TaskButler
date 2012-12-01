/*
 * TaskListAdapter.java
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

package edu.worcester.cs499summer2012.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.activity.SettingsActivity;
import edu.worcester.cs499summer2012.comparator.TaskAutoComparator;
import edu.worcester.cs499summer2012.comparator.TaskCategoryComparator;
import edu.worcester.cs499summer2012.comparator.TaskCompletionComparator;
import edu.worcester.cs499summer2012.comparator.TaskDateCreatedComparator;
import edu.worcester.cs499summer2012.comparator.TaskDateDueComparator;
import edu.worcester.cs499summer2012.comparator.TaskDateModifiedComparator;
import edu.worcester.cs499summer2012.comparator.TaskNameComparator;
import edu.worcester.cs499summer2012.comparator.TaskPriorityComparator;
import edu.worcester.cs499summer2012.database.TasksDataSource;
import edu.worcester.cs499summer2012.service.TaskAlarm;
import edu.worcester.cs499summer2012.task.Task;

/**
 * ListView adapter for the TaskList container. Enables tasks in a TaskList
 * to be viewed in a ListView. Also allows for list sorting using task 
 * comparators.
 * @author Jonathan Hasenzahl
 */
public class TaskListAdapter extends ArrayAdapter<Task> {

	/**************************************************************************
	 * Static fields and methods                                              *
	 **************************************************************************/
	
	public static final int AUTO_SORT = 0;
	public static final int CUSTOM_SORT = 1;
	
	static class ViewHolder {
		public CheckBox is_completed;
		public TextView name;
		public View category;
		public ImageView priority;
		public TextView due_date;
	}
	
	/**************************************************************************
	 * Private fields                                                         *
	 **************************************************************************/
	
	private final Activity activity;
	private final ArrayList<Task> tasks;
	private TasksDataSource data_source;
	private SharedPreferences prefs;
	private int sort_type;
	
	/**************************************************************************
	 * Constructors                                                           *
	 **************************************************************************/
	
	/**
	 * Default constructor. Creates a new TaskListAdapter containing a TaskList
	 * and assigned to an Activity.
	 * @param activity the Activity that owns this adapter
	 * @param tasks the TaskList handled by this adapter
	 */
	public TaskListAdapter(Activity activity, ArrayList<Task> tasks) {
		super(activity, R.layout.row_task, tasks);
		this.activity = activity;
		this.tasks = tasks;
		data_source = TasksDataSource.getInstance(this.activity);
		prefs = PreferenceManager.getDefaultSharedPreferences(this.activity);
		setNotifyOnChange(true);
	}
	
	/**************************************************************************
	 * Overridden parent methods                                              *
	 **************************************************************************/
	
	/**
	 * This method is called automatically when the user scrolls the ListView.
	 * Updates the View of a single visible row, reflecting the list being 
	 * scrolled by the user.
	 * @param position the index of the TaskList
	 * @param convert_view the View to be updated
	 * @param parent the parent ViewGroup of convert_view
	 * @return the updated View
	 */
	@Override
	public View getView(int position, View convert_view, ViewGroup parent) {
		View view = convert_view;
		Task task = tasks.get(position);
		
		if (view == null) {		
			LayoutInflater inflater = activity.getLayoutInflater();
			view = inflater.inflate(R.layout.row_task, null);
			
			final ViewHolder view_holder = new ViewHolder();
			view_holder.is_completed = (CheckBox) view.findViewById(R.id.checkbox_row_complete);
			view_holder.is_completed.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Task task = (Task) view_holder.is_completed.getTag();
					task.toggleIsCompleted();
					task.setDateModified(System.currentTimeMillis());
					
					// Update DB
					data_source.updateTask(task);
					
					// Alarm logic: Complete/Uncomplete a task
					// * Don't forget to update date modified!
					// * Task must be updated in database first
					// * Cancel alarm first to be safe
					// * If user completed the task:
					// *	If is repeating:
					// *		Set repeating alarm to get new due date (also uncompletes the task)
					// *		Notify user that repeated task has been rescheduled
					// *		Set alarm
					// *	 	(Future repeating due date will be handled by the service after alarm rings)
					// * Else user uncompleted the task:
					// *	If has due date:
					// *		Set alarm
					TaskAlarm alarm = new TaskAlarm();
					alarm.cancelAlarm(activity, task.getID());
					if (task.isCompleted()) {
						toast(R.string.toast_task_completed);
						if (task.isRepeating()) {
							task = alarm.setRepeatingAlarm(activity, task.getID());
							
							StringBuilder repeat_message = new StringBuilder(); 
							repeat_message.append(activity.getString(R.string.toast_task_repeated));
							repeat_message.append(DateFormat.format(" MMM d", task.getDateDueCal()));
							repeat_message.append('.');
							toast(repeat_message.toString());
							
							alarm.setAlarm(activity, task);
						}
					} else {
						if (task.hasDateDue())
							alarm.setAlarm(activity, task);
					}
					
					// If "hide completed tasks" option, then remove the task from the adapter
					if (prefs.getBoolean(SettingsActivity.HIDE_COMPLETED, true) && task.isCompleted())
							tasks.remove(task);
					
					sort();
				}
			});
			view_holder.name = (TextView) view.findViewById(R.id.text_row_name);
			view_holder.category = (View) view.findViewById(R.id.view_row_category);
			view_holder.priority = (ImageView) view.findViewById(R.id.image_row_priority);
			view_holder.due_date = (TextView) view.findViewById(R.id.text_row_due_date);
			
			view.setTag(view_holder);
			view_holder.is_completed.setTag(task);
		} else
			((ViewHolder) view.getTag()).is_completed.setTag(task); 

		ViewHolder holder = (ViewHolder) view.getTag();
		
		// Set is completed
		boolean is_complete = task.isCompleted();
		holder.is_completed.setChecked(is_complete);
		
		// Set name
		holder.name.setText(task.getName());
		holder.name.setTextColor(is_complete ? Color.GRAY : Color.WHITE);
		
		// Set category
		holder.category.setVisibility(View.VISIBLE);
		holder.category.setBackgroundColor(data_source.getCategory(task.getCategory()).getColor());
		
		// Set priority
		if (is_complete)
			holder.priority.setVisibility(View.INVISIBLE);
		else {
			holder.priority.setVisibility(View.VISIBLE);
			
			switch (task.getPriority()) {
			case Task.URGENT:
				holder.priority.setImageResource(R.drawable.ic_urgent);
				break;
			case Task.TRIVIAL:
				holder.priority.setImageResource(R.drawable.ic_trivial);
				break;
			case Task.NORMAL:
			default:
				holder.priority.setImageResource(R.drawable.ic_normal);
				break;
			}
		}
		
		// Set due date
		if (is_complete)
			holder.due_date.setVisibility(View.INVISIBLE);
		else {
			holder.due_date.setVisibility(View.VISIBLE);
			holder.due_date.setTextColor(Color.LTGRAY);
			
			if (task.hasDateDue()) {
				Calendar current_date = GregorianCalendar.getInstance();
				Calendar due_date = task.getDateDueCal();
				
				if (due_date.get(Calendar.YEAR) > current_date.get(Calendar.YEAR)) {
					// Due date is in a future year
					holder.due_date.setText(DateFormat.format("MMM d'\n'yyyy", due_date));
				} else if (due_date.get(Calendar.DAY_OF_YEAR) - current_date.get(Calendar.DAY_OF_YEAR) > 6) {
					// Due date is more than a week away
					holder.due_date.setText(DateFormat.format("MMM d", due_date));
				} else if (due_date.get(Calendar.DAY_OF_YEAR) > current_date.get(Calendar.DAY_OF_YEAR)) {
					// Due date is after today
					holder.due_date.setText(DateFormat.format("E'\n'h:mmaa", due_date));
				} else if (!task.isPastDue()) {
					// Due date is today
					holder.due_date.setText(DateFormat.format("'Today\n'h:mmaa", due_date));
				} else {
					// Due date is past
					holder.due_date.setText("Past due");
					holder.due_date.setTextColor(Color.RED);
				}	
			} else
				holder.due_date.setText("");
		}
		
		return view;
	}

	public void sort() {
		if (sort_type == AUTO_SORT) {
			this.sort(new TaskAutoComparator());
		} else {
			ArrayList<edu.worcester.cs499summer2012.task.Comparator> comparators = data_source.getComparators();
			// Must iterate through the list backwards so higher sorting is done later
			for (int i = comparators.size(); i > 0; i--) {
				edu.worcester.cs499summer2012.task.Comparator comparator = comparators.get(i - 1);
				if (comparator.isEnabled()) {
					switch (comparator.getId()) {
					case edu.worcester.cs499summer2012.task.Comparator.NAME:
						this.sort(new TaskNameComparator());
						break;
						
					case edu.worcester.cs499summer2012.task.Comparator.COMPLETION:
						this.sort(new TaskCompletionComparator());
						break;
						
					case edu.worcester.cs499summer2012.task.Comparator.PRIORITY:
						this.sort(new TaskPriorityComparator());
						break;
						
					case edu.worcester.cs499summer2012.task.Comparator.CATEGORY:
						this.sort(new TaskCategoryComparator());
						break;
						
					case edu.worcester.cs499summer2012.task.Comparator.DATE_DUE:
						this.sort(new TaskDateDueComparator());
						break;
						
					case edu.worcester.cs499summer2012.task.Comparator.DATE_CREATED:
						this.sort(new TaskDateCreatedComparator());
						break;
						
					case edu.worcester.cs499summer2012.task.Comparator.DATE_MODIFIED:
						this.sort(new TaskDateModifiedComparator());
						break;
						
					default: 
						break;
					}
				}
			}
		}
		this.notifyDataSetChanged();
	}
	
	/**************************************************************************
	 * Getters and setters                                                    *
	 **************************************************************************/
	
	public int getSortType() {
		return sort_type;
	}
	
	public void setSortType(int sort_type) {
		if (sort_type == AUTO_SORT || sort_type == CUSTOM_SORT)
			this.sort_type = sort_type;
	}
	
	/**
	 * Displays a message in a Toast notification for a short duration.
	 */
	private void toast(String message) {
		Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Displays a message in a Toast notification for a short duration.
	 */
	private void toast(int message) {
		Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
	}
}
