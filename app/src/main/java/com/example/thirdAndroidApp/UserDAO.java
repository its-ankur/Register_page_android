package com.example.thirdAndroidApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    // Constructor
    public UserDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Method to get writable database
    private SQLiteDatabase getWritableDatabase() {
        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
        return database;
    }

    // Method to get readable database
    private SQLiteDatabase getReadableDatabase() {
        if (database == null || !database.isOpen()) {
            database = dbHelper.getReadableDatabase();
        }
        return database;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    // Insert a new user into the database
    public void insertUser(String firstName, String lastName, String email, String country, String gender, String password, String dateOfBirth, String contactNumber, boolean acceptedTerms) {
        SQLiteDatabase db = this.getWritableDatabase();
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
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

        db.insert(DatabaseHelper.TABLE_USERS, null, values);
        db.close();
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(DatabaseHelper.TABLE_USERS, null, null, null, null, null, null);
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                DatabaseHelper.COLUMN_EMAIL
        };

        String selection = DatabaseHelper.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { email };

        Cursor cursor = db.query(
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
        SQLiteDatabase db = this.getReadableDatabase();
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
                DatabaseHelper.COLUMN_ACCEPTED_TERMS,
                DatabaseHelper.COLUMN_IMAGE
        };
        String selection = DatabaseHelper.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { email };
        Cursor cursor = db.query(
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
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCEPTED_TERMS)) > 0,
                    cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE))
            );
            cursor.close();
        }

        return user;
    }


    public Boolean updateUserDetails(String oldEmail, User updatedUser) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            if (updatedUser.getFirstName() != null) {
                values.put(DatabaseHelper.COLUMN_FIRST_NAME, updatedUser.getFirstName());
            }
            if(updatedUser.getLastName()!=null){
                values.put(DatabaseHelper.COLUMN_LAST_NAME,updatedUser.getLastName());
            }
            if(updatedUser.getContactNumber()!=null){
                values.put(DatabaseHelper.COLUMN_CONTACT_NUMBER,updatedUser.getContactNumber());
            }
            if (updatedUser.getDateOfBirth() != null) {
                values.put(DatabaseHelper.COLUMN_DATE_OF_BIRTH, updatedUser.getDateOfBirth());
            }
            if (updatedUser.getCountry() != null) {
                values.put(DatabaseHelper.COLUMN_COUNTRY, updatedUser.getCountry());
            }
            if (updatedUser.getGender() != null) {
                values.put(DatabaseHelper.COLUMN_GENDER, updatedUser.getGender());
            }
            if (updatedUser.getPassword() != null) {
                String hashedPassword = BCrypt.hashpw(updatedUser.getPassword(), BCrypt.gensalt());
                values.put(DatabaseHelper.COLUMN_PASSWORD, hashedPassword);
            }
            if (updatedUser.getImage() != null) {
                values.put(DatabaseHelper.COLUMN_IMAGE, updatedUser.getImage()); // Update image
            }



            int result = db.update(DatabaseHelper.TABLE_USERS, values, DatabaseHelper.COLUMN_EMAIL + " = ?", new String[]{oldEmail});
            return result > 0;
        } catch (Exception e) {
            Log.e("UserDAO", "Update failed", e);
            return false;
        }
    }

    public Boolean updateUserWithoutPassword(String oldEmail, User updatedUser) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            if (updatedUser.getFirstName() != null) {
                values.put(DatabaseHelper.COLUMN_FIRST_NAME, updatedUser.getFirstName());
            }
            if(updatedUser.getLastName()!=null){
                values.put(DatabaseHelper.COLUMN_LAST_NAME,updatedUser.getLastName());
            }
            if(updatedUser.getContactNumber()!=null){
                values.put(DatabaseHelper.COLUMN_CONTACT_NUMBER,updatedUser.getContactNumber());
            }
            if (updatedUser.getDateOfBirth() != null) {
                values.put(DatabaseHelper.COLUMN_DATE_OF_BIRTH, updatedUser.getDateOfBirth());
            }
            if (updatedUser.getCountry() != null) {
                values.put(DatabaseHelper.COLUMN_COUNTRY, updatedUser.getCountry());
            }
            if (updatedUser.getGender() != null) {
                values.put(DatabaseHelper.COLUMN_GENDER, updatedUser.getGender());
            }
            if (updatedUser.getImage() != null) {
                values.put(DatabaseHelper.COLUMN_IMAGE, updatedUser.getImage()); // Update image
            }

            int result = db.update(DatabaseHelper.TABLE_USERS, values, DatabaseHelper.COLUMN_EMAIL + " = ?", new String[]{oldEmail});
            return result > 0;
        } catch (Exception e) {
            Log.e("UserDAO", "Update failed", e);
            return false;
        }
    }

    // Separate method to set or update user image
    public void setUserImage(String email, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IMAGE, image);

        db.update(DatabaseHelper.TABLE_USERS, values, DatabaseHelper.COLUMN_EMAIL + " = ?", new String[]{email});
        db.close();
    }
}
