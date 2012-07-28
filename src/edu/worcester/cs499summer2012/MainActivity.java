/**
 * MainActivity.java
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

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	static final int ADD_TASK_REQUEST = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    	tasks = new ArrayList<Task>(0);
    	list_view = (ListView) findViewById(R.id.list_tasks);
    	text_view = (TextView) findViewById(R.id.text_list_size);
    	text_view.setText(Integer.toString(tasks.size()));        
    }
    
    public void onStart() {
    	super.onStart();
    }
    
    public void getNewTask(View view) {
    	startActivityForResult(new Intent(this, AddTaskActivity.class), 
    			ADD_TASK_REQUEST);
    }
    
    public void onActivityResult(int request_code, int result_code, 
    		Intent intent) {
    	if (request_code == ADD_TASK_REQUEST && result_code == RESULT_OK) {
    		Task task = intent.getParcelableExtra(AddTaskActivity.EXTRA_TASK);
    		tasks.add(task);
    		text_view.setText(Integer.toString(tasks.size()));
    		ArrayAdapter<Task> list_adapter = new ArrayAdapter<Task>(this, 
        			android.R.layout.simple_list_item_1, android.R.id.text1, 
        			tasks);
        	list_view.setAdapter(list_adapter);
    	}
    }
    
    private ArrayList<Task> tasks;
    private ListView list_view;
    private TextView text_view;
}
