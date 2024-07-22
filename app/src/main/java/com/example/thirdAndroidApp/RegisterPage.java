package com.example.thirdAndroidApp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firstandroidapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterPage extends AppCompatActivity {

    // Views and Widgets
    private TextInputLayout fnameLayout, lnameLayout, emailLayout, passwordLayout, confirmPasswordLayout , dateOfBirthLayout,contactNumberLayout;
    private TextInputEditText fnameEditText, lnameEditText, emailEditText, passwordEditText, confirmPasswordEditText,dateOfBirthEditText,contactNumber;
    private Spinner countrySpinner;
    private Button btnRegister;
    private RadioGroup genderRadioGroup;
    private TextView genderErrorText, termsErrorText, selectGender,loginHere;
    private CheckBox termsCheckBox;
    private Boolean flag = true;
    private ImageButton datePickerButton;
    private View genderView;

    // Database
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        setSupportActionBar(findViewById(R.id.my_toolbar));
        // Setting the title for the app bar
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Initialize views and widgets
        init();
        // Initialize the database
        userDAO = new UserDAO(this);
        try {
            userDAO.open();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the error
        }
        // Set up text change listeners for input validation
        textWatchListener();
        buttonClick();
        // Assuming dateOfBirthEditText is defined somewhere in your RegisterPage class
//// Example: defining dateOfBirthEditText
// Setting up OnTouchListener for dateOfBirthEditText
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void buttonClick(){
        dateOfBirthEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (dateOfBirthEditText.getRight() - dateOfBirthEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Clicked on the calendar icon
                        showDatePickerDialog();
                        return true; // Consume the touch event
                    }
                }
                return false; // Let other touch events pass through
            }
        });

        passwordEditText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Toggle between showing and hiding the password
                        if (passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            // Change the drawable to viewOn
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_crossed_eye, 0);
                        } else {
                            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            // Change the drawable to viewOff
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_remove_red_eye_24, 0);
                        }
                        // Move the cursor to the end of the text
                        passwordEditText.setSelection(passwordEditText.getText().length());
                        // Request focus on the EditText
                        passwordEditText.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });

        confirmPasswordEditText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (confirmPasswordEditText.getRight() - confirmPasswordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Toggle between showing and hiding the password
                        if (confirmPasswordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                            confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            confirmPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_crossed_eye, 0);
                        } else {
                            confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            confirmPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_remove_red_eye_24, 0);
                        }
                        // Move the cursor to the end of the text
                        confirmPasswordEditText.setSelection(confirmPasswordEditText.getText().length());
                        confirmPasswordEditText.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });



        loginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterPage.this,LoginPage.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailEditText.getText().toString().trim();
                // Validate inputs when register button is clicked
                if (validateInputs(v)) {
                    saveFormDetails();
                    // If inputs are valid, clear fields
                    flag = false;
                    clearFields();
                    Utility.displaySuccessSnackbar(v,"Registered Successfully",RegisterPage.this);
                    Intent intent = new Intent(RegisterPage.this, LoginPage.class);
                    startActivity(intent);
                }
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

    // Method to initialize views and widgets
    private void init() {
        fnameLayout = findViewById(R.id.fnameLayout);
        lnameLayout = findViewById(R.id.lnameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        dateOfBirthLayout=findViewById(R.id.dateOfBirthLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        fnameEditText = findViewById(R.id.fname);
        lnameEditText = findViewById(R.id.lname);
        emailEditText = findViewById(R.id.email);
        contactNumber=findViewById(R.id.contactNumber);
        contactNumberLayout=findViewById(R.id.contactNumberLayout);
        dateOfBirthEditText=findViewById(R.id.dateOfBirth);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        countrySpinner = findViewById(R.id.countrySpinner);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        genderErrorText = findViewById(R.id.genderErrorText);
        selectGender = findViewById(R.id.selectGender);
        termsCheckBox = findViewById(R.id.termsCheckBox);
        termsErrorText = findViewById(R.id.termsErrorText);
        btnRegister = findViewById(R.id.btnRegister);
        genderView=findViewById(R.id.genderView);
        loginHere=findViewById(R.id.loginHere);
        dateOfBirthEditText = findViewById(R.id.dateOfBirth);

        // Populate the Spinner with data
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.country_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

    }

    // Method to clear input fields and remove the cursor blinking from them
    private void clearFields() {
        // Clear any error messages first
        fnameLayout.setError(null);
        emailLayout.setError("");
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);
        genderErrorText.setVisibility(View.GONE);
        selectGender.setTextColor(Color.BLACK);
        termsErrorText.setVisibility(View.GONE);
        dateOfBirthEditText.setError(null);

        // Clear input fields and reset selections
        fnameEditText.setText("");
        lnameEditText.setText("");
        emailEditText.setText("");
        dateOfBirthEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
        contactNumber.setText("");
        countrySpinner.setSelection(0); // Reset spinner to default position
        genderRadioGroup.clearCheck(); // Clear gender selection
        termsCheckBox.setChecked(false); // Uncheck terms checkbox
        flag = true;

        // Hide the keyboard on the click of the button
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(btnRegister.getWindowToken(), 0);
    }

    // Method to validate user inputs
    private boolean validateInputs(View v) {
        String fname = fnameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();

        boolean isValid = true;

        // Validate first name
        if (fname.isEmpty()) {
            fnameLayout.setError("First name is required");
            isValid = false;
            Utility.displayErrorSnackbar(v,"Please provide your First Name",RegisterPage.this);
        } else {
            fnameLayout.setError(null);
            isValid=true;
        }

        // Validate email address
        if (email.isEmpty()) {
            emailLayout.setError("Email Address is required");
            Utility.displayErrorSnackbar(v,"Please provide your Email Address",RegisterPage.this);
            isValid = false;

        } else if (!isValidEmail(email)) {
            emailLayout.setError("Please enter a valid email address");
            isValid = false;
            Utility.displayErrorSnackbar(v,"Please provide a valid Email Address",RegisterPage.this);
        } else if (userDAO.isEmailExists(email)) {
            emailLayout.setError("Email already exists");
            isValid = false;
            Utility.displayErrorSnackbar(v,"Email already exists",RegisterPage.this);
        } else {
            emailLayout.setError(null);
            isValid=true;
        }

        // Validate password criteria
        if (password.isEmpty()) {
            passwordLayout.setError("Password is required");
            passwordLayout.setErrorIconDrawable(null);
            isValid = false;
            Utility.displayErrorSnackbar(v,"Please provide a Password",RegisterPage.this);
        } else if (password.length() < 6) {
            passwordLayout.setError("Password should be at least 6 characters");
            passwordLayout.setErrorIconDrawable(null);
            isValid = false;
            Utility.displayErrorSnackbar(v,"Password should be atleast 6 characters long",RegisterPage.this);
        } else if (!isValidPassword(password)) {
            passwordLayout.setError("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
            passwordLayout.setErrorIconDrawable(null);
            isValid = false;
            Utility.displayErrorSnackbar(v,"Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character",RegisterPage.this);
        } else {
            passwordLayout.setError(null);
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            confirmPasswordLayout.setError("Confirm Password is required");
            confirmPasswordLayout.setErrorIconDrawable(null);
            isValid = false;
            Utility.displayErrorSnackbar(v,"Confirm Password is required",RegisterPage.this);

        } else if (!confirmPassword.equals(password)) {
            confirmPasswordLayout.setError("Passwords do not match");
            confirmPasswordLayout.setErrorIconDrawable(null);
            isValid = false;
            Utility.displayErrorSnackbar(v,"Passwords do not match",RegisterPage.this);
        } else {
            confirmPasswordLayout.setError(null);
        }

        // Validate gender selection
        if (selectedGenderId == -1) {
            genderErrorText.setVisibility(View.VISIBLE);
            genderErrorText.setText("Gender is required");
            selectGender.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            genderView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            isValid = false;
            Utility.displayErrorSnackbar(v,"Gender is required",RegisterPage.this);
        } else {
            genderErrorText.setVisibility(View.GONE);
            selectGender.setTextColor(Color.BLACK);
            genderView.setBackgroundColor(Color.GRAY);
            isValid=true;// Reset to default color
        }

        // Validate terms and conditions
        if (!termsCheckBox.isChecked()) {
            termsErrorText.setVisibility(View.VISIBLE);
            termsErrorText.setText("Please accept the terms and conditions");
            isValid = false;
            Utility.displayErrorSnackbar(v,"Please accept the terms and conditions",RegisterPage.this);
        } else {
            termsErrorText.setVisibility(View.GONE);
        }

        // Validate date of birth if not empty
        String dateOfBirth = dateOfBirthEditText.getText().toString().trim();
        if (!dateOfBirth.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            sdf.setLenient(false); // Ensure strict date parsing
            try {
                Date dobDate = sdf.parse(dateOfBirth);

                // Validate age (minimum 18 years)
                if (!isAgeAboveThreshold(dobDate, 18)) {
                    dateOfBirthLayout.setError("You must be at least 18 years old");
                    isValid = false;
                    Utility.displayErrorSnackbar(v,"You need to be atleast 18 years old",RegisterPage.this);
                } else {
                    dateOfBirthLayout.setError(null);
                }
            } catch (ParseException e) {
                dateOfBirthLayout.setError("Invalid date format (DD/MM/YYYY)");
                isValid = false;
                Utility.displayErrorSnackbar(v,"Invalid date format (DD/MM/YYYY)",RegisterPage.this);
            }
        }

        return isValid;
    }

    private boolean isAgeAboveThreshold(Date dob, int thresholdAge) {
        Calendar dobCalendar = Calendar.getInstance();
        dobCalendar.setTime(dob);
        Calendar thresholdCalendar = Calendar.getInstance();
        thresholdCalendar.add(Calendar.YEAR, -thresholdAge);

        return !dobCalendar.after(thresholdCalendar);
    }



    // Utility method to validate email format
    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }



    // Utility method to validate password criteria
    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@&$^])(?=\\S+$).{6,}$";
        return password.matches(passwordPattern);
    }


    // Method to set up text change listeners for input fields
    private void textWatchListener() {

        fnameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                fnameEditText.setCursorVisible(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear any previous error when user starts typing
                fnameLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        lnameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lnameEditText.setCursorVisible(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear any previous error when user starts typing
                lnameLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                emailEditText.setCursorVisible(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear any previous error when user starts typing
                String email = s.toString().trim();
                if (flag) {
                    if (email.isEmpty()) {
                        emailLayout.setError("Email Address is required");
                    } else if (!isValidEmail(email)) {
                        emailLayout.setError("Please enter a valid email address");
                    }
                    else if (userDAO.isEmailExists(email)) {
                        emailLayout.setError("Email already exists");
                    }else {
                        emailLayout.setError(null);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                passwordEditText.setCursorVisible(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString().trim();
                if (flag) {
                    if (password.isEmpty()) {
                        passwordLayout.setError("Password is required");
                        passwordLayout.setErrorIconDrawable(null);
                    } else if (password.length() < 6) {
                        passwordLayout.setError("Password should be at least 6 characters");
                        passwordLayout.setErrorIconDrawable(null);
                    } else if (!isValidPassword(password)) {
                        passwordLayout.setError("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
                        passwordLayout.setErrorIconDrawable(null);
                    } else {
                        passwordLayout.setError(null);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                confirmPasswordEditText.setCursorVisible(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear any previous error when user starts typing
//                String confirmPassword=s.toString().trim();
//                String password= String.valueOf(R.id.password);
//                if(flag){
//                    if (confirmPassword.isEmpty()) {
//                        confirmPasswordLayout.setError("Confirm Password is required");
//                    } else if (!confirmPassword.equals(password)) {
//                        confirmPasswordLayout.setError("Passwords do not match");
//                    } else {
//                        confirmPasswordLayout.setError(null);
//                    }
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String confirmPassword=s.toString().trim();
                String password= passwordEditText.getText().toString().trim();
                if(flag){
                    if (confirmPassword.isEmpty()) {
                        confirmPasswordLayout.setError("Confirm Password is required");
                        confirmPasswordLayout.setErrorIconDrawable(null);
                    } else if (!confirmPassword.equals(password)) {
                        confirmPasswordLayout.setError("Passwords do not match");
                        confirmPasswordLayout.setErrorIconDrawable(null);
                    } else {
                        confirmPasswordLayout.setError(null);
                    }
                }
            }
        });

        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    genderErrorText.setVisibility(View.GONE);
                    selectGender.setTextColor(Color.BLACK);
                    genderView.setBackgroundColor(Color.GRAY);// Reset to default color
                }
            }
        });

        termsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    termsErrorText.setVisibility(View.GONE);
                }
            }
        });
        dateOfBirthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String dateOfBirth = dateOfBirthEditText.getText().toString().trim();
                if (!dateOfBirth.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                    sdf.setLenient(false); // Ensure strict date parsing
                    try {
                        Date dobDate = sdf.parse(dateOfBirth);

                        // Validate age (minimum 18 years)
                        if (!isAgeAboveThreshold(dobDate, 18)) {
                            dateOfBirthLayout.setError("You must be at least 18 years old");
                        } else {
                            dateOfBirthLayout.setError(null);
                        }
                    } catch (ParseException e) {
                        dateOfBirthLayout.setError("Invalid date format (DD/MM/YYYY)");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        contactNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String contact=s.toString().trim();
                if(contact.isEmpty()){
                    contactNumberLayout.setError(null);
                }
                if(!contact.isEmpty()) {
                    if (contact.length() < 10) {
                        contactNumberLayout.setError("Contact number must be ten digits");
                    } else if (contact.length()>10) {
                        contactNumberLayout.setError("Contact should not be greater than ten digits");
                    } else {
                        contactNumberLayout.setError(null);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    // Method to save form details to the database
    private void saveFormDetails() {
        User user = new User(
                fnameEditText.getText().toString(),
                lnameEditText.getText().toString(),
                emailEditText.getText().toString().toLowerCase(),
                countrySpinner.getSelectedItem().toString(),
                ((RadioButton) findViewById(genderRadioGroup.getCheckedRadioButtonId())).getText().toString(),
                passwordEditText.getText().toString(),
                dateOfBirthEditText.getText().toString(),
                contactNumber.getText().toString(),
                termsCheckBox.isChecked()
        );

        userDAO.insertUser(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCountry(),
                user.getGender(),
                user.getPassword(),
                user.getDateOfBirth(),
                user.getContactNumber(),
                user.isTermsAccepted()
        );
    }

    // Method to set up click listener for the registration button



    // Method to display DatePickerDialog for date of birth selection

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                RegisterPage.this,
                R.style.YellowDatePickerDialog, // Apply the custom theme here
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update the dateOfBirthEditText with the selected date
                        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        dateOfBirthEditText.setText(date);
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }
}