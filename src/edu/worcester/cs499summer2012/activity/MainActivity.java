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

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.gesture.GestureOverlayView;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.adapter.TaskListAdapter;
import edu.worcester.cs499summer2012.database.TasksDataSource;
import edu.worcester.cs499summer2012.service.TaskAlarm;
import edu.worcester.cs499summer2012.service.TaskButlerService;
import edu.worcester.cs499summer2012.service.WakefulIntentService;
import edu.worcester.cs499summer2012.task.Category;
import edu.worcester.cs499summer2012.task.Task;

/**
 * Main app activity. Displays current task list and allows user to access
 * task creation, task modification, and task sorting activities.
 * @author Jonathan Hasenzahl
 * @author Dhimitraq Jorgji
 * @author James Celona
 */
public final class MainActivity extends SherlockListActivity implements 
OnItemLongClickListener, ActionMode.Callback, OnClickListener, OnGestureListener, OnTouchListener {

	/**************************************************************************
	 * Static fields and methods                                              *
	 **************************************************************************/

	public static final String PREF_SORT_TYPE = "sort_type";
	public static final String DISPLAY_CATEGORY = "display_category";
	public static final int ADD_TASK_REQUEST = 0;
	public static final int VIEW_TASK_REQUEST = 1;
	public static final int DELETE_MODE_SINGLE = 0;
	public static final int DELETE_MODE_FINISHED = 1;
	public static final int DELETE_MODE_ALL = 2;
	public static final int DISPLAY_ALL_CATEGORIES = 1;

	/**************************************************************************
	 * Private fields                                                         *
	 **************************************************************************/

	private TasksDataSource data_source;
	private SharedPreferences prefs;
	private SharedPreferences.Editor prefs_editor;
	private static TaskListAdapter adapter;
	private GestureDetector gesture_detector;
	private Object action_mode;
	private int selected_task;

	/**************************************************************************
	 * Class methods                                                          *
	 **************************************************************************/

	/**
	 * Displays a message in a Toast notification for a short duration.
	 */
	private void toast(String message)
	{
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	private void deleteAlert(String question, final int mode)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(question)
		.setCancelable(true)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				int deleted_tasks;
				switch (mode) {
				case DELETE_MODE_SINGLE:
					Task task = adapter.getItem(selected_task);
					data_source.deleteTask(task);
					adapter.remove(task);
					if (task.hasDateDue()) {
						TaskAlarm alarm = new TaskAlarm();
						alarm.cancelAlarm(getApplicationContext(), task.getID());
					}
					toast("Task deleted");
					break;

				case DELETE_MODE_FINISHED:
					deleted_tasks = data_source.deleteFinishedTasks();
					for (int i = 0; i < adapter.getCount(); i++)
					{
						if (adapter.getItem(i).isCompleted())
						{
							adapter.remove(adapter.getItem(i));
							i--;
						}
					}
					toast(deleted_tasks + " tasks deleted");
					break;

				case DELETE_MODE_ALL:
					ArrayList<Task> tasks = data_source.getAllTasks();
					TaskAlarm alarm = new TaskAlarm();
					for (Task t : tasks) {
						if (t.hasDateDue())
							alarm.cancelAlarm(getApplicationContext(), t.getID());
					}
					deleted_tasks = data_source.deleteAllTasks();
					adapter.clear();
					toast(deleted_tasks + " tasks deleted");
					break;
				}
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	public static synchronized TaskListAdapter getAdapter(){
		return adapter;
	}
	
	private void createCategoryBar(int display_category) {
		// Populate bottom category bar
		ArrayList<Category> categories = data_source.getCategories();
		
		if (categories.size() == 1) {
			findViewById(R.id.main_ruler).setVisibility(View.GONE);
			((HorizontalScrollView) findViewById(R.id.main_category_bar_scroll)).setVisibility(View.GONE);
		} else {
			LinearLayout category_bar = (LinearLayout) findViewById(R.id.main_category_bar);
			category_bar.removeAllViews();
			LayoutInflater inflater = getLayoutInflater();
			
			for (Category category : categories) {
				View view = inflater.inflate(R.layout.category_bar_item, null);
				
				TextView name = (TextView) view.findViewById(R.id.main_category_bar_item_name);
				View color = view.findViewById(R.id.main_category_bar_item_color);
				
				color.setBackgroundColor(category.getColor());
				
				if (category.getID() == DISPLAY_ALL_CATEGORIES)
					name.setText(R.string.text_main_all_categories);
				else
					name.setText(category.getName());
				
				Resources r = getResources();
				
				if (display_category == category.getID()) {
					name.setBackgroundColor(r.getColor(android.R.color.background_light));
					name.setTextColor(r.getColor(android.R.color.secondary_text_light));
				} else {
					name.setBackgroundColor(r.getColor(android.R.color.background_dark));
					name.setTextColor(r.getColor(android.R.color.secondary_text_dark));
				}
				
				view.setTag(category);
				view.setOnClickListener(this);
				
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
				params.setMargins(2, 2, 2, 2);
				category_bar.addView(view, params);
			}
		}
	}

	/**************************************************************************
	 * Overridden parent methods                                              *
	 **************************************************************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Assign the layout to this activity
		setContentView(R.layout.activity_main);

		// Open the database
		data_source = TasksDataSource.getInstance(getApplicationContext());

		// Read preferences from file
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs_editor = prefs.edit();

		// Initialize gesture detector and set an onTouchListener to the gesture overlay
		gesture_detector = new GestureDetector(this, this);
		GestureOverlayView overlay = (GestureOverlayView) findViewById(R.id.main_gesture_overlay);
		overlay.setOnTouchListener(this);
		
		// Set an onItemLongClickListener to the list view
		getListView().setOnItemLongClickListener(this);
		
		//Start service to check for alarms
		WakefulIntentService.acquireStaticLock(this);
		this.startService(new Intent(this, TaskButlerService.class));
	}

	@Override
	public void onStart() {
		super.onStart();
		
		// Create an adapter for the task list
		int display_category = prefs.getInt(DISPLAY_CATEGORY, DISPLAY_ALL_CATEGORIES);
		if (display_category == DISPLAY_ALL_CATEGORIES)
			adapter = new TaskListAdapter(this, data_source.getAllTasks());
		else
			adapter = new TaskListAdapter(this, data_source.getTasksByCategory(data_source.getCategory(display_category)));
		setListAdapter(adapter);

		// Set sort type and sort the list
		adapter.setSortType(prefs.getInt(PREF_SORT_TYPE, 
				TaskListAdapter.AUTO_SORT));
		adapter.sort();
		
		createCategoryBar(display_category);
	}
	
	@Override
	public void onStop() {
		// Destroy the adapter, it will be recreated in onStart
		adapter = null;

		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);

		//add switch account button if more than 2 accounts on the device
		// TODO: (Jon) Figure out why this isn't working for me
		/*if (accountManager.getAccounts().length >= 2) {
			menu.add(0, MENU_ACCOUNTS, 0, "Switch Account");
		}*/
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
			prefs_editor.commit();
			adapter.sort();
			return true;

		case R.id.menu_main_custom_sort:
			adapter.setSortType(TaskListAdapter.CUSTOM_SORT);
			prefs_editor.putInt(PREF_SORT_TYPE, TaskListAdapter.CUSTOM_SORT);
			prefs_editor.commit();
			startActivity(new Intent(this, CustomSortActivity.class));
			return true;

		case R.id.menu_delete_finished:
			deleteAlert("Are you sure you want to delete all completed tasks? This cannot be undone.",
					DELETE_MODE_FINISHED);
			return true;

		case R.id.menu_delete_all:
			deleteAlert("Are you sure you want to delete all tasks? This cannot be undone.",
					DELETE_MODE_ALL);
			return true;	

		case R.id.menu_main_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;

		case R.id.menu_main_about:
			AlertDialog.Builder about_builder = new AlertDialog.Builder(this);
			about_builder.setTitle("About Task Butler");
			about_builder.setMessage(R.string.dialog_about);
			about_builder.setCancelable(true);
			about_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			about_builder.setNeutralButton("Source", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/CS-Worcester/CS499Summer2012"));
					startActivity(browserIntent);
					dialog.dismiss();
				}
			});
			about_builder.create().show();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onListItemClick(ListView list_view, View view, int position, 
			long id) {
		Intent intent = new Intent(this, ViewTaskActivity.class);
		intent.putExtra(Task.EXTRA_TASK_ID, adapter.getItem(position).getID());
		startActivityForResult(intent, VIEW_TASK_REQUEST);
	}

	@Override
	public void onActivityResult(int request_code, int result_code, 
			Intent intent) {
		Task task;
		switch(request_code){
		case ADD_TASK_REQUEST:
			if(result_code == RESULT_OK){
				// Get the task from the db using the ID in the intent
				task = data_source.getTask(intent.getIntExtra(Task.EXTRA_TASK_ID, 0));

				if (!task.isCompleted() && task.hasDateDue() &&
						(task.getDateDue() >= System.currentTimeMillis())) {
					TaskAlarm alarm = new TaskAlarm();
					alarm.setAlarm(this, task.getID());
				}
			}
			break;

		case VIEW_TASK_REQUEST:
			if(result_code == RESULT_OK){
				// Get the task from the db using the ID in the intent
				task = data_source.getTask(intent.getIntExtra(Task.EXTRA_TASK_ID, 0));
				
				if (!task.isCompleted() && task.hasDateDue() &&
						(task.getDateDue() >= System.currentTimeMillis())) {
					TaskAlarm alarm = new TaskAlarm();
					alarm.setAlarm(this, task.getID());
				}
			}
			break;
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
			//startActivityForResult(new Intent(this, EditTaskActivity.class), 
        		//	ADD_TASK_REQUEST);
			toast("Coming soon!");
			mode.finish();
			return true;

		case R.id.menu_main_delete_task:
			deleteAlert("Are you sure you want to delete this task?",
					DELETE_MODE_SINGLE);
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


	/**************************************************************************
	 * Methods implementing OnClickListener interface                         *
	 **************************************************************************/  
	
	@Override
	public void onClick(View v) {
		Category category = (Category) v.getTag();
		adapter.clear();
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (category.getID() != DISPLAY_ALL_CATEGORIES)
				adapter.addAll(data_source.getTasksByCategory(category));
			else
				adapter.addAll(data_source.getAllTasks());
		} else {
			// addAll is not supported in under API 11
			if (category.getID() != DISPLAY_ALL_CATEGORIES) {
				for (Task task : data_source.getTasksByCategory(category))
					adapter.add(task);
			} else {
				for (Task task : data_source.getAllTasks())
					adapter.add(task);
			}
		}
		
		adapter.sort();
		
		createCategoryBar(category.getID());
		
		prefs_editor.putInt(DISPLAY_CATEGORY, category.getID());
		prefs_editor.commit();
	}

	/**************************************************************************
	 * Methods implementing OnGestureListener interface                       *
	 **************************************************************************/
	
	@Override
	public boolean onDown(MotionEvent e) {
		// Not used
		return false;
	}

	/**
	 * This method is called when the user flings/swipes the list view. Swiping
	 * right or left will change the display category, if there are defined
	 * categories.
	 * @param e1 Not used
	 * @param e2 Not used
	 * @param velocityX The velocity (pixels per second) of the swipe along the X-axis.
	 * @param velocityY Not used
	 * @return true if the user swiped left or right, false otherwise
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		
		// Get list of categories
		ArrayList<Category> categories = data_source.getCategories();
		
		// Swiping won't work unless there are categories
		if (categories.size() == 1)
			return false;
		
		// Get selected category
		Category current_category = data_source.getCategory(prefs.getInt(DISPLAY_CATEGORY, 1));
		
		int current_index = categories.indexOf(current_category);
		int new_index;
		
		if (velocityX <= -1000) {
			// Swipe left: increase index by 1
			
			// Check if we are at the end of the list
			if (current_index == categories.size() - 1)
				return false;
			
			new_index = current_index + 1;
		} else if (velocityX >= 1000) {
			// Swipe right: decrease index by 1
			
			// Check if we are at the beginning of the list
			if (current_index == 0)
				return false;
			
			new_index = current_index - 1;
		} else
			// A clear left or right swipe was not registered
			return false;
		
		// Swiping has the same result as the user clicking on a category, so
		// let's tag a view with the new category and send it over to onClick
		View view = new View(this);
		view.setTag(categories.get(new_index));
		onClick(view);
		
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// Not used
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// Not used
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// Not used
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// Not used
		return false;
	}

	/**************************************************************************
	 * Methods implementing OnTouchListener interface                         *
	 **************************************************************************/
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		gesture_detector.onTouchEvent(event);
		return true;
	}
}
