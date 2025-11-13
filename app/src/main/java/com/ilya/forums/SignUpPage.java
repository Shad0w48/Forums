package com.ilya.forums;
import com.ilya.forums.services.DatabaseService;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ilya.forums.model.User;

public class SignUpPage extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";
    EditText etFName, etLName,etPhone,etEmail,etPassword;
    Button btnRegister;
    TextView tvReg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /// get the views
        etEmail = findViewById(R.id.etEmailRegister;
        etPassword = findViewById(R.id.etPasswordRegister);
        etFName = findViewById(R.id.etFNameRegister);
        etLName = findViewById(R.id.etLNameRegister);
        etPhone = findViewById(R.id.etPhoneRegister);
        btnRegister = findViewById(R.id.btnRegister);
        tvReg = findViewById(R.id.tvRegister);

        /// set the click listener
        btnRegister.setOnClickListener(this);
        tvReg.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == btnRegister.getId()) {
            Log.d(TAG, "onClick: Register button clicked");

            /// get the input from the user
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String fName = etFName.getText().toString();
            String lName = etLName.getText().toString();
            String phone = etPhone.getText().toString();


            /// Validate input
            Log.d(TAG, "onClick: Validating input...");
            if (!checkInput(email, password, fName, lName, phone)) {
                /// stop if input is invalid
                return;
            }

            Log.d(TAG, "onClick: Registering user...");

            /// Register user
            registerUser(fName, lName, phone, email, password);
        } else if (v.getId() == tvReg.getId()) {
            /// Navigate back to Login Activity
            finish();
        }
    }






    /// Register the user
    private void registerUser(String fname, String lname, String phone, String email, String password) {
        Log.d(TAG, "registerUser: Registering user...");

        String uid = databaseService.generateUserId();

        /// create a new user object
        User user = new User(uid, fname, lname, phone,email, password, false);

        databaseService.checkIfEmailExists(email, new DatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(Boolean exists) {
                if (exists) {
                    Log.e(TAG, "onCompleted: Email already exists");
                    /// show error message to user
                    Toast.makeText(SignUpPage.this, "Email already exists", Toast.LENGTH_SHORT).show();
                } else {
                    /// proceed to create the user
                    createUserInDatabase(user);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Failed to check if email exists", e);
                /// show error message to user
                Toast.makeText(SignUpPage.this, "Failed to register user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUserInDatabase(User user) {
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Log.d(TAG, "createUserInDatabase: User created successfully");
                /// save the user to shared preferences

                Log.d(TAG, "createUserInDatabase: Redirecting to MainActivity");
                /// Redirect to MainActivity and clear back stack to prevent user from going back to register screen
                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                /// clear the back stack (clear history) and start the MainActivity
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "createUserInDatabase: Failed to create user", e);
                /// show error message to user
                Toast.makeText(RegisterActivity.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                /// sign out the user if failed to register

            }
        });
    }
}