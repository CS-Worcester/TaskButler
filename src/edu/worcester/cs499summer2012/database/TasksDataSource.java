/*
 * TasksDataSource.java
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

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import edu.worcester.cs499summer2012.task.Task;

/**
 * Wrapper for the database handler. Gives some CRUD (Create, Read, Update and 
 * Delete) functionality to the task database. Implemented as a Singleton to allow
 * for thread management, call TasksDataSource.getInstance(Context) to get access
 * to the instance before using the database. THIS IS NOT A CLONABLE CLASS
 * @author Dhimitraq Jorgji, Jonathan Hasenzahl
 */
public class TasksDataSource {

	private SQLiteDatabase db;
	private DatabaseHandler handler;
	private static TasksDataSource instance;
	
	private TasksDataSource(){
		
	}
	
	private TasksDataSource(Context context) {
		handler = new DatabaseHandler(context);
	}
	/**
	 * Call this to get access to the instance of TasksDataSource Singleton
	 * @param context
	 * @return instance of TasksDataSource
	 */
	public static synchronized TasksDataSource getInstance(Context context) {
		instance = new TasksDataSource(context);
		instance.open();
        return instance;
    }
	
	private void open() throws SQLException {
		db = handler.getWritableDatabase();
	}
	
	private void close() {
		handler.close();
	}

	public Task getTask(int id) {
		open();
		Cursor cursor = db.query(DatabaseHandler.TABLE_TASKS, new String[] { 
				DatabaseHandler.KEY_ID,
				DatabaseHandler.KEY_NAME, 
				DatabaseHandler.KEY_COMPLETION, 
				DatabaseHandler.KEY_PRIORITY, 
				DatabaseHandler.KEY_CATEGORY,
				DatabaseHandler.KEY_CREATION_DATE,
				DatabaseHandler.KEY_MODIFICATION_DATE, 
				DatabaseHandler.KEY_DUE_DATE,
				DatabaseHandler.KEY_FINAL_DUE_DATE,
				DatabaseHandler.KEY_NOTES }, 
				DatabaseHandler.KEY_ID + " = " + id,
				null, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		Task task = new Task(cursor.getInt(0), cursor.getString(1), 
				cursor.getInt(2) > 0, cursor.getInt(3), cursor.getInt(4), 
				cursor.getLong(5), cursor.getLong(6), 
				cursor.getLong(7), cursor.getLong(8), 
				cursor.getString(9));
		close();
		cursor.close();
		return task;
	}
	
	public ArrayList<Task> getAllTasks() {
		ArrayList<Task> taskList = new ArrayList<Task>();
		
		// Select All Query
		String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_TASKS;

		open();
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
				task.setDateCreated(cursor.getLong(5));
				task.setDateModified(cursor.getLong(6));
				task.setDateDue(cursor.getLong(7));
				task.setFinalDateDue(cursor.getLong(8));
				task.setNotes(cursor.getString(9));

				// Adding task to list
				taskList.add(task);
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		close();
		// return task list
		return taskList;
	}
	
	/**
	 * Returns the next available ID to be assigned to a new task. This
	 * number is equal to the highest current ID + 1.
	 * @return the next available task ID to be assigned to a new task
	 */
	public int getNextID() {
		
		String selectQuery = "SELECT MAX(" + DatabaseHandler.KEY_ID +
				") FROM " + DatabaseHandler.TABLE_TASKS;
		open();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if (cursor.moveToFirst()){
			int i = cursor.getInt(0) + 1;
			cursor.close();
			close();
			return i;
		}
		else{
			cursor.close();
			close();
			return 1;
		}
	}
	
	public void addTask(Task task) {
		open();
		ContentValues values = new ContentValues();
		values.put(DatabaseHandler.KEY_ID, task.getID());
		values.put(DatabaseHandler.KEY_NAME, task.getName()); // Task Name
		values.put(DatabaseHandler.KEY_COMPLETION, task.isCompleted()); // Task completion
		values.put(DatabaseHandler.KEY_PRIORITY, task.getPriority()); // Task priority
		values.put(DatabaseHandler.KEY_CATEGORY, task.getCategory()); // Task category
		values.put(DatabaseHandler.KEY_CREATION_DATE, task.getDateCreated()); //Task creation date
		values.put(DatabaseHandler.KEY_MODIFICATION_DATE, task.getDateModified()); // Task modification date
		values.put(DatabaseHandler.KEY_DUE_DATE, task.getDateDue()); //Task due date
		values.put(DatabaseHandler.KEY_FINAL_DUE_DATE, task.getFinalDateDue()); // Task final due date
		values.put(DatabaseHandler.KEY_NOTES, task.getNotes()); //Task notes
		
		// Inserting Row
		db.insert(DatabaseHandler.TABLE_TASKS, null, values);
		close();
	}
	/**
	 * 
	 * @param task
	 * @return number of rows affected
	 */
	public int updateTask(Task task) {
		open();
		ContentValues values = new ContentValues();
		values.put(DatabaseHandler.KEY_NAME, task.getName()); // Task Name
		values.put(DatabaseHandler.KEY_COMPLETION, task.isCompleted()); // Task completion
		values.put(DatabaseHandler.KEY_PRIORITY, task.getPriority()); // Task priority
		values.put(DatabaseHandler.KEY_CATEGORY, task.getCategory()); // Task category
		values.put(DatabaseHandler.KEY_CREATION_DATE, task.getDateCreated()); //Task creation date
		values.put(DatabaseHandler.KEY_MODIFICATION_DATE, task.getDateModified()); // Task modification date
		values.put(DatabaseHandler.KEY_DUE_DATE, task.getDateDue()); //Task due date
		values.put(DatabaseHandler.KEY_FINAL_DUE_DATE, task.getFinalDateDue()); // Task final due date
		values.put(DatabaseHandler.KEY_NOTES, task.getNotes()); //Task notes
		
		// updating row
		int i = db.update(DatabaseHandler.TABLE_TASKS, values, 
				DatabaseHandler.KEY_ID + " = " + task.getID(), null);
		close();
		return i;		
	}
	
	public void deleteTask(Task task) {
		open();
		db.delete(DatabaseHandler.TABLE_TASKS, 
				DatabaseHandler.KEY_ID + " = " + task.getID(), null);
		close();
	}
	
	public int deleteFinishedTasks() {
		open();
		int i = db.delete(DatabaseHandler.TABLE_TASKS,
				DatabaseHandler.KEY_COMPLETION + " = 1", null);
		close();
		return i;
	}
	
	public int deleteAllTasks() {
		open();
		int i = db.delete(DatabaseHandler.TABLE_TASKS, null, null);
		close();
		return i;
	}
	
	   @Override
	    protected Object clone() throws CloneNotSupportedException {
	        throw new CloneNotSupportedException("Clone is not allowed.");
	    }
}
