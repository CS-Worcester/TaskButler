/* 
 * SettingsActivity.java
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

package edu.worcester.cs499summer2012.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.adapter.TaskListAdapter;
import edu.worcester.cs499summer2012.database.TasksDataSource;
import edu.worcester.cs499summer2012.service.TaskAlarm;
import edu.worcester.cs499summer2012.service.TaskButlerService;
import edu.worcester.cs499summer2012.service.WakefulIntentService;
import edu.worcester.cs499summer2012.task.Task;

public class SettingsActivity extends SherlockPreferenceActivity implements 
	OnPreferenceClickListener, OnClickListener, OnPreferenceChangeListener {

	public static final String EDIT_CATEGORIES = "edit_categories";
	public static final String AUTO_SORT = "auto_sort";
	public static final String CUSTOM_SORT = "custom_sort";
	public static final String HIDE_COMPLETED = "hide_completed";
	public static final String DEFAULT_HOUR = "default_hour";
	public static final String DELETE_FINISHED_TASKS = "delete_finished_tasks";
	public static final String DELETE_ALL_TASKS = "delete_all_tasks";
	public static final String VIBRATE_ON_ALARM = "vibrate_on_alarm";
	public static final String REMINDER_TIME = "reminder_time";
	public static final String ALARM_TIME = "alarm_time";
	public static final String SORT_TYPE = "sort_type";
	public static final String DISPLAY_CATEGORY = "display_category";
	
	public static final String DEFAULT_REMINDER_TIME = "6";
	public static final String DEFAULT_ALARM_TIME = "15";
	public static final String DEFAULT_HOUR_VALUE = "12";
	private static final int DELETE_MODE_FINISHED = 0;
	private static final int DELETE_MODE_ALL = 1;
	
	private TasksDataSource data_source;
	private SharedPreferences prefs;
	private SharedPreferences.Editor prefs_editor;
	private int delete_mode;
	
	private PreferenceScreen ps_edit_categories;
	private CheckBoxPreference cbp_auto_sort;
	private CheckBoxPreference cbp_custom_sort;
	private PreferenceScreen ps_delete_finished_tasks;
	private PreferenceScreen ps_delete_all_tasks;
	private ListPreference lp_reminder_time;
	private ListPreference lp_alarm_time;
	private ListPreference lp_default_hour;
	
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preferences);
        
        // Allow Action bar icon to act as a button
        ActionBar action_bar = getSupportActionBar();
        action_bar.setIcon(R.drawable.ic_settings);
        action_bar.setHomeButtonEnabled(true);
        action_bar.setDisplayHomeAsUpEnabled(true);
        
		// Open the database
		data_source = TasksDataSource.getInstance(getApplicationContext());

		// Read preferences from file
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs_editor = prefs.edit();
        
		// Initialize preferences objects
        ps_edit_categories = (PreferenceScreen) this.findPreference(EDIT_CATEGORIES);
        cbp_auto_sort = (CheckBoxPreference) this.findPreference(AUTO_SORT);
        cbp_custom_sort = (CheckBoxPreference) this.findPreference(CUSTOM_SORT);
        ps_delete_finished_tasks = (PreferenceScreen) this.findPreference(DELETE_FINISHED_TASKS);
        ps_delete_all_tasks = (PreferenceScreen) this.findPreference(DELETE_ALL_TASKS);
        lp_reminder_time = (ListPreference) this.findPreference(REMINDER_TIME);
        lp_alarm_time = (ListPreference) this.findPreference(ALARM_TIME);
        lp_default_hour = (ListPreference) this.findPreference(DEFAULT_HOUR);
        
        // Set listeners
        ps_edit_categories.setOnPreferenceClickListener(this);
        cbp_auto_sort.setOnPreferenceClickListener(this);
        cbp_custom_sort.setOnPreferenceClickListener(this);
        ps_delete_finished_tasks.setOnPreferenceClickListener(this);
        ps_delete_all_tasks.setOnPreferenceClickListener(this);
        lp_reminder_time.setOnPreferenceChangeListener(this);
        lp_alarm_time.setOnPreferenceChangeListener(this);
        lp_default_hour.setOnPreferenceChangeListener(this);
        
        
        // Set checkbox states
        if (prefs.getInt(SORT_TYPE, TaskListAdapter.AUTO_SORT) == TaskListAdapter.AUTO_SORT) {
        	cbp_auto_sort.setChecked(true);
        	cbp_custom_sort.setChecked(false);
        } else {
        	cbp_auto_sort.setChecked(false);
        	cbp_custom_sort.setChecked(true);
        }
        
        // Set ListPreferences
        lp_reminder_time.setSummary(getReminderSummary(REMINDER_TIME, 
        		prefs.getString(REMINDER_TIME, DEFAULT_REMINDER_TIME)));
        lp_alarm_time.setSummary(getReminderSummary(ALARM_TIME, 
        		prefs.getString(ALARM_TIME, DEFAULT_ALARM_TIME)));
        lp_default_hour.setSummary(getHourSummary(prefs.getString(DEFAULT_HOUR, DEFAULT_HOUR_VALUE)));
    }
    
    private String getReminderSummary(String key, String value) {
    	StringBuilder builder = new StringBuilder();
    	
    	builder.append("Every ");
    	builder.append(value);
    	
    	if (key.equals(REMINDER_TIME))
    		builder.append(" hours");
    	else
    		builder.append(" minutes");
    		
    	return builder.toString();
    }
    
    private String getHourSummary(String value) {
		String summary;
		
		if (value.equals("0"))
			summary = "Midnight";
		else if (value.equals("12"))
			summary = "Noon";
		else if (value.equals("6") || value.equals("9"))
			summary = value + ":00 am";
		else if (value.equals("15"))
			summary = "3:00 pm";
		else
			summary = "6:00 pm";
							
		return summary;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == android.R.id.home) {
    		finish();
    		return true;
    	} else
    		return super.onOptionsItemSelected(item);
    }

	@Override
	public boolean onPreferenceClick(Preference p) {
		String key = p.getKey();
		
		if (key.equals(EDIT_CATEGORIES)) {
			Intent intent = new Intent(this, EditCategoriesActivity.class);
			startActivity(intent);
			return true;
		}
		
		if (key.equals(AUTO_SORT)) {
        	cbp_auto_sort.setChecked(true);
        	cbp_custom_sort.setChecked(false);
        	prefs_editor.putInt(SORT_TYPE, TaskListAdapter.AUTO_SORT);
        	prefs_editor.commit();
			return true;
		}
		
		if (key.equals(CUSTOM_SORT)) {
        	cbp_auto_sort.setChecked(false);
        	cbp_custom_sort.setChecked(true);
        	prefs_editor.putInt(SORT_TYPE, TaskListAdapter.CUSTOM_SORT);
        	prefs_editor.commit();
        	startActivity(new Intent(this, CustomSortActivity.class));
			return true;
		}
		
		if (key.equals(DELETE_FINISHED_TASKS)) {
			deleteAlert("Are you sure you want to delete all finished tasks? This cannot be undone. Repeating tasks will not be deleted.",
					DELETE_MODE_FINISHED);
			return true;
		}
		
		if (key.equals(DELETE_ALL_TASKS)) {
			deleteAlert("Are you sure you want to delete all tasks? This cannot be undone.",
					DELETE_MODE_ALL);
			return true;
		}
		
		return false;
	}
	
	private void deleteAlert(String question, final int mode) {
		delete_mode = mode;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(question)
		.setCancelable(true)
		.setTitle("Delete tasks")
		.setPositiveButton("Yes", this)
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		int deleted_tasks;
		
		switch (delete_mode) {
		case DELETE_MODE_FINISHED:
			deleted_tasks = data_source.deleteFinishedTasks();
			toastDeletedTasks(deleted_tasks);
			finish();
			break;

		case DELETE_MODE_ALL:
			ArrayList<Task> tasks = data_source.getTasks(true, null);
			
			// Alarm logic: Delete several tasks (SettingsActivity)
			// * Tasks must not be deleted from database yet!
			// * Iterate through list of tasks to be deleted:
			// * 	Cancel alarm
			TaskAlarm alarm = new TaskAlarm();
			for (Task task : tasks)
				alarm.cancelAlarm(this, task.getID());
			
			deleted_tasks = data_source.deleteAllTasks();
			toastDeletedTasks(deleted_tasks);
			finish();
			break;
		}
	}
	
	/**
	 * Displays a Toast notification informing the user about the number of
	 * tasks deleted.
	 * @param val the number of tasks deleted
	 */
	private void toastDeletedTasks(int val) {
		if (val == 0)
			toast("No tasks were deleted.");
		else if (val == 1)
			toast(val + " task deleted");
		else
			toast(val + " tasks deleted");
	}
	
	/**
	 * Displays a message in a Toast notification for a short duration.
	 */
	private void toast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		String key = preference.getKey();
		
		if (key.equals(REMINDER_TIME) || key.equals(ALARM_TIME)) {
			if (key.equals(REMINDER_TIME))
				lp_reminder_time.setSummary(getReminderSummary(REMINDER_TIME, (String) newValue));
			else
				lp_alarm_time.setSummary(getReminderSummary(ALARM_TIME, (String) newValue));
				
			// Start service which will update all of the task alarms with the new reminder intervals
			WakefulIntentService.acquireStaticLock(this);
			this.startService(new Intent(this, TaskButlerService.class));
			
			return true;   
		}
		
		if (key.equals(DEFAULT_HOUR)) {

			
			lp_default_hour.setSummary(getHourSummary((String) newValue));
			return true;
		}
			
		return false;
	}
}
