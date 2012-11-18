package edu.worcester.cs499summer2012.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.database.DatabaseHandler;
import edu.worcester.cs499summer2012.database.TasksDataSource;
import edu.worcester.cs499summer2012.task.Task;
/**
 * this activity will eventually allow for editing of tasks
 * and resaving into the database.
 * 
 * 
 * @author James Celona
 *
 */
public class EditTaskActivity extends SherlockActivity implements OnClickListener, DialogInterface.OnClickListener {
	public final static String EXTRA_TASK = "edu.worcester.cs499summer2012.TASK";
	
	// Intent to be returned
    private Intent intent;
    
    // Database
    private TasksDataSource data_source;
    private Task task;
    SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_add_task);
		 data_source = TasksDataSource.getInstance(this);
		
		// Allow Action bar icon to act as a button
		getSupportActionBar().setHomeButtonEnabled(true);
		data_source.getTask(getTaskId());
	}
	
	@Override
	public void onListItemClick(ListView list_view, View view, int position, 
			long id) {
		/*adapter.getItem(position).toggleIsCompleted();
    	adapter.getItem(position).setDateModified(GregorianCalendar.getInstance().getTimeInMillis());

    	// Update database
    	data_source.updateTask(adapter.getItem(position));

    	// Sort the list
    	adapter.sort();*/

		Intent intent = new Intent(this, ViewTaskActivity.class);
		intent.putExtra(Task.EXTRA_TASK_ID, adapter.getItem(position).getID());
		startActivityForResult(intent, VIEW_TASK_REQUEST);
	}
	
	
	
	
	
	
	
		
		public Task getTask(int id) {
			
			
			Cursor cursor = db.query(DatabaseHandler.TABLE_TASKS, new String[] { 
					DatabaseHandler.KEY_ID,
					DatabaseHandler.KEY_NAME, 
					DatabaseHandler.KEY_COMPLETION, 
					DatabaseHandler.KEY_PRIORITY, 
					DatabaseHandler.KEY_CATEGORY,
					DatabaseHandler.KEY_HAS_DUE_DATE,
					DatabaseHandler.KEY_HAS_FINAL_DUE_DATE,
					DatabaseHandler.KEY_IS_REPEATING,
					DatabaseHandler.KEY_HAS_STOP_REPEATING_DATE,
					DatabaseHandler.KEY_REPEAT_TYPE,
					DatabaseHandler.KEY_REPEAT_INTERVAL,
					DatabaseHandler.KEY_CREATION_DATE,
					DatabaseHandler.KEY_MODIFICATION_DATE, 
					DatabaseHandler.KEY_DUE_DATE,
					DatabaseHandler.KEY_FINAL_DUE_DATE,
					DatabaseHandler.KEY_STOP_REPEATING_DATE,
					DatabaseHandler.KEY_G_ID,
					DatabaseHandler.KEY_NOTES }, 
					DatabaseHandler.KEY_ID + " = " + id,
					null, null, null, null, null);			
			return task;
		
	}

		
	/**
	 * 
	 * @param task
	 *            the task to be manipulated.
	 * @return
	 */
	public boolean editTask(){
		EditText name = (EditText) findViewById(R.id.edit_add_task_name);

		//name.setText(task.getName());		
		//EditText foop = (EditText) findViewById(R.id.edit_add_task_name);		
		//foop.setText(task.getName());		
		//tds.updateTask(task);
		

    	intent = new Intent(this, MainActivity.class);    	
    	//intent.putExtra(EXTRA_TASK, task);
		

		return true;

	}

	public void viewEdit(View v) {
		Intent intent = new Intent(this, EditTaskActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_add_task, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
		case R.id.menu_add_task_cancel:
			setResult(RESULT_CANCELED);
			finish();
			return true;

			
			  case R.id.menu_add_task_confirm:
			   if (true){ //what is the condition here..  Set the return result to OK and finish the activity 
			  setResult(RESULT_OK, intent); 
			  Toast.makeText(EditTaskActivity.this, "Task edited", Toast.LENGTH_LONG).show();
			  finish(); 
			  }
			 
			  return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

}
