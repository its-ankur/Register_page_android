package com.example.thirdAndroidApp;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;

import com.example.firstandroidapp.R;
import com.google.android.material.snackbar.Snackbar;

public class Utility {

    // Method to display a Snackbar for success messages
    public static void displaySuccessSnackbar(View view, String message, Context context) {
        // Create a Snackbar with the provided message and indefinite duration
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);

        // Customize the text color and background tint of the Snackbar
        snackbar.setTextColor(ContextCompat.getColor(context, R.color.white));
        snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.success));

        // Get the Snackbar view to modify its layout parameters
        View snackBarView = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();

        // Set gravity of the Snackbar to be at the top and horizontally centered
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        snackBarView.setLayoutParams(params);

        // Add an action button (close button) to dismiss the Snackbar when clicked
        snackbar.setAction("✕", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        // Set text color of the action button
        snackbar.setActionTextColor(ContextCompat.getColor(context, android.R.color.white));

        // Show the Snackbar
        snackbar.show();
    }

    // Method to display a Snackbar for error messages
    public static void displayErrorSnackbar(View view, String message, Context context) {
        // Create a Snackbar with the provided message and a longer duration
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);

        // Customize the text color and background tint of the Snackbar
        snackbar.setTextColor(ContextCompat.getColor(context, R.color.white));
        snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.faliure));

        // Get the Snackbar view to modify its layout parameters
        View snackBarView = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();

        // Set gravity of the Snackbar to be at the top and horizontally centered
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        snackBarView.setLayoutParams(params);

        snackbar.setAction("✕", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });


        snackbar.setActionTextColor(ContextCompat.getColor(context, android.R.color.white));

        // Show the Snackbar
        snackbar.show();
    }
}
