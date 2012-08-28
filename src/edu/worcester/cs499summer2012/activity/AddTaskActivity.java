/**
 * NewTaskActivity.java
 * 
 * @file
 * This is the class to add tasks to the for the application.
 * @author James Celona
 * @author Jonathan Hasenzahl 
 * @version 1.0 dev
 * 
 * 
 * Copyright 2012 Jonathan Hasenzahl
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

 * 
 */

package edu.worcester.cs499summer2012.activity;

import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.task.Task;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddTaskActivity extends Activity {
	
	public final static String EXTRA_TASK = "edu.worcester.cs499summer2012.TASK";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
    }
      
    public void addTask(View view) {
    	Intent intent = new Intent(this, MainActivity.class);
    	EditText edit_text = (EditText) findViewById(R.id.edit_task_name);
    	edit_text=(EditText) findViewById(R.id.edit_task_name);   
    	EditText edit_text2 = (EditText) findViewById(R.id.add_task_notes);    	
    	Task task = new Task(edit_text.getText().toString(), false, 0, edit_text2.getText().toString());   	 
    						
    	
    	if (!task.getName().equals("")) {
	    	intent.putExtra(EXTRA_TASK, task);
	    	setResult(RESULT_OK, intent);
	    	finish();
    	}
    	else {
    		Toast.makeText(this, "Task needs a name!", 
    				Toast.LENGTH_SHORT).show();
    	}
    }
    /*
    public void addMessage(View view) {
    	Intent intent = new Intent(this, MainActivity.class);
    	EditText edit_text = (EditText) findViewById(R.id.add_task_notes);
    	Task task = new Task(edit_text.getText().toString());
    	intent.putExtra(EXTRA_TASK, task);
    	setResult(RESULT_OK, intent);
    		
    	}
    	
    */ 
    
    
    
    public void cancel(View view) {
    	setResult(RESULT_CANCELED);
    	finish();
    }
}
