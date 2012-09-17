/*
 * AppService.java
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

import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class AppService extends WakefulIntentService {
  public AppService() {
    super("AppService");
  }
  
  @Override
  protected void onHandleIntent(Intent intent) {
    File log=new File(Environment.getExternalStorageDirectory(),
                     "AlarmLog.txt");
try {
      BufferedWriter out=new BufferedWriter(new 
FileWriter(log.getAbsolutePath(), true));
      
      out.write(new Date().toString());
      out.write("\n");
      out.close();
    }
    catch (IOException e) {
      Log.e("AppService", "Exception appending to log file", e);
    }
super.onHandleIntent(intent);
  }
}