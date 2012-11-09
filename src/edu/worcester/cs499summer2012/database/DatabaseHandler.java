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

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Creates SQLite table for storing tasks to a database. DO NOT call this class directly
 * get an instance of TasksDataSource instead.
 * @author Dhimitraq Jorgji, Jonathan Hasenzahl
 */
public class DatabaseHandler extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 3;

	// Database Name
	private static final String DATABASE_NAME = "TaskButler.db";

	// Table names
	public static final String TABLE_TASKS = "tasks";
	public static final String TABLE_CATEGORIES = "categories";

	// Column names
	public static final String KEY_ID = "id";
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
	public static final String KEY_UPDATED = "update";


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
				+ KEY_NOTES + " TEXT)";
		
		String create_categories_table = "CREATE TABLE " + TABLE_CATEGORIES + "(" 
				+ KEY_ID + " INTEGER PRIMARY KEY,"
				+ KEY_NAME + " TEXT,"
				+ KEY_COLOR + " INTEGER)"
				+ KEY_UPDATED + " DATETIME,";
				
		db.execSQL(create_tasks_table);
		db.execSQL(create_categories_table);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed just for testing purposes, 
		//TODO:change to copy over old database 
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);

		// Create tables again
		onCreate(db);
	}
}
