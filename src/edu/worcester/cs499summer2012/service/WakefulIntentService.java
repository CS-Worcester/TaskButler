/*
 * WakeFulIntentService.java
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

package edu.worcester.cs499summer2012.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Acquires a partial WakeLock, allows TaskButtlerService to keep the CPU alive
 * until the work is done.
 * @author Dhimitraq Jorgji
 *
 */
public class WakefulIntentService extends IntentService {
	public static final String
	LOCK_NAME_STATIC="edu.worcester.cs499summer2012.TaskButlerService.Static";
	public static final String
	LOCK_NAME_LOCAL="edu.worcester.cs499summer2012.TaskButlerService.Local";
	private static PowerManager.WakeLock lockStatic=null;
	private PowerManager.WakeLock lockLocal=null;

	public WakefulIntentService(String name) {
		super(name);
	}
	/**
	 * Acquire a partial static WakeLock, you need too call this within the class
	 * that calls startService()
	 * @param context
	 */
	public static void acquireStaticLock(Context context) {
		getLock(context).acquire();
	}

	synchronized private static PowerManager.WakeLock getLock(Context context) {
		if (lockStatic==null) {
			PowerManager
			mgr=(PowerManager)context.getSystemService(Context.POWER_SERVICE);
			lockStatic=mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					LOCK_NAME_STATIC);
			lockStatic.setReferenceCounted(true);
		}
		return(lockStatic);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		PowerManager mgr=(PowerManager)getSystemService(Context.POWER_SERVICE);
		lockLocal=mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				LOCK_NAME_LOCAL);
		lockLocal.setReferenceCounted(true);
	}

	@Override
	public void onStart(Intent intent, final int startId) {
		lockLocal.acquire();
		super.onStart(intent, startId);
		getLock(this).release();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		lockLocal.release();
	}
}