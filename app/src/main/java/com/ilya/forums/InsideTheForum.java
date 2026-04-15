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

import com.google.firebase.auth.FirebaseAuth;
import com.ilya.forums.adapters.PostAdapter;
import com.ilya.forums.model.Forum;
import com.ilya.forums.model.Post;
import com.ilya.forums.model.User;
import com.ilya.forums.services.DatabaseService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InsideTheForum extends AppCompatActivity implements View.OnClickListener {

    TextView tvName, tvDate, tvDescription;
    private FirebaseAuth mAuth;

    Button btnBack, btnNewPost;
    ArrayList<Post> postArrayList=new ArrayList<>();
    DatabaseService databaseService;
    RecyclerView rvPostOfForum;
    PostAdapter postAdapter;
    String forumId="",forumName="";
    Forum currentForum;

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
        mAuth = FirebaseAuth.getInstance();
        Intent goToForum = getIntent();
        forumId = goToForum.getStringExtra("ForumId");
        databaseService.getForum(forumId,  new DatabaseService.DatabaseCallback<Forum>() {


            @Override
            public void onCompleted(Forum object) {
                currentForum = object;
                forumName=object.getName();
                if (currentForum.getCreatedAt() != null) {
                    // Choose how you want the date to look.
                    // "dd/MM/yyyy HH:mm" will look like: 25/10/2023 14:30
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                    // Convert the Date object to a String
                    String formattedDate = sdf.format(currentForum.getCreatedAt());

                    // Set the String into the TextView
                    tvDate.setText(formattedDate);
                }

                tvDescription.setText(currentForum.getDescription());
                tvName.setText(currentForum.getName());
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("InsideTheForum", "Failed to load forum: " + e.getMessage());
            }
        });
        btnNewPost=findViewById(R.id.btnNewPost);
        btnNewPost.setOnClickListener(this);
        btnBack=findViewById(R.id.btnBackFromInsideForum);
        btnBack.setOnClickListener(this);


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



        readPosts(forumId);
        rvPostOfForum.setAdapter(postAdapter);

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
            Intent goAddPost = new Intent(InsideTheForum.this, CreateNewPost.class);
            goAddPost.putExtra("forumId",forumId);
            startActivity(goAddPost);
        }
        if (view==btnBack){
            Intent goBack = new Intent(InsideTheForum.this, UserMain.class);
            startActivity(goBack);
        }
    }
}