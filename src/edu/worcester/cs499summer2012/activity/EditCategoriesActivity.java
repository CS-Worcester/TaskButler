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
import java.util.GregorianCalendar;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import edu.worcester.cs499summer2012.database.DatabaseHandler;
import edu.worcester.cs499summer2012.database.TasksDataSource;
import edu.worcester.cs499summer2012.task.Category;
import edu.worcester.cs499summer2012.task.Task;

public class EditCategoriesActivity extends SherlockListActivity implements ActionMode.Callback, OnClickListener {

	public static final int CREATE_DIALOG = 0;
	public static final int EDIT_DIALOG = 1;
	public static final int DELETE_DIALOG = 2;
	
	private TasksDataSource data_source;
	private static CategoryListAdapter adapter;
	private ActionMode action_mode;
	private Category selected_category;
	private int selected_dialog;
	private EditText et_category_name;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Assign the layout to this activity
		setContentView(R.layout.activity_edit_categories);

		// Open the database
		data_source = TasksDataSource.getInstance(this);
		
		// Create an adapter for the category list
		ArrayList<Category> categories = data_source.getCategories();
		categories.remove(0);
		adapter = new CategoryListAdapter(this, R.layout.row_category, categories);
		setListAdapter(adapter);
		
		// Allow Action bar icon to act as a button
        ActionBar action_bar = getSupportActionBar();
        action_bar.setIcon(R.drawable.ic_category);
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
			selected_dialog = CREATE_DIALOG;
		
			LayoutInflater li = LayoutInflater.from(this);
			View category_name_view = li.inflate(R.layout.dialog_category_name, null);
			et_category_name = (EditText) category_name_view.findViewById(R.id.edit_category_name);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(category_name_view);
			builder.setTitle("Enter category name");
			builder.setPositiveButton("Next", this);
			builder.setNegativeButton("Cancel", this);
    		builder.create().show();
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
			selected_dialog = EDIT_DIALOG;
			
			LayoutInflater li = LayoutInflater.from(this);
			View category_name_view = li.inflate(R.layout.dialog_category_name, null);
			et_category_name = (EditText) category_name_view.findViewById(R.id.edit_category_name);
			et_category_name.setText(selected_category.getName());
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(category_name_view);
			builder.setTitle("Set name");
			builder.setPositiveButton("Next", this);
			builder.setNegativeButton("Cancel", this);
    		builder.create().show();
			mode.finish();
			return true;
			
		case R.id.menu_delete_category:
			selected_dialog = DELETE_DIALOG;
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
    		builder2.setMessage("Are you sure you want to delete this category?");
    		builder2.setCancelable(true);
    		builder2.setPositiveButton("Yes", this);
    		builder2.setNegativeButton("No", this);
    		builder2.create().show();
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
		switch (selected_dialog) {
		case CREATE_DIALOG:
		case EDIT_DIALOG:
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				String name = et_category_name.getText().toString().trim();
				if (name.equals("")) {
					// No name, cancel dialog
					Toast.makeText(this, "Category needs a name!", Toast.LENGTH_SHORT).show();
					dialog.cancel();
				} else if (selected_dialog == CREATE_DIALOG && data_source.getExistingCategory(name) != null) {
					// Category name already exists, cancel dialog
					Toast.makeText(this, "Category name already exists", Toast.LENGTH_SHORT).show();
					dialog.cancel();
				} else {
					int color = selected_dialog == EDIT_DIALOG ? selected_category.getColor() : Color.RED;
					
					AmbilWarnaDialog color_dialog = new AmbilWarnaDialog(this, color, new OnAmbilWarnaListener() {
						
						@Override
						public void onCancel(AmbilWarnaDialog dialog) {
							// Do nothing
						}
	
						@Override
						public void onOk(AmbilWarnaDialog dialog, int color) {
							if (selected_dialog == CREATE_DIALOG) {
								Category new_category = new Category(et_category_name.getText().toString(), 
										color, 
										GregorianCalendar.getInstance().getTimeInMillis());
								new_category.setID(data_source.getNextID(DatabaseHandler.TABLE_CATEGORIES));
								data_source.addCategory(new_category);
								adapter.add(new_category);
							} else {
								selected_category.setName(et_category_name.getText().toString());
								selected_category.setColor(color);
								selected_category.setUpdated(GregorianCalendar.getInstance().getTimeInMillis());
								data_source.updateCategory(selected_category);
							}
							adapter.notifyDataSetChanged();
						}
					});
					color_dialog.show();					
				}
				break;
				
			case DialogInterface.BUTTON_NEGATIVE:
			default:
				dialog.cancel();
				break;
			}
			break;
		
		case DELETE_DIALOG:
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
			break;
		}
	}
}
