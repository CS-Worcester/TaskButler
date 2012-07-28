package edu.worcester.cs499summer2012;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class NewTaskActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
    }
    
    public void cancel(View view) {
    	Intent intent = new Intent(this, MainActivity.class);
    	startActivity(intent);
    }
}
