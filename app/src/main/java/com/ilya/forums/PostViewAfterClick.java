package com.ilya.forums;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PostViewAfterClick extends AppCompatActivity implements View.OnClickListener {

    String title,content,forumId, userId,time;

    Button btnBack;
    TextView tvTitle, tvContent,tvForumUser,tvTime;

    int Upvote, Downvote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_view_after_click);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent goToPostViewing = getIntent();
        title= goToPostViewing.getStringExtra("PostTitle");
        content=goToPostViewing.getStringExtra("PostContent");
        forumId=goToPostViewing.getStringExtra("PostForumId");
        userId=goToPostViewing.getStringExtra("PostUser");
        time = goToPostViewing.getStringExtra("PostTime");
        Upvote = goToPostViewing.getIntExtra("PostUpVote");

        tvTitle=findViewById(R.id.tvTitleViewingPost);
        tvContent=findViewById(R.id.tvContentViewingPost);
        tvForumUser=findViewById(R.id.tvForumAndUserViewingPost);
        tvTime=findViewById(R.id.tvViewingPostTime);
        tvTitle.setText(title);
        tvContent.setText(content);
        tvForumUser.setText(forumId+"/"+userId);
        tvTime.setText(time);
        btnBack=findViewById(R.id.btnViewingBackToMain);
        btnBack.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        Intent GoBack= new Intent(PostViewAfterClick.this, UserMain.class);
        startActivity(GoBack);
    }
}