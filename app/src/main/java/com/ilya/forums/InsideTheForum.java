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
import com.ilya.forums.adapters.PostAdapter;
import com.ilya.forums.model.Forum;
import com.ilya.forums.model.Post;
import com.ilya.forums.services.DatabaseService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections; // Added this import for Collections.reverse()
import java.util.List;
import java.util.Locale;

public class InsideTheForum extends AppCompatActivity implements View.OnClickListener {

    // --- UI Variables ---
    private TextView tvName, tvDate, tvDescription;
    private EditText etSearchPosts;
    private ImageButton btnBack;
    private MaterialButton btnNewPost;

    // NEW: The Sort button we added to the XML
    private ImageButton btnSortToggle;

    // --- Data Variables ---
    private ArrayList<Post> postArrayList = new ArrayList<>();
    private DatabaseService databaseService;
    private RecyclerView rvPostOfForum;
    private PostAdapter postAdapter;
    private String forumId = "", forumName = "";
    private Forum currentForum;

    // NEW: A boolean flag to remember our current sorting state.
    // Firebase defaults to Oldest First, so we start with isNewestFirst = false.
    private boolean isNewestFirst = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // EdgeToEdge makes the app draw behind the system status bars for a modern look
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inside_the_forum);

        // Adjusts padding so UI elements don't hide behind the phone's notch or navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Initialize Views by linking them to the XML IDs
        tvDate = findViewById(R.id.tvDateOfChosenForum);
        tvDescription = findViewById(R.id.tvDescriptionOfChosenForum);
        tvName = findViewById(R.id.tvNameOfChosenForum);
        etSearchPosts = findViewById(R.id.etSearchPosts);
        btnNewPost = findViewById(R.id.btnNewPost);
        btnBack = findViewById(R.id.btnBackFromInsideForum);
        rvPostOfForum = findViewById(R.id.rvPostOfForum);

        // NEW: Link the new Sort Button
        btnSortToggle = findViewById(R.id.btnSortToggle);

        // Initialize our Singleton Database Service
        databaseService = DatabaseService.getInstance();

        // Tell the RecyclerView to draw items in a vertical, linear list
        rvPostOfForum.setLayoutManager(new LinearLayoutManager(this));

        // 2. Attach Standard Click Listeners
        btnNewPost.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        // --- NEW: SORT BUTTON LOGIC ---
        btnSortToggle.setOnClickListener(v -> {
            // Prevent crashing or empty actions if the list hasn't loaded yet
            if (postArrayList == null || postArrayList.isEmpty()) return;

            // Toggle the boolean state (if false, make it true. If true, make it false)
            isNewestFirst = !isNewestFirst;

            // Animate the button to spin!
            // If isNewestFirst is true, it rotates to 180 degrees. If false, it returns to 0.
            v.animate().rotation(isNewestFirst ? 180f : 0f).setDuration(300).start();

            // Mathematically reverse the items in our data list
            Collections.reverse(postArrayList);

            // Re-create the adapter with the newly sorted list.
            // We recreate it so the adapter's internal "Search Filter" copy also gets updated.
            setupAdapter();

            // Smoothly scroll the user back to the top of the list to see the changes
            rvPostOfForum.smoothScrollToPosition(0);
        });

        // 3. Get the Forum ID passed from the previous screen
        forumId = getIntent().getStringExtra("ForumId");
        if (forumId == null) {
            // If no ID was passed, show an error and close the activity to prevent a crash
            Toast.makeText(this, "Forum not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 4. Load the data
        loadForumHeader();
        setupSearch();
        readPosts(forumId);
    }

    /// Fetches the details of the forum (Name, Description, Date) to display at the top
    private void loadForumHeader() {
        databaseService.getForum(forumId, new DatabaseService.DatabaseCallback<Forum>() {
            @Override
            public void onCompleted(Forum object) {
                currentForum = object;
                forumName = object.getName();

                // Format the raw Date object into a readable string (e.g., "14/05/2026 15:30")
                if (currentForum.getCreatedAt() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    tvDate.setText(sdf.format(currentForum.getCreatedAt()));
                }

                // Update the Text Views with the fetched data
                tvDescription.setText(currentForum.getDescription());
                tvName.setText(currentForum.getName());
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("InsideTheForum", "Failed to load forum: " + e.getMessage());
            }
        });
    }

    /// Listens for the user typing in the search bar and filters the RecyclerView live
    private void setupSearch() {
        etSearchPosts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Whenever a letter is typed or deleted, pass the new text to the adapter
                if (postAdapter != null) {
                    postAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /// Fetches the list of posts inside this specific forum from Firebase
    private void readPosts(String forumId) {
        databaseService.getPostList(forumId, new DatabaseService.DatabaseCallback<List<Post>>() {
            @Override
            public void onCompleted(List<Post> postList) {
                // Clear out the old list to prevent duplicates when reloading
                postArrayList.clear();
                postArrayList.addAll(postList);

                // --- NEW: RESPECT SORT STATE ---
                // If the user has already clicked the "Newest First" button,
                // we need to make sure this fresh data from Firebase is reversed
                // before we hand it to the adapter.
                if (isNewestFirst) {
                    Collections.reverse(postArrayList);
                }

                // Build the adapter and attach it to the RecyclerView
                setupAdapter();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("InsideTheForum", "Post error: " + e);
            }
        });
    }

    /// Helper method to create and attach the PostAdapter.
    /// We extracted this so it can be used both when fetching data AND when sorting data.
    private void setupAdapter() {
        postAdapter = new PostAdapter(postArrayList, new PostAdapter.OnPostClickListener() {
            @Override
            public void onPostClick(Post post) {
                // When a post is clicked, navigate to PostViewAfterClick Activity
                // Pass the whole Post object and the Forum Name for display context
                Intent goToPostViewing = new Intent(InsideTheForum.this, PostViewAfterClick.class);
                goToPostViewing.putExtra("post", post);
                goToPostViewing.putExtra("forumName", forumName);
                startActivity(goToPostViewing);
            }

            @Override
            public void onLongPostClick(Post post) {
                // Optional: Handle long click on a post (e.g., delete or report)
            }
        });

        // Connect the adapter to the visual RecyclerView
        rvPostOfForum.setAdapter(postAdapter);
    }

    /// Handles standard button clicks for the activity (implements View.OnClickListener)
    @Override
    public void onClick(View view) {
        if (view == btnNewPost) {
            // Navigate to the screen to write a new post, passing the forumId so it saves correctly
            Intent goAddPost = new Intent(InsideTheForum.this, CreateNewPost.class);
            goAddPost.putExtra("forumId", forumId);
            startActivity(goAddPost);
        } else if (view == btnBack) {
            // Navigate back to the main user screen
            Intent goB = new Intent(InsideTheForum.this, UserMain.class);
            startActivity(goB);
        }
    }
}