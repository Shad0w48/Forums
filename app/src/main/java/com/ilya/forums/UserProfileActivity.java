package com.ilya.forums;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.ilya.forums.R;
import com.ilya.forums.model.User;
import com.ilya.forums.services.DatabaseService;
//import com.ilya.forums.utils.SharedPreferencesUtil;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "UserProfileActivity";
    private DatabaseService databaseService;
    private FirebaseAuth mAuth;


    private EditText etUserFirstName, etUserLastName, etUserEmail, etUserPhone, etUserPassword;
    private TextView tvUserDisplayName, tvUserDisplayEmail;
    private Button btnBack,btnUpdateProfile, btnSignOut;
    private View adminBadge;
    String ownId,selectedUid;
    User currentUser,selectedUser;
    boolean isCurrentUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService=DatabaseService.getInstance();
        mAuth = FirebaseAuth.getInstance();

        selectedUid = getIntent().getStringExtra("USER_UID");
        ownId=getIntent().getStringExtra("Own_USER_UID");
        databaseService.getUser(ownId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                currentUser = user;
            }
            @Override
            public void onFailed(Exception e) {}
        });


        Log.d(TAG, "Selected user: " + selectedUid);

        // Initialize the EditText fields
        etUserFirstName = findViewById(R.id.et_user_first_name);
        etUserLastName = findViewById(R.id.et_user_last_name);
        etUserEmail = findViewById(R.id.et_user_email);
        etUserPhone = findViewById(R.id.et_user_phone);
        etUserPassword = findViewById(R.id.et_user_password);
        tvUserDisplayName = findViewById(R.id.tv_user_display_name);
        tvUserDisplayEmail = findViewById(R.id.tv_user_display_email);
        btnUpdateProfile = findViewById(R.id.btn_edit_profile);
        btnSignOut = findViewById(R.id.btn_sign_out);
        btnBack =findViewById(R.id.btn_back_profile);
        btnBack.setOnClickListener(this);
        btnUpdateProfile.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);


        if (ownId != null && ownId.equals(selectedUid)) {
            isCurrentUser = true; // מעדכנים את המשתנה הלוגי
            btnSignOut.setVisibility(View.VISIBLE); // מציגים את כפתור היציאה
        } else {
            isCurrentUser = false;
            btnSignOut.setVisibility(View.GONE); // מסתירים לחלוטין אם זה פרופיל של מישהו אחר
        }

        showUserProfile();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_edit_profile) {
            updateUserProfile();
            return;
        }
        if(v.getId() == R.id.btn_sign_out) {
            // ניפוח ה-View מה-XML שייצרנו
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_sign_out, null);
            AlertDialog dialog = new AlertDialog.Builder(UserProfileActivity.this)
                    .setView(dialogView)
                    .create();

// הגדרת רקע שקוף כדי שהפינות המעוגלות של ה-Card/Pill שלך יעבדו יפה
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            }

// קישור הכפתורים מתוך ה-dialogView
            Button btnYes = dialogView.findViewById(R.id.btnSignOutYes);
            Button btnNo = dialogView.findViewById(R.id.btnSignOutNo);

// לחיצה על YES - מתנתקים ועוברים ל-Login
            // לחיצה על YES - משנים את שם המשתנה ל-view
            btnYes.setOnClickListener(view -> {
                com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserProfileActivity.this, LogInPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                dialog.dismiss();
            });

// לחיצה על NO - משנים את שם המשתנה ל-view
            btnNo.setOnClickListener(view -> dialog.dismiss());

