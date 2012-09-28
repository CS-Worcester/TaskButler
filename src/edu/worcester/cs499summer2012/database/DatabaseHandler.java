/*
 *DatabaseHandler.java
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

/**
 * Creates SQLite table for the App, also gives some CRUD (Create, Read, Update and Delete) functionality
 * when working with tasks
 * @author Dhimitraq Jorgji
 */

package edu.worcester.cs499summer2012.database;

import java.util.ArrayList;
import java.util.List;

import edu.worcester.cs499summer2012.task.Task;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "taskButler";

	// tasks table name
	private static final String TABLE_TASKS = "tasks";

	// tasks Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name"; //data type: TEXT
	private static final String KEY_COMPLETION = "completion"; //data type: INTEGER, indirectly DATETIME as Unix Time
	private static final String KEY_PRIORITY = "priority"; //data type: INTEGER, 2=URGENT, 1=REGULAR,0=TRIVIAL 
	private static final String KEY_CATEGORY = "category"; //data type: INTEGER
	private static final String KEY_CREATION_DATE = "creationDate"; //data type: INTEGER, indirectly DATETIME as Unix Time
	private static final String KEY_MODIFICATION_DATE = "modificationDate"; //data type: INTEGER, indirectly DATETIME as Unix Time
	private static final String KEY_DUE_DATE = "dueDate"; //data type: INTEGER, indirectly DATETIME as Unix Time
	private static final String KEY_FINAL_DUE_DATE = "finalDueDate"; //data type: INTEGER, indirectly DATETIME as Unix Time
	private static final String KEY_NOTES = "notes"; //data type: TEXT can be null


	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Table
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY,"
				+ KEY_NAME + " TEXT,"
				+ KEY_COMPLETION + " INTEGER,"
				+ KEY_PRIORITY + " INTEGER,"
				+ KEY_CATEGORY + " INTEGER,"
				+ KEY_CREATION_DATE + " INTEGER,"
				+ KEY_MODIFICATION_DATE + " INTEGER,"
				+ KEY_DUE_DATE + " INTEGER,"
				+ KEY_FINAL_DUE_DATE + " INTEGER"
				+ KEY_NOTES + " TEXT" + ")";

		db.execSQL(CREATE_TASKS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed just for testing purposes, will change to copy over old database later on
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);

		// Create tables again
		onCreate(db);
	}


	// Adding new task
	void addTask(Task task) {
		SQLiteDatabase db = this.getWritableDatabase(); //open db as writeable

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, task.getName()); // Task Name
		values.put(KEY_COMPLETION, task.isCompleted()); // Task completion
		values.put(KEY_PRIORITY, task.getPriority()); // Task priority
		values.put(KEY_CATEGORY, task.getCategory()); // Task category
		values.put(KEY_CREATION_DATE, task.getDateCreated()); //Task creation date
		values.put(KEY_MODIFICATION_DATE, task.getDateModified()); // Task modification date
		values.put(KEY_DUE_DATE, task.getDateDue()); //Task due date
		values.put(KEY_FINAL_DUE_DATE, task.getFinalDateDue()); // Task final due date
		values.put(KEY_NOTES, task.getNotes()); //Task notes

		// Inserting Row
		db.insert(TABLE_TASKS, null, values);
		db.close(); // Closing database connection
	}

	// Getting single task
	Task getTask(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_TASKS, new String[] { KEY_ID,
				KEY_NAME, KEY_COMPLETION, KEY_PRIORITY, KEY_CATEGORY,
				KEY_CREATION_DATE, KEY_MODIFICATION_DATE, KEY_DUE_DATE, 
				KEY_FINAL_DUE_DATE, KEY_NOTES}, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		return new Task(cursor.getInt(0), cursor.getString(1), 
				cursor.getInt(2) > 0, cursor.getInt(3), cursor.getInt(4), 
				cursor.getInt(5), cursor.getInt(6), cursor.getInt(7),
				cursor.getInt(8), cursor.getString(9));
	}

	// Getting All Tasks
	public List<Task> getAllTasks() {
		List<Task> taskList = new ArrayList<Task>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_TASKS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Task task = new Task();
				task.setID(cursor.getInt(0));
				task.setName(cursor.getString(1));
				task.setIsCompleted(cursor.getInt(2) > 0); // if value > 0 then isCompleted is set to true
				task.setPriority(cursor.getInt(3)); // urgent=2 regular=1 trivial=0
				task.setCategory(cursor.getInt(4));
				task.setDateCreated(cursor.getInt(5));
				task.setDateModified(cursor.getInt(6));
				task.setDateDue(cursor.getInt(7));
				task.setFinalDateDue(cursor.getInt(8));
				task.setNotes(cursor.getString(9));

				// Adding task to list
				taskList.add(task);
			} while (cursor.moveToNext());
		}

		// return task list
		return taskList;
	}

	// Updating single task
	public int updateTask(Task task) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, task.getName()); // Task Name
		values.put(KEY_COMPLETION, task.isCompleted()); // Task completion
		values.put(KEY_PRIORITY, task.getPriority()); // Task priority
		values.put(KEY_CATEGORY, task.getCategory()); // Task category
		values.put(KEY_CREATION_DATE, task.getDateCreated()); //Task creation date
		values.put(KEY_MODIFICATION_DATE, task.getDateModified()); // Task modification date
		values.put(KEY_DUE_DATE, task.getDateDue()); //Task due date
		values.put(KEY_FINAL_DUE_DATE, task.getFinalDateDue()); // Task final due date
		values.put(KEY_NOTES, task.getNotes()); //Task notes

		// updating row
		return db.update(TABLE_TASKS, values, KEY_ID + " =?",
				new String[] { String.valueOf(task.getID()) });
	}

	// Deleting single task
	public void deleteTask(Task task) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TASKS, KEY_ID + " =?",
				new String[] { String.valueOf(task.getID()) });
		db.close();
	}

}
