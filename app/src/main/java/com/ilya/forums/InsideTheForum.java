package com.ilya.forums;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.ilya.forums.adapters.PostAdapter;
import com.ilya.forums.model.Forum;
import com.ilya.forums.model.Post;
import com.ilya.forums.services.DatabaseService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InsideTheForum extends AppCompatActivity implements View.OnClickListener {

    private TextView tvName, tvDate, tvDescription;
    private EditText etSearchPosts;
    private ImageButton btnBack; // Changed to ImageButton to match upgraded XML
    private MaterialButton btnNewPost; // Changed to MaterialButton

    private ArrayList<Post> postArrayList = new ArrayList<>();
    private DatabaseService databaseService;
    private RecyclerView rvPostOfForum;
    private PostAdapter postAdapter;
    private String forumId = "", forumName = "";
    private Forum currentForum;

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

        // 1. Initialize Views with new IDs
        tvDate = findViewById(R.id.tvDateOfChosenForum);
        tvDescription = findViewById(R.id.tvDescriptionOfChosenForum);
        tvName = findViewById(R.id.tvNameOfChosenForum);
        etSearchPosts = findViewById(R.id.etSearchPosts);
        btnNewPost = findViewById(R.id.btnNewPost);
        btnBack = findViewById(R.id.btnBackFromInsideForum);
        rvPostOfForum = findViewById(R.id.rvPostOfForum);

        databaseService = DatabaseService.getInstance();
        rvPostOfForum.setLayoutManager(new LinearLayoutManager(this));

        // 2. Click Listeners
        btnNewPost.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        // 3. Get Forum Data
        forumId = getIntent().getStringExtra("ForumId");
        if (forumId == null) {
            Toast.makeText(this, "Forum not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadForumHeader();
        setupSearch();
        readPosts(forumId);
    }

    private void loadForumHeader() {
        databaseService.getForum(forumId, new DatabaseService.DatabaseCallback<Forum>() {
            @Override
            public void onCompleted(Forum object) {
                currentForum = object;
                forumName = object.getName();
                if (currentForum.getCreatedAt() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    tvDate.setText(sdf.format(currentForum.getCreatedAt()));
                }
                tvDescription.setText(currentForum.getDescription());
                tvName.setText(currentForum.getName());
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("InsideTheForum", "Failed to load forum: " + e.getMessage());
            }
        });
    }

    private void setupSearch() {
        etSearchPosts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (postAdapter != null) {
                    postAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void readPosts(String forumId) {
        databaseService.getPostList(forumId, new DatabaseService.DatabaseCallback<List<Post>>() {
            @Override
            public void onCompleted(List<Post> postList) {
                postArrayList.clear();
                postArrayList.addAll(postList);

                // Re-initialize adapter with the fresh list to refresh the search copy (postListFull)
                postAdapter = new PostAdapter(postArrayList, new PostAdapter.OnPostClickListener() {
                    @Override
                    public void onPostClick(Post post) {
                        // FIX: By using the 'post' object passed directly from the click,
                        // we avoid index mismatches after searching.
                        Intent goToPostViewing = new Intent(InsideTheForum.this, PostViewAfterClick.class);
                        goToPostViewing.putExtra("post", post);
                        goToPostViewing.putExtra("forumName", forumName);
                        startActivity(goToPostViewing);
                    }

                    @Override
                    public void onLongPostClick(Post post) {}
                });

                rvPostOfForum.setAdapter(postAdapter);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("InsideTheForum", "Post error: " + e);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == btnNewPost) {
            Intent goAddPost = new Intent(InsideTheForum.this, CreateNewPost.class);
            goAddPost.putExtra("forumId", forumId);
            startActivity(goAddPost);
        } else if (view == btnBack) {
            finish(); // Use finish() to simply return to the previous screen
        }
    }
}