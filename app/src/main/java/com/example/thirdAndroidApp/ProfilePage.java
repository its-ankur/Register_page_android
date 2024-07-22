package com.example.thirdAndroidApp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.firstandroidapp.R;
import com.example.firstandroidapp.databinding.ActivityProfilePageBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
        String fullName = binding.fname.getText().toString().trim();
        String lastName=binding.lname.getText().toString().trim();
        String contactNumber=binding.contactNumber.getText().toString().trim();
        String dateOfBirth = binding.dateOfBirth.getText().toString().trim();
        String country = binding.countrySpinner.getSelectedItem().toString();
        String gender = ((RadioButton) findViewById(binding.genderRadioGroup.getCheckedRadioButtonId())).getText().toString();
        String password = binding.passwordEditText.getText().toString().trim();

        // Create a User object with the updated details
        User updatedUser = new User();
        updatedUser.setFirstName(fullName);
        updatedUser.setLastName(lastName);
        updatedUser.setContactNumber(contactNumber);
        updatedUser.setDateOfBirth(dateOfBirth);
        updatedUser.setCountry(country);
        updatedUser.setGender(gender);
        if (passwordCheck) {
            updatedUser.setPassword(password); // Include password if it needs to be updated
        } else {
            updatedUser.setPassword(null); // Ensure password is null if not updating
        }

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

            } else {
                Log.d("ProfilePage", "No user found with email: " + userEmail);
            }
        }
    }
}
