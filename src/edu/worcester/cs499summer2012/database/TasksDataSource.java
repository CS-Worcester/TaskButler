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
 * Delete) functionality to the task database.
 * @author Dhimitraq Jorgji, Jonathan Hasenzahl
 */
public class TasksDataSource {

	private SQLiteDatabase db;
	private DatabaseHandler handler;
	
	public TasksDataSource(Context context) {
		handler = new DatabaseHandler(context);
	}
	
	public void open() throws SQLException {
		db = handler.getWritableDatabase();
	}
	
	public void close() {
		handler.close();
	}

	Task getTask(int id) {
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

		return new Task(cursor.getInt(0), cursor.getString(1), 
				cursor.getInt(2) > 0, cursor.getInt(3), cursor.getInt(4), 
				cursor.getLong(5) * 60000, cursor.getLong(6) * 60000, 
				cursor.getLong(7) * 60000, cursor.getLong(8) * 60000, 
				cursor.getString(9));
	}
	
	public ArrayList<Task> getAllTasks() {
		ArrayList<Task> taskList = new ArrayList<Task>();
		// Select All Query
		String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_TASKS;

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
				task.setDateCreated(cursor.getLong(5) * 60000);
				task.setDateModified(cursor.getLong(6) * 60000);
				task.setDateDue(cursor.getLong(7) * 60000);
				task.setFinalDateDue(cursor.getLong(8) * 60000);
				task.setNotes(cursor.getString(9));

				// Adding task to list
				taskList.add(task);
			} while (cursor.moveToNext());
		}
		
		cursor.close();

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
		
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if (cursor.moveToFirst())
			return cursor.getInt(0) + 1;
		else
			return 1;
	}
	
	public void addTask(Task task) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHandler.KEY_ID, task.getID());
		values.put(DatabaseHandler.KEY_NAME, task.getName()); // Task Name
		values.put(DatabaseHandler.KEY_COMPLETION, task.isCompleted()); // Task completion
		values.put(DatabaseHandler.KEY_PRIORITY, task.getPriority()); // Task priority
		values.put(DatabaseHandler.KEY_CATEGORY, task.getCategory()); // Task category
		values.put(DatabaseHandler.KEY_CREATION_DATE, task.getDateCreated() / 60000); //Task creation date
		values.put(DatabaseHandler.KEY_MODIFICATION_DATE, task.getDateModified() / 60000); // Task modification date
		values.put(DatabaseHandler.KEY_DUE_DATE, task.getDateDue() / 60000); //Task due date
		values.put(DatabaseHandler.KEY_FINAL_DUE_DATE, task.getFinalDateDue() / 60000); // Task final due date
		values.put(DatabaseHandler.KEY_NOTES, task.getNotes()); //Task notes

		// Inserting Row
		db.insert(DatabaseHandler.TABLE_TASKS, null, values);
	}
	
	public int updateTask(Task task) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHandler.KEY_NAME, task.getName()); // Task Name
		values.put(DatabaseHandler.KEY_COMPLETION, task.isCompleted()); // Task completion
		values.put(DatabaseHandler.KEY_PRIORITY, task.getPriority()); // Task priority
		values.put(DatabaseHandler.KEY_CATEGORY, task.getCategory()); // Task category
		values.put(DatabaseHandler.KEY_CREATION_DATE, task.getDateCreated() / 60000); //Task creation date
		values.put(DatabaseHandler.KEY_MODIFICATION_DATE, task.getDateModified() / 60000); // Task modification date
		values.put(DatabaseHandler.KEY_DUE_DATE, task.getDateDue() / 60000); //Task due date
		values.put(DatabaseHandler.KEY_FINAL_DUE_DATE, task.getFinalDateDue() / 60000); // Task final due date
		values.put(DatabaseHandler.KEY_NOTES, task.getNotes()); //Task notes

		// updating row
		return db.update(DatabaseHandler.TABLE_TASKS, values, 
				DatabaseHandler.KEY_ID + " = " + task.getID(), null);		
	}
	
	public void deleteTask(Task task) {
		db.delete(DatabaseHandler.TABLE_TASKS, 
				DatabaseHandler.KEY_ID + " = " + task.getID(), null);
	}
	
	public int deleteFinishedTasks() {
		return db.delete(DatabaseHandler.TABLE_TASKS,
				DatabaseHandler.KEY_COMPLETION + " = 1", null);
	}
	
	public int deleteAllTasks() {
		return db.delete(DatabaseHandler.TABLE_TASKS, null, null);
	}
}
