package edu.worcester.cs499summer2012.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.database.TasksDataSource;
import edu.worcester.cs499summer2012.task.Task;

public class EditTaskActivity extends SherlockActivity {
	public final static String EXTRA_TASK = "edu.worcester.cs499summer2012.TASK";
	//the intent that will be returned.
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_task);
		// Allow Action bar icon to act as a button
		getSupportActionBar().setHomeButtonEnabled(true);
		//Button editButton = (Button) findViewById(R.id.menu_main_edit_task)
		//EditText foo = (EditText) findViewById(R.id.edit_add_task_name);
		
		
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

	// TODO have it resave using onUpdate().
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

}
