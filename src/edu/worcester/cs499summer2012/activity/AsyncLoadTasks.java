package edu.worcester.cs499summer2012.activity;

import com.google.api.services.tasks.model.Task;

import edu.worcester.cs499summer2012.adapter.TaskListAdapter;
import edu.worcester.cs499summer2012.database.DatabaseHandler;
import edu.worcester.cs499summer2012.database.TasksDataSource;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Asynchronously load the tasks with a progress dialog.
 * 
 * @author Yaniv Inbar
 */
class AsyncLoadTasks extends AsyncTask<Void, Void, List<String>> {

	private final MainActivity mainActivity;
	private final ProgressDialog dialog;
	private com.google.api.services.tasks.Tasks service;
	TasksDataSource tds ;
	
	AsyncLoadTasks(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
		service = mainActivity.service;
		dialog = new ProgressDialog(mainActivity);
	}

	@Override
	protected void onPreExecute() {
		dialog.setMessage("Loading tasks...");
		dialog.show();
		tds = TasksDataSource.getInstance(mainActivity);
	}

	@Override
	protected List<String> doInBackground(Void... arg0) {
		try {
			List<String> result = new ArrayList<String>();
			com.google.api.services.tasks.Tasks.TasksOperations.List listRequest =
					service.tasks().list("@default");
			listRequest.setFields("items");
			
			List<Task> tasks = listRequest.execute().getItems();
			if (tasks != null) {
				for (Task task : tasks) {
					result.add(task.getId());
					result.add(task.getTitle());
					result.add(""+task.getUpdated());
					
				}
			} else {
				result.add("No tasks.");
			}
			return result;
		} catch (IOException e) {
			mainActivity.handleGoogleException(e);
			return Collections.singletonList(e.getMessage());
		} finally {
			mainActivity.onRequestCompleted();
		}
	}

	@Override
	protected void onPostExecute(List<String> result) {
		dialog.dismiss();
		edu.worcester.cs499summer2012.task.Task task;
		TaskListAdapter adapter = MainActivity.getAdapter();
		TasksDataSource tds = TasksDataSource.getInstance(mainActivity);
		
		for (String string : result) {
			task = new edu.worcester.cs499summer2012.task.Task(string,
					false, 0,0,false, false, false, false, 0, 0, 0, 0, 0, 0, string);
			// Assign the task a unique ID and store it in the database, and display it in the 
			task.setID(tds.getNextID(DatabaseHandler.TABLE_TASKS));
	    	tds.addTask(task);
			adapter.add(task);
		}
		
	}
}