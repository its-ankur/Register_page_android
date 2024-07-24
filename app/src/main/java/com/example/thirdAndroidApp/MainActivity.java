// Package declaration, defining the package name for this class
package com.example.thirdAndroidApp;

// Import statements to include necessary Android and Java classes

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle; // Importing the Bundle class used to pass data between activities
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.content.pm.ActivityInfo;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar; // Importing the Toolbar widget for app bar
import androidx.appcompat.app.AppCompatActivity; // Importing AppCompatActivity class for compatibility support

import com.example.firstandroidapp.R;

// Main activity class declaration, extending AppCompatActivity to use support library features
public class MainActivity extends AppCompatActivity {

    // Overriding the onCreate method which is called when the activity is created
    // Declaration of the TextView for the continue button
    private TextView countineToRegisterPage;
    // Intent to start the RegisterPage activity
    Intent intent;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting the content view to the welcome page layout
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Initializing UI components
        init();
        // Setting up the click listener for the continue button
        goToRegisterPage();
    }

    // Method to initialize UI components
    protected void init() {
        // Finding the Toolbar view by its ID and assigning it to the myToolbar variable
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        // Setting the Toolbar as the app bar for the activity
//        setSupportActionBar(myToolbar);
        // Setting the title for the app bar
//        getSupportActionBar().setTitle(R.string.toolbar_heading);
        // Finding the continue button TextView by its ID


        myToolbar.setTitle(getString(R.string.toolbar_heading));

        countineToRegisterPage = findViewById(R.id.countineToRegisterPage);
        sharedPreferences = getSharedPreferences(Utility.saveDetailsFilename, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Method to set up the click listener for the continue button
    protected void goToRegisterPage() {
        countineToRegisterPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating an intent to start the RegisterPage activity
                String a=sharedPreferences.getString(Utility.status,"");
                if(a.equals("true")){
                    intent=new Intent(MainActivity.this, ProfilePage.class);
                    startActivity(intent);
                }
                else {
                    intent = new Intent(MainActivity.this, LoginPage.class);
                    // Starting the RegisterPage activity
                    startActivity(intent);
                }
            }
        });
    }
}
