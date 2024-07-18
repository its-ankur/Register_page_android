package com.example.thirdAndroidApp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firstandroidapp.R;

public class ShowDetailsPage extends AppCompatActivity {

    private UserDAO userDAO;
    private String userEmail; // To store logged in user's email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details_page);

        userDAO = new UserDAO(this);
        userDAO.open();

        // Retrieve the logged in user's email from the intent
        userEmail = getIntent().getStringExtra("email");

        displayUserDetails();
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
            LinearLayout countrySet = findViewById(R.id.countrySet);
            ImageView correctSign = findViewById(R.id.correctSign);
            TextView registeredText = findViewById(R.id.registeredText);

            String country = user.getCountry().trim();
            if (country.equals("Select Country")) {
                countrySet.setVisibility(View.GONE);
            } else {
                countryView.setText(country);
            }

            String fullName = user.getFirstName() + " " + user.getLastName();
            nameView.setText("Hi " + fullName + " !");
            emailView.setText(user.getEmail());
            genderView.setText(user.getGender());
            registeredText.setText("Successfully logged in");
            correctSign.setImageResource(R.drawable.tick);
            // Assuming the correct image drawable is present
            String end="end";
            Log.d("End",end);
        }
    }
}
