/*
 * TaskListAdapter.java
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

package edu.worcester.cs499summer2012.adapter;

import java.util.ArrayList;
import java.util.Comparator;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
		public TextView name;
		public TextView priority;
		public TextView date_due;
		public TextView is_completed;
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
			view_holder.name = (TextView) 
					row_view.findViewById(R.id.text_main_row_name);
			view_holder.priority = (TextView)
					row_view.findViewById(R.id.text_main_row_priority);
			view_holder.date_due = (TextView)
					row_view.findViewById(R.id.text_main_row_date_due);
			view_holder.is_completed = (TextView)
					row_view.findViewById(R.id.text_main_row_is_completed);
			row_view.setTag(view_holder);
		}

		ViewHolder holder = (ViewHolder) row_view.getTag();
		Task task = tasks.get(position);
		holder.name.setText(task.getName());
		holder.priority.setText(Task.LABELS[task.getPriority()]);
		
		holder.date_due.setText("No due date");
		
		holder.is_completed.setText(task.isCompleted() ? "Done" : "Not done");
		
		if (!tasks.get(position).isCompleted()) {
			holder.name.setTextAppearance(getContext(), 
					R.style.text_task_not_completed);
			holder.priority.setTextAppearance(getContext(), 
					R.style.text_task_not_completed);
		} else {
			holder.name.setTextAppearance(getContext(), 
					R.style.text_task_completed);
			holder.priority.setTextAppearance(getContext(), 
					R.style.text_task_completed);
		}
		
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
