/**
 * TaskArrayAdapter.java
 * 
 * Copyright 2012 Jonathan Hasenzahl
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

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.task.Task;
import edu.worcester.cs499summer2012.task.TaskList;

public class TaskListAdapter extends ArrayAdapter<Task> {

	private final Activity activity;
	private final TaskList tasks;
	
	static class ViewHolder {
		public TextView text;
	}
	
	public TaskListAdapter(Activity activity, TaskList tasks) {
		super(activity, R.layout.row_layout, tasks);
		this.activity = activity;
		this.tasks = tasks;
	}
	
	@Override
	public View getView(int position, View convert_view, ViewGroup parent) {
		View row_view = convert_view;
		if (row_view == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			row_view = inflater.inflate(R.layout.row_layout, null);
			ViewHolder view_holder = new ViewHolder();
			view_holder.text = (TextView) 
					row_view.findViewById(R.id.text_row_name);
			row_view.setTag(view_holder);
		}

		ViewHolder holder = (ViewHolder) row_view.getTag();
		holder.text.setText(tasks.get(position).getName());
		
		if (!tasks.get(position).getIsCompleted())
			holder.text.setTextAppearance(getContext(), R.style.text_task_not_completed);
		else
			holder.text.setTextAppearance(getContext(), R.style.text_task_completed);
		
		return row_view;
	}
}
