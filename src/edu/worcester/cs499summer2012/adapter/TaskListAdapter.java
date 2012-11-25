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
import java.util.GregorianCalendar;

import android.app.Activity;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.comparator.TaskAutoComparator;
import edu.worcester.cs499summer2012.comparator.TaskCategoryComparator;
import edu.worcester.cs499summer2012.comparator.TaskCompletionComparator;
import edu.worcester.cs499summer2012.comparator.TaskDateCreatedComparator;
import edu.worcester.cs499summer2012.comparator.TaskDateDueComparator;
import edu.worcester.cs499summer2012.comparator.TaskDateModifiedComparator;
import edu.worcester.cs499summer2012.comparator.TaskFinalDateDueComparator;
import edu.worcester.cs499summer2012.comparator.TaskNameComparator;
import edu.worcester.cs499summer2012.comparator.TaskPriorityComparator;
import edu.worcester.cs499summer2012.database.TasksDataSource;
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
		public ImageView alarm;
		public ImageView recurrence;
	}
	
	/**************************************************************************
	 * Private fields                                                         *
	 **************************************************************************/
	
	private final Activity activity;
	private final ArrayList<Task> tasks;
	private TasksDataSource data_source;
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
					task.setDateModified(GregorianCalendar.getInstance().getTimeInMillis());
					
					// Update DB
					data_source.updateTask(task);
					
					sort();
				}
			});
			view_holder.name = (TextView) view.findViewById(R.id.text_row_name);
			view_holder.category = (View) view.findViewById(R.id.view_row_category);
			view_holder.priority = (ImageView) view.findViewById(R.id.image_row_priority);
			view_holder.due_date = (TextView) view.findViewById(R.id.text_row_due_date);
			view_holder.alarm = (ImageView) view.findViewById(R.id.image_row_alarm);
			view_holder.recurrence = (ImageView) view.findViewById(R.id.image_row_recurrence);
			
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
		holder.name.setTextColor(is_complete ? Color.DKGRAY : Color.WHITE);
		
		// Set category
		if (is_complete)
			holder.category.setVisibility(View.GONE);
		else {
			holder.category.setVisibility(View.VISIBLE);
			holder.category.setBackgroundColor(data_source.getCategory(task.getCategory()).getColor());
		}
		
		// Set priority
		if (is_complete)
			holder.priority.setVisibility(View.GONE);
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
			holder.due_date.setVisibility(View.GONE);
		else {
			holder.due_date.setVisibility(View.VISIBLE);
			
			if (task.hasDateDue()) {
				holder.due_date.setText(DateFormat.format("'Due' MM/dd/yy h:mmAA", task.getDateDueCal()));
				
				if (task.isPastDue())
	        		holder.due_date.setTextColor(Color.RED);
			} else
				holder.due_date.setText("");
		}
		
		// Set alarm
		if (is_complete)
			holder.alarm.setVisibility(View.GONE);
		else if (task.hasFinalDateDue())
			holder.alarm.setVisibility(View.VISIBLE);
		else
			holder.alarm.setVisibility(View.INVISIBLE);
		
		// Set recurrence
		if (is_complete)
			holder.recurrence.setVisibility(View.GONE);
		else if (task.isRepeating())
			holder.recurrence.setVisibility(View.VISIBLE);
		else
			holder.recurrence.setVisibility(View.INVISIBLE);
		
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
						
					case edu.worcester.cs499summer2012.task.Comparator.FINAL_DATE_DUE:
						this.sort(new TaskFinalDateDueComparator());
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
	
}
