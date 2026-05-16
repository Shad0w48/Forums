package com.ilya.forums;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.messaging.FirebaseMessaging;

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
        //permissons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        //for broadcast message
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
        .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FCM", "Subscribed to topic successfully");
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "default_channel_id",
                    "General Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }        }
        Btnlogin=findViewById(R.id.btnLogIn);
        Btnsignup=findViewById(R.id.btnSignUp);
        Btncreds=findViewById(R.id.btnCreds);
        Btnlogin.setOnClickListener(this);
        Btnsignup.setOnClickListener(this);
        Btncreds.setOnClickListener(this);
        com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("all_users")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        android.util.Log.d("FCM_TOPIC", "Successfully subscribed to all_users topic!");
                    } else {
                        android.util.Log.e("FCM_TOPIC", "Failed to subscribe to topic.", task.getException());
                    }
                });
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