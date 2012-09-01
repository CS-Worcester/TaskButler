package edu.worcester.cs499summer2012.activity;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import edu.worcester.cs499summer2012.R;
import android.os.Bundle;

public class SettingsActivity extends SherlockPreferenceActivity {

    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preferences);
    }
    
}
