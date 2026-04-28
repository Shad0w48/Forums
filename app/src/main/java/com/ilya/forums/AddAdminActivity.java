package com.ilya.forums;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ilya.forums.adapters.UserAdapter;
import com.ilya.forums.model.User;
import com.ilya.forums.services.DatabaseService;

import java.util.List;

public class AddAdminActivity extends AppCompatActivity {

    private static final String TAG = "AddAdminActivity";
    private UserAdapter userAdapter;
    private TextView tvAddAdmin;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService = DatabaseService.getInstance();
        RecyclerView usersList = findViewById(R.id.rv_userList_AddAdmin);
        tvAddAdmin = findViewById(R.id.tvAddAdminTitle);
        usersList.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new UserAdapter(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                // When a user is clicked, show the dialog
                showMakeAdminDialog(user);
            }

            @Override
            public void onLongUserClick(User user) {
                Log.d(TAG, "User long clicked: " + user);
            }
        });
        usersList.setAdapter(userAdapter);
    }

    private void showMakeAdminDialog(User user) {
        // Inflate the custom dialog XML
        View dialogView = LayoutInflater.from(this).inflate(R.layout.add_admin, null);

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Find views inside the dialog
        TextView tvQuestion = dialogView.findViewById(R.id.tvMakeAdminquestion);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelNewAdmin);
        Button btnApply = dialogView.findViewById(R.id.btnMakeNewAdmin);

        // NOTE: If you have a getName() method in your User model, use that instead of getEmail()
        String userName = user.getEmail();
        tvQuestion.setText("Are you sure that you want to make " + userName + " an admin?");

        // Dismiss dialog on cancel
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Update database on apply
        btnApply.setOnClickListener(v -> {

            // NOTE: Ensure this matches the exact setter name in your User.java model
            // (e.g., it might be setIsAdmin(true) depending on how you named it)
            user.setAdmin(true);

            databaseService.updateUser(user, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    // UI changes must happen on the main thread
                    runOnUiThread(() -> {
                        Toast.makeText(AddAdminActivity.this, "Successfully updated admin status!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        userAdapter.notifyDataSetChanged(); // Refresh the list view if needed
                    });
                }

                @Override
                public void onFailed(Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddAdminActivity.this, "Failed to make admin.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error updating user", e);
                    });
                }
            });
        });

        // Show the dialog
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseService.getUserList(new DatabaseService.DatabaseCallback<List<User>>() {
            @Override
            public void onCompleted(List<User> users) {
                userAdapter.setUserList(users);
                tvAddAdmin.setText("Add a new admin");
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to get users list", e);
            }
        });
    }
}