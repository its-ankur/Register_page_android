package com.example.thirdAndroidApp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firstandroidapp.R;

public class ShowDetailsPage extends AppCompatActivity {

    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details_page);

        userDAO = new UserDAO(this);
        userDAO.open();

        displayUserDetails();
    }

    @Override
    protected void onDestroy() {
        userDAO.close();
        super.onDestroy();
    }

    private void displayUserDetails() {
        Cursor cursor = userDAO.getAllUsers();

        TextView nameView = findViewById(R.id.nameView);
        TextView emailView = findViewById(R.id.emailView);
        TextView genderView = findViewById(R.id.genderView);
        TextView countryView = findViewById(R.id.countryView);
        ImageView correctSign = findViewById(R.id.correctSign);
        TextView registeredText = findViewById(R.id.registeredText);

        if (cursor != null && cursor.moveToLast()) { // Move to the last row
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LAST_NAME));
            nameView.setText("Hi " + firstName +" "+lastName+ " !");
            emailView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)));
            genderView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GENDER)));
            countryView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COUNTRY)));
            registeredText.setText("Successfully registered");
            correctSign.setImageResource(R.drawable.tick); // Assuming the correct image drawable is present
        }

        cursor.close();
    }
}
