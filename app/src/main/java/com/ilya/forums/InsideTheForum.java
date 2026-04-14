package com.ilya.forums;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ilya.forums.adapters.PostAdapter;
import com.ilya.forums.model.Forum;
import com.ilya.forums.model.Post;
import com.ilya.forums.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class InsideTheForum extends AppCompatActivity implements View.OnClickListener {

    TextView tvName, tvDate, tvDescription;
    Button btnNewPost;
    ArrayList<Post> postArrayList=new ArrayList<>();
    DatabaseService databaseService;
    RecyclerView rvPostOfForum;
    PostAdapter postAdapter;
    String forumId="",forumName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inside_the_forum);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvDate=findViewById(R.id.tvDateOfChosenForum);
        tvDescription=findViewById(R.id.tvDescriptionOfChosenForum);
        tvName=findViewById(R.id.tvNameOfChosenForum);
        databaseService=databaseService.getInstance();
        rvPostOfForum=findViewById(R.id.rvPostOfForum);
        rvPostOfForum.setLayoutManager(new LinearLayoutManager(this));

        rvPostOfForum.setAdapter(postAdapter);
        Intent goToForum = getIntent();
        forumId = goToForum.getStringExtra("ForumId");
        btnNewPost=findViewById(R.id.btnNewPost);
        btnNewPost.setOnClickListener(this);


        postAdapter = new PostAdapter(postArrayList, new PostAdapter.OnPostClickListener() {
            @Override
            public void onPostClick(Post post) {
                Log.d("TAG", "Post clicked: " + post.toString());

                Intent goToPostViewing = new Intent(InsideTheForum.this, PostViewAfterClick.class);

                goToPostViewing.putExtra("post",post);
                goToPostViewing.putExtra("forumName",forumName);

                startActivity(goToPostViewing);

            }

            @Override
            public void onLongPostClick(Post post) {

            }
        } );



        readPosts(forumName);
    }
    private void readPosts(String forumId) {
        databaseService.getPostList(forumId, new DatabaseService.DatabaseCallback<List<Post>>() {
            @Override
            public void onCompleted(List<Post> postList) {
                postArrayList.clear();


                postArrayList.addAll(postList);


                postAdapter.notifyDataSetChanged();

                rvPostOfForum.setAdapter(postAdapter);
                Log.d("TAG", "Post clicked: " + postArrayList.toString());


            }

            @Override
            public void onFailed(Exception e) {
                Log.d("Post", "Post error: " + e);

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view==btnNewPost){
            Intent goAddPost = new Intent(this, CreateNewPost.class);
            goAddPost.putExtra("forumId",forumId);
            startActivity(goAddPost);
        }
    }
}