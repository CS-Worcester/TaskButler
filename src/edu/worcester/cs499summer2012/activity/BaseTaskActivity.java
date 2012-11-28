/* 
 * BaseTaskActivity.java
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

import java.util.Calendar;
import java.util.GregorianCalendar;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.database.DatabaseHandler;
import edu.worcester.cs499summer2012.database.TasksDataSource;
import edu.worcester.cs499summer2012.task.Category;

public abstract class BaseTaskActivity extends SherlockActivity implements 
	OnCheckedChangeListener, OnClickListener, DialogInterface.OnClickListener,
	OnItemSelectedListener {
	
	/**************************************************************************
	 * Static fields and methods                                              *
	 **************************************************************************/

	public final static String DEFAULT_INTERVAL = "1";
	public final static int DATETIME_DIALOG = 0;
	public final static int CATEGORY_DIALOG = 1;
	public final static long SECOND_MS = 1000;
	public final static long MINUTE_MS = SECOND_MS * 60;
	public final static long HOUR_MS = MINUTE_MS * 60;
	public final static long DAY_MS = HOUR_MS * 24;
	public final static long WEEK_MS = DAY_MS * 7;
	public final static long MONTH_MS = DAY_MS * 30;
	public final static long YEAR_MS = DAY_MS * 365;
	public final static long[] REPEAT_TYPES_MS = { MINUTE_MS, HOUR_MS, DAY_MS, WEEK_MS, MONTH_MS, YEAR_MS };
	public final static String PREVENT_DUE_DATE = "prevent_due_date";
    
    /**************************************************************************
     * Private fields                                                         *
     **************************************************************************/
	
	// Intent to be returned
    protected Intent intent;
    
    // Database
    protected TasksDataSource data_source;
    
    // Prefs
    protected SharedPreferences prefs;
    
    // Category spinner array adapter
    protected ArrayAdapter<Category> category_adapter;
    
    // UI elements
    protected CheckBox cb_due_date;
    protected TextView tv_due_date;
    protected TextView tv_due_time;
    protected CheckBox cb_final_due_date;
    protected CheckBox cb_repeating;   
    protected EditText et_repeat_interval;
    protected Spinner s_category;
    protected Spinner s_priority;
    protected Spinner s_repeat_type;
    protected DatePicker date_picker;
    protected TimePicker time_picker;
    protected EditText et_category;
    protected TextView tv_at;
    
    // Task properties that are modified by UI elements
    protected Calendar due_date_cal;
    protected int selected_dialog;
    
    // Flags to prevent date-time picker dialogs popping up immediately on  	
    // entering EditTaskActivity
    protected boolean prevent_initial_due_date_popup = false;
    
    protected String repeat_interval_string = DEFAULT_INTERVAL;
    
    /**************************************************************************
	 * Class methods                                                          *
	 **************************************************************************/
    
    protected abstract boolean addTask();
    
    /**
	 * Displays a message in a Toast notification for a short duration.
	 */
	protected void toast(String message)
	{
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
    
	/**************************************************************************
	 * Overridden parent methods                                              *
	 **************************************************************************/
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        
        data_source = TasksDataSource.getInstance(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        // Initialize the fields that can be enabled/disabled or listened to
        cb_due_date = (CheckBox) findViewById(R.id.checkbox_has_due_date);
        tv_due_date = (TextView) findViewById(R.id.text_add_task_due_date);
        tv_due_time = (TextView) findViewById(R.id.text_add_task_due_time);
        cb_final_due_date = (CheckBox) findViewById(R.id.checkbox_has_final_due_date);
        cb_repeating = (CheckBox) findViewById(R.id.checkbox_has_repetition);   
        et_repeat_interval = (EditText) findViewById(R.id.edit_add_task_repeat_interval);
        s_category = (Spinner) findViewById(R.id.spinner_add_task_category);
        s_priority = (Spinner) findViewById(R.id.spinner_add_task_priority);
        s_repeat_type = (Spinner) findViewById(R.id.spinner_add_task_repeat_type);
        tv_at = (TextView) findViewById(R.id.text_at);
                
        // Set listeners
        cb_due_date.setOnCheckedChangeListener(this);
        cb_repeating.setOnCheckedChangeListener(this);
        tv_due_date.setOnClickListener(this);
        tv_due_time.setOnClickListener(this);
        
        // Allow Action bar icon to act as a button
        ActionBar action_bar = getSupportActionBar();
        action_bar.setHomeButtonEnabled(true);
        action_bar.setDisplayHomeAsUpEnabled(true);
        
        // Populate the category spinner
        category_adapter = new ArrayAdapter<Category>(this, 
        				android.R.layout.simple_spinner_item, 
        				data_source.getCategories());
        category_adapter.add(new Category(0, "New category...", 0, Category.NEW_CATEGORY));
        category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_category.setAdapter(category_adapter);
        s_category.setOnItemSelectedListener(this);
        
        // Populate the priority spinner
        ArrayAdapter<CharSequence> priority_adapter = 
        		ArrayAdapter.createFromResource(this, 
        				R.array.spinner_priorities, 
        				android.R.layout.simple_spinner_item);
        priority_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_priority.setAdapter(priority_adapter);
        
        // Populate the repeat type spinner
        ArrayAdapter<CharSequence> repeat_type_adapter = 
        		ArrayAdapter.createFromResource(this, 
        				R.array.spinner_repeat_types, 
        				android.R.layout.simple_spinner_item);
        repeat_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_repeat_type.setAdapter(repeat_type_adapter);
        
        // Hide certain due date items to start
        tv_due_date.setVisibility(View.INVISIBLE);
        tv_due_time.setVisibility(View.INVISIBLE);
        cb_final_due_date.setVisibility(View.INVISIBLE);
        cb_repeating.setVisibility(View.INVISIBLE);
        et_repeat_interval.setVisibility(View.INVISIBLE);
        s_repeat_type.setVisibility(View.INVISIBLE);
        tv_at.setVisibility(View.INVISIBLE);
        
        // Check bundle for prevent popup flag
        if (savedInstanceState != null)
        	prevent_initial_due_date_popup = savedInstanceState.getBoolean(PREVENT_DUE_DATE);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	outState.putBoolean(PREVENT_DUE_DATE, cb_due_date.isChecked() ? true : false);
    	super.onSaveInstanceState(outState);
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
    		if (addTask()) {
    	    	// Set the return result to OK and finish the activity
    	    	setResult(RESULT_OK, intent);
    		    finish();
    		}
    		return true;
    		
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }

	/**************************************************************************
	 * Methods implementing OnCheckedChangedListener interface                *
	 **************************************************************************/

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.checkbox_has_due_date:
			if (isChecked) {
				// Make edit button, textview, and other checkboxes visible
				tv_due_date.setVisibility(View.VISIBLE);
				tv_at.setVisibility(View.VISIBLE);
				tv_due_time.setVisibility(View.VISIBLE);
				tv_due_date.setText(DateFormat.format("MM/dd/yy", due_date_cal));
				tv_due_time.setText(DateFormat.format("h:mm AA", due_date_cal));
				cb_final_due_date.setVisibility(View.VISIBLE);
				cb_repeating.setVisibility(View.VISIBLE);
				
				// Pop up date-time dialog as if user had clicked edit button
				if (prevent_initial_due_date_popup)
					prevent_initial_due_date_popup = false;
				else
					onClick(tv_due_date);
			} else {
				// Hide edit button, textview, and other checkboxes
				tv_due_date.setVisibility(View.INVISIBLE);
				tv_at.setVisibility(View.INVISIBLE);
				tv_due_time.setVisibility(View.INVISIBLE);
				cb_final_due_date.setVisibility(View.INVISIBLE);
				cb_repeating.setVisibility(View.INVISIBLE);
				
				// Uncheck final due date and repetition boxes
				if (cb_final_due_date.isChecked())
					cb_final_due_date.setChecked(false);
				if (cb_repeating.isChecked())
					cb_repeating.setChecked(false);
			}
			break;
			
		case R.id.checkbox_has_repetition:
			if (isChecked) {
				// Make textview, edittext, spinner, and checkbox visible
				et_repeat_interval.setVisibility(View.VISIBLE);
				et_repeat_interval.setText(repeat_interval_string);
				s_repeat_type.setVisibility(View.VISIBLE);
			} else {
				// Hide edittext, spinner
				et_repeat_interval.setVisibility(View.INVISIBLE);
				s_repeat_type.setVisibility(View.INVISIBLE);
			}
			break;
		}
	}

	/**************************************************************************
	 * Methods implementing OnClickListener interface                         *
	 **************************************************************************/
	
	@Override
	public void onClick(View v) {	
		LayoutInflater li = LayoutInflater.from(this);
		View picker_view = li.inflate(R.layout.date_time_picker, null);
		
		date_picker = (DatePicker) picker_view.findViewById(R.id.dialog_date_picker);
        time_picker = (TimePicker) picker_view.findViewById(R.id.dialog_time_picker);
		
		if (v.getId() == R.id.text_add_task_due_date) {
			date_picker.updateDate(due_date_cal.get(Calendar.YEAR), 
					due_date_cal.get(Calendar.MONTH), 
					due_date_cal.get(Calendar.DAY_OF_MONTH));
			time_picker.setCurrentHour(due_date_cal.get(Calendar.HOUR_OF_DAY));
			time_picker.setCurrentMinute(due_date_cal.get(Calendar.MINUTE));
			
			selected_dialog = DATETIME_DIALOG;
			AlertDialog.Builder picker_dialog = new AlertDialog.Builder(this);
			picker_dialog.setView(picker_view)
			             .setTitle("Set date and time")
			             .setCancelable(true)
			             .setPositiveButton("Accept", this)
			             .setNegativeButton("Cancel", this);
			picker_dialog.show();
		}
	}

	/**************************************************************************
	 * Methods implementing DialogInterface.OnClickListener interface         *
	 **************************************************************************/
	
	@Override
	public void onClick(DialogInterface dialog, int id) {
		switch (selected_dialog)
		{
		case DATETIME_DIALOG:
			if (id == DialogInterface.BUTTON_POSITIVE) {
				// User modified the due date
				due_date_cal.set(Calendar.YEAR, date_picker.getYear());
				due_date_cal.set(Calendar.MONTH, date_picker.getMonth());
				due_date_cal.set(Calendar.DAY_OF_MONTH, date_picker.getDayOfMonth());
				due_date_cal.set(Calendar.HOUR_OF_DAY, time_picker.getCurrentHour());
				due_date_cal.set(Calendar.MINUTE, time_picker.getCurrentMinute());
				tv_due_date.setText(DateFormat.format("MM/dd/yy", due_date_cal));
				tv_due_time.setText(DateFormat.format("h:mm AA", due_date_cal));
				
				dialog.dismiss();
			} else
				dialog.cancel();
			break;
			
		case CATEGORY_DIALOG:
			if (id == DialogInterface.BUTTON_POSITIVE) {
				String name = et_category.getText().toString();
				if (name.equals("")) {
					// No name, cancel dialog
					Toast.makeText(this, "Category needs a name!", Toast.LENGTH_SHORT).show();
					s_category.setSelection(0);
					dialog.cancel();
				} else if (data_source.doesCategoryNameExist(name)) {
					// Category name already exists, cancel dialog
					Toast.makeText(this, "Category name already exists", Toast.LENGTH_SHORT).show();
					s_category.setSelection(0);
					dialog.cancel();
				} else  {
					AmbilWarnaDialog color_dialog = new AmbilWarnaDialog(this, Color.RED, new OnAmbilWarnaListener() {
	
						@Override
						public void onCancel(AmbilWarnaDialog dialog) {
							s_category.setSelection(0);
						}
	
						@Override
						public void onOk(AmbilWarnaDialog dialog, int color) {
							Category new_category = new Category(et_category.getText().toString(), 
									color, 
									GregorianCalendar.getInstance().getTimeInMillis());
							new_category.setID(data_source.getNextID(DatabaseHandler.TABLE_CATEGORIES));
							data_source.addCategory(new_category);
							category_adapter.insert(new_category, category_adapter.getCount() - 1);
							category_adapter.notifyDataSetChanged();
						}
					});
					color_dialog.show();
				}
			} else {
				s_category.setSelection(0);
				dialog.cancel();
			}
			break;
		}
	}
	
	/**************************************************************************
	 * Methods implementing OnItemSelectedListener interface                  *
	 **************************************************************************/

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if (((Category) s_category.getItemAtPosition(position)).getID() == Category.NEW_CATEGORY) {
			selected_dialog = CATEGORY_DIALOG;
			
			LayoutInflater li = LayoutInflater.from(this);
			View category_name_view = li.inflate(R.layout.dialog_category_name, null);
			et_category = (EditText) category_name_view.findViewById(R.id.edit_category_name);
			
			AlertDialog.Builder new_category_builder = new AlertDialog.Builder(this);
			new_category_builder.setView(category_name_view);
			new_category_builder.setTitle("Enter category name");
			new_category_builder.setPositiveButton("Next", this);
			new_category_builder.setNegativeButton("Cancel", this);
			new_category_builder.create().show();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Do nothing
	}
}
