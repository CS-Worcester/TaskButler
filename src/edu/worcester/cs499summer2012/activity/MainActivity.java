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
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.adapter.TaskListAdapter;
import edu.worcester.cs499summer2012.task.DeprecatedTask;

/**
 * Main app activity. Displays current task list and allows user to access
 * task creation, task modification, and task sorting activities.
 * @author Jonathan Hasenzahl
 * @author James Celona
 */
public class MainActivity extends SherlockListActivity implements OnItemLongClickListener, 
		ActionMode.Callback {

	/**************************************************************************
	 * Static fields and methods                                              *
	 **************************************************************************/
	
	public static final String TASK_FILE_NAME = "tasks";
	public static final String PREF_SORT_TYPE = "sort_type";
	public static final int ADD_TASK_REQUEST = 0;
	public static final int DELETE_MODE_SINGLE = 0;
	public static final int DELETE_MODE_FINISHED = 1;
	public static final int DELETE_MODE_ALL = 2;

	/**************************************************************************
	 * Private fields                                                         *
	 **************************************************************************/
		
	private SharedPreferences prefs;
	private SharedPreferences.Editor prefs_editor;
    private TaskListAdapter adapter;
    private Object action_mode;
    private int selected_task;

	/**************************************************************************
	 * Class methods                                                          *
	 **************************************************************************/
    
    /**
     * Reads settings from a SharedPreferences file.
     */
    private void readSettingsFromFile() {
    	prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	prefs_editor = prefs.edit();
    	
    	adapter.setSortType(prefs.getInt(PREF_SORT_TYPE, 
    			TaskListAdapter.AUTO_SORT));
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
    			adapter.add(DeprecatedTask.taskFromString(line));
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
     * Writes settings to a SharedPreferences file.
     */
    private void writeSettingsToFile() {
    	prefs_editor.commit();
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
			for (int i = 0; i < adapter.getCount(); i++) {
				file.write(adapter.getItem(i) + eol);
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
     * Displays a message in a Toast notification for a short duration.
     */
    private void toast(String message)
    {
    	Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    private void deleteAlert(String question, final int mode, 
    		final String confirmation)
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(question)
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		    	   public void onClick(DialogInterface dialog, int id) {
		    		   switch (mode) {
		    		   case DELETE_MODE_SINGLE:
		    			   adapter.remove(adapter.getItem(selected_task));
		    			   break;
		    		   case DELETE_MODE_FINISHED:
		    			   for (int i = 0; i < adapter.getCount(); i++)
		    			   {
		    				   if (adapter.getItem(i).getIsCompleted())
		    				   {
		    					   adapter.remove(adapter.getItem(i));
		    					   i--;
		    				   }
		    			   }
		    			   break;
		    		   case DELETE_MODE_ALL:
		    			   adapter.clear();
		    			   break;
		    		   }
		    		   toast(confirmation);
		    	   }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		    	   public void onClick(DialogInterface dialog, int id) {
		    		   dialog.cancel();
		    	   }
		       });
		AlertDialog alert = builder.create();
		alert.show();
    }
    
	/**************************************************************************
	 * Overridden parent methods                                              *
	 **************************************************************************/
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Assign the layout to this activity
        setContentView(R.layout.activity_main);
    	
    	// Create an adapter for the task list
		adapter = new TaskListAdapter(this, new ArrayList<DeprecatedTask>(0));
    	setListAdapter(adapter);
    	
        // Read tasks from file
    	readTasksFromFile();
    	
        // Read settings from file
    	readSettingsFromFile();
    	
    	// Set up a long item click listener
    	getListView().setOnItemLongClickListener(this);
    }
    
    @Override
	public void onStop() {
    	writeTasksToFile();
    	writeSettingsToFile();
    	super.onStop();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getSupportMenuInflater();
    	inflater.inflate(R.menu.activity_main, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.menu_main_add_task:
    		startActivityForResult(new Intent(this, AddTaskActivity.class), 
        			ADD_TASK_REQUEST);
    		return true;
    		
    	case R.id.menu_main_sort:
    		SubMenu sort_menu = item.getSubMenu();
    		sort_menu.getItem(adapter.getSortType()).setChecked(true);
    		return true;
    		
    	case R.id.menu_main_auto_sort:
    		adapter.setSortType(TaskListAdapter.AUTO_SORT);
    		prefs_editor.putInt(PREF_SORT_TYPE, TaskListAdapter.AUTO_SORT);
    		adapter.sort();
    		return true;
    		
    	case R.id.menu_main_custom_sort:
    		adapter.setSortType(TaskListAdapter.CUSTOM_SORT);
    		prefs_editor.putInt(PREF_SORT_TYPE, TaskListAdapter.CUSTOM_SORT);
    		return true;
    		
    	case R.id.menu_delete_finished:
    		deleteAlert("Are you sure you want to delete all completed tasks? This cannot be undone.",
    				DELETE_MODE_FINISHED, "Tasks deleted");
    		return true;
    		
    	case R.id.menu_delete_all:
    		deleteAlert("Are you sure you want to delete all tasks? This cannot be undone.",
    				DELETE_MODE_ALL, "All tasks deleted");
    		return true;
    		
    	case R.id.menu_main_settings:
    		startActivity(new Intent(this, SettingsActivity.class));
    		return true;
    		
    	case R.id.menu_main_help:
    		toast("Help coming soon!");
    		return true;
    		
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
    @Override
    public void onListItemClick(ListView list_view, View view, int position, 
    		long id) {
    	adapter.getItem(position).toggleIsCompleted();
    	adapter.sort();
    }
    
    @Override
	public void onActivityResult(int request_code, int result_code, 
    		Intent intent) {
    	if (request_code == ADD_TASK_REQUEST && result_code == RESULT_OK) {
    		DeprecatedTask deprecatedTask = intent.getParcelableExtra(AddTaskActivity.EXTRA_TASK);
    		adapter.add(deprecatedTask);
    		adapter.sort();
    	}
    }
    
	/**************************************************************************
	 * Methods implementing OnItemLongClickListener interface                 *
	 **************************************************************************/ 

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, 
			int position, long id) {
		if (action_mode != null)
			return false;
		
		selected_task = position;
		action_mode = startActionMode(this);
		view.setSelected(true);
		return true;
	}
    
	/**************************************************************************
	 * Methods implementing ActionMode.Callback interface                     *
	 **************************************************************************/    

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.context_modify_task, menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// Return false if nothing is done
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_main_edit_task:
			toast("Coming soon!");
			mode.finish();
			return true;
		
		case R.id.menu_main_delete_task:
			deleteAlert("Are you sure you want to delete this task?",
					DELETE_MODE_SINGLE, "Task deleted");
			mode.finish();
			return true;
			
		default:
			return false;
		}
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		action_mode = null;			
	}
	
}
