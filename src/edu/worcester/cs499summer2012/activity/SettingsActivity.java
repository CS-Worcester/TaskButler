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
import edu.worcester.cs499summer2012.task.Task;

public class SettingsActivity extends SherlockPreferenceActivity implements 
	OnPreferenceClickListener, OnClickListener, OnPreferenceChangeListener {

	public static final String EDIT_CATEGORIES = "edit_categories";
	public static final String AUTO_SORT = "auto_sort";
	public static final String CUSTOM_SORT = "custom_sort";
	public static final String HIDE_COMPLETED = "hide_completed";
	public static final String DELETE_FINISHED_TASKS = "delete_finished_tasks";
	public static final String DELETE_ALL_TASKS = "delete_all_tasks";
	public static final String REMINDER_TIME = "reminder_time";
	public static final String ALARM_TIME = "alarm_time";
	public static final String SORT_TYPE = "sort_type";
	public static final String DISPLAY_CATEGORY = "display_category";
	
	private static final String DEFAULT_REMINDER_TIME = "24";
	private static final String DEFAULT_ALARM_TIME = "15";
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
	
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preferences);
        
        // Allow Action bar icon to act as a button
        ActionBar action_bar = getSupportActionBar();
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
        
        // Set listeners
        ps_edit_categories.setOnPreferenceClickListener(this);
        cbp_auto_sort.setOnPreferenceClickListener(this);
        cbp_custom_sort.setOnPreferenceClickListener(this);
        ps_delete_finished_tasks.setOnPreferenceClickListener(this);
        ps_delete_all_tasks.setOnPreferenceClickListener(this);
        lp_reminder_time.setOnPreferenceChangeListener(this);
        lp_alarm_time.setOnPreferenceChangeListener(this);
        
        
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
			deleteAlert("Are you sure you want to delete all completed tasks? This cannot be undone.",
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
			toast(deleted_tasks + " tasks deleted");
			finish();
			break;

		case DELETE_MODE_ALL:
			ArrayList<Task> tasks = data_source.getTasks(true, null);
			TaskAlarm alarm = new TaskAlarm();
			for (Task t : tasks) {
				if (t.hasDateDue())
					alarm.cancelAlarm(getApplicationContext(), t.getID());
			}
			deleted_tasks = data_source.deleteAllTasks();
			toast(deleted_tasks + " tasks deleted");
			finish();
			break;
		}
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
		
		if (key.equals(REMINDER_TIME)) {
			lp_reminder_time.setSummary(getReminderSummary(REMINDER_TIME, (String) newValue));
			return true;   
		}
		
		if (key.equals(ALARM_TIME)) {
			lp_alarm_time.setSummary(getReminderSummary(ALARM_TIME, (String) newValue));
			return true;   
		}
			
		return false;
	}
}
