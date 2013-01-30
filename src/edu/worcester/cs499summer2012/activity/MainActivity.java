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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
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

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.adapter.TaskListAdapter;
import edu.worcester.cs499summer2012.database.TasksDataSource;
import edu.worcester.cs499summer2012.service.TaskAlarm;
import edu.worcester.cs499summer2012.service.TaskButlerService;
import edu.worcester.cs499summer2012.service.TaskButlerWidgetProvider;
import edu.worcester.cs499summer2012.service.WakefulIntentService;
import edu.worcester.cs499summer2012.task.BackupManager;
import edu.worcester.cs499summer2012.task.Category;
import edu.worcester.cs499summer2012.task.Task;
import edu.worcester.cs499summer2012.task.ToastMaker;

/**
 * Main app activity. Displays current task list and allows user to access
 * task creation, task modification, and task sorting activities.
 * @author Jonathan Hasenzahl
 * @author Dhimitraq Jorgji
 * @author James Celona
 */
public final class MainActivity extends SherlockListActivity implements 
OnItemLongClickListener, ActionMode.Callback, OnClickListener, OnGestureListener, OnTouchListener, android.content.DialogInterface.OnClickListener {

	/**************************************************************************
	 * Static fields and methods                                              *
	 **************************************************************************/
	
	public static final int ADD_TASK_REQUEST = 0;
	public static final int VIEW_TASK_REQUEST = 1;
	public static final int EDIT_TASK_REQUEST = 2;
	public static final int DISPLAY_ALL_CATEGORIES = 1;
	
	private static final int DELETE_MODE_SINGLE = 0;
	private static final int DELETE_MODE_FINISHED = 1;
	private static final int DELETE_MODE_ALL = 2;

	/**************************************************************************
	 * Private fields                                                         *
	 **************************************************************************/

	private TasksDataSource data_source;
	private SharedPreferences prefs;
	private SharedPreferences.Editor prefs_editor;
	private static TaskListAdapter adapter;
	private BackupManager backup_manager;
	private GestureDetector gesture_detector;
	private Object action_mode;
	private int selected_task;
	private int delete_mode;
	private ArrayList<Category> categories;
	private Context context;
	private Activity activity;

	/**************************************************************************
	 * Class methods                                                          *
	 **************************************************************************/

	private void deleteAlert(int resId, final int mode) {
		delete_mode = mode;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(resId)
		.setCancelable(true)
		.setPositiveButton(R.string.menu_delete_task, this)
		.setNegativeButton(R.string.menu_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}
	
	/**
	 * Displays a Toast notification informing the user about the number of
	 * tasks deleted.
	 * @param val the number of tasks deleted
	 */
	private void toastDeletedTasks(int val) {
		if (val == 0)
			ToastMaker.toast(this, R.string.toast_no_tasks_deleted);
		else if (val == 1)
			ToastMaker.toast(this, val + " task deleted");
		else
			ToastMaker.toast(this, val + " tasks deleted");
	}

	public static synchronized TaskListAdapter getAdapter(){
		return adapter;
	}

	private void createCategoryBar(int display_category) {
		// Populate bottom category bar
		ArrayList<Category> all_categories = data_source.getCategories();
		categories = new ArrayList<Category>(all_categories);
		
		for (Category category : all_categories) {
			if (!data_source.categoryHasTasks(category) && category.getID() != Category.NO_CATEGORY)
				categories.remove(category);
		}

		if (categories.size() == 1) {
			findViewById(R.id.main_ruler).setVisibility(View.GONE);
			((HorizontalScrollView) findViewById(R.id.main_category_bar_scroll)).setVisibility(View.GONE);
		} else {
			findViewById(R.id.main_ruler).setVisibility(View.VISIBLE);
			((HorizontalScrollView) findViewById(R.id.main_category_bar_scroll)).setVisibility(View.VISIBLE);
			LinearLayout category_bar = (LinearLayout) findViewById(R.id.main_category_bar);
			category_bar.removeAllViews();
			LayoutInflater inflater = getLayoutInflater();

			for (Category category : categories) {
				View view = inflater.inflate(R.layout.category_bar_item, null);

				TextView name = (TextView) view.findViewById(R.id.main_category_bar_item_name);
				View color = view.findViewById(R.id.main_category_bar_item_color);
				View selected = view.findViewById(R.id.main_category_bar_item_selection);

				color.setBackgroundColor(category.getColor());

				if (category.getID() == DISPLAY_ALL_CATEGORIES)
					name.setText(R.string.text_main_all_categories);
				else
					name.setText(category.getName());

				if (display_category == category.getID()) {
					selected.setBackgroundColor(Color.LTGRAY);
					name.setTextColor(Color.WHITE);
				} else {
					selected.setBackgroundColor(Color.TRANSPARENT);
					name.setTextColor(Color.LTGRAY);
				}

				view.setTag(category);
				view.setOnClickListener(this);

				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
				params.setMargins(4, 4, 4, 4);
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
		activity = this;
		context = this;

		// Open the database
		data_source = TasksDataSource.getInstance(this);

		// Read preferences from file
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs_editor = prefs.edit();

		// Initialize gesture detector and set an onTouchListener to the gesture overlay
		gesture_detector = new GestureDetector(this, this);
		GestureOverlayView overlay = (GestureOverlayView) findViewById(R.id.main_gesture_overlay);
		overlay.setOnTouchListener(this);

		// Set an onItemLongClickListener to the list view
		getListView().setOnItemLongClickListener(this);
		getListView().setOnTouchListener(this);

		//Start service to check for alarms
		WakefulIntentService.acquireStaticLock(this);
		this.startService(new Intent(this, TaskButlerService.class));
	}

	@Override
	public void onStart() {
		super.onStart();
		
		boolean hide_completed = prefs.getBoolean(SettingsActivity.HIDE_COMPLETED, true);

		// Create an adapter for the task list
		int display_category = prefs.getInt(SettingsActivity.DISPLAY_CATEGORY, DISPLAY_ALL_CATEGORIES);
		if (display_category == DISPLAY_ALL_CATEGORIES)
			adapter = new TaskListAdapter(this, data_source.getTasks(!hide_completed, null));
		else {
			Category cat = data_source.getCategory(display_category);
			if (cat != null)
				adapter = new TaskListAdapter(this, data_source.getTasks(!hide_completed, cat));
			else {
				// Bug fix: trying to load a category that doesn't exist (has been deleted)
				// will crash the app.
				prefs_editor.putInt(SettingsActivity.DISPLAY_CATEGORY, DISPLAY_ALL_CATEGORIES);
				prefs_editor.commit();
				display_category = DISPLAY_ALL_CATEGORIES;
				adapter = new TaskListAdapter(this, data_source.getTasks(!hide_completed, null));
			}
		}
		setListAdapter(adapter);

		// Set sort type and sort the list
		adapter.setSortType(prefs.getInt(SettingsActivity.SORT_TYPE, TaskListAdapter.AUTO_SORT));
		adapter.sort();
		
		adapter.setActivity(this);

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
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_main_add_task:
			startActivityForResult(new Intent(this, AddTaskActivity.class), 
					ADD_TASK_REQUEST);
			return true;
			
		case R.id.menu_main_categories:
			startActivity(new Intent(this, EditCategoriesActivity.class));
			return true;
			
		case R.id.menu_delete_finished:
			deleteAlert(R.string.dialog_delete_completed, DELETE_MODE_FINISHED);
			return true;
			
		case R.id.menu_delete_all:
			deleteAlert(R.string.dialog_delete_all, DELETE_MODE_ALL);
			return true;
			
		case R.id.menu_backup_restore:
			backup_manager = new BackupManager();
			StringBuilder message = new StringBuilder();
			
			message.append(getString(R.string.dialog_last_backup));
			message.append(' ');
			long date = backup_manager.getBackupDate();
			message.append(date == BackupManager.NO_BACKUP_EXISTS ? 
					getString(R.string.dialog_no_backup) :
					DateFormat.format("M/d/yy h:mmaa", date));
			
			AlertDialog.Builder backup_builder = new AlertDialog.Builder(this);
			backup_builder.setTitle(R.string.menu_backup_restore)
					.setMessage(message.toString())
					.setCancelable(true)
					.setPositiveButton(R.string.menu_backup, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ToastMaker.toast(context, BackupManager.interpretStringCode(backup_manager.backup()));
							dialog.dismiss();
						}
					})
					.setNeutralButton(R.string.menu_restore, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (backup_manager.getBackupDate() == BackupManager.NO_BACKUP_EXISTS) {
								ToastMaker.toast(context, BackupManager.interpretStringCode(BackupManager.NO_RESTORE_EXISTS));
								dialog.dismiss();
								return;
							}
							
							AlertDialog.Builder restore_builder = new AlertDialog.Builder(context);
							restore_builder.setMessage(R.string.dialog_restore_backup)
									.setCancelable(true)
									.setPositiveButton(R.string.menu_restore, new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// Alarm logic: Restore database
											// * Iterate through list of tasks
											// * 	Cancel alarm
											// *    Cancel existing notifications
											ArrayList<Task> tasks = data_source.getTasks(true, null);
											TaskAlarm alarm = new TaskAlarm();
											for (Task task : tasks) {
												alarm.cancelAlarm(context, task.getID());
												alarm.cancelNotification(context, task.getID());
											}
											
											// Disconnect database
											data_source = null;
											
											// Restore tasks and inform the user
											String result = backup_manager.restore();
											ToastMaker.toast(context, BackupManager.interpretStringCode(result));
											
											// Reconnect database
											data_source = TasksDataSource.getInstance(context);
											
											// Remake the task list
											adapter.clear();
											boolean hide_completed = prefs.getBoolean(SettingsActivity.HIDE_COMPLETED, true);
											if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
												adapter.addAll(data_source.getTasks(!hide_completed, null));
											} else {
												// addAll is not supported in under API 11
												for (Task task : data_source.getTasks(!hide_completed, null))
														adapter.add(task);
											}
											adapter.sort();
											
											// Remake categories bar and set "all categories" to be the default
											createCategoryBar(DISPLAY_ALL_CATEGORIES);
											prefs_editor.putInt(SettingsActivity.DISPLAY_CATEGORY, DISPLAY_ALL_CATEGORIES);
											prefs_editor.commit();
											
											// Update widget
											if (result.equals(BackupManager.RESTORE_OK)) {
												// Update homescreen widget (after change has been saved to DB)
												TaskButlerWidgetProvider.updateWidget(activity);
											}
										}
									})
									.setNegativeButton(R.string.menu_cancel, new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.cancel();
										}
									});
							
							restore_builder.create().show();
							dialog.dismiss();
						}
					})
					.setNegativeButton(R.string.menu_cancel, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			backup_builder.show();
			return true;

		case R.id.menu_main_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;

		case R.id.menu_main_about:
			AlertDialog.Builder about_builder = new AlertDialog.Builder(this);
			about_builder.setTitle(R.string.dialog_about_title);
			about_builder.setIcon(R.drawable.ic_about);
			about_builder.setMessage(R.string.dialog_about);
			about_builder.setCancelable(true);
			about_builder.setPositiveButton(R.string.menu_ok, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			about_builder.setNeutralButton(R.string.menu_source, new DialogInterface.OnClickListener() {

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

		// There is currently no special handling for activity results
		
		/*switch(request_code){
		case EDIT_TASK_REQUEST:
		case VIEW_TASK_REQUEST:
		case ADD_TASK_REQUEST:
			if(result_code == RESULT_OK){

			}
			break;
		}*/
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

			Intent intent = new Intent(this, EditTaskActivity.class);
			intent.putExtra(Task.EXTRA_TASK_ID, adapter.getItem(selected_task).getID());
			startActivityForResult(intent, EDIT_TASK_REQUEST);
			mode.finish();
			return true;

		case R.id.menu_main_delete_task:
			deleteAlert(R.string.dialog_delete_single, DELETE_MODE_SINGLE);
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
		
		boolean hide_completed = prefs.getBoolean(SettingsActivity.HIDE_COMPLETED, true);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (category.getID() != DISPLAY_ALL_CATEGORIES)
				adapter.addAll(data_source.getTasks(!hide_completed, category));
			else
				adapter.addAll(data_source.getTasks(!hide_completed, null));
		} else {
			// addAll is not supported in under API 11
			if (category.getID() != DISPLAY_ALL_CATEGORIES) {
				for (Task task : data_source.getTasks(!hide_completed, category))
					adapter.add(task);
			} else {
				for (Task task : data_source.getTasks(!hide_completed, null))
					adapter.add(task);
			}
		}

		adapter.sort();

		createCategoryBar(category.getID());

		prefs_editor.putInt(SettingsActivity.DISPLAY_CATEGORY, category.getID());
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

		// TODO: This logic doesn't work that well. Find a better way to swipe.
		/*
		// Swiping won't work unless there are categories
		if (categories.size() == 1)
			return true;

		// Get selected category
		Category current_category = data_source.getCategory(prefs.getInt(SettingsActivity.DISPLAY_CATEGORY, DISPLAY_ALL_CATEGORIES));

		int current_index = categories.indexOf(current_category);
		int new_index;

		if (velocityX <= -500) {
			// Swipe left: increase index by 1

			// Check if we are at the end of the list
			if (current_index == categories.size() - 1)
				return true;

			new_index = current_index + 1;
		} else if (velocityX >= 500) {
			// Swipe right: decrease index by 1

			// Check if we are at the beginning of the list
			if (current_index == 0)
				return true;

			new_index = current_index - 1;
		} else
			// A clear left or right swipe was not registered
			return true;

		// Swiping has the same result as the user clicking on a category, so
		// let's tag a view with the new category and send it over to onClick
		View view = new View(this);
		view.setTag(categories.get(new_index));
		onClick(view);
		*/

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
		return gesture_detector.onTouchEvent(event);
	}
	
	/**************************************************************************
	 * Methods implementing DialogInterFace.OnClickListener interface         *
	 **************************************************************************/

	@Override
	public void onClick(DialogInterface dialog, int which) {
		TaskAlarm alarm;
		int deleted_tasks;
		switch (delete_mode) {
		case DELETE_MODE_SINGLE:
			Task task = adapter.getItem(selected_task);
			
			// Alarm logic: Delete a task
			// * Task must not be deleted from database yet!
			// * Cancel alarm
			// * Cancel existing notification
			alarm = new TaskAlarm();
			alarm.cancelAlarm(this, task.getID());
			alarm.cancelNotification(this, task.getID());
			
			data_source.deleteTask(task);
			adapter.remove(task);
			ToastMaker.toast(this, R.string.toast_task_deleted);
			
			// Update homescreen widget (after change has been saved to DB)
			TaskButlerWidgetProvider.updateWidget(this);
			break;
			
		case DELETE_MODE_FINISHED:
			deleted_tasks = data_source.deleteFinishedTasks();
			
			for (int i = 0; i < adapter.getCount(); i++) {
				if (adapter.getItem(i).isCompleted()) {
					adapter.remove(adapter.getItem(i));
					i--;
				}
			}
			
			ToastMaker.toast(this, R.string.toast_task_deleted);
			toastDeletedTasks(deleted_tasks);
			break;
			
		case DELETE_MODE_ALL:
			ArrayList<Task> tasks = data_source.getTasks(true, null);
			
			// Alarm logic: Delete several tasks (SettingsActivity)
			// * Tasks must not be deleted from database yet!
			// * Iterate through list of tasks to be deleted:
			// * 	Cancel alarm
			// *    Cancel existing notifications
			alarm = new TaskAlarm();
			for (Task t : tasks) {
				alarm.cancelAlarm(this, t.getID());
				alarm.cancelNotification(this, t.getID());
			}
			
			deleted_tasks = data_source.deleteAllTasks();
			adapter.clear();
			toastDeletedTasks(deleted_tasks);
			
			// Update homescreen widget (after change has been saved to DB)
			TaskButlerWidgetProvider.updateWidget(this);
			break;
		}
				
		int display_category = prefs.getInt(SettingsActivity.DISPLAY_CATEGORY, DISPLAY_ALL_CATEGORIES);
		createCategoryBar(display_category);
	}
}