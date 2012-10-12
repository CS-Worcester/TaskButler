/*
 * OnBootReceiver.java
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * BroadCastReceiver for android.intent.action.BOOT_COMPLETED
 * passes all responsibility to TaskButlerService.
 * @author Dhimitraq Jorgji
 *
 */
public class OnBootReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("onBootReciever", "beginning  onReceive");
		WakefulIntentService.acquireStaticLock(context); //acquire a partial WakeLock
		context.startService(new Intent(context, TaskButlerService.class)); //start TaskButlerService
		Log.d("onBootReciever", "ending  onReceive");
		
	}
}
