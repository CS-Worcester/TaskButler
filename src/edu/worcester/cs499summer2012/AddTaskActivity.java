/**
 * NewTaskActivity.java
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
 */

package edu.worcester.cs499summer2012;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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
    	Task task = new Task(edit_text.getText().toString());
    	intent.putExtra(EXTRA_TASK, task);
    	setResult(RESULT_OK, intent);
    	finish();
    }
    
    public void cancel(View view) {
    	setResult(RESULT_CANCELED);
    	finish();
    }
}
