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
import java.util.Comparator;

import android.app.Activity;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.comparator.TaskAutoComparator;
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
	private int sort_type;
	private final Comparator<Task> auto_comparator = new TaskAutoComparator();
	//private final Comparator<Task> name_comparator = new TaskNameComparator();
	//private final Comparator<Task> completion_comparator = new TaskCompletionComparator();
	//private final Comparator<Task> priority_comparator = new TaskPriorityComparator();
	//private final Comparator<Task> date_created_comparator = new TaskDateCreatedComparator();
	//private final Comparator<Task> date_due_comparator = new TaskDateDueComparator();
	
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
		this.setNotifyOnChange(true);
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
		View row_view = convert_view;
		if (row_view == null) {		
			LayoutInflater inflater = activity.getLayoutInflater();
			row_view = inflater.inflate(R.layout.row_task, null);
			
			ViewHolder view_holder = new ViewHolder();
			view_holder.is_completed = (CheckBox) row_view.findViewById(R.id.checkbox_row_complete);
			view_holder.name = (TextView) row_view.findViewById(R.id.text_row_name);
			view_holder.category = (View) row_view.findViewById(R.id.view_row_category);
			view_holder.priority = (ImageView) row_view.findViewById(R.id.image_row_priority);
			view_holder.due_date = (TextView) row_view.findViewById(R.id.text_row_due_date);
			view_holder.alarm = (ImageView) row_view.findViewById(R.id.image_row_alarm);
			view_holder.recurrence = (ImageView) row_view.findViewById(R.id.image_row_recurrence);
			
			row_view.setTag(view_holder);
		}

		ViewHolder holder = (ViewHolder) row_view.getTag();
		Task task = tasks.get(position);
		
		// Set is completed
		if (task.isCompleted())
			holder.is_completed.setChecked(true);
		else
			holder.is_completed.setChecked(false);
		
		// Set name
		holder.name.setText(task.getName());
		
		// Set category
		// TODO: Implement this
		holder.category.setVisibility(View.VISIBLE);
		holder.category.setBackgroundColor(Color.parseColor("#AA66CC"));
		
		// Set priority
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
		
		// Set due date
		if (task.hasDateDue())
			holder.due_date.setText(DateFormat.format("'Due:' MM/dd/yy 'at' h:mm AA", task.getDateDueCal()));
		
		// Set alarm
		if (task.hasFinalDateDue())
			holder.alarm.setVisibility(View.VISIBLE);
		else
			holder.alarm.setVisibility(View.INVISIBLE);
		
		// Set recurrence
		if (task.isRepeating())
			holder.recurrence.setVisibility(View.VISIBLE);
		else
			holder.recurrence.setVisibility(View.INVISIBLE);
		
		// Set styles
		/*if (!tasks.get(position).isCompleted()) {
			holder.name.setTextAppearance(getContext(), 
					R.style.text_task_not_completed);
			holder.is_completed.setTextAppearance(getContext(), 
					R.style.text_task_not_completed);
		} else {
			holder.name.setTextAppearance(getContext(), 
					R.style.text_task_completed);
			holder.is_completed.setTextAppearance(getContext(), 
					R.style.text_task_completed);
		}*/
		
		return row_view;
	}
	
	public void sort() {
		if (sort_type == AUTO_SORT)
		{
			this.sort(auto_comparator);
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