// הצגת הדיאלוג על המסך
            dialog.show();
        }

        if(v==btnBack){
            finish();
        }
    }

    private void showUserProfile() {
        // Get the user data from database
        databaseService.getUser(selectedUid, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                selectedUser = user;
                // Set the user data to the EditText fields
                etUserFirstName.setText(user.getFname());
                etUserLastName.setText(user.getLname());
                etUserEmail.setText(user.getEmail());
                etUserPhone.setText(user.getPhone());
                etUserPassword.setText(user.getPassword());

                // Update display fields
                String displayName = user.getFname() + " " + user.getLname();
                tvUserDisplayName.setText(displayName);
                tvUserDisplayEmail.setText(user.getEmail());

                // Show/hide admin badge based on user's admin status
                //if (user.getIsAdmin()) {
                    //adminBadge.setVisibility(View.VISIBLE);
                    //Log.d(TAG, "User is admin, showing admin badge");
                //} else {
                    //adminBadge.setVisibility(View.GONE);
                   // Log.d(TAG, "User is not admin, hiding admin badge");
                //}
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Error getting user profile", e);
            }
        });

        // disable the EditText fields if the user is not the current user
        if (!isCurrentUser) {
            etUserEmail.setEnabled(false);
            etUserPassword.setEnabled(false);
        } else {
            etUserEmail.setEnabled(true);
            etUserPassword.setEnabled(true);
            btnUpdateProfile.setVisibility(View.VISIBLE);
        }
    }

    private void updateUserProfile() {
        if (selectedUser == null) {
            Log.e(TAG, "User not found");
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get the updated user data from the EditText fields
        String firstName = etUserFirstName.getText().toString();
        String lastName = etUserLastName.getText().toString();
        String phone = etUserPhone.getText().toString();
        String email = etUserEmail.getText().toString();
        String password = etUserPassword.getText().toString();

//        if (!isValid(firstName, lastName, phone, email, password)) {
//            Log.e(TAG, "Invalid input");
//            return;
//        }

        // Update the user object
        selectedUser.setFname(firstName);
        selectedUser.setLname(lastName);
        selectedUser.setPhone(phone);
        selectedUser.setEmail(email);
        selectedUser.setPassword(password);

        // Update the user data in the authentication
        Log.d(TAG, "Updating user profile");
        Log.d(TAG, "Selected user UID: " + selectedUser.getId());
        Log.d(TAG, "Is current user: " + isCurrentUser);
        Log.d(TAG, "User email: " + selectedUser.getEmail());
        Log.d(TAG, "User password: " + selectedUser.getPassword());



        if (!isCurrentUser && !selectedUser.getAdmin()) {
            Log.e(TAG, "Only the current user can update their profile");
            Toast.makeText(this, "You can only update your own profile", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (isCurrentUser) {
            updateUserInDatabase(selectedUser);
        }
        else if (selectedUser.getIsAdmin()) {
            // update the user in the database
            updateUserInDatabase(selectedUser);
        }
    }

    private void updateUserInDatabase(User user) {
        Log.d(TAG, "Updating user in database: " + user.getId());
        databaseService.updateUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void result) {
                Log.d(TAG, "User profile updated successfully");
                Toast.makeText(UserProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                showUserProfile(); // Refresh the profile view
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Error updating user profile", e);
                Toast.makeText(UserProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private boolean isValid(String firstName, String lastName, String phone, String email, String password) {
//        if (!Validator.isNameValid(firstName)) {
//            etUserFirstName.setError("First name is required");
//            etUserFirstName.requestFocus();
//            return false;
//        }
//        if (!Validator.isNameValid(lastName)) {
//            etUserLastName.setError("Last name is required");
//            etUserLastName.requestFocus();
//            return false;
//        }
//        if (!Validator.isPhoneValid(phone)) {
//            etUserPhone.setError("Phone number is required");
//            etUserPhone.requestFocus();
//            return false;
//        }
//        if (!Validator.isEmailValid(email)) {
//            etUserEmail.setError("Email is required");
//            etUserEmail.requestFocus();
//            return false;
//        }
//        if (!Validator.isPasswordValid(password)) {
//            etUserPassword.setError("Password is required");
//            etUserPassword.requestFocus();
//            return false;
//        }
//        return true;
//    }
//
  //   private void signOut() {
//        Log.d(TAG, "Sign out button clicked");
  //      SharedPreferencesUtil.signOutUser(UserProfileActivity.this);

 //       Log.d(TAG, "User signed out, redirecting to LandingActivity");
  //      Intent landingIntent = new Intent(UserProfileActivity.this, LandingActivity.class);
 //       landingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
  //      startActivity(landingIntent);
 //   }
}