/*
 * EditCategoriesActivity.java
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

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.adapter.CategoryListAdapter;
import edu.worcester.cs499summer2012.database.TasksDataSource;

public class EditCategoriesActivity extends SherlockListActivity {

	private TasksDataSource data_source;
	private static CategoryListAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Assign the layout to this activity
		setContentView(R.layout.activity_edit_categories);

		// Open the database
		data_source = TasksDataSource.getInstance(this);
		
		// Create an adapter for the category list
		adapter = new CategoryListAdapter(this, data_source.getCategories());
		setListAdapter(adapter);
		
		// Allow Action bar icon to act as a button
        ActionBar action_bar = getSupportActionBar();
        action_bar.setHomeButtonEnabled(true);
        action_bar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_edit_categories, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
    		return true;
    		
		case R.id.menu_add_category:
			// TODO: Something
			return true;
    		
    	default:
    		return super.onOptionsItemSelected(item);
    	}	
    }
}
