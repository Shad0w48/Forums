package com.ilya.forums;

import android.os.Bundle;
import android.widget.Button; // אל תשכח את הייבוא הזה!
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;

public class Creds extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_creds);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_creds_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // הפעלת ה-GIF
        ImageView logoGif = findViewById(R.id.imgCredsLogo);
        if (logoGif != null) {
            Glide.with(this)
                    .asGif()
                    .load(R.drawable.gif100)
                    .into(logoGif);
        }

        // --- הגדרת כפתור החזרה ---
        Button btnBack = findViewById(R.id.btnBackFromCreds);
        btnBack.setOnClickListener(v -> {
            finish(); // פקודה זו סוגרת את המסך וחוזרת למסך הקודם
        });
    }
}