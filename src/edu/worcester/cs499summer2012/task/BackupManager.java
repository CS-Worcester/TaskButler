/* 
 * BackupManager.java
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

package edu.worcester.cs499summer2012.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import android.os.Environment;
import edu.worcester.cs499summer2012.database.DatabaseHandler;

/**
 * Class to handle backup and restoring of user data (the database and
 * preference file)
 * @author Jonathan Hasenzahl
 */
public class BackupManager {

	public static final String BACKUP_OK = "backup_ok";
	public static final String BACKUP_EXCEPTION = "backup_exception";
	public static final String RESTORE_OK = "restore_ok";
	public static final String RESTORE_EXCEPTION = "restore_exception";
	public static final String NO_RESTORE_EXISTS = "no_restore_exists";
	
	private static final String PACKAGE_NAME = "edu.worcester.cs499summer2012";
	private static final String DB_FILENAME = DatabaseHandler.DATABASE_NAME;
	private static final String DB_INTERNAL_PATH = "//data//" + PACKAGE_NAME + "//databases//";
	private static final String DB_EXTERNAL_PATH = "//TaskButler//backup//";
	
	public static String interpretStringCode(String code) {
		if (code.equals(BACKUP_OK))
			return "Backup successfull!";
		
		if (code.equals(RESTORE_OK))
			return "Restore successfull!";
		
		if (code.equals(NO_RESTORE_EXISTS))
			return "No backup exists!";
		
		if (code.equals(BACKUP_EXCEPTION))
			return "The backup failed unexpectedly";
		
		if (code.equals(RESTORE_EXCEPTION))
			return "The restore failed unexpectedly";
		
		if (code.equals(Environment.MEDIA_BAD_REMOVAL))
			return "Error: Media was removed before it was unmounted";
		
		if (code.equals(Environment.MEDIA_CHECKING))
			return "Error: Media is being disk-checked";
		
		if (code.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
			return "Error: Media is mounted with read-only access";
		
		if (code.equals(Environment.MEDIA_NOFS))
			return "Error: Media filesystem cannot be recognized";
		
		if (code.equals(Environment.MEDIA_REMOVED))
			return "Error: Media is not present";
		
		if (code.equals(Environment.MEDIA_SHARED))
			return "Error: Media is being shared via USB";
		
		if (code.equals(Environment.MEDIA_UNMOUNTABLE))
			return "Error: Media cannot be mounted";
		
		if (code.equals(Environment.MEDIA_UNMOUNTED))
			return "Error: Media is not mounted";
		
		return "Error: Unknown code";
	}
	
	public BackupManager() {}
	
	/**
	 * Checks if a backup file exists.
	 * @return true if a backup exists, false otherwise
	 */
	public boolean doesBackupExist() {
		String media_state = Environment.getExternalStorageState();
		
		// Check for read access
		if (!media_state.equals(Environment.MEDIA_MOUNTED) && 
				!media_state.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
			return false;
		
		try {
			File sd_card = Environment.getExternalStorageDirectory();
			
			if (sd_card.canRead()) {
				// Restore database
				File backup = new File(sd_card, DB_EXTERNAL_PATH + DB_FILENAME);
				return backup.exists();
			} else
				return false;
			
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Backs up the app database and preferences file to SD card.
	 * @return BACKUP_OK if successful, else a MEDIA code describing why the
	 *                   backup was not possible
	 */
	public String backup() {
		String media_state = Environment.getExternalStorageState();
		
		// Check for write access
		if (!media_state.equals(Environment.MEDIA_MOUNTED))
			return media_state;
		
		try {
			File sd_card = Environment.getExternalStorageDirectory();
			File internal_storage = Environment.getDataDirectory();
			
			if (sd_card.canWrite()) {
				// Backup database
				File backup_dir = new File(sd_card, DB_EXTERNAL_PATH);
				backup_dir.mkdirs();
				File backup = new File(backup_dir, DB_FILENAME);
				File current = new File(internal_storage, DB_INTERNAL_PATH + DB_FILENAME);
				
				FileInputStream input_stream = new FileInputStream(current);
				FileOutputStream output_stream = new FileOutputStream(backup);
				FileChannel source = input_stream.getChannel();
				FileChannel dest = output_stream.getChannel();
				
				dest.transferFrom(source, 0, source.size());
				source.close();
				dest.close();
				input_stream.close();
				output_stream.close();
			} else {
				return BACKUP_EXCEPTION;
			}
				
		} catch (Exception e) {
			return BACKUP_EXCEPTION;
		}
		
		return BACKUP_OK;
	}
	
	/**
	 * Restores the app database and preferences file from SD card.
	 * @return RESTORE_OK if successful, else a MEDIA code describing why the
	 *                    restore was not possible
	 */
	public String restore() {
		String media_state = Environment.getExternalStorageState();
		
		// Check for read access
		if (!media_state.equals(Environment.MEDIA_MOUNTED) && 
				!media_state.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
			return media_state;
		
		try {
			File sd_card = Environment.getExternalStorageDirectory();
			File internal_storage = Environment.getDataDirectory();
			
			if (sd_card.canRead()) {
				// Restore database
				File backup = new File(sd_card, DB_EXTERNAL_PATH + DB_FILENAME);
				if (!backup.exists())
					return NO_RESTORE_EXISTS;
				File restored = new File(internal_storage, DB_INTERNAL_PATH + DB_FILENAME);
				
				FileInputStream input_stream = new FileInputStream(backup);
				FileOutputStream output_stream = new FileOutputStream(restored);
				FileChannel source = input_stream.getChannel();
				FileChannel dest = output_stream.getChannel();
				
				dest.transferFrom(source, 0, source.size());
				source.close();
				dest.close();
				input_stream.close();
				output_stream.close();
			} else
				return RESTORE_EXCEPTION;
			
		} catch (Exception e) {
			return RESTORE_EXCEPTION;
		}
		
		return RESTORE_OK;
	}
	
}
