package com.example.thirdAndroidApp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.firstandroidapp.R;
import com.example.firstandroidapp.databinding.ActivityProfilePageBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProfilePage extends AppCompatActivity {

    private ActivityProfilePageBinding binding;
    private Boolean passwordCheck = false;
    private UserDAO userDAO;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String userEmail;
    private boolean flag = true;
    private static final int REQUEST_PERMISSIONS = 123;
    private static final int IMAGE_PICKER_REQUEST_CODE = 100;
    private static final int UCROP_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfilePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Toolbar
        Toolbar toolbar = binding.myToolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");

        userDAO = new UserDAO(this);
        userDAO.open();

        init();
        setupListeners();

        displayUserDetails();
        textWatcherListener();
    }

    private void init() {
        sharedPreferences = getSharedPreferences(Utility.saveDetailsFilename, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userEmail = sharedPreferences.getString(Utility.emailAddressKey, "");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.country_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.countrySpinner.setAdapter(adapter);
    }

    private void setupListeners() {

        binding.editProfileImageView.setOnClickListener(v->{
            if (arePermissionsGranted()) {
                showImageSourceDialog();
            } else {
                requestPermissionsIfNeeded();
            }
        });

        binding.changePasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.passwordInputLayout.setVisibility(View.VISIBLE);
                binding.confirmPasswordInputLayout.setVisibility(View.VISIBLE);
                passwordCheck = isChecked;
                Log.d("CheckBox", "CheckBox Checked");
            } else {
                binding.passwordInputLayout.setVisibility(View.GONE);
                binding.confirmPasswordInputLayout.setVisibility(View.GONE);
                passwordCheck = isChecked;
                Log.d("CheckBox", "Checkbox Unchecked");
            }
        });

        binding.exitIcon.setOnClickListener(v -> showLogoutConfirmationDialog(v));

        binding.dateOfBirth.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (binding.dateOfBirth.getRight() - binding.dateOfBirth.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    showDatePickerDialog();
                    return true;
                }
            }
            return false;
        });

        binding.passwordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (binding.passwordEditText.getRight() - binding.passwordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (binding.passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        binding.passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        binding.passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_crossed_eye, 0);
                    } else {
                        binding.passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        binding.passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_remove_red_eye_24, 0);
                    }
                    binding.passwordEditText.setSelection(binding.passwordEditText.getText().length());
                    binding.passwordEditText.requestFocus();
                    return true;
                }
            }
            return false;
        });

        binding.confirmPasswordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (binding.confirmPasswordEditText.getRight() - binding.confirmPasswordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (binding.confirmPasswordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        binding.confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        binding.confirmPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_crossed_eye, 0);
                    } else {
                        binding.confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        binding.confirmPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_remove_red_eye_24, 0);
                    }
                    binding.confirmPasswordEditText.setSelection(binding.confirmPasswordEditText.getText().length());
                    binding.confirmPasswordEditText.requestFocus();
                    return true;
                }
            }
            return false;
        });

        binding.updateButton.setOnClickListener(v -> {
            if (validateInputs(v)) {
                saveFormDetails(v);
                binding.changePasswordCheckBox.setChecked(false);
                Utility.displaySuccessSnackbar(v, "Data updated successfully", ProfilePage.this);
                binding.fnameLayout.setError(null);

            } else {
                Utility.displayErrorSnackbar(v, "Data not updated", ProfilePage.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Toggle the flag to true when the activity is resumed
        userDAO.open();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("MainActivity", "onActivityResult called with requestCode: " + requestCode + ", resultCode: " + resultCode);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == IMAGE_PICKER_REQUEST_CODE) {
                Log.d("MainActivity", "request code matches image picker request code");
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    Log.d("MainActivity", "imageUri not equal to null");
                    startCrop(imageUri);
                }
                else{
                    Log.e("MainActivity","imageUri is null");
                }
            } else if (requestCode == UCROP_REQUEST_CODE) {
                Log.d("MainActivity", "request code matches UCROP request code");
                Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    Log.d("MainActivity", "Cropped Image URI: " + resultUri.toString());
                    displayCroppedImage(resultUri);
                    // Convert URI to byte[] and update user image in database
                    try {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        byte[] imageBytes = outputStream.toByteArray();

                        // Assuming `userEmail` is defined elsewhere in your class
                        userDAO.setUserImage(userEmail, imageBytes);
                    } catch (IOException e) {
                        Log.e("MainActivity", "Failed to convert image URI to byte array", e);
                    }
                } else {
                    Log.e("MainActivity", "Cropped Image URI is null.");
                }
            } else {
                Log.d("MainActivity", "requestCode does not match any known request code");
            }
        } else {
            Log.d("MainActivity", "resultCode not OK or data is null");
        }
    }

    private void startCrop(Uri uri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "profile_image_crop.jpg"));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(80);
        options.setMaxBitmapSize(1024);
        options.withMaxResultSize(1000, 1000);

        UCrop.of(uri, destinationUri)
                .withOptions(options)
                .start(this, UCROP_REQUEST_CODE); // Ensure request code is passed
    }
    private void displayCroppedImage(Uri uri) {
        binding.profileImageView.setImageURI(uri);
    }


    private void requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Request READ_MEDIA_IMAGES for API level 33 and above
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSIONS);
        } else {
            // Request READ_EXTERNAL_STORAGE for older versions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
        }
    }

    private boolean arePermissionsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted
                Log.d("MainActivity","Permission Granted");
                showImageSourceDialog();
            } else {
                // Permissions denied
                Log.d("MainActivity","Permission not Granted");
            }
        }
    }

    private void showImageSourceDialog() {
        String[] options = {"Camera", "Gallery", "Files"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    // Camera
                    ImagePicker.with(this)
                            .cameraOnly()
                            .start(IMAGE_PICKER_REQUEST_CODE);
                    break;
                case 1:
                    // Gallery
                    ImagePicker.with(this)
                            .galleryOnly()
                            .start(IMAGE_PICKER_REQUEST_CODE);
                    break;
                case 2:
                    // Files
                    ImagePicker.with(this)
                            .galleryMimeTypes(new String[]{"image/*"})
                            .start(IMAGE_PICKER_REQUEST_CODE);
                    break;
            }
        });
        builder.show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v != null && (event.getRawX() < v.getLeft() || event.getRawX() > v.getRight() || event.getRawY() < v.getTop() || event.getRawY() > v.getBottom())) {
                KeyboardUtils.hideSoftKeyboard(this);
                v.clearFocus();
            }
        }
        return super.dispatchTouchEvent(event);
    }



    @Override
    protected void onDestroy() {
        userDAO.close();
        super.onDestroy();
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                ProfilePage.this,
                R.style.YellowDatePickerDialog,
                (view, year1, month1, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    binding.dateOfBirth.setText(date);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void showLogoutConfirmationDialog(View v) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_custom, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(false);

        AlertDialog dialog = builder.create();

        Button positiveButton = dialogView.findViewById(R.id.dialog_positive_button);
        Button negativeButton = dialogView.findViewById(R.id.dialog_negative_button);

        positiveButton.setOnClickListener(v1 -> {
            logout(v);
            dialog.dismiss();
        });

        negativeButton.setOnClickListener(v1 -> dialog.dismiss());

        dialog.show();
    }

    private void logout(View v) {
        Log.d("Logout", "Logout button pressed");

        editor.putString(Utility.status, "");
        editor.putString(Utility.emailAddressKey, "");
        boolean success = editor.commit();

        Log.d("Logout", "SharedPreferences cleared: " + success);
        Utility.displaySuccessSnackbar(v, "Log Out Successfully", ProfilePage.this);
        Intent intent = new Intent(ProfilePage.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void textWatcherListener() {

        binding.fname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fname.setCursorVisible(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear any previous error when user starts typing
                String fame=binding.fname.toString();
                if(fame.isEmpty()){
                    binding.fnameLayout.setError("First Name is required");
                }
                else{
                    binding.fnameLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        binding.passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.passwordEditText.setCursorVisible(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString().trim();
                if (flag) {
                    if (password.isEmpty()) {
                        binding.passwordInputLayout.setError("Password is required");
                        binding.passwordInputLayout.setErrorIconDrawable(null);
                    } else if (password.length() < 6) {
                        binding.passwordInputLayout.setError("Password should be at least 6 characters");
                        binding.passwordInputLayout.setErrorIconDrawable(null);
                    } else if (!isValidPassword(password)) {
                        binding.passwordInputLayout.setError("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
                        binding.passwordInputLayout.setErrorIconDrawable(null);
                    } else {
                        binding.passwordInputLayout.setError(null);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePasswords();
            }
        });

        binding.confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.confirmPasswordEditText.setCursorVisible(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                String confirmPassword=s.toString().trim();
                String password= binding.passwordEditText.getText().toString().trim();
                if(flag){
                    if (confirmPassword.isEmpty()) {
                        binding.confirmPasswordInputLayout.setError("Confirm Password is required");
                        binding.confirmPasswordInputLayout.setErrorIconDrawable(null);
                    } else if (!confirmPassword.equals(password)) {
                        binding.confirmPasswordInputLayout.setError("Passwords do not match");
                        binding.confirmPasswordInputLayout.setErrorIconDrawable(null);
                    } else {
                        binding.confirmPasswordInputLayout.setError(null);
                    }
                }
            }
        });

        binding.dateOfBirth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String dateOfBirth = binding.dateOfBirth.getText().toString().trim();
                if (!dateOfBirth.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                    sdf.setLenient(false); // Ensure strict date parsing
                    try {
                        Date dobDate = sdf.parse(dateOfBirth);

                        // Validate age (minimum 18 years)
                        if (!isAgeAboveThreshold(dobDate, 18)) {
                            binding.dateOfBirthLayout.setError("You must be at least 18 years old");
                        } else {
                            binding.dateOfBirthLayout.setError(null);
                        }
                    } catch (ParseException e) {
                        binding.dateOfBirthLayout.setError("Invalid date format (DD/MM/YYYY)");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.contactNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String contact=s.toString().trim();
                if(contact.isEmpty()){
                    binding.contactNumberLayout.setError(null);
                }
                if(!contact.isEmpty()) {
                    if (contact.length() < 10) {
                        binding.contactNumberLayout.setError("Contact number must be ten digits");
                    } else if (contact.length()>10) {
                        binding.contactNumberLayout.setError("Contact should not be greater than ten digits");
                    } else {
                        binding.contactNumberLayout.setError(null);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private boolean isAgeAboveThreshold(Date dob, int thresholdAge) {
        Calendar dobCalendar = Calendar.getInstance();
        dobCalendar.setTime(dob);
        Calendar thresholdCalendar = Calendar.getInstance();
        thresholdCalendar.add(Calendar.YEAR, -thresholdAge);

        return !dobCalendar.after(thresholdCalendar);
    }

    // Utility method to validate password criteria
    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@&$^])(?=\\S+$).{6,}$";
        return password.matches(passwordPattern);
    }

    private void validatePasswords() {
        String password = binding.passwordEditText.getText().toString();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString();

        if (password.equals(confirmPassword)) {
            binding.confirmPasswordInputLayout.setError(null);
            binding.confirmPasswordInputLayout.setErrorIconDrawable(null);
        } else {
            binding.confirmPasswordInputLayout.setError("Passwords do not match");
            binding.confirmPasswordInputLayout.setErrorIconDrawable(null);
        }
    }

    private boolean validateInputs(View v) {
        boolean isValid = true;

        if (binding.fname.getText().toString().trim().isEmpty()) {
            binding.fnameLayout.setError("Full name is required");
            isValid = false;
        } else {
            binding.fname.setError(null);
        }



        if (passwordCheck) {
            if (binding.passwordEditText.getText().toString().trim().isEmpty()) {
                binding.passwordInputLayout.setError("Password is required");
                binding.passwordInputLayout.setErrorIconDrawable(null);
                isValid = false;
            } else {
                binding.passwordInputLayout.setError(null);
            }

            if (binding.confirmPasswordEditText.getText().toString().trim().isEmpty()) {
                binding.confirmPasswordInputLayout.setError("Confirm password is required");
                binding.confirmPasswordInputLayout.setErrorIconDrawable(null);
                isValid = false;
            } else {
                binding.confirmPasswordInputLayout.setError(null);
            }
        }

        return isValid;
    }

    private void saveFormDetails(View v) {
        // Retrieve form details
        String firstName = binding.fname.getText().toString().trim();
        String lastName=binding.lname.getText().toString().trim();
        String contactNumber=binding.contactNumber.getText().toString().trim();
        String dateOfBirth = binding.dateOfBirth.getText().toString().trim();
        String country = binding.countrySpinner.getSelectedItem().toString();
        String gender = ((RadioButton) findViewById(binding.genderRadioGroup.getCheckedRadioButtonId())).getText().toString();

        // Check if password update is required
        String password = passwordCheck ? binding.passwordEditText.getText().toString().trim() : null;

        // Retrieve the profile image bitmap if available
        Bitmap profileImage = ((BitmapDrawable) binding.profileImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        profileImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] profileImageBytes = outputStream.toByteArray();



        // Create a User object with the updated details
        User updatedUser = new User();
        updatedUser.setFirstName(firstName);
        updatedUser.setLastName(lastName);
        updatedUser.setContactNumber(contactNumber);
        updatedUser.setDateOfBirth(dateOfBirth);
        updatedUser.setCountry(country);
        updatedUser.setGender(gender);
        updatedUser.setImage(profileImageBytes);

        // Call appropriate method based on whether the password is checked
        if (passwordCheck) {
            // Assuming `userEmail` is already defined elsewhere in your class
            userDAO.updateUserDetails(userEmail, updatedUser);
        } else {
            userDAO.updateUserWithoutPassword(userEmail, updatedUser);
        }
    }


    private void displayUserDetails() {
        User user = userDAO.getUserByEmail(userEmail);

        if (user != null) {
            binding.fname.setText(user.getFirstName());
            if (!user.getLastName().equals("")) {
                binding.lname.setText(user.getLastName());
            }
            binding.email.setText(user.getEmail());
            if (!user.getContactNumber().equals("")) {
                binding.contactNumber.setText(user.getContactNumber());
            }
            if (!user.getDateOfBirth().equals("")) {
                binding.dateOfBirth.setText(user.getDateOfBirth());
            }
            binding.countrySpinner.setSelection(((ArrayAdapter<String>) binding.countrySpinner.getAdapter()).getPosition(user.getCountry()));
            String userGender = user.getGender();
            if (userGender != null) {
                switch (userGender) {
                    case "Male":
                        binding.genderRadioGroup.check(R.id.radioMale);
                        break;
                    case "Female":
                        binding.genderRadioGroup.check(R.id.radioFemale);
                        break;
                    case "Other":
                        binding.genderRadioGroup.check(R.id.radioOther);
                        // Add other cases if needed
                    default:
                        binding.genderRadioGroup.clearCheck(); // Clear selection if no match
                        break;
                }

                if(user.getImage()!=null){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(user.getImage(), 0, user.getImage().length);
                    binding.profileImageView.setImageBitmap(bitmap);
                }

            } else {
                Log.d("ProfilePage", "No user found with email: " + userEmail);
            }
        }
    }
}
