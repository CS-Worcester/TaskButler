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
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.adapter.CategoryListAdapter;
import edu.worcester.cs499summer2012.adapter.PriorityListAdapter;
import edu.worcester.cs499summer2012.database.DatabaseHandler;
import edu.worcester.cs499summer2012.database.TasksDataSource;
import edu.worcester.cs499summer2012.task.Category;
import edu.worcester.cs499summer2012.task.Task;

public abstract class BaseTaskActivity extends SherlockActivity implements 
	OnCheckedChangeListener, OnClickListener, DialogInterface.OnClickListener,
	OnItemSelectedListener, OnDateSetListener, OnTimeSetListener {
	
	/**************************************************************************
	 * Static fields and methods                                              *
	 **************************************************************************/

	private final static String DEFAULT_INTERVAL = "1";
	private final static String PREVENT_DUE_DATE = "prevent_due_date";
	private final static int NO_DIALOG = 0;
	private final static int CATEGORY_DIALOG = 1;
    
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
    protected CategoryListAdapter category_adapter;
    
    // Priority spinner array adapter
    protected PriorityListAdapter priority_adapter;
    
    // UI elements
    protected EditText et_name;
    protected EditText et_notes;
    protected CheckBox cb_due_date;
    protected TextView tv_due_date;
    protected TextView tv_due_time;
    protected CheckBox cb_final_due_date;
    protected CheckBox cb_repeating;   
    protected EditText et_repeat_interval;
    protected Spinner s_category;
    protected Spinner s_priority;
    protected Spinner s_repeat_type;
    protected DatePickerDialog date_dialog;
    protected TimePickerDialog time_dialog;
    protected AlertDialog category_dialog;
    AmbilWarnaDialog color_dialog;
    protected EditText et_category;
    protected TextView tv_at;
    
    // Task properties that are modified by UI elements
    protected Calendar due_date_cal;
    protected int selected_dialog;
    protected Category default_category;
    
    // Flag to prevent date picker dialog popping up immediately on  	
    // entering EditTaskActivity
    protected boolean prevent_initial_due_date_popup = false;
    
    // Flag to kill new category dialog if screen is rotated
    protected boolean new_category_dialog_active = false;
    
    
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
        
        // Initialize calendar
		String hour = prefs.getString(SettingsActivity.DEFAULT_HOUR, SettingsActivity.DEFAULT_HOUR_VALUE);
        due_date_cal = GregorianCalendar.getInstance();
        due_date_cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
        due_date_cal.set(Calendar.MINUTE, 0);
        due_date_cal.set(Calendar.SECOND, 0);
        due_date_cal.set(Calendar.MILLISECOND, 0);
        if (due_date_cal.getTimeInMillis() < System.currentTimeMillis())
        	due_date_cal.add(Calendar.DAY_OF_YEAR, 1);
        
        // Initialize the fields that can be enabled/disabled or listened to
        et_name = (EditText) findViewById(R.id.edit_add_task_name);
        et_notes = (EditText) findViewById(R.id.edit_add_task_notes);
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
        
        // Fix white line bug in Gingerbread
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.GINGERBREAD ||
				Build.VERSION.SDK_INT == Build.VERSION_CODES.GINGERBREAD_MR1) {
			et_name.setBackgroundColor(Color.parseColor("#F0F0F0"));
			et_notes.setBackgroundColor(Color.parseColor("#F0F0F0"));
			et_repeat_interval.setBackgroundColor(Color.parseColor("#F0F0F0"));
		}        
                
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
        category_adapter = new CategoryListAdapter(this, R.layout.row_category_small, data_source.getCategories());
        category_adapter.add(new Category(0, "New category...", Color.TRANSPARENT, Category.NEW_CATEGORY));
        s_category.setAdapter(category_adapter);
        s_category.setOnItemSelectedListener(this);
        
        // Populate the priority spinner
        priority_adapter = new PriorityListAdapter(this, R.layout.row_priority_small, Task.PRIORITY_LABELS);
        priority_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_priority.setAdapter(priority_adapter);
        
        // Populate the repeat type spinner
        ArrayAdapter<CharSequence> repeat_type_adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, Task.REPEAT_LABELS);
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
    	
    	if (new_category_dialog_active) {
    		new_category_dialog_active = false;
    		selected_dialog = NO_DIALOG;
    		s_category.setSelection(category_adapter.getPosition(default_category));
    	}
			
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
    		
    	case R.id.menu_add_task_help:
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setTitle("Procrastination alarm");
    		builder.setIcon(R.drawable.ic_help);
    		builder.setMessage(R.string.dialog_procrastinator_help);
    		builder.setCancelable(true);
    		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
				
			});
    		builder.create().show();
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
	
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {	
        switch (v.getId()) {
        case R.id.text_add_task_due_date:
        	// Initialize picker dialog
    		date_dialog = new DatePickerDialog(this, this, 
    				due_date_cal.get(Calendar.YEAR), 
    				due_date_cal.get(Calendar.MONTH), 
    				due_date_cal.get(Calendar.DAY_OF_MONTH)) {
    			
    			@Override
    			public Bundle onSaveInstanceState() {
    				if (date_dialog.isShowing())
    					date_dialog.cancel();
    				return super.onSaveInstanceState();
    			}
    			
    		};
    		
    		// Show calendar view (only available for API 11+)
    		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
    			DatePicker date_picker = date_dialog.getDatePicker();
    			date_picker.setCalendarViewShown(true);
    			date_picker.setSpinnersShown(false);
    		}
    		
    		date_dialog.show();
        	break;
        	
        case R.id.text_add_task_due_time:
        	// Initialize picker dialog
    		time_dialog = new TimePickerDialog(this, this, 
    				due_date_cal.get(Calendar.HOUR_OF_DAY), 
    				due_date_cal.get(Calendar.MINUTE), false) {
    			
    			@Override
    			public Bundle onSaveInstanceState() {
    				if (time_dialog.isShowing())
    					time_dialog.cancel();
    				return super.onSaveInstanceState();
    			}
    			
    		};
    		
    		time_dialog.show();
        	break;
        }
	}

	/**************************************************************************
	 * Methods implementing DialogInterface.OnClickListener interface         *
	 **************************************************************************/
	
	@Override
	public void onClick(DialogInterface dialog, int id) {
		if (selected_dialog == CATEGORY_DIALOG) {
			selected_dialog = NO_DIALOG;
			if (id == DialogInterface.BUTTON_POSITIVE) {
				String name = et_category.getText().toString().trim();
				if (name.equals("")) {
					// No name, cancel dialog
					Toast.makeText(this, "Category needs a name!", Toast.LENGTH_SHORT).show();
					s_category.setSelection(category_adapter.getPosition(default_category));
					new_category_dialog_active = false;
					dialog.cancel();
					return;
				} 
				
				Category existing_category = data_source.getExistingCategory(name);
				
				if (existing_category != null) {
					// Category name already exists, cancel dialog
					Toast.makeText(this, "Category name already exists", Toast.LENGTH_SHORT).show();
					s_category.setSelection(category_adapter.getPosition(existing_category));
					new_category_dialog_active = false;
					dialog.cancel();
				} else  {
					 color_dialog = new AmbilWarnaDialog(this, Color.RED, new OnAmbilWarnaListener() {
	
						@Override
						public void onCancel(AmbilWarnaDialog dialog) {
							s_category.setSelection(category_adapter.getPosition(default_category));
							new_category_dialog_active = false;
						}
	
						@Override
						public void onOk(AmbilWarnaDialog dialog, int color) {
							Category new_category = new Category(et_category.getText().toString().trim(), 
									color, 
									GregorianCalendar.getInstance().getTimeInMillis());
							new_category.setID(data_source.getNextID(DatabaseHandler.TABLE_CATEGORIES));
							data_source.addCategory(new_category);
							category_adapter.insert(new_category, category_adapter.getCount() - 1);
							category_adapter.notifyDataSetChanged();
							new_category_dialog_active = false;
						}
					});
					color_dialog.show();
				}
			} else {
				s_category.setSelection(0);
				dialog.cancel();
			}
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
			
			// Fix white line bug in Gingerbread
			if (Build.VERSION.SDK_INT == Build.VERSION_CODES.GINGERBREAD ||
					Build.VERSION.SDK_INT == Build.VERSION_CODES.GINGERBREAD_MR1) {
				et_category.setBackgroundColor(Color.parseColor("#F0F0F0"));
			}
			
			AlertDialog.Builder new_category_builder = new AlertDialog.Builder(this);
			new_category_builder.setView(category_name_view);
			new_category_builder.setTitle("Set name");
			new_category_builder.setPositiveButton("Next", this);
			new_category_builder.setNegativeButton("Cancel", this);
			category_dialog = new_category_builder.create();
			new_category_dialog_active = true;
			category_dialog.show();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Do nothing
	}
	
	/**************************************************************************
	 * Methods implementing OnDateSetListener interface                       *
	 **************************************************************************/
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		due_date_cal.set(year, monthOfYear, dayOfMonth);
		tv_due_date.setText(DateFormat.format("MM/dd/yy", due_date_cal));
	}
	
	/**************************************************************************
	 * Methods implementing OnTimeSetListener interface                       *
	 **************************************************************************/
	
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		due_date_cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		due_date_cal.set(Calendar.MINUTE, minute);
		tv_due_time.setText(DateFormat.format("h:mm AA", due_date_cal));
	}
}
