/*
 * WakefulIntentService.java
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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class WakefulIntentService extends IntentService {
	public static final String 
	LOCK_NAME_STATIC="com.commonsware.android.syssvc.AppService.Static";
	public static final String 
	LOCK_NAME_LOCAL="com.commonsware.android.syssvc.AppService.Local";
	private static PowerManager.WakeLock lockStatic=null;
	private PowerManager.WakeLock lockLocal=null;
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
	public WakefulIntentService(String name) {
		super(name);
	}
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
