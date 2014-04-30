package com.jereksel.rommanager;

import android.app.Activity;
import android.os.Bundle;

import android.view.WindowManager;
import android.view.Window;

public class AboutScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_about_screen);
    }

}
