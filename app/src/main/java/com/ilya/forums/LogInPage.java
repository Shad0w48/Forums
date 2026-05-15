package com.ilya.forums;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ilya.forums.services.DatabaseService;

public class LogInPage extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    public static final String MyPreferences = "MyPrefs";
    SharedPreferences sharedPreferences;

    private EditText etEmail, etPassword;
    private MaterialButton btnLogin, btnBack; // Changed to MaterialButton
    private TextView tvRegister, tvLoginTitle;
    private ImageView ivLoginLogo;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in_page);

        // Handle Edge-to-Edge system bar padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Initialize views with updated IDs from your XML
        tvLoginTitle = findViewById(R.id.tvLoginTitle);
        ivLoginLogo = findViewById(R.id.ivLoginLogo);
        etEmail = findViewById(R.id.etEmailLogIn);
        etPassword = findViewById(R.id.etpasswordLogIn);
        btnLogin = findViewById(R.id.btnLogIn);
        btnBack = findViewById(R.id.btnBackFromLogin); // Match the gray button ID
        tvRegister = findViewById(R.id.tvLogIn); // The "Sign Up" navigation text

        databaseService = DatabaseService.getInstance();
        sharedPreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);

        // 2. Load saved credentials from SharedPreferences
        String savedEmail = sharedPreferences.getString("email", "");
        String savedPass = sharedPreferences.getString("password", "");
        etEmail.setText(savedEmail);
        etPassword.setText(savedPass);

        // 3. Set click listeners
        btnLogin.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnLogIn) {
            handleLogin();
        } else if (id == R.id.tvLogIn) {
            // Navigate to Register Activity
            Intent registerIntent = new Intent(LogInPage.this, RegisterActivity.class);
            startActivity(registerIntent);
        } else if (id == R.id.btnBackFromLogin) {
            // Go back to MainActivity
            finish();
        }
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Attempting login for: " + email);
        loginUser(email, password);
    }

    private void loginUser(String email, String password) {
        databaseService.loginUser(email, password, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "onCompleted: User logged in UID: " + uid);

                // Save credentials to shared preferences for next time
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.apply();

                // Handle Cloud Messaging Token
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }
                            String token = task.getResult();
                            saveTokenToDatabase(uid, token);
                        });

                // Clear history and move to UserMain
                Intent mainIntent = new Intent(LogInPage.this, UserMain.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Login failed", e);
                etPassword.setError("Invalid email or password");
                etPassword.requestFocus();
            }
        });
    }

    private void saveTokenToDatabase(String uid, String token) {
        com.google.firebase.database.FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("fcmToken")
                .setValue(token)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Token saved for user: " + uid);
                    } else {
                        Log.e(TAG, "Failed to save token", task.getException());
                    }
                });
    }
}