package com.example.thirdAndroidApp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firstandroidapp.R;

public class ShowDetailsPage extends AppCompatActivity {

    private UserDAO userDAO;
    private String userEmail;
    private Button logout; // To store logged-in user's email
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details_page);

        userDAO = new UserDAO(this);
        userDAO.open();

        // Retrieve the logged-in user's email from the SharedPreferences
        logout = findViewById(R.id.LogoutButton);
        sharedPreferences = getSharedPreferences(Utility.saveDetailsFilename, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userEmail = sharedPreferences.getString(Utility.emailAddressKey, "");

        // Display user details
        displayUserDetails();

        // Set up the logout button click listener
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show confirmation dialog
                showLogoutConfirmationDialog(v);
            }
        });
    }

    @Override
    protected void onDestroy() {
        userDAO.close();
        super.onDestroy();
    }

    private void displayUserDetails() {
        // Fetch user details from the database based on userEmail
        User user = userDAO.getUserByEmail(userEmail);

        if (user != null) {
            // Display user details on the UI elements
            TextView nameView = findViewById(R.id.nameView);
            TextView emailView = findViewById(R.id.emailView);
            TextView genderView = findViewById(R.id.genderView);
            TextView countryView = findViewById(R.id.countryView);
            TextView dateOfBirthView = findViewById(R.id.dateOfBirthView);
            TextView contactView = findViewById(R.id.contactView);
            LinearLayout countrySet = findViewById(R.id.countrySet);
            LinearLayout dateOfBirthSet = findViewById(R.id.dateOfBirth);
            LinearLayout contactSet = findViewById(R.id.contactSet);
            ImageView correctSign = findViewById(R.id.correctSign);
            TextView registeredText = findViewById(R.id.registeredText);

            String country = user.getCountry().trim();
            if (country.equals("Select Country")) {
                countrySet.setVisibility(View.GONE);
            } else {
                countryView.setText(country);
            }

            String dateOfBirth = user.getDateOfBirth().trim();
            if (dateOfBirth.equals("")) {
                dateOfBirthSet.setVisibility(View.GONE);
            } else {
                dateOfBirthView.setText(dateOfBirth);
            }

            String contact = user.getContactNumber().trim();
            if (contact.equals("")) {
                contactSet.setVisibility(View.GONE);
            } else {
                contactView.setText(contact);
            }

            String fullName = user.getFirstName() + " " + user.getLastName();
            nameView.setText("Hi " + fullName + " !");
            emailView.setText(user.getEmail());
            genderView.setText(user.getGender());
            registeredText.setText("Successfully logged in");
            correctSign.setImageResource(R.drawable.tick);
            // Assuming the correct image drawable is present
        }
    }

    private void showLogoutConfirmationDialog(View v) {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_custom, null);

        // Create and configure the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(false); // Prevent closing by tapping outside

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Find and configure the buttons
        Button positiveButton = dialogView.findViewById(R.id.dialog_positive_button);
        Button negativeButton = dialogView.findViewById(R.id.dialog_negative_button);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // User clicked Yes button
                logout(v);
                dialog.dismiss(); // Close the dialog
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // User cancelled the dialog
                dialog.dismiss(); // Close the dialog
            }
        });

        // Show the dialog
        dialog.show();
    }



    private void logout(View v) {
        // Log the logout event
        Log.d("Logout", "Logout button pressed");

        // Clear the SharedPreferences values
        editor.putString(Utility.status, "");
        editor.putString(Utility.emailAddressKey, "");
        boolean success = editor.commit();

        // Log the success of the commit operation
        Log.d("Logout", "SharedPreferences cleared: " + success);
        Utility.displaySuccessSnackbar(v,"Log Out Successfull",ShowDetailsPage.this);
        // Start the LoginPage activity
        Intent intent = new Intent(ShowDetailsPage.this, LoginPage.class);
        startActivity(intent);
        finish(); // Close the current activity
    }
}
