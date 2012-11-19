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
import edu.worcester.cs499summer2012.task.Category;
import edu.worcester.cs499summer2012.task.Comparator;
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

	/*********************************************************************
	 * Task																 *
	 *********************************************************************/

	/**
	 * Query a task using its id
	 * @param id
	 * @return
	 */
	public Task getTask(int id) {
		open();
		Cursor cursor = db.query(DatabaseHandler.TABLE_TASKS, new String[] { 
				DatabaseHandler.KEY_ID,
				DatabaseHandler.KEY_NAME, 
				DatabaseHandler.KEY_COMPLETION, 
				DatabaseHandler.KEY_PRIORITY, 
				DatabaseHandler.KEY_CATEGORY,
				DatabaseHandler.KEY_HAS_DUE_DATE,
				DatabaseHandler.KEY_HAS_FINAL_DUE_DATE,
				DatabaseHandler.KEY_IS_REPEATING,
				DatabaseHandler.KEY_HAS_STOP_REPEATING_DATE,
				DatabaseHandler.KEY_REPEAT_TYPE,
				DatabaseHandler.KEY_REPEAT_INTERVAL,
				DatabaseHandler.KEY_CREATION_DATE,
				DatabaseHandler.KEY_MODIFICATION_DATE, 
				DatabaseHandler.KEY_DUE_DATE,
				DatabaseHandler.KEY_FINAL_DUE_DATE,
				DatabaseHandler.KEY_STOP_REPEATING_DATE,
				DatabaseHandler.KEY_G_ID,
				DatabaseHandler.KEY_NOTES }, 
				DatabaseHandler.KEY_ID + " = " + id,
				null, null, null, null, null);
		if (cursor.moveToFirst()) {
			Task task = new Task(
					cursor.getInt(0), 
					cursor.getString(1), 
					cursor.getInt(2) > 0, 
					cursor.getInt(3),
					cursor.getInt(4),
					cursor.getInt(5) > 0,
					cursor.getInt(6) > 0,
					cursor.getInt(7) > 0,
					cursor.getInt(8) > 0,
					cursor.getInt(9),
					cursor.getInt(10), 
					cursor.getLong(11), 
					cursor.getLong(12), 
					cursor.getLong(13), 
					cursor.getLong(14),
					cursor.getLong(15),
					cursor.getString(16),
					cursor.getString(17));
			close();
			cursor.close();
			return task;
		} else {
			close();
			cursor.close();
			return null;
		}
	}
	
	public ArrayList<Task> getTasksByCategory(Category c) {
		ArrayList<Task> taskList = new ArrayList<Task>();
		
		String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_TASKS + 
				" WHERE " + DatabaseHandler.KEY_CATEGORY + " = " + c.getID();
		
		open();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Task task = new Task(
						cursor.getInt(0), 
						cursor.getString(1), 
						cursor.getInt(2) > 0, 
						cursor.getInt(3),
						cursor.getInt(4),
						cursor.getInt(5) > 0,
						cursor.getInt(6) > 0,
						cursor.getInt(7) > 0,
						cursor.getInt(8) > 0,
						cursor.getInt(9),
						cursor.getInt(10), 
						cursor.getLong(11), 
						cursor.getLong(12), 
						cursor.getLong(13), 
						cursor.getLong(14),
						cursor.getLong(15),
						cursor.getString(16),
						cursor.getString(17));

				// Adding task to list
				taskList.add(task);
			} while (cursor.moveToNext());
		}

		cursor.close();
		close();
		
		return taskList;
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
				Task task = new Task(
						cursor.getInt(0), 
						cursor.getString(1), 
						cursor.getInt(2) > 0, 
						cursor.getInt(3),
						cursor.getInt(4),
						cursor.getInt(5) > 0,
						cursor.getInt(6) > 0,
						cursor.getInt(7) > 0,
						cursor.getInt(8) > 0,
						cursor.getInt(9),
						cursor.getInt(10), 
						cursor.getLong(11), 
						cursor.getLong(12), 
						cursor.getLong(13), 
						cursor.getLong(14),
						cursor.getLong(15),
						cursor.getString(16),
						cursor.getString(17));

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
	public int getNextID(String table) {

		String selectQuery = "SELECT MAX(" + DatabaseHandler.KEY_ID +
				") FROM " + table;
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
	/**
	 * Insert a task to the tasks table
	 * @param task
	 */
	public void addTask(Task task) {
		open();
		ContentValues values = new ContentValues();
		values.put(DatabaseHandler.KEY_ID, task.getID());
		values.put(DatabaseHandler.KEY_NAME, task.getName());
		values.put(DatabaseHandler.KEY_COMPLETION, task.isCompleted());
		values.put(DatabaseHandler.KEY_PRIORITY, task.getPriority());
		values.put(DatabaseHandler.KEY_CATEGORY, task.getCategory());
		values.put(DatabaseHandler.KEY_HAS_DUE_DATE, task.hasDateDue());
		values.put(DatabaseHandler.KEY_HAS_FINAL_DUE_DATE, task.hasFinalDateDue());
		values.put(DatabaseHandler.KEY_IS_REPEATING, task.isRepeating());
		values.put(DatabaseHandler.KEY_HAS_STOP_REPEATING_DATE, task.hasStopRepeatingDate());
		values.put(DatabaseHandler.KEY_REPEAT_TYPE, task.getRepeatType());
		values.put(DatabaseHandler.KEY_REPEAT_INTERVAL, task.getRepeatInterval());
		values.put(DatabaseHandler.KEY_CREATION_DATE, task.getDateCreated());
		values.put(DatabaseHandler.KEY_MODIFICATION_DATE, task.getDateModified());
		values.put(DatabaseHandler.KEY_DUE_DATE, task.getDateDue());
		values.put(DatabaseHandler.KEY_FINAL_DUE_DATE, task.getFinalDateDue());
		values.put(DatabaseHandler.KEY_STOP_REPEATING_DATE, task.getStopRepeatingDate());
		values.put(DatabaseHandler.KEY_NOTES, task.getNotes());

		// Inserting Row
		db.insert(DatabaseHandler.TABLE_TASKS, null, values);
		close();
	}
	/**
	 * Update the database information about a task
	 * @param task
	 * @return number of rows affected
	 */
	public int updateTask(Task task) {
		open();
		ContentValues values = new ContentValues();
		values.put(DatabaseHandler.KEY_NAME, task.getName());
		values.put(DatabaseHandler.KEY_COMPLETION, task.isCompleted());
		values.put(DatabaseHandler.KEY_PRIORITY, task.getPriority());
		values.put(DatabaseHandler.KEY_CATEGORY, task.getCategory());
		values.put(DatabaseHandler.KEY_HAS_DUE_DATE, task.hasDateDue());
		values.put(DatabaseHandler.KEY_HAS_FINAL_DUE_DATE, task.hasFinalDateDue());
		values.put(DatabaseHandler.KEY_IS_REPEATING, task.isRepeating());
		values.put(DatabaseHandler.KEY_HAS_STOP_REPEATING_DATE, task.hasStopRepeatingDate());
		values.put(DatabaseHandler.KEY_REPEAT_TYPE, task.getRepeatType());
		values.put(DatabaseHandler.KEY_REPEAT_INTERVAL, task.getRepeatInterval());
		values.put(DatabaseHandler.KEY_CREATION_DATE, task.getDateCreated());
		values.put(DatabaseHandler.KEY_MODIFICATION_DATE, task.getDateModified());
		values.put(DatabaseHandler.KEY_DUE_DATE, task.getDateDue());
		values.put(DatabaseHandler.KEY_FINAL_DUE_DATE, task.getFinalDateDue());
		values.put(DatabaseHandler.KEY_STOP_REPEATING_DATE, task.getStopRepeatingDate());
		values.put(DatabaseHandler.KEY_NOTES, task.getNotes());

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

	/********************************************************
	 *  Categories											*
	 ********************************************************/

	/**
	 * Insert Category in the categories table
	 * @param c
	 * @param color
	 */
	public void addCategory(Category c){
		open();
		ContentValues values = new ContentValues();
		values.put(DatabaseHandler.KEY_ID, c.getID());
		values.put(DatabaseHandler.KEY_NAME, c.getName());
		values.put(DatabaseHandler.KEY_COLOR, c.getColor());
		values.put(DatabaseHandler.KEY_UPDATED, c.getUpdated());
		// Inserting row
		db.insert(DatabaseHandler.TABLE_CATEGORIES, null, values);
		close();
	}

	/**
	 * Delete category from the categories table, 
	 * you still need to update tasks that where in this category
	 * @param c
	 */
	public void deleteCategory(Category c){
		open();
		// deleting row
		db.delete(DatabaseHandler.TABLE_CATEGORIES, 
				DatabaseHandler.KEY_ID + " = " + c.getID(), null);
		close();
	}

	/**
	 * Update the database information on an category
	 * @param c
	 * @return
	 */
	public int updateCategory(Category c){
		open();
		ContentValues values = new ContentValues();
		values.put(DatabaseHandler.KEY_NAME, c.getName());
		values.put(DatabaseHandler.KEY_COLOR, c.getColor());
		values.put(DatabaseHandler.KEY_UPDATED, c.getUpdated());

		// updating row
		int i = db.update(DatabaseHandler.TABLE_CATEGORIES, values, 
				DatabaseHandler.KEY_ID + " = " + c.getID(), null);
		
		close();
		return i;
	}

	/**
	 * Query a category using its id
	 * @param id
	 * @return
	 */
	public Category getCategory(int id){
		open();
		Cursor cursor = db.query(DatabaseHandler.TABLE_CATEGORIES, new String[] {
				DatabaseHandler.KEY_ID,
				DatabaseHandler.KEY_NAME,
				DatabaseHandler.KEY_COLOR,
				DatabaseHandler.KEY_UPDATED,
				DatabaseHandler.KEY_G_ID}, 
				DatabaseHandler.KEY_ID + " = " + id,
				null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		Category c = new Category(
				cursor.getInt(0),
				cursor.getString(1),
				cursor.getInt(2),
				cursor.getLong(3),
				cursor.getString(4));
		close();
		cursor.close();
		return c;
	}

	public ArrayList<Category> getCategories() {
		ArrayList<Category> categories = new ArrayList<Category>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_CATEGORIES;

		open();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// Loop through all rows and add to list
		if (cursor.moveToFirst()) {
			do {
				Category category = new Category(
						cursor.getInt(0),
						cursor.getString(1),
						cursor.getInt(2),
						cursor.getLong(3),
						cursor.getString(4));
				// Add category to list
				categories.add(category);
			} while (cursor.moveToNext());
		}

		cursor.close();
		close();

		return categories;
	}
	
	public boolean doesCategoryNameExist(String name) {
		// Select All Query
		String selectQuery = "SELECT * FROM " + 
				DatabaseHandler.TABLE_CATEGORIES + " WHERE " +
				DatabaseHandler.KEY_NAME + " = '" + name + "'";
		
		boolean exists = false;

		open();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst())
			exists = true;

		cursor.close();
		close();

		return exists;
	}
	
	/************************************************************
	 * Comparators   											*
	 ************************************************************/
	
	public Comparator getComparator(int id) {
		open();
		Cursor cursor = db.query(DatabaseHandler.TABLE_COMPARATORS, new String[] {
				DatabaseHandler.KEY_ID,
				DatabaseHandler.KEY_NAME,
				DatabaseHandler.KEY_ENABLED,
				DatabaseHandler.KEY_ORDER}, 
				DatabaseHandler.KEY_ID + " = " + id,
				null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		Comparator c = new Comparator(
				cursor.getInt(0),
				cursor.getString(1),
				cursor.getInt(2) > 0,
				cursor.getInt(3));
		close();
		cursor.close();
		return c;
	}
	
	/**
	 * Creates an ArrayList of Comparators in the order specified by the comparators
	 * @return an ArrayList of Comparators
	 */
	public ArrayList<Comparator> getComparators() {
		Comparator[] comparators = new Comparator[Comparator.NUM_COMPARATORS];

		// Select All Query
		String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_COMPARATORS;

		open();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// Loop through all rows and add to list
		if (cursor.moveToFirst()) {
			do {
				Comparator c = new Comparator(
						cursor.getInt(0),
						cursor.getString(1),
						cursor.getInt(2) > 0,
						cursor.getInt(3));
				// Add comparactor to array
				comparators[c.getOrder()] = c;
			} while (cursor.moveToNext());
		}

		cursor.close();
		close();
		
		// Copy array into ArrayList
		ArrayList<Comparator> comparator_list = new ArrayList<Comparator>(Comparator.NUM_COMPARATORS);
		for (int i = 0; i < Comparator.NUM_COMPARATORS; i++)
			comparator_list.add(comparators[i]);

		return comparator_list;
	}
	
	public int updateComparator(Comparator c) {
		open();
		ContentValues values = new ContentValues();
		values.put(DatabaseHandler.KEY_NAME, c.getName());
		values.put(DatabaseHandler.KEY_ENABLED, c.isEnabled());
		values.put(DatabaseHandler.KEY_ORDER, c.getOrder());

		// Update row
		int i = db.update(DatabaseHandler.TABLE_COMPARATORS, values, 
				DatabaseHandler.KEY_ID + " = " + c.getId(), null);
		
		close();
		return i;
	}
	
	/************************************************************
	 * 	Google Tasks											*
	 ************************************************************/

	/**
	 * Update a Task by using its name
	 * @param task
	 * @return number of rows affected 
	 */
	public int updateTaskBygID(Task task) {
		open();
		ContentValues values = new ContentValues();
		values.put(DatabaseHandler.KEY_NAME, task.getName());
		values.put(DatabaseHandler.KEY_COMPLETION, task.isCompleted());
		values.put(DatabaseHandler.KEY_PRIORITY, task.getPriority());
		values.put(DatabaseHandler.KEY_CATEGORY, task.getCategory());
		values.put(DatabaseHandler.KEY_HAS_DUE_DATE, task.hasDateDue());
		values.put(DatabaseHandler.KEY_HAS_FINAL_DUE_DATE, task.hasFinalDateDue());
		values.put(DatabaseHandler.KEY_IS_REPEATING, task.isRepeating());
		values.put(DatabaseHandler.KEY_HAS_STOP_REPEATING_DATE, task.hasStopRepeatingDate());
		values.put(DatabaseHandler.KEY_REPEAT_TYPE, task.getRepeatType());
		values.put(DatabaseHandler.KEY_REPEAT_INTERVAL, task.getRepeatInterval());
		values.put(DatabaseHandler.KEY_CREATION_DATE, task.getDateCreated());
		values.put(DatabaseHandler.KEY_MODIFICATION_DATE, task.getDateModified());
		values.put(DatabaseHandler.KEY_DUE_DATE, task.getDateDue());
		values.put(DatabaseHandler.KEY_FINAL_DUE_DATE, task.getFinalDateDue());
		values.put(DatabaseHandler.KEY_STOP_REPEATING_DATE, task.getStopRepeatingDate());
		values.put(DatabaseHandler.KEY_NOTES, task.getNotes());

		// updating row
		int i = db.update(DatabaseHandler.TABLE_TASKS, values, 
				DatabaseHandler.KEY_NAME + " = " + "\"" + task.getgID() + "\"", null);
		close();
		return i;		
	}

	/**
	 * Query a task using its name
	 * @param name
	 * @return
	 */
	public Task getTaskBygID(String gID) {
		open();
		Cursor cursor = db.query(DatabaseHandler.TABLE_TASKS, new String[] { 
				DatabaseHandler.KEY_ID,
				DatabaseHandler.KEY_NAME, 
				DatabaseHandler.KEY_COMPLETION, 
				DatabaseHandler.KEY_PRIORITY, 
				DatabaseHandler.KEY_CATEGORY,
				DatabaseHandler.KEY_HAS_DUE_DATE,
				DatabaseHandler.KEY_HAS_FINAL_DUE_DATE,
				DatabaseHandler.KEY_IS_REPEATING,
				DatabaseHandler.KEY_HAS_STOP_REPEATING_DATE,
				DatabaseHandler.KEY_REPEAT_TYPE,
				DatabaseHandler.KEY_REPEAT_INTERVAL,
				DatabaseHandler.KEY_CREATION_DATE,
				DatabaseHandler.KEY_MODIFICATION_DATE, 
				DatabaseHandler.KEY_DUE_DATE,
				DatabaseHandler.KEY_FINAL_DUE_DATE,
				DatabaseHandler.KEY_STOP_REPEATING_DATE,
				DatabaseHandler.KEY_G_ID,
				DatabaseHandler.KEY_NOTES }, 
				DatabaseHandler.KEY_G_ID + " = " + "\"" + gID + "\"",
				null, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		else return null;
		Task task = new Task(
				cursor.getInt(0), 
				cursor.getString(1), 
				cursor.getInt(2) > 0, 
				cursor.getInt(3),
				cursor.getInt(4),
				cursor.getInt(5) > 0,
				cursor.getInt(6) > 0,
				cursor.getInt(7) > 0,
				cursor.getInt(8) > 0,
				cursor.getInt(9),
				cursor.getInt(10), 
				cursor.getLong(11), 
				cursor.getLong(12), 
				cursor.getLong(13), 
				cursor.getLong(14),
				cursor.getLong(15),
				cursor.getString(16),
				cursor.getString(17));
		close();
		cursor.close();
		return task;
	}

	/**
	 * Query a category using its google ID of the task
	 * @param google ID
	 * @return Category
	 */
	public Category getCategoryBygID(String gID){
		open();
		Cursor cursor = db.query(DatabaseHandler.TABLE_CATEGORIES, new String[] {
				DatabaseHandler.KEY_ID,
				DatabaseHandler.KEY_NAME,
				DatabaseHandler.KEY_COLOR,
				DatabaseHandler.KEY_UPDATED,
				DatabaseHandler.KEY_G_ID}, 
				DatabaseHandler.KEY_G_ID + " = " + "\"" + gID + "\"",
				null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		else
			return null;
		Category c = new Category(
				cursor.getInt(0),
				cursor.getString(1),
				cursor.getInt(2),
				cursor.getLong(3),
				cursor.getString(4));
		close();
		cursor.close();
		return c;
	}

	/**
	 * Update the database information on an category
	 * @param c
	 * @return
	 */
	public int updateCategoryByName(Category c){
		open();
		ContentValues values = new ContentValues();
		values.put(DatabaseHandler.KEY_NAME, c.getName());
		values.put(DatabaseHandler.KEY_COLOR, c.getColor());
		values.put(DatabaseHandler.KEY_UPDATED, c.getUpdated());
		values.put(DatabaseHandler.KEY_G_ID, c.getgID());
		// updating row
		int i = db.update(DatabaseHandler.TABLE_CATEGORIES, values, 
				DatabaseHandler.KEY_G_ID + " = " + "\"" + c.getgID() + "\"", null);
		return i;
	}
}