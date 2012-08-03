/**
 * MainActivity.java
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

package edu.worcester.cs499summer2012.activity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.adapter.TaskListAdapter;
import edu.worcester.cs499summer2012.task.Task;
import edu.worcester.cs499summer2012.task.TaskList;

public class MainActivity extends ListActivity {
	
	static final int ADD_TASK_REQUEST = 0;
	
    private TaskList tasks;
    private TaskListAdapter adapter;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    	tasks = new TaskList();
    	readTasksFromFile();
		adapter = new TaskListAdapter(this, tasks);
    	setListAdapter(adapter);
    }
    
    @Override
	protected void onStop() {
    	writeTasksToFile();
    	super.onStop();
    }
    
    @Override
    protected void onListItemClick(ListView list_view, View view, int position, 
    		long id) {
    	Task task = (Task) getListAdapter().getItem(position);
    	task.toggleIsCompleted();
    	tasks.set(position, task);
    	setListAdapter(adapter);
    }
    
    @Override
	protected void onActivityResult(int request_code, int result_code, 
    		Intent intent) {
    	if (request_code == ADD_TASK_REQUEST && result_code == RESULT_OK) {
    		Task task = intent.getParcelableExtra(AddTaskActivity.EXTRA_TASK);
    		tasks.add(task);
    		setListAdapter(adapter);
    	}
    }
    
    public void getNewTask(View view) {
    	startActivityForResult(new Intent(this, AddTaskActivity.class), 
    			ADD_TASK_REQUEST);
    }
    
    private void readTasksFromFile() {
    	BufferedReader file = null;
    	try {
    		file = new BufferedReader(new
    				InputStreamReader(openFileInput("tasks.txt")));
    		String line;
    		while ((line = file.readLine()) != null) {
    			tasks.add(Task.getTaskFromString(line));
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		if (file != null) {
    			try {
    				file.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    }
    
    private void writeTasksToFile() {
    	String eol = System.getProperty("line.separator");
    	BufferedWriter file = null;
    	try {
    		file = new BufferedWriter(new 
    				OutputStreamWriter(openFileOutput("tasks.txt", 
    						MODE_PRIVATE)));
    		if (tasks.size() > 0) {
    			for (Task task : tasks) {
    				file.write(task + eol);
    			}
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		if (file != null) {
    			try {
    				file.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    }
    
}
