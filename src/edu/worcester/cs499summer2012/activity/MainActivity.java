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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.GoogleKeyInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.adapter.TaskListAdapter;
import edu.worcester.cs499summer2012.database.TasksDataSource;
import edu.worcester.cs499summer2012.service.TaskAlarm;
import edu.worcester.cs499summer2012.task.Task;

/**
 * Main app activity. Displays current task list and allows user to access
 * task creation, task modification, and task sorting activities.
 * @author Jonathan Hasenzahl
 * @author Dhimitraq Jorgji
 * @author James Celona
 */
public final class MainActivity extends SherlockListActivity implements 
OnItemLongClickListener, ActionMode.Callback {

	/**************************************************************************
	 * Static fields and methods                                              *
	 **************************************************************************/

	public static final String PREF_SORT_TYPE = "sort_type";
	public static final int ADD_TASK_REQUEST = 0;
	public static final int VIEW_TASK_REQUEST = 1;
	public static final int DELETE_MODE_SINGLE = 0;
	public static final int DELETE_MODE_FINISHED = 1;
	public static final int DELETE_MODE_ALL = 2;

	/**************************************************************************
	 * Private fields                                                         *
	 **************************************************************************/

	private TasksDataSource data_source;
	private SharedPreferences prefs;
	private SharedPreferences.Editor prefs_editor;
	private static TaskListAdapter adapter;
	private Object action_mode;
	private int selected_task;

	/**************************************************************************
	 * Google Tasks Fields                                                    *
	 **************************************************************************/

	/** Logging level for HTTP requests/responses. */
	private static final Level LOGGING_LEVEL = Level.OFF;
	private static final String TAG = "MainActivity";

	// This must be the exact string, and is a special for alias OAuth 2 scope
	// "https://www.googleapis.com/auth/tasks"
	private static final String AUTH_TOKEN_TYPE = "Manage your tasks";
	private static final int MENU_ACCOUNTS = 0;
	private static final int REQUEST_AUTHENTICATE = 2;

	final HttpTransport transport = AndroidHttp.newCompatibleTransport();
	final JsonFactory jsonFactory = new GsonFactory();

	static final String PREF_ACCOUNT_NAME = "accountName";
	static final String PREF_AUTH_TOKEN = "authToken";

	GoogleAccountManager accountManager;
	SharedPreferences settings;
	String accountName;

	GoogleCredential credential = new GoogleCredential();
	com.google.api.services.tasks.Tasks service;

	private boolean received401;
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
					data_source.deleteTask(adapter.getItem(selected_task));
					adapter.remove(adapter.getItem(selected_task));
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

		// Create an adapter for the task list
		adapter = new TaskListAdapter(this, data_source.getAllTasks());
		setListAdapter(adapter);

		// Read preferences from file
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs_editor = prefs.edit();

		// Set sort type and sort the list
		adapter.setSortType(prefs.getInt(PREF_SORT_TYPE, 
				TaskListAdapter.AUTO_SORT));
		adapter.sort();

		// Set up a long item click listener
		getListView().setOnItemLongClickListener(this);

		// Get Google Tasks service and account
		ClientCredentials.errorIfNotSpecified();
		service = new com.google.api.services.tasks.Tasks.Builder(
				transport, jsonFactory, credential).setApplicationName("Google-TasksAndroidSample/1.0")
				.setJsonHttpRequestInitializer(new GoogleKeyInitializer(ClientCredentials.KEY)).build();
		settings = getPreferences(MODE_PRIVATE);
		accountName = settings.getString(PREF_ACCOUNT_NAME, null);
		credential.setAccessToken(settings.getString(PREF_AUTH_TOKEN, null));
		Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);
		accountManager = new GoogleAccountManager(this);
		//gotAccount(); //uncomment if you want to check out the way tasks are accessed on google tasks
	}

	@Override
	public void onStop() {
		// Save preferences to file
		prefs_editor.commit();

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
			adapter.sort();
			return true;

		case R.id.menu_main_custom_sort:
			adapter.setSortType(TaskListAdapter.CUSTOM_SORT);
			prefs_editor.putInt(PREF_SORT_TYPE, TaskListAdapter.CUSTOM_SORT);
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

		case MENU_ACCOUNTS:
			chooseAccount();
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
				adapter.add(task);
				adapter.sort();
				if (task.hasDateDue() && !task.isCompleted()) {
					TaskAlarm alarm = new TaskAlarm();
					alarm.setOnetimeAlarm(this, task.getID());
				}
			}
			break;

		case VIEW_TASK_REQUEST:
			if(result_code == RESULT_OK){
				// Get the task from the db using the ID in the intent
				task = data_source.getTask(intent.getIntExtra(Task.EXTRA_TASK_ID, 0));
				adapter.remove(task);	// Update the adapter
				adapter.add(task);
				adapter.sort();
				if (task.hasDateDue() && !task.isCompleted()) {
					TaskAlarm alarm = new TaskAlarm();
					alarm.setOnetimeAlarm(this, task.getID());
				}
			}
			break;

		case REQUEST_AUTHENTICATE:
			if (result_code == RESULT_OK) {
				gotAccount();
			} else {
				chooseAccount();
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
	 * Google Tasks methods implementation				                      *
	 **************************************************************************/

	@SuppressWarnings("deprecation")
	void gotAccount() {
		Account account = accountManager.getAccountByName(accountName);
		if (account == null) {
			chooseAccount();
			return;
		}
		if (credential.getAccessToken() != null) {
			onAuthToken();
			return;
		}
		accountManager.getAccountManager()
		.getAuthToken(account, AUTH_TOKEN_TYPE, true, new AccountManagerCallback<Bundle>() {

			public void run(AccountManagerFuture<Bundle> future) {
				try {
					Bundle bundle = future.getResult();
					if (bundle.containsKey(AccountManager.KEY_INTENT)) {
						Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
						intent.setFlags(intent.getFlags() & ~Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivityForResult(intent, REQUEST_AUTHENTICATE);
					} else if (bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
						setAuthToken(bundle.getString(AccountManager.KEY_AUTHTOKEN));
						onAuthToken();
					}
				} catch (Exception e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		}, null);
	}

	private void chooseAccount() {
		accountManager.getAccountManager().getAuthTokenByFeatures(GoogleAccountManager.ACCOUNT_TYPE,
				AUTH_TOKEN_TYPE,
				null,
				MainActivity.this,
				null,
				null,
				new AccountManagerCallback<Bundle>() {

			public void run(AccountManagerFuture<Bundle> future) {
				Bundle bundle;
				try {
					bundle = future.getResult();
					setAccountName(bundle.getString(AccountManager.KEY_ACCOUNT_NAME));
					setAuthToken(bundle.getString(AccountManager.KEY_AUTHTOKEN));
					onAuthToken();
				} catch (OperationCanceledException e) {
					// user canceled
				} catch (AuthenticatorException e) {
					Log.e(TAG, e.getMessage(), e);
				} catch (IOException e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		},
		null);
	}

	void setAccountName(String accountName) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_ACCOUNT_NAME, accountName);
		editor.commit();
		this.accountName = accountName;
	}

	void setAuthToken(String authToken) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_AUTH_TOKEN, authToken);
		editor.commit();
		credential.setAccessToken(authToken);
	}

	void onAuthToken() {
		new AsyncLoadTasks(this).execute();
	}

	void onRequestCompleted() {
		received401 = false;
	}

	void handleGoogleException(IOException e) {
		if (e instanceof GoogleJsonResponseException) {
			GoogleJsonResponseException exception = (GoogleJsonResponseException) e;
			if (exception.getStatusCode() == 401 && !received401) {
				received401 = true;
				accountManager.invalidateAuthToken(credential.getAccessToken());
				credential.setAccessToken(null);
				SharedPreferences.Editor editor2 = settings.edit();
				editor2.remove(PREF_AUTH_TOKEN);
				editor2.commit();
				gotAccount();
				return;
			}
		}
		Log.e(TAG, e.getMessage(), e);
	}
}