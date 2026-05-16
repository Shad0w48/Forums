package com.ilya.forums;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ilya.forums.R;
import com.ilya.forums.adapters.UserAdapter;
import com.ilya.forums.model.User;
import com.ilya.forums.services.DatabaseService;

import java.util.List;

public class UsersListActivity extends AppCompatActivity {

    private static final String TAG = "UsersListActivity";
    private UserAdapter userAdapter;
    private TextView tvUserCount;
    private DatabaseService databaseService;
    String ownId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_table);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        databaseService=DatabaseService.getInstance();
        ownId=getIntent().getStringExtra("Own_USER_UID");
        RecyclerView usersList = findViewById(R.id.rv_users_list);
        tvUserCount = findViewById(R.id.tv_user_count);
        usersList.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                // Handle user click
                Log.d(TAG, "User clicked: " + user);
                Intent intent = new Intent(UsersListActivity.this, UserProfileActivity.class);
                intent.putExtra("USER_UID", user.getId());
                intent.putExtra("Own_USER_UID",ownId);
                startActivity(intent);
            }

            @Override
            public void onLongUserClick(User user) {
                // Handle long user click
                Log.d(TAG, "User long clicked: " + user);
            }
        });
        usersList.setAdapter(userAdapter);
        ImageButton btnBack = findViewById(R.id.btn_back_users);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // סוגר את המסך הנוכחי ומחזיר למסך הניהול
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        databaseService.getUserList(new DatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(List<User> users) {
                userAdapter.setUserList(users);
                tvUserCount.setText("Total users: " + users.size());
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to get users list", e);
            }
        });
    }

}