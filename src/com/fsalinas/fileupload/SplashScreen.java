package com.fsalinas.fileupload;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends Activity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        // Sleep for a few seconds
        try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Intent loginIntent = new Intent();
        loginIntent.setComponent(new ComponentName(this, Login.class));
        startActivity(loginIntent);
    }
}
