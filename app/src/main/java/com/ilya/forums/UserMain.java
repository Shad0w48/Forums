package com.ilya.forums;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.ilya.forums.model.User;
import com.ilya.forums.services.DatabaseService;

public class UserMain extends AppCompatActivity implements View.OnClickListener {
    private DatabaseService databaseService;
    private FirebaseAuth mAuth;
    String userId;
    Button btnAdminPage;

    RecyclerView recyclerView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RecyclerView recyclerView = findViewById(R.id.rvpostRecyclerView);

        databaseService = DatabaseService.getInstance();
        mAuth = FirebaseAuth.getInstance();
        btnAdminPage = findViewById(R.id.btnAdminPage);
        btnAdminPage.setOnClickListener(this);
        userId = mAuth.getCurrentUser().getUid();

        databaseService.getUser(userId,  new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (user.getAdmin())
                    btnAdminPage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnAdminPage.getId()){
            Intent i = new Intent(this, AdminActivity.class);
            startActivity(i);
        }
    }
}