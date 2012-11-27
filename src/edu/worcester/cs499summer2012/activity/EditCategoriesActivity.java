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

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.adapter.CategoryListAdapter;
import edu.worcester.cs499summer2012.database.TasksDataSource;
import edu.worcester.cs499summer2012.task.Category;
import edu.worcester.cs499summer2012.task.Task;

public class EditCategoriesActivity extends SherlockListActivity implements ActionMode.Callback, OnClickListener {

	private TasksDataSource data_source;
	private static CategoryListAdapter adapter;
	private ActionMode action_mode;
	private Category selected_category;
	
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
	
	@Override
	public void onListItemClick(ListView list_view, View view, int position, 
			long id) {
		if (action_mode != null)
			return;

		selected_category = adapter.getItem(position);
		action_mode = startActionMode(this);
		view.setSelected(true);
	}

	/**************************************************************************
	 * Methods implementing ActionMode.Callback interface                     *
	 **************************************************************************/  
	
	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.context_modify_category, menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// Not used
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_edit_category:
			mode.finish();
			return true;
			
		case R.id.menu_delete_category:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage("Are you sure you want to delete this category?");
    		builder.setCancelable(true);
    		builder.setPositiveButton("Yes", this);
    		builder.setNegativeButton("No", this);
    		builder.create().show();
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
	 * Methods implementing DialogInterface.OnClickListener interface         *
	 **************************************************************************/

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			// Delete the task from the adapter and database
			adapter.remove(selected_category);
			adapter.notifyDataSetChanged();
			data_source.deleteCategory(selected_category);
			
			// Update any tasks that had this category
			ArrayList<Task> tasks = data_source.getTasks(true, selected_category);
			for (Task task : tasks) {
				task.setCategory(Category.NO_CATEGORY);
				data_source.updateTask(task);
			}
			
			Toast.makeText(this, "Category deleted", Toast.LENGTH_SHORT).show();
			
			dialog.dismiss();
			break;
			
		case DialogInterface.BUTTON_NEGATIVE:
		default:
			dialog.cancel();
			break;
		}
	}
}
