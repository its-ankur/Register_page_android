package com.example.thirdAndroidApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    // Constructor
    public UserDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Open the database connection
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    // Close the database connection
    public void close() {
        dbHelper.close();
    }

    // Insert a new user into the database
    public void insertUser(String firstName, String lastName, String email, String country, String gender, String password, String dateOfBirth, String contactNumber, boolean acceptedTerms) {
        String hashedPassword= BCrypt.hashpw(password,BCrypt.gensalt());
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_FIRST_NAME, firstName);
        values.put(DatabaseHelper.COLUMN_LAST_NAME, lastName);
        values.put(DatabaseHelper.COLUMN_EMAIL, email);
        values.put(DatabaseHelper.COLUMN_COUNTRY, country);
        values.put(DatabaseHelper.COLUMN_GENDER, gender);
        values.put(DatabaseHelper.COLUMN_PASSWORD, hashedPassword);
        values.put(DatabaseHelper.COLUMN_DATE_OF_BIRTH, dateOfBirth);
        values.put(DatabaseHelper.COLUMN_CONTACT_NUMBER, contactNumber);
        values.put(DatabaseHelper.COLUMN_ACCEPTED_TERMS, acceptedTerms ? 1 : 0);

        database.insert(DatabaseHelper.TABLE_USERS,null,values);
    }

    public Cursor getAllUsers() {
        return database.query(DatabaseHelper.TABLE_USERS, null, null, null, null, null, null);
    }

    public boolean isEmailExists(String email) {
        String[] projection = {
                DatabaseHelper.COLUMN_EMAIL
        };

        String selection = DatabaseHelper.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { email };

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_USERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );





        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public User getUserByEmail(String email) {
        User user = null;
        String[] columns = {
                DatabaseHelper.COLUMN_FIRST_NAME,
                DatabaseHelper.COLUMN_LAST_NAME,
                DatabaseHelper.COLUMN_EMAIL,
                DatabaseHelper.COLUMN_COUNTRY,
                DatabaseHelper.COLUMN_GENDER,
                DatabaseHelper.COLUMN_PASSWORD,
                DatabaseHelper.COLUMN_DATE_OF_BIRTH,
                DatabaseHelper.COLUMN_CONTACT_NUMBER,
                DatabaseHelper.COLUMN_ACCEPTED_TERMS
        };
        String selection = DatabaseHelper.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { email };
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FIRST_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LAST_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COUNTRY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GENDER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_OF_BIRTH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTACT_NUMBER)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCEPTED_TERMS)) > 0
            );
            cursor.close();
        }

        return user;
    }


}
