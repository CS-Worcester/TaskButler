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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.task.Task;
import edu.worcester.cs499summer2012.task.TaskList;

public class TaskListAdapter extends ArrayAdapter<Task> {

	private final Context context;
	private final TaskList tasks;
	
	public TaskListAdapter(Context context, TaskList tasks) {
		super(context, R.layout.row_layout, tasks);
		this.context = context;
		this.tasks = tasks;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) 
				context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row_view = inflater.inflate(R.layout.row_layout, parent, false);
		TextView text_view = (TextView) 
				row_view.findViewById(R.id.text_row_name);
		text_view.setText(tasks.get(position).getName());
		return row_view;
	}
}
