package com.ilya.forums;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ilya.forums.adapters.CommentAdapter;
import com.ilya.forums.model.Comment;
import com.ilya.forums.model.Post;
import com.ilya.forums.model.User;
import com.ilya.forums.services.DatabaseService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostViewAfterClick extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PostViewAfterClick";
    private CommentAdapter commentAdapter;
    private FirebaseAuth mAuth;
    private DatabaseService databaseService;

    // UI Elements
    private ImageButton btnBack;
    private MaterialButton btnGoAddComment;
    private ImageButton btnUp, btnDown;
    private ImageView img;
    private MaterialCardView cardPostImage;
    private TextView tvTitle, tvContent, tvForumUser, tvTime, tvNumberOfVotes;

    private RecyclerView rvComments;
    private User postCreator, currentUser;
    private ArrayList<Comment> commentList;

    // Logic Variables
    private int up, down;
    private int currentVoteState = 0;      // 0 = none, 1 = up, -1 = down
    private int initialVoteState = 0;      // Track original state fetched from DB
    private boolean isDataChanged = false; // Flag checking if we need to write to server
    private Post thePost = null;
    private String forumName, userId;

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

        // 1. Core Services Init
        mAuth = FirebaseAuth.getInstance();
        databaseService = DatabaseService.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        // 2. Intent Data
        Intent intent = getIntent();
        thePost = (Post) intent.getSerializableExtra("post");
        forumName = intent.getStringExtra("forumName");

        // 3. UI Initialization
        img = findViewById(R.id.imgViewingPost);
        cardPostImage = findViewById(R.id.cardPostImage);
        tvTitle = findViewById(R.id.tvTitleViewingPost);
        tvContent = findViewById(R.id.tvContentViewingPost);
        tvForumUser = findViewById(R.id.tvForumAndUserViewingPost);
        tvTime = findViewById(R.id.tvViewingPostTime);
        tvNumberOfVotes = findViewById(R.id.tvPostVotesCount);

        btnUp = findViewById(R.id.btnPostUpVote);
        btnDown = findViewById(R.id.btnPostDownVote);
        btnBack = findViewById(R.id.btnViewingBackToMain);
        btnGoAddComment = findViewById(R.id.btnGOAddComment);

        rvComments = findViewById(R.id.rvPostComment);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList);
        rvComments.setAdapter(commentAdapter);

        // 4. Listeners
        btnBack.setOnClickListener(this);
        btnUp.setOnClickListener(this);
        btnDown.setOnClickListener(this);
        btnGoAddComment.setOnClickListener(this);

        createNotificationChannel();

        if (thePost != null) {
            // 1. CACHE THE POST ID SAFELY
            final String safePostId = thePost.getPostId();

            // Initial display from intent data
            displayPostData();

            // FIX #1: Strictly use ONLY the real Forum ID. No more fallback to forumName!
            String targetForumKey = thePost.getForumId();

            if (targetForumKey != null && !targetForumKey.trim().isEmpty() && safePostId != null) {
                databaseService.getPost(targetForumKey, safePostId, new DatabaseService.DatabaseCallback<Post>() {
                    @Override
                    public void onCompleted(Post updatedPost) {
                        if (updatedPost != null) {
                            thePost = updatedPost;

                            if (thePost.getPostId() == null) {
                                thePost.setPostId(safePostId);
                            }

                            up = updatedPost.getUpVote();
                            down = updatedPost.getDownVote();
                            updateVoteText();
                        }

                        databaseService.getUserVote(safePostId, userId, new DatabaseService.DatabaseCallback<Integer>() {
                            @Override
                            public void onCompleted(Integer voteValue) {
                                currentVoteState = voteValue;
                                initialVoteState = voteValue;
                                updateVoteUI();
                            }
                            @Override
                            public void onFailed(Exception e) { Log.e(TAG, "Vote load failed", e); }
                        });
                    }
                    @Override
                    public void onFailed(Exception e) { Log.e(TAG, "Post refresh failed", e); }
                });
            }

            // Load user profile
            databaseService.getUser(userId, new DatabaseService.DatabaseCallback<User>() {
                @Override
                public void onCompleted(User user) { currentUser = user; }
                @Override
                public void onFailed(Exception e) {}
            });

        } else {
            Toast.makeText(this, "Error: Post data missing", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 5. Handle Back Navigation safely
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                syncVoteToFirebase();
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void displayPostData() {
        postCreator = thePost.getUser();
        tvTitle.setText(thePost.getTitle());
        tvContent.setText(thePost.getContent());
        tvForumUser.setText("f/" + forumName + " • u/" + (thePost.getUser() != null ? thePost.getUser().getFname() : "Anonymous"));

        if (thePost.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            tvTime.setText(sdf.format(thePost.getDate()));
        }

        up = thePost.getUpVote();
        down = thePost.getDownVote();
        updateVoteText();

        String base64String = thePost.getPostPic();
        if (base64String != null && !base64String.trim().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                img.setImageBitmap(decodedByte);
                cardPostImage.setVisibility(View.VISIBLE);
            } catch (Exception e) { cardPostImage.setVisibility(View.GONE); }
        } else {
            cardPostImage.setVisibility(View.GONE);
        }

        // Fetch Comments
        databaseService.getCommentList(thePost.getPostId(), new DatabaseService.DatabaseCallback<List<Comment>>() {
            @Override
            public void onCompleted(List<Comment> comments) {
                if (comments != null) {
                    commentList.clear();
                    commentList.addAll(comments);
                    commentAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailed(Exception e) {}
        });
    }

    private void updateVoteText() {
        int sum = up - down;
        tvNumberOfVotes.setText(String.valueOf(sum));
    }

    private void updateVoteUI() {
        btnUp.setColorFilter(Color.parseColor("#5F6368"));
        btnDown.setColorFilter(Color.parseColor("#5F6368"));

        if (currentVoteState == 1) {
            btnUp.setColorFilter(Color.parseColor("#03A9F4"));
        } else if (currentVoteState == -1) {
            btnDown.setColorFilter(Color.parseColor("#F44336"));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnViewingBackToMain) {
            getOnBackPressedDispatcher().onBackPressed();
        }
        else if (id == R.id.btnPostUpVote) handleUpvote();
        else if (id == R.id.btnPostDownVote) handleDownvote();
        else if (id == R.id.btnGOAddComment) showAddCommentDialog();
    }

    private void handleUpvote() {
        if (currentVoteState == 1) {
            up--;
            currentVoteState = 0;
        } else if (currentVoteState == -1) {
            down--;
            up++;
            currentVoteState = 1;
        } else {
            up++;
            currentVoteState = 1;
        }

        isDataChanged = (currentVoteState != initialVoteState);
        updateVoteUI();
        updateVoteText();
    }

    private void handleDownvote() {
        if (currentVoteState == -1) {
            down--;
            currentVoteState = 0;
        } else if (currentVoteState == 1) {
            up--;
            down++;
            currentVoteState = -1;
        } else {
            down++;
            currentVoteState = -1;
        }

        isDataChanged = (currentVoteState != initialVoteState);
        updateVoteUI();
        updateVoteText();
    }

    private void syncVoteToFirebase() {
        if (!isDataChanged || thePost == null || thePost.getPostId() == null) {
            return;
        }

        isDataChanged = false;

        thePost.setUpVote(up);
        thePost.setDownVote(down);

        // FIX #2: Strictly use ONLY the real Forum ID.
        String targetForumKey = thePost.getForumId();

        if (targetForumKey != null && !targetForumKey.trim().isEmpty()) {
            DatabaseReference postRef = FirebaseDatabase.getInstance()
                    .getReference("forums_posts")
                    .child(targetForumKey)
                    .child(thePost.getPostId());

            postRef.child("upVote").setValue(up);
            postRef.child("downVote").setValue(down);
        } else {
            Log.e(TAG, "Could not save global vote counts: Missing Forum ID. Skipping ghost folder creation.");
        }

        databaseService.performVote(
                thePost.getPostId(),
                userId,
                currentVoteState,
                initialVoteState,
                new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void object) {
                        initialVoteState = currentVoteState;
                    }
                    @Override
                    public void onFailed(Exception e) {}
                }
        );
    }



    @Override
    protected void onStop() {
        super.onStop();
        syncVoteToFirebase();
    }

    private void showAddCommentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.addcomment, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        EditText etContent = dialogView.findViewById(R.id.etCommentContent);
        dialogView.findViewById(R.id.btnAddTheComment).setOnClickListener(v -> {
            String content = etContent.getText().toString();

            Comment newComment = new Comment(databaseService.generateCommentId(), new Date(), content, thePost.getPostId(), currentUser);

            databaseService.createNewComment(newComment, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {

                    commentList.add(newComment);
                    commentAdapter.notifyDataSetChanged();

                    // FIX #4: Strictly use ONLY the real Forum ID.
                    String targetForumKey = thePost.getForumId();

                    if (targetForumKey != null && !targetForumKey.trim().isEmpty()) {
                        DatabaseReference postRef = FirebaseDatabase.getInstance()
                                .getReference("forums_posts")
                                .child(targetForumKey)
                                .child(thePost.getPostId());

                        postRef.child("commentCount").setValue(com.google.firebase.database.ServerValue.increment(1));
                        thePost.setCommentCount(thePost.getCommentCount() + 1);
                    } else {
                        Log.e(TAG, "Cannot increment comment count: Missing Forum ID. Skipping ghost folder creation.");
                    }

                    if (postCreator != null) {
                        // Pass the ID instead of the token
                        triggerNotification(postCreator.getId(), "New Reply!", currentUser.getFname() + " replied to your post");
                    }
                }
                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(PostViewAfterClick.this, "Failed to add comment", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.btnCancelComment).setOnClickListener(v -> dialog.dismiss());
    }

    private void triggerNotification(String targetUserId, String title, String message) {
        if (targetUserId == null || targetUserId.isEmpty()) return;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("NotificationRequests").push();
        HashMap<String, String> map = new HashMap<>();

        // We send the ID now, and let our index.js do the hard work of finding the token!
        map.put("targetUserId", targetUserId);
        map.put("title", title);
        map.put("message", message);

        ref.setValue(map);
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel("default_channel_id", "Post Notifications", android.app.NotificationManager.IMPORTANCE_HIGH);
            android.app.NotificationManager manager = getSystemService(android.app.NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }
}