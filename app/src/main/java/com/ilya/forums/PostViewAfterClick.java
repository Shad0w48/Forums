package com.ilya.forums;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ilya.forums.model.Post;

public class PostViewAfterClick extends AppCompatActivity implements View.OnClickListener {


    Button btnBack,btnUp,btnDown,btnGoAddComment;
    ImageView img;
    TextView tvTitle, tvContent,tvForumUser,tvTime,tvNumberOfVotes;


    int up,down;

    Post thePost=null;
    String forumName;

    public Dialog dialogAddComment;
    public Button btnAdd, btnCancel;
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
        thePost= (Post) goToPostViewing.getSerializableExtra("post");

        forumName= goToPostViewing.getStringExtra("forumName");


        img=findViewById(R.id.imgViewingPost);

        tvTitle=findViewById(R.id.tvTitleViewingPost);
        tvContent=findViewById(R.id.tvContentViewingPost);
        tvForumUser=findViewById(R.id.tvForumAndUserViewingPost);
        tvTime=findViewById(R.id.tvViewingPostTime);
        tvNumberOfVotes=findViewById(R.id.tvPostVotesCount);
        btnUp=findViewById(R.id.btnPostUpVote);
        btnDown=findViewById(R.id.btnPostDownVote);
        btnBack=findViewById(R.id.btnViewingBackToMain);
        btnBack.setOnClickListener(this);
        btnUp.setOnClickListener(this);
        btnDown.setOnClickListener(this);
        btnGoAddComment=findViewById(R.id.btnGOAddComment);
        btnGoAddComment.setOnClickListener(this);



        if(thePost!=null) {
            tvTitle.setText(thePost.getTitle());
            tvContent.setText(thePost.getContent());
            tvForumUser.setText(thePost.getUser().getFname() + "/" +forumName );
            tvTime.setText(thePost.getTimestamp());
            up= thePost.getUpVote();
            down=thePost.getDownVote();
            tvNumberOfVotes.setText("number of votes:"+(up-down));
           // img.setImageResource(thePost.getPostPic());


        }


    }

    @Override
    public void onClick(View v) {
        if (v == btnBack) {
            Intent GoBack= new Intent(PostViewAfterClick.this, UserMain.class);
            startActivity(GoBack);
        }
        if(v==btnUp) {
            up++;
        }
        if(v==btnDown){
            down++;
        }
        if(v==btnGoAddComment){

             dialogAddComment = new Dialog(PostViewAfterClick.this);
            dialogAddComment.requestWindowFeature(Window.FEATURE_NO_TITLE); // בלי כותרת
            dialog.setContentView(R.layout.my_dialog);

            EditText etName = dialog.findViewById(R.id.etName);
            Button btnClose = dialog.findViewById(R.id.btnClose);

            btnClose.setOnClickListener(v -> {
                String name = etName.getText().toString();
                Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            dialog.show();

        });

        }

    }
}