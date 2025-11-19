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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Intent Go;
    Button Btnlogin,Btnsignup,Btncreds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Btnlogin=findViewById(R.id.btnLogIn);
        Btnsignup=findViewById(R.id.btnSignUp);
        Btncreds=findViewById(R.id.btnCreds);
        Btnlogin.setOnClickListener(this);
        Btnsignup.setOnClickListener(this);
        Btncreds.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==Btncreds){
            Go= new Intent(this, Creds.class);
            startActivity(Go);
        }

        if(v==Btnlogin){
            Go= new Intent(this, LogInPage.class);
            startActivity(Go);
        }

        if(v==Btnsignup){
            Go= new Intent(this, RegisterActivity.class);
            startActivity(Go);
        }
    }
}