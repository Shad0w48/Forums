package com.ilya.forums;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ilya.forums.model.User;
import com.ilya.forums.services.DatabaseService;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LogInPage extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    public static final String MyPreferences="MyPrefs";
    SharedPreferences sharedPreferences;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private String email2, pass2;
    private DatabaseService databaseService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /// get the views
        etEmail = findViewById(R.id.etEmailLogIn);
        etPassword = findViewById(R.id.etpasswordLogIn);
        btnLogin = findViewById(R.id.btnLogIn);
        tvRegister = findViewById(R.id.tvLogIn);
        sharedPreferences=getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        databaseService = DatabaseService.getInstance();

        email2=sharedPreferences.getString("email","");
        pass2=sharedPreferences.getString("password","");
        etEmail.setText(email2);
        etPassword.setText(pass2);
        /// set the click listener
        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnLogin.getId()) {
            Log.d(TAG, "onClick: Login button clicked");

            /// get the email and password entered by the user
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            /// log the email and password
            Log.d(TAG, "onClick: Email: " + email);
            Log.d(TAG, "onClick: Password: " + password);

            Log.d(TAG, "onClick: Validating input...");


            Log.d(TAG, "onClick: Logging in user...");

            /// Login user
            loginUser(email, password);
        } else if (v.getId() == tvRegister.getId()) {
            /// Navigate to Register Activity
            Intent registerIntent = new Intent(LogInPage.this, RegisterActivity.class);
            startActivity(registerIntent);
        }
    }

    /// Method to check if the input is valid
    /// It checks if the email and password are valid



    private void loginUser(String email, String password) {
        databaseService.LoginUser(email, password, new DatabaseService.DatabaseCallback<String>() {
            /// Callback method called when the operation is completed
            // @param email  & password is logged in
            @Override
            public void onCompleted(String  uid) {
                Log.d(TAG, "onCompleted: User logged in: " + uid.toString());
                /// save the user data to shared preferences
                // SharedPreferencesUtil.saveUser(LoginActivity.this, user);
                /// Redirect to main activity and clear back stack to prevent user from going back to login screen



                Intent mainIntent = new Intent(LogInPage.this, UserMain.class);
                /// Clear the back stack (clear history) and start the MainActivity
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Failed to retrieve user data", e);
                /// Show error message to user
                etPassword.setError("Invalid email or password");
                etPassword.requestFocus();
                /// Sign out the user if failed to retrieve user data
                /// This is to prevent the user from being logged in again
                //SharedPreferencesUtil.signOutUser(LogInPage.this);
            }
        });
    }
}