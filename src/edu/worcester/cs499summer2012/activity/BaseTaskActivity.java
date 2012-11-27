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
import android.widget.Button;
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
	public final static int SECOND_MS = 1000;
	public final static int MINUTE_MS = SECOND_MS * 60;
	public final static int HOUR_MS = MINUTE_MS * 60;
    
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
    protected CheckBox has_due_date;
    protected Button edit_due_date;
    protected TextView due_date;
    protected CheckBox has_final_due_date;
    protected Button edit_final_due_date;
    protected TextView final_due_date;
    protected CheckBox has_repetition;   
    protected TextView repeats;
    protected EditText repeat_interval;
    protected Spinner category;
    protected Spinner repeat_type;
    protected CheckBox stop_repeating;
    protected Button edit_stop_repeating_date;
    protected TextView stop_repeating_date;
    protected DatePicker date_picker;
    protected TimePicker time_picker;
    protected EditText category_name;
    
    // Task properties that are modified by UI elements
    protected Calendar due_date_cal;
    protected Calendar final_due_date_cal;
    protected Calendar stop_repeating_date_cal;
    protected int selected_calendar;
    protected int selected_dialog;
    
    // Flags to prevent date-time picker dialogs popping up immediately on
    // entering EditTaskActivity
    protected boolean prevent_initial_due_date_popup = false;
    protected boolean prevent_initial_final_due_date_popup = false;
    protected boolean prevent_initial_stop_repeating_date_popup = false;
    
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
        has_due_date = (CheckBox) findViewById(R.id.checkbox_has_due_date);
        edit_due_date = (Button) findViewById(R.id.button_edit_due_date);
        due_date = (TextView) findViewById(R.id.text_add_task_due_date);
        has_final_due_date = (CheckBox) findViewById(R.id.checkbox_has_final_due_date);
        edit_final_due_date = (Button) findViewById(R.id.button_edit_final_due_date);
        final_due_date = (TextView) findViewById(R.id.text_add_task_final_due_date);
        has_repetition = (CheckBox) findViewById(R.id.checkbox_has_repetition);   
        repeats = (TextView) findViewById(R.id.text_add_task_repeats);
        repeat_interval = (EditText) findViewById(R.id.edit_add_task_repeat_interval);
        category = (Spinner) findViewById(R.id.spinner_add_task_category);
        repeat_type = (Spinner) findViewById(R.id.spinner_add_task_repeat_type);
        stop_repeating = (CheckBox) findViewById(R.id.checkbox_stop_repeating);
        edit_stop_repeating_date = (Button) findViewById(R.id.button_edit_stop_repeating_date);
        stop_repeating_date = (TextView) findViewById(R.id.text_add_task_stop_repeating_date);
                
        // Set listeners
        has_due_date.setOnCheckedChangeListener(this);
        has_final_due_date.setOnCheckedChangeListener(this);
        has_repetition.setOnCheckedChangeListener(this);
        stop_repeating.setOnCheckedChangeListener(this);
        edit_due_date.setOnClickListener(this);
        edit_final_due_date.setOnClickListener(this);
        edit_stop_repeating_date.setOnClickListener(this);
        
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
        category.setAdapter(category_adapter);
        category.setOnItemSelectedListener(this);
        
        // Populate the repeat type spinner
        ArrayAdapter<CharSequence> repeat_type_adapter = 
        		ArrayAdapter.createFromResource(this, 
        				R.array.spinner_repeat_types, 
        				android.R.layout.simple_spinner_item);
        repeat_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeat_type.setAdapter(repeat_type_adapter);
        
        // Hide certain due date items to start
        edit_due_date.setVisibility(View.GONE);
        due_date.setVisibility(View.GONE);
        has_final_due_date.setVisibility(View.GONE);
        edit_final_due_date.setVisibility(View.GONE);
        final_due_date.setVisibility(View.GONE);
        has_repetition.setVisibility(View.GONE);
        repeats.setVisibility(View.GONE);
        repeat_interval.setVisibility(View.GONE);
        repeat_type.setVisibility(View.GONE);
        stop_repeating.setVisibility(View.GONE);
        edit_stop_repeating_date.setVisibility(View.GONE);
        stop_repeating_date.setVisibility(View.GONE);
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
				edit_due_date.setVisibility(View.VISIBLE);
				due_date.setVisibility(View.VISIBLE);
				due_date.setText(DateFormat.format("'Due:' MM/dd/yy 'at' h:mm AA", 
						due_date_cal));
				has_final_due_date.setVisibility(View.VISIBLE);
				has_repetition.setVisibility(View.VISIBLE);
				
				// Pop up date-time dialog as if user had clicked edit button
				if (prevent_initial_due_date_popup)
					prevent_initial_due_date_popup = false;
				else
					onClick(edit_due_date);
			} else {
				// Hide edit button, textview, and other checkboxes
				edit_due_date.setVisibility(View.GONE);
				due_date.setVisibility(View.GONE);
				has_final_due_date.setVisibility(View.GONE);
				has_repetition.setVisibility(View.GONE);
				
				// Uncheck final due date and repetition boxes
				if (has_final_due_date.isChecked())
					has_final_due_date.setChecked(false);
				if (has_repetition.isChecked())
					has_repetition.setChecked(false);
			}
			break;
			
		case R.id.checkbox_has_final_due_date:
			if (isChecked) {
				// Make edit button and textview visible
				edit_final_due_date.setVisibility(View.VISIBLE);
				final_due_date.setVisibility(View.VISIBLE);
				final_due_date.setText(DateFormat.format("'Alarm:' MM/dd/yy 'at' h:mm AA", 
						final_due_date_cal));
				
				// Pop up date-time dialog as if user had clicked edit button
				// Pop up date-time dialog as if user had clicked edit button
				if (prevent_initial_final_due_date_popup)
					prevent_initial_final_due_date_popup = false;
				else
					onClick(edit_final_due_date);
			} else {
				// Hide edit button and textview
				edit_final_due_date.setVisibility(View.GONE);
				final_due_date.setVisibility(View.GONE);
			}
			break;
			
		case R.id.checkbox_has_repetition:
			if (isChecked) {
				// Make textview, edittext, spinner, and checkbox visible
				repeats.setVisibility(View.VISIBLE);
				repeat_interval.setVisibility(View.VISIBLE);
				repeat_interval.setText(repeat_interval_string);
				repeat_type.setVisibility(View.VISIBLE);
				stop_repeating.setVisibility(View.VISIBLE);
			} else {
				// Hide textview, edittext, spinner, and checkbox
				repeats.setVisibility(View.GONE);
				repeat_interval.setVisibility(View.GONE);
				repeat_type.setVisibility(View.GONE);
				stop_repeating.setVisibility(View.GONE);
				
				// Uncheck stop repeating box
				if (stop_repeating.isChecked())
					stop_repeating.setChecked(false);
			}
			break;
			
		case R.id.checkbox_stop_repeating:
			if (isChecked) {
				// Make edit button and textview visible
				edit_stop_repeating_date.setVisibility(View.VISIBLE);
				stop_repeating_date.setVisibility(View.VISIBLE);
				stop_repeating_date.setText(DateFormat.format("'Ends:' MM/dd/yy 'at' h:mm AA", 
						stop_repeating_date_cal));
				
				// Pop up date-time dialog as if user had clicked edit button
				if (prevent_initial_stop_repeating_date_popup)
					prevent_initial_stop_repeating_date_popup = false;
				else
					onClick(edit_stop_repeating_date);
			} else {
				// Hide edit button and textview
				edit_stop_repeating_date.setVisibility(View.GONE);
				stop_repeating_date.setVisibility(View.GONE);
			}
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
		
		switch (v.getId()) {
		case R.id.button_edit_due_date:
			selected_calendar = R.id.button_edit_due_date;
			date_picker.updateDate(due_date_cal.get(Calendar.YEAR), 
					due_date_cal.get(Calendar.MONTH), 
					due_date_cal.get(Calendar.DAY_OF_MONTH));
			time_picker.setCurrentHour(due_date_cal.get(Calendar.HOUR_OF_DAY));
			time_picker.setCurrentMinute(due_date_cal.get(Calendar.MINUTE));
			break;
			
		case R.id.button_edit_final_due_date:
			selected_calendar = R.id.button_edit_final_due_date;
			date_picker.updateDate(final_due_date_cal.get(Calendar.YEAR), 
					final_due_date_cal.get(Calendar.MONTH), 
					final_due_date_cal.get(Calendar.DAY_OF_MONTH));
			time_picker.setCurrentHour(final_due_date_cal.get(Calendar.HOUR_OF_DAY));
			time_picker.setCurrentMinute(final_due_date_cal.get(Calendar.MINUTE));
			break;
			
		case R.id.button_edit_stop_repeating_date:
			selected_calendar = R.id.button_edit_stop_repeating_date;
			date_picker.updateDate(stop_repeating_date_cal.get(Calendar.YEAR), 
					stop_repeating_date_cal.get(Calendar.MONTH), 
					stop_repeating_date_cal.get(Calendar.DAY_OF_MONTH));
			time_picker.setCurrentHour(stop_repeating_date_cal.get(Calendar.HOUR_OF_DAY));
			time_picker.setCurrentMinute(stop_repeating_date_cal.get(Calendar.MINUTE));
		}
		
		selected_dialog = DATETIME_DIALOG;
		AlertDialog.Builder picker_dialog = new AlertDialog.Builder(this);
		picker_dialog.setView(picker_view)
		             .setTitle("Set date and time")
		             .setCancelable(true)
		             .setPositiveButton("Accept", this)
		             .setNegativeButton("Cancel", this);
		picker_dialog.show();
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
				switch (selected_calendar) {
				case R.id.button_edit_due_date:
					due_date_cal.set(Calendar.YEAR, date_picker.getYear());
					due_date_cal.set(Calendar.MONTH, date_picker.getMonth());
					due_date_cal.set(Calendar.DAY_OF_MONTH, date_picker.getDayOfMonth());
					due_date_cal.set(Calendar.HOUR_OF_DAY, time_picker.getCurrentHour());
					due_date_cal.set(Calendar.MINUTE, time_picker.getCurrentMinute());
					due_date.setText(DateFormat.format("'Due:' MM/dd/yy 'at' h:mm AA", 
							due_date_cal));
					
					if (due_date_cal.getTimeInMillis() > final_due_date_cal.getTimeInMillis())
						final_due_date_cal.setTimeInMillis(due_date_cal.getTimeInMillis() + HOUR_MS);
					break;
					
				case R.id.button_edit_final_due_date:
					final_due_date_cal.set(Calendar.YEAR, date_picker.getYear());
					final_due_date_cal.set(Calendar.MONTH, date_picker.getMonth());
					final_due_date_cal.set(Calendar.DAY_OF_MONTH, date_picker.getDayOfMonth());
					final_due_date_cal.set(Calendar.HOUR_OF_DAY, time_picker.getCurrentHour());
					final_due_date_cal.set(Calendar.MINUTE, time_picker.getCurrentMinute());
					final_due_date.setText(DateFormat.format("'Alarm:' MM/dd/yy 'at' h:mm AA", 
							final_due_date_cal));
					break;
				
				case R.id.button_edit_stop_repeating_date:
					stop_repeating_date_cal.set(Calendar.YEAR, date_picker.getYear());
					stop_repeating_date_cal.set(Calendar.MONTH, date_picker.getMonth());
					stop_repeating_date_cal.set(Calendar.DAY_OF_MONTH, date_picker.getDayOfMonth());
					stop_repeating_date_cal.set(Calendar.HOUR_OF_DAY, time_picker.getCurrentHour());
					stop_repeating_date_cal.set(Calendar.MINUTE, time_picker.getCurrentMinute());
					stop_repeating_date.setText(DateFormat.format("'Ends:' MM/dd/yy 'at' h:mm AA", 
							stop_repeating_date_cal));
					break;
				}
			} else
				dialog.cancel();
			break;
			
		case CATEGORY_DIALOG:
			if (id == DialogInterface.BUTTON_POSITIVE) {
				String name = category_name.getText().toString();
				if (name.equals("")) {
					// No name, cancel dialog
					Toast.makeText(this, "Category needs a name!", Toast.LENGTH_SHORT).show();
					category.setSelection(0);
					dialog.cancel();
				} else if (data_source.doesCategoryNameExist(name)) {
					// Category name already exists, cancel dialog
					Toast.makeText(this, "Category name already exists", Toast.LENGTH_SHORT).show();
					category.setSelection(0);
					dialog.cancel();
				} else  {
					AmbilWarnaDialog color_dialog = new AmbilWarnaDialog(this, Color.parseColor("#00FFFFFF"), new OnAmbilWarnaListener() {
	
						@Override
						public void onCancel(AmbilWarnaDialog dialog) {
							category.setSelection(0);
						}
	
						@Override
						public void onOk(AmbilWarnaDialog dialog, int color) {
							Category new_category = new Category(category_name.getText().toString(), 
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
				category.setSelection(0);
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
		if (((Category) category.getItemAtPosition(position)).getID() == Category.NEW_CATEGORY) {
			selected_dialog = CATEGORY_DIALOG;
			
			LayoutInflater li = LayoutInflater.from(this);
			View category_name_view = li.inflate(R.layout.dialog_category_name, null);
			category_name = (EditText) category_name_view.findViewById(R.id.edit_category_name);
			
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
