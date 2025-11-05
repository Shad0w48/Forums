package com.ilya.forums;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splash extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imageView=(ImageView) findViewById(R.id.imageView);
        Thread mSplashThread = new Thread(){
          @Override
          public void run(){
              try {
                  synchronized (this) {
                      wait(3000);
                  }

              }
              catch(InterruptedException ex){

              }
              finish();
              Intent intent= new Intent(Splash.this,LogInPage.class);
          }
        };
        mSplashThread.start();
    }
}