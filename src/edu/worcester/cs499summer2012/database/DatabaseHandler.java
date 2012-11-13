/*
 * DatabaseHandler.java
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


package edu.worcester.cs499summer2012.database;

import java.util.GregorianCalendar;

import edu.worcester.cs499summer2012.task.Comparator;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

/**
 * Creates SQLite table for storing tasks to a database. DO NOT call this class directly
 * get an instance of TasksDataSource instead.
 * @author Dhimitraq Jorgji, Jonathan Hasenzahl
 */
public class DatabaseHandler extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 7;


	// Database Name
	private static final String DATABASE_NAME = "TaskButler.db";

	// Table names
	public static final String TABLE_TASKS = "tasks";
	public static final String TABLE_CATEGORIES = "categories";
	public static final String TABLE_COMPARATORS = "comparators";

	// Column names
	public static final String KEY_ID = "id";										 // INTEGER PRIMARY KEY
	public static final String KEY_NAME = "name"; 									 // TEXT
	public static final String KEY_COMPLETION = "completion"; 						 // INTEGER, indirectly boolean
	public static final String KEY_PRIORITY = "priority"; 							 // INTEGER
	public static final String KEY_CATEGORY = "category"; 							 // INTEGER
	public static final String KEY_HAS_DUE_DATE = "hasDueDate"; 					 // INTEGER, indirectly boolean
	public static final String KEY_HAS_FINAL_DUE_DATE = "hasFinalDueDate"; 			 // INTEGER, indirectly boolean
	public static final String KEY_IS_REPEATING = "isRepeating"; 					 // INTEGER, indirectly boolean
	public static final String KEY_HAS_STOP_REPEATING_DATE = "hasStopRepeatingDate"; // INTEGER, indirectly boolean
	public static final String KEY_REPEAT_TYPE = "repeatType"; 						 // INTEGER
	public static final String KEY_REPEAT_INTERVAL = "repeatInterval"; 				 // INTEGER
	public static final String KEY_CREATION_DATE = "creationDate"; 					 // DATETIME
	public static final String KEY_MODIFICATION_DATE = "modificationDate"; 			 // DATETIME
	public static final String KEY_DUE_DATE = "dueDate"; 							 // DATETIME
	public static final String KEY_FINAL_DUE_DATE = "finalDueDate";					 // DATETIME
	public static final String KEY_STOP_REPEATING_DATE = "stopRepeatingDate"; 		 // DATETIME
	public static final String KEY_NOTES = "notes"; 								 // TEXT, can be null
	public static final String KEY_COLOR = "color"; 								 // INTEGER, used in category table
	public static final String KEY_UPDATED = "updated";								 // DATETIME
	public static final String KEY_G_ID = "gID";									 // STRING
	public static final String KEY_ENABLED = "enabled";								 // INTEGER, indirectly boolean, used in comparators table
	public static final String KEY_ORDER = "list_order";									 // INTEGER, used in comparators table


	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Table
	@Override
	public void onCreate(SQLiteDatabase db) {
		String create_tasks_table = "CREATE TABLE " + TABLE_TASKS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY,"
				+ KEY_NAME + " TEXT,"
				+ KEY_COMPLETION + " INTEGER,"
				+ KEY_PRIORITY + " INTEGER,"
				+ KEY_CATEGORY + " INTEGER,"
				+ KEY_HAS_DUE_DATE + " INTEGER,"
				+ KEY_HAS_FINAL_DUE_DATE + " INTEGER,"
				+ KEY_IS_REPEATING + " INTEGER,"
				+ KEY_HAS_STOP_REPEATING_DATE + " INTEGER,"
				+ KEY_REPEAT_TYPE + " INTEGER,"
				+ KEY_REPEAT_INTERVAL + " INTEGER,"
				+ KEY_CREATION_DATE + " DATETIME,"
				+ KEY_MODIFICATION_DATE + " DATETIME,"
				+ KEY_DUE_DATE + " DATETIME,"
				+ KEY_FINAL_DUE_DATE + " DATETIME,"
				+ KEY_STOP_REPEATING_DATE + " DATETIME,"
				+ KEY_G_ID + " TEXT,"
				+ KEY_NOTES + " TEXT)";

		String create_categories_table = "CREATE TABLE " + TABLE_CATEGORIES + "(" 
				+ KEY_ID + " INTEGER PRIMARY KEY,"
				+ KEY_NAME + " TEXT,"
				+ KEY_COLOR + " INTEGER,"
				+ KEY_UPDATED + " DATETIME,"
				+ KEY_G_ID + " TEXT)";
		
		String create_comparators_table = "CREATE TABLE " + TABLE_COMPARATORS + "(" 
				+ KEY_ID + " INTEGER PRIMARY KEY,"
				+ KEY_NAME + " TEXT,"
				+ KEY_ENABLED + " INTEGER,"
				+ KEY_ORDER + " INTEGER)";


		db.execSQL(create_tasks_table);
		db.execSQL(create_categories_table);
		db.execSQL(create_comparators_table);

		// Create first entry of categories table
		ContentValues values = new ContentValues();
		values.put(KEY_ID, 1);
		values.put(KEY_NAME, "No category");
		values.put(KEY_COLOR, Color.parseColor("#00FFFFFF"));
		values.put(KEY_UPDATED, GregorianCalendar.getInstance().getTimeInMillis());
		db.insert(TABLE_CATEGORIES, null, values);
		
		// Create all entries of comparators table
		values = new ContentValues();
		values.put(KEY_ID, Comparator.NAME);
		values.put(KEY_NAME, "Task name");
		values.put(KEY_ENABLED, 0);
		values.put(KEY_ORDER, 0);
		db.insert(TABLE_COMPARATORS, null, values);
		values = new ContentValues();
		values.put(KEY_ID, Comparator.COMPLETION);
		values.put(KEY_NAME, "Completion status");
		values.put(KEY_ENABLED, 0);
		values.put(KEY_ORDER, 1);
		db.insert(TABLE_COMPARATORS, null, values);
		values = new ContentValues();
		values.put(KEY_ID, Comparator.PRIORITY);
		values.put(KEY_NAME, "Priority");
		values.put(KEY_ENABLED, 0);
		values.put(KEY_ORDER, 2);
		db.insert(TABLE_COMPARATORS, null, values);
		values = new ContentValues();
		values.put(KEY_ID, Comparator.CATEGORY);
		values.put(KEY_NAME, "Category");
		values.put(KEY_ENABLED, 0);
		values.put(KEY_ORDER, 3);
		db.insert(TABLE_COMPARATORS, null, values);
		values = new ContentValues();
		values.put(KEY_ID, Comparator.DATE_DUE);
		values.put(KEY_NAME, "Due date");
		values.put(KEY_ENABLED, 0);
		values.put(KEY_ORDER, 4);
		db.insert(TABLE_COMPARATORS, null, values);
		values = new ContentValues();
		values.put(KEY_ID, Comparator.FINAL_DATE_DUE);
		values.put(KEY_NAME, "Procrastination alarm date");
		values.put(KEY_ENABLED, 0);
		values.put(KEY_ORDER, 5);
		db.insert(TABLE_COMPARATORS, null, values);
		values = new ContentValues();
		values.put(KEY_ID, Comparator.DATE_CREATED);
		values.put(KEY_NAME, "Date created");
		values.put(KEY_ENABLED, 0);
		values.put(KEY_ORDER, 6);
		db.insert(TABLE_COMPARATORS, null, values);
		values = new ContentValues();
		values.put(KEY_ID, Comparator.DATE_MODIFIED);
		values.put(KEY_NAME, "Date modified");
		values.put(KEY_ENABLED, 0);
		values.put(KEY_ORDER, 7);
		db.insert(TABLE_COMPARATORS, null, values);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed just for testing purposes, 
		//TODO:change to copy over old database 
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPARATORS);

		// Create tables again
		onCreate(db);
	}
}
