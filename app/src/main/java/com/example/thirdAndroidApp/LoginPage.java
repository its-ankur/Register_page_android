package com.example.thirdAndroidApp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firstandroidapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.mindrot.jbcrypt.BCrypt;

public class LoginPage extends AppCompatActivity {
    private TextInputLayout emailLayout, passwordLayout;
    private TextInputEditText emailInput, passwordInput;
    private Button btnLogin;
    private TextView registerHere;
    private UserDAO userDAO;
    private boolean flag=true;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        init();
        userDAO = new UserDAO(this);
        userDAO.open();
        textWatchListener();
    }

    private void buttonClicked(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(v);
            }
        });

        registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the register activity
                clearfields();
                Intent intent = new Intent(LoginPage.this, RegisterPage.class);
                startActivity(intent);
            }
        });

        passwordInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (passwordInput.getRight() - passwordInput.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Toggle between showing and hiding the password
                        if (passwordInput.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            // Change the drawable to viewOn
                            passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_crossed_eye, 0);
                        } else {
                            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            // Change the drawable to viewOff
                            passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_remove_red_eye_24, 0);
                        }
                        // Move the cursor to the end of the text
                        passwordInput.setSelection(passwordInput.getText().length());
                        // Request focus on the EditText
                        passwordInput.requestFocus();
                        return true;
                    }
                }
                return false;
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

    private void init() {
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        registerHere = findViewById(R.id.registerHere);
        sharedPreferences = getSharedPreferences(Utility.saveDetailsFilename, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void loginUser(View v) {
        // Get user input
        String email = emailInput.getText().toString().trim().toLowerCase();
        String password = passwordInput.getText().toString().trim();
        // Validate input
        if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
            emailLayout.setError("Email is required");
            emailLayout.setErrorIconDrawable(null);
            passwordLayout.setError("Password is required");
            passwordLayout.setErrorIconDrawable(null);
            Utility.displayErrorSnackbar(v,"Please provide your Email and Password",LoginPage.this);
        }
        else if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email is required");
            emailLayout.setErrorIconDrawable(null);
            Utility.displayErrorSnackbar(v,"Please provide your Email Address",LoginPage.this);
        } else if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password is required");
            passwordLayout.setErrorIconDrawable(null);
            Utility.displayErrorSnackbar(v,"Please provide your Password",LoginPage.this);
        } else {
            emailLayout.setError(null);
            passwordLayout.setError(null);

            // Check if email exists
            User user = userDAO.getUserByEmail(email);
            if (user == null) {
                // Email does not exist, show a toast and redirect to RegisterPage
                Utility.displayErrorSnackbar(v,"Email does not exist. Redirecting to registration page...",LoginPage.this);
                Intent intent = new Intent(LoginPage.this, RegisterPage.class);
                startActivity(intent);
                flag=false;
                clearfields();
            } else {
                // Validate password
                if (BCrypt.checkpw(password, user.getPassword())) {
                    // Password is correct, login successful
                    Utility.displaySuccessSnackbar(v,"Login Successful",LoginPage.this);
                    Intent intent=new Intent(LoginPage.this, ShowDetailsPage.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    flag=false;
                    clearfields();
                    editor.putString(Utility.status,"true");
                    editor.putString(Utility.emailAddressKey,email);
                    editor.apply();
                } else {
                    // Password is incorrect
                    passwordLayout.setError("Invalid password");
                    passwordLayout.setErrorIconDrawable(null);
                    Utility.displayErrorSnackbar(v,"Entered password is wrong",LoginPage.this);
                }
            }
        }
    }

    private void textWatchListener(){
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                emailInput.setCursorVisible(true);
                emailInput.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear any previous error when user starts typing
                String email = s.toString().trim();
                if (flag) {
                    if (email.isEmpty()) {
                        emailLayout.setError("Email Address is required");
                        emailLayout.setErrorIconDrawable(null);
                    } else if (!isValidEmail(email)) {
                        emailLayout.setError("Please enter a valid email address");
                        emailLayout.setErrorIconDrawable(null);
                    }
                    else {
                        emailLayout.setError(null);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password=s.toString().trim();
                if(flag){
                    if(password.isEmpty()){
                        passwordLayout.setError("Password is required");
                        passwordLayout.setErrorIconDrawable(null);
                    }
                    else{
                        passwordLayout.setError(null);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void clearfields(){
        emailInput.setText("");
        passwordInput.setText("");
    }
}
