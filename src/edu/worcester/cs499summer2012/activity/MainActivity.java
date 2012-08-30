/*
 * MainActivity.java
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

package edu.worcester.cs499summer2012.activity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;
import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.adapter.TaskListAdapter;
import edu.worcester.cs499summer2012.task.Task;
import edu.worcester.cs499summer2012.task.TaskList;

/**
 * Main app activity. Displays current task list and allows user to access
 * task creation, task modification, and task sorting activities.
 * @author Jonathan Hasenzahl
 * @author James Celona
 */
public class MainActivity extends ListActivity {
	
	public static final int ADD_TASK_REQUEST = 0;
	
    private TaskList tasks;
    private TaskListAdapter adapter;
    private final String TASK_FILE_NAME = "tasks";

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Assign the layout to this activity
        setContentView(R.layout.activity_main);
    	
        // Create a task list and read contents from file
        tasks = new TaskList();
    	readTasksFromFile();
    	
    	// Create an adapter for the task list
		adapter = new TaskListAdapter(this, tasks);
    	setListAdapter(adapter);
    	
    	// Enable long-pressing the list view to pop up a context menu
    	ListView list = getListView();
    	registerForContextMenu(list);
    }
    
    @Override
	protected void onStop() {
    	writeTasksToFile();
    	super.onStop();
    }
    
    @Override
    protected void onListItemClick(ListView list_view, View view, int position, 
    		long id) {
    	tasks.get(position).toggleIsCompleted();
    	adapter.sort(tasks.completionComparator());
    	adapter.notifyDataSetChanged();
    }
    
    @Override
	public void onCreateContextMenu(ContextMenu menu, View view, 
			ContextMenuInfo info) {
    	super.onCreateContextMenu(menu, view, info);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.context_edit_delete_task, menu);
    	
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	final int position = info.position;
    	
    	switch (item.getItemId()) {
    	case R.id.context_edit_task:
    		toast("Coming soon!");
    		return true;
    	
    	case R.id.context_delete_task:
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage("Are you sure you want to delete?")
    		       .setCancelable(false)
    		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    		    	   public void onClick(DialogInterface dialog, int id) {
    		    		   tasks.remove(position);
    		    		   adapter.notifyDataSetChanged();
    		    		   toast("Task deleted");
    		    	   }
    		       })
    		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    		    	   public void onClick(DialogInterface dialog, int id) {
    		    		   dialog.cancel();
    		    	   }
    		       });
			AlertDialog alert = builder.create();
			alert.show();
    		return true;
    	
    	default:
    		return super.onContextItemSelected(item);	
    	}
    }
    
    @Override
	protected void onActivityResult(int request_code, int result_code, 
    		Intent intent) {
    	if (request_code == ADD_TASK_REQUEST && result_code == RESULT_OK) {
    		Task task = intent.getParcelableExtra(AddTaskActivity.EXTRA_TASK);
    		tasks.add(0, task);
    		adapter.notifyDataSetChanged();
    	}
    }
    
    /**
     * This method is called when the user clicks the Add Task button. The
     * AddTaskActivity is started to get back an intent with the new task.
     * @param view The view from which the user called this method
     */
    public void getNewTask(View view) {
    	startActivityForResult(new Intent(this, AddTaskActivity.class), 
    			ADD_TASK_REQUEST);
    }
    
    /**
     * Reads tasks from a text file and populates a TaskList.
     */
    private void readTasksFromFile() {
    	BufferedReader file = null;
    	try {
    		file = new BufferedReader(new
    				InputStreamReader(openFileInput(TASK_FILE_NAME)));
    		String line;
    		while ((line = file.readLine()) != null) {
    			tasks.add(Task.taskFromString(line));
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
    
    /**
     * Writes the contents of a TaskList to a text file.
     */
    private void writeTasksToFile() {
    	String eol = System.getProperty("line.separator");
    	BufferedWriter file = null;
    	try {
    		file = new BufferedWriter(new 
    				OutputStreamWriter(openFileOutput(TASK_FILE_NAME, 
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
    
    /**
     * This is a convenience method which displays a message in a Toast
     * notification for a short duration.
     * @param message the message to be displayed
     */
    private void toast(String message)
    {
    	Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
