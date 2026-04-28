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
        imageView=(ImageView) findViewById(R.id.imgSplash);
        // Load the GIF using Glide
        com.bumptech.glide.Glide.with(this)
                .asGif()
                .load(R.drawable.gif100)
                .into(imageView);
        Thread mSplashThread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(3000); // 3-second delay
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                finish();
                // Add startActivity here to actually move to the next page
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
            }
        };
        mSplashThread.start();
    }
}