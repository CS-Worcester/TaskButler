/* 
 * TaskButlerWidgetProvider.java
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

package edu.worcester.cs499summer2012.service;

import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;
import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.activity.MainActivity;
import edu.worcester.cs499summer2012.activity.SettingsActivity;
import edu.worcester.cs499summer2012.adapter.TaskListAdapter;
import edu.worcester.cs499summer2012.database.TasksDataSource;
import edu.worcester.cs499summer2012.task.Task;

/**
 * Updates Task Butler widgets with onClick functionality and the ability to
 * launch activities.
 * @author Jonathan Hasenzahl
 *
 */
public class TaskButlerWidgetProvider extends AppWidgetProvider {
	
	private static final int PRIORITY = 0;
	private static final int NAME = 1;
	private static final int COLOR = 2;
	private static final int WIDGET_ROWS = 6;
	private static final int[][] ROW_IDS = { { R.id.widget_priority_0, R.id.widget_name_0, R.id.widget_color_0 },
											 { R.id.widget_priority_1, R.id.widget_name_1, R.id.widget_color_1 },
											 { R.id.widget_priority_2, R.id.widget_name_2, R.id.widget_color_2 },
											 { R.id.widget_priority_3, R.id.widget_name_3, R.id.widget_color_3 },
											 { R.id.widget_priority_4, R.id.widget_name_4, R.id.widget_color_4 },
											 { R.id.widget_priority_5, R.id.widget_name_5, R.id.widget_color_5 } };
	/**
	 * Updates the homescreen widget when the task list has changed. New task,
	 * delete task, edit task, change sorting, etc. Make sure the changes are
	 * in the database before calling this method.
	 * @param activity The activity the method is being called from (pass in this).
	 */
	public static void updateWidget(Activity activity) {
		if (activity == null)
			return;
		
		Intent intent = new Intent(activity, TaskButlerWidgetProvider.class);
		intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
		int[] ids = AppWidgetManager.getInstance(activity.getApplication()).getAppWidgetIds(new ComponentName(activity.getApplication(), 
				TaskButlerWidgetProvider.class));
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
		activity.sendBroadcast(intent);
	}

	/**
	 * Updates widgets on the home screen. Not the *content* of the widget, but 
	 * things like onClickListeners and pending intents. For updating the
	 * content of the widget, see TaskButlerWidgetService.
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int id : appWidgetIds) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
			
			// Get the list of unfinished tasks
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			TasksDataSource data_source = TasksDataSource.getInstance(context);
			ArrayList<Task> tasks = new ArrayList<Task>(data_source.getTasks(false, null));
			TaskListAdapter adapter = new TaskListAdapter(context, tasks);
			adapter.setSortType(prefs.getInt(SettingsActivity.SORT_TYPE, TaskListAdapter.AUTO_SORT));
			adapter.sort();
			
			int size = adapter.getCount();
			
			// Create a new widget item for each task and at it to the widget
			for (int row = 0; row < WIDGET_ROWS; row++) {
				// Check if there are less tasks to display than rows
				if (size <= row) {
					// Hide the rows as there is no data there
					views.setInt(ROW_IDS[row][PRIORITY], "setVisibility", View.INVISIBLE);
					views.setInt(ROW_IDS[row][NAME], "setVisibility", View.INVISIBLE);
					views.setInt(ROW_IDS[row][COLOR], "setVisibility", View.INVISIBLE);
				} else {
					Task task = adapter.getItem(row);
					
					// Set task priority
					views.setInt(ROW_IDS[row][PRIORITY], "setVisibility", View.VISIBLE);
					switch(task.getPriority()) {
					case Task.URGENT:
						views.setImageViewResource(ROW_IDS[row][PRIORITY], R.drawable.ic_urgent);
						break;
					case Task.NORMAL:
						views.setImageViewResource(ROW_IDS[row][PRIORITY], R.drawable.ic_normal);
						break;
					case Task.TRIVIAL:
						views.setImageViewResource(ROW_IDS[row][PRIORITY], R.drawable.ic_trivial);
						break;
					}
					
					// Set task name
					views.setInt(ROW_IDS[row][NAME], "setVisibility", View.VISIBLE);
					views.setTextViewText(ROW_IDS[row][NAME], task.getName());
					views.setTextColor(ROW_IDS[row][NAME], task.isPastDue() ? Color.RED : Color.WHITE);
					
					// Set task color
					views.setInt(ROW_IDS[row][COLOR], "setVisibility", View.VISIBLE);
					views.setInt(ROW_IDS[row][COLOR], "setBackgroundColor", data_source.getCategory(task.getCategory()).getColor());
				}
			}
			
			// Clicking the widget will launch Main Activity
			Intent intent = new Intent(context, MainActivity.class);
			PendingIntent pending_intent = PendingIntent.getActivity(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.widget_linear_layout, pending_intent);
			
			// Update the widget
			appWidgetManager.updateAppWidget(id, views);
		}
	}
	
}
