package com.ilya.forums;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.ilya.forums.model.User;
import com.ilya.forums.services.DatabaseService;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    // Tag used for logging errors or debug messages in the Logcat
    private static final String TAG = "Register";

    // UI Elements: The actual text boxes where the user types
    EditText etFname, etLname, etMail, etPhone, etPassword;

    // UI Elements: The layout wrappers that allow us to show red error messages below the text boxes
    TextInputLayout layoutFname, layoutLname, layoutEmail, layoutPhone, layoutPassword;

    // Strings to temporarily hold the text the user typed in
    String fName, lName, email, phone, password;

    // Buttons for submission and navigation
    Button btnBack, btnSubmit;

    // Our custom service that handles all Firebase Database and Authentication connections
    private DatabaseService databaseService;

    // SharedPreferences allows us to save small pieces of data (like emails) to the phone's memory
    public static final String MyPreferences = "MyPrefs";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Makes the app UI stretch to the very edges of the screen (behind the status and navigation bars)
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Adds padding so our UI doesn't actually overlap with the system battery icon or bottom swipe bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize SharedPreferences in private mode (only this app can read it)
        sharedPreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);

        // Grab the single instance of our DatabaseService
        databaseService = DatabaseService.getInstance();

        // Bind the standard EditTexts from the XML layout
        etFname = findViewById(R.id.etFNameRegister);
        etLname = findViewById(R.id.etLNameRegister);
        etMail = findViewById(R.id.etEmailRegister);
        etPhone = findViewById(R.id.etPhoneRegister);
        etPassword = findViewById(R.id.etPasswordRegister);

        // Bind the TextInputLayout wrappers from the XML layout (needed for the red error states)
        layoutFname = findViewById(R.id.layoutFName);
        layoutLname = findViewById(R.id.layoutLName);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPhone = findViewById(R.id.layoutPhone);
        layoutPassword = findViewById(R.id.layoutPassword);

        // Bind the buttons
        btnSubmit = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBackFromReg);

        // Tell Android that this class will handle the click events for these buttons
        btnSubmit.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Check if the user clicked the "Register" button
        if (v.getId() == btnSubmit.getId()) {

            // .trim() removes any accidental spaces the user put at the start or end of their input
            fName = etFname.getText().toString().trim();
            lName = etLname.getText().toString().trim();
            email = etMail.getText().toString().trim();
            phone = etPhone.getText().toString().trim();
            password = etPassword.getText().toString().trim();

            // Clear any old red error messages before we start checking the new input
            clearErrors();

            // A flag to track if all fields pass our tests
            boolean isValid = true;

            // 1. FIRST NAME VALIDATION (Must not be empty, must be at least 2 characters)
            if (fName.isEmpty()) {
                layoutFname.setError("First name is required");
                isValid = false;
            } else if (fName.length() < 2) {
                layoutFname.setError("First name must be at least 2 characters");
                isValid = false;
            }

            // 2. LAST NAME VALIDATION (Must not be empty, must be at least 2 characters)
            if (lName.isEmpty()) {
                layoutLname.setError("Last name is required");
                isValid = false;
            } else if (lName.length() < 2) {
                layoutLname.setError("Last name must be at least 2 characters");
                isValid = false;
            }

            // 3. EMAIL VALIDATION
            // strictEmailRegex ensures the format is absolutely correct: words + @ + words + . + 2-4 letter domain
            String strictEmailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

            if (email.isEmpty()) {
                layoutEmail.setError("Email address is required");
                isValid = false;
            } else if (!email.matches(strictEmailRegex)) {
                layoutEmail.setError("Please enter a valid email address (e.g., name@gmail.com)");
                isValid = false;
            }

            // 4. PHONE VALIDATION
            // ^[0-9]{10}$ forces the string to be exactly 10 digits long with no letters or special characters
            if (phone.isEmpty()) {
                layoutPhone.setError("Phone number is required");
                isValid = false;
            } else if (!phone.matches("^[0-9]{10}$")) {
                layoutPhone.setError("Please enter a valid 10-digit phone number");
                isValid = false;
            }

            // 5. PASSWORD VALIDATION (Firebase requires a minimum of 6 characters)
            if (password.isEmpty()) {
                layoutPassword.setError("Password is required");
                isValid = false;
            } else if (password.length() < 6) {
                layoutPassword.setError("Password must be at least 6 characters");
                isValid = false;
            }

            // If ANY of the checks above failed (isValid became false), stop the function right here.
            // The user will see the red error boxes and have to try again.
            if (!isValid) {
                return;
            }

            // If the code reaches this line, all fields are perfect! Proceed to register.
            Log.d(TAG, "onClick: Registering user...");
            registerUser(fName, lName, phone, email, password);
        }
        // Check if the user clicked the "Go Back" button
        else if (v.getId() == btnBack.getId()){
            Intent goBack = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(goBack);
        }
    }

    // Helper method to hide all red error text on the screen
    private void clearErrors() {
        layoutFname.setError(null);
        layoutLname.setError(null);
        layoutEmail.setError(null);
        layoutPhone.setError(null);
        layoutPassword.setError(null);
    }

    // Packages the user's validated input into a User object
    private void registerUser(String fname, String lname, String phone, String email, String password) {
        // "temp_id" is used here because Firebase Authentication will generate the real unique ID for us
        User user = new User("temp_id", fname, lname, email, phone, password, false);
        createUserInDatabase(user);
    }

    // Sends the User object to Firebase via our DatabaseService
    private void createUserInDatabase(User user) {
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<String>() {

            // This method runs automatically when Firebase successfully creates the account
            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "createUserInDatabase: User created successfully");

                // Save the email and password to the phone so they can be auto-filled in the future
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.apply(); // .apply() saves the data in the background without freezing the app

                // UPDATED: Send the user to the LogInPage so they can log in with their brand new account
                Intent intent = new Intent(RegisterActivity.this, LogInPage.class);

                // These flags clear the back history, so if the user presses the 'Back' button on their phone
                // from the Login Page, it won't accidentally take them back to this Register screen.
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            // This method runs if Firebase rejects the registration (e.g. no internet, or email already in use)
            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "createUserInDatabase: Failed to create user", e);
                // Extract the exact reason Firebase failed, and display it as a red error under the Email box
                layoutEmail.setError(e.getMessage());
            }
        });
    }
}