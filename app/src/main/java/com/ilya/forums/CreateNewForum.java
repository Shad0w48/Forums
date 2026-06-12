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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ilya.forums.model.Forum;
import com.ilya.forums.model.User;
import com.ilya.forums.services.DatabaseService;

import java.util.Date;
import java.util.HashMap;

// ============================================================================
// CREATE NEW FORUM ACTIVITY
// This screen allows an Admin to create a new forum category (e.g., "Tech", "Cars").
// Once created, it updates the database and triggers a notification for everyone.
// ============================================================================
public class CreateNewForum extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Create Forum";

    // UI Elements
    TextView tvCreateForumtitle;
    Button btnCreateForum, btnBack;
    EditText etTitle, etContent;

    // Data Variables
    String userId, title, description;
    private DatabaseService databaseService;
    private FirebaseAuth mAuth;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_new_forum);

        // Handle window insets for modern full-screen designs
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Initialize Firebase services
        databaseService = DatabaseService.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();

        // 2. Load the current user profile (we need this to attach the Creator's info to the Forum)
        databaseService.getUser(userId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                // Keep a simplified version of the user for the Forum model
                currentUser = new User(user.getId(), user.getFname(), user.getLname());
            }
            @Override
            public void onFailed(Exception e) { /* Handle error */ }
        });

        // 3. Link UI elements
        tvCreateForumtitle = findViewById(R.id.tvCreateForum);
        btnCreateForum = findViewById(R.id.btnCreateForum);
        etTitle = findViewById(R.id.etForumTitle);
        etContent = findViewById(R.id.etForumDescription);
        btnBack = findViewById(R.id.btnBackFromAddForum);

        // 4. Attach click listeners
        btnCreateForum.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // --- BUTTON: CREATE FORUM ---
        if (v == btnCreateForum) {
            title = etTitle.getText().toString();
            description = etContent.getText().toString();

            // Generate a unique ID for the new forum category
            String forumId = databaseService.generateForumId();
            Date currentDate = new Date();

            // Package the data into our Forum model
            Forum newForum = new Forum(forumId, title, description, currentUser, currentDate);

            // Send to database
            databaseService.createNewForum(newForum, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    // --- TRIGGER GLOBAL NOTIFICATION ---
                    // By pushing a new record to "GlobalNotifications", our index.js
                    // Cloud Function will wake up and send a push message to ALL users!
                    DatabaseReference globalRef = FirebaseDatabase.getInstance()
                            .getReference("GlobalNotifications")
                            .push();

                    //בניית ההודעה+
                    HashMap<String, String> data = new HashMap<>();
                    data.put("title", "פורום חדש נוצר!");
                    data.put("message", "בואו לראות את הפורום: " + title);
                    globalRef.setValue(data);

                    Toast.makeText(CreateNewForum.this, "Forum Created!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(CreateNewForum.this, "Failed to create", Toast.LENGTH_SHORT).show();
                }
            });

            // Return to Admin screen
            Intent goBack = new Intent(this, AdminActivity.class);
            startActivity(goBack);
            finish(); // Close this screen so the user doesn't come back to it with 'back' button
        }

        // --- BUTTON: BACK ---
        else if (v == btnBack) {
            Intent goBack = new Intent(this, AdminActivity.class);
            startActivity(goBack);
            finish();
        }
    }
}