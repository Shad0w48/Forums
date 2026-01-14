package com.ilya.forums;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnAddForum, btnUserManage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnAddForum=findViewById(R.id.btnAddForum);
        btnAddForum.setOnClickListener(this);
        btnUserManage=findViewById(R.id.btnUserManage);
        btnUserManage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==btnAddForum){
            Intent go=new Intent(this,CreateNewForum.class);
            startActivity(go);
        }
        if(v==btnUserManage){
            Intent goUL=new Intent(this,UsersListActivity.class);
            startActivity(goUL);
        }
    }
}