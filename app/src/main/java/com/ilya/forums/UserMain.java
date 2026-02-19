package com.ilya.forums;

import static android.widget.Toast.LENGTH_LONG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.ilya.forums.adapters.ForumAdapter;
import com.ilya.forums.adapters.PostAdapter;
import com.ilya.forums.adapters.UserAdapter;
import com.ilya.forums.model.Forum;
import com.ilya.forums.model.Post;
import com.ilya.forums.model.User;
import com.ilya.forums.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class UserMain extends AppCompatActivity implements View.OnClickListener {
    private DatabaseService databaseService;
    private FirebaseAuth mAuth;
    String userId;
    Button btnToAdminPage,btnGoToAddPost;

    RecyclerView rvForum, rvLastPost;

    ArrayList<Forum> forumArrayList=new ArrayList<>();
    ArrayList<Post> postArrayList=new ArrayList<>();
    PostAdapter postAdapter;
    private ForumAdapter forumAdapter;
    String forumId, forumName="";



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
         rvForum = findViewById(R.id.rvForum);

         btnGoToAddPost=findViewById(R.id.btnCreatePost);
         btnGoToAddPost.setOnClickListener(this);
        databaseService = DatabaseService.getInstance();
        mAuth = FirebaseAuth.getInstance();
        btnToAdminPage=findViewById(R.id.btnToAdminPage2);
        btnToAdminPage.setOnClickListener(this);
        userId = mAuth.getCurrentUser().getUid();

        rvLastPost=findViewById(R.id.rvLastPost);
        rvLastPost.setLayoutManager(new LinearLayoutManager(this));
        databaseService.getUser(userId,  new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {

                if (user.getAdmin())
                    btnToAdminPage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });



        postAdapter = new PostAdapter(postArrayList, new PostAdapter.OnPostClickListener() {
            @Override
            public void onPostClick(Post post) {
                Log.d("TAG", "Post clicked: " + post.toString());

                Intent goToPostViewing = new Intent(UserMain.this, PostViewAfterClick.class);

                goToPostViewing.putExtra("post",post);
                goToPostViewing.putExtra("forumName",forumName);

                startActivity(goToPostViewing);

            }

            @Override
            public void onLongPostClick(Post post) {

            }
        } );



        rvLastPost.setAdapter(postAdapter);




        rvForum.setLayoutManager(new LinearLayoutManager(this));
        forumAdapter = new ForumAdapter(forumArrayList, new ForumAdapter.OnForumClickListener() {
            @Override
            public void onForumClick(Forum forum) {
                forumId=forum.getForumId();


               readPosts(forumId);
               forumName=forum.getName();



                Log.d("TAG", "Forum clicked: " + forumId);


            }

            @Override
            public void onLongForumClick(Forum forum) {

            }



        });
        rvForum.setAdapter(forumAdapter);



        databaseService.getForumList(new DatabaseService.DatabaseCallback<List<Forum>>() {
            @Override
            public void onCompleted(List<Forum> forums) {

              for(int i=0; i<forums.size();i++){

                  forumArrayList.add( new Forum(forums.get(i)));

              }




                forumAdapter.notifyDataSetChanged();
                Log.d("jkh",forums.toString());


            }



            @Override
            public void onFailed(Exception e) {


                Log.d("error",e.toString());
            }
        });





    }

    private void readPosts(String forumId) {
        databaseService.getPostList(forumId, new DatabaseService.DatabaseCallback<List<Post>>() {
            @Override
            public void onCompleted(List<Post> postList) {
                postArrayList.clear();


                postArrayList.addAll(postList);



                postAdapter.notifyDataSetChanged();

                rvLastPost.setAdapter(postAdapter);
                Log.d("TAG", "Post clicked: " + postArrayList.toString());


            }

            @Override
            public void onFailed(Exception e) {
                Log.d("Post", "Post error: " + e);

            }
        });

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onClick(View v) {
        if(v==btnToAdminPage){
            Intent go = new Intent(this, AdminActivity.class);
            startActivity(go);
        }
        if(v==btnGoToAddPost){
            Intent goAddPost = new Intent(this, CreateNewPost.class);
            goAddPost.putExtra("forumId",forumId);
            startActivity(goAddPost);
        }
    }
}