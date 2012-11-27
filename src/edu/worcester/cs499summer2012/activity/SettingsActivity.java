/* 
 * SettingsActivity.java
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

package edu.worcester.cs499summer2012.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import edu.worcester.cs499summer2012.R;

public class SettingsActivity extends SherlockPreferenceActivity implements OnPreferenceClickListener {

	public static String EDIT_CATEGORIES = "edit_categories";
	
	private PreferenceScreen ps_edit_categories;
	
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preferences);
        
        // Allow Action bar icon to act as a button
        ActionBar action_bar = getSupportActionBar();
        action_bar.setHomeButtonEnabled(true);
        action_bar.setDisplayHomeAsUpEnabled(true);
        
        ps_edit_categories = (PreferenceScreen) this.findPreference(EDIT_CATEGORIES);
        ps_edit_categories.setOnPreferenceClickListener(this);
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == android.R.id.home) {
    		finish();
    		return true;
    	} else
    		return super.onOptionsItemSelected(item);
    }

	@Override
	public boolean onPreferenceClick(Preference p) {
		if (p.getKey().equals(EDIT_CATEGORIES)) {
			Intent intent = new Intent(this, EditCategoriesActivity.class);
			startActivity(intent);
			return true;
		}
		return false;
	}
}
