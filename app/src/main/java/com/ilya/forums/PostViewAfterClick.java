package com.ilya.forums;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ilya.forums.adapters.CommentAdapter;
import com.ilya.forums.model.Comment;
import com.ilya.forums.model.Post;
import com.ilya.forums.model.User;
import com.ilya.forums.services.DatabaseService;
import com.ilya.forums.utils.ImageUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostViewAfterClick extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Viewing the post";

    CommentAdapter commentAdapter;
    private FirebaseAuth mAuth;
    private DatabaseService databaseService;

    Button btnBack,btnUp,btnDown,btnGoAddComment;
    ImageView img;
    TextView tvTitle, tvContent,tvForumUser,tvTime,tvNumberOfVotes;

    RecyclerView rvComments;
    ArrayList<Comment> commentArrayList=new ArrayList<>();

    User postCreator, currentUser;

    int up,down;
    // 0 = no vote, 1 = upvoted, -1 = downvoted
    int currentVoteState = 0;

    Post thePost=null;
    String forumName, userId;

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

        mAuth = FirebaseAuth.getInstance();
        databaseService = DatabaseService.getInstance();
        userId = mAuth.getCurrentUser().getUid();


        databaseService.getUser(userId,  new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {

                currentUser=user;

            }

            @Override
            public void onFailed(Exception e) {

            }
        });


        databaseService=DatabaseService.getInstance();

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
        rvComments = findViewById(R.id.rvPostComment);
        rvComments.setLayoutManager(new LinearLayoutManager(this)); // This tells it to list items vertically

        commentAdapter= new CommentAdapter(commentArrayList);



        if(thePost!=null) {
            postCreator=thePost.getUser();
            tvTitle.setText(thePost.getTitle());
            tvContent.setText(thePost.getContent());
            tvForumUser.setText( forumName+ "/" + thePost.getUser().getFname() );
            if (thePost.getDate() != null) {
                // Choose how you want the date to look.
                // "dd/MM/yyyy HH:mm" will look like: 25/10/2023 14:30
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                // Convert the Date object to a String
                String formattedDate = sdf.format(thePost.getDate());

                // Set the String into the TextView
                tvTime.setText(formattedDate);
            }

            up= thePost.getUpVote();
            down=thePost.getDownVote();
            updateVoteText(); // Helper method to update text
            //tvNumberOfVotes.setText("votes:"+(up-down));



            // We get the Base64 string from the post
            String base64String = thePost.getPostPic();

            // Check if the string exists and is not empty
            if (base64String != null && !base64String.trim().isEmpty()) {

                // 1. Convert the string into bytes
                byte[] decodedString = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT);

                // 2. Convert the bytes into a Bitmap
                android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                // 3. Set the Bitmap to your ImageView
                img.setImageBitmap(decodedByte);

                // 4. MAKE SURE IT IS VISIBLE (Just in case it was hidden previously)
                img.setVisibility(View.VISIBLE);

            } else {
                // THERE IS NO IMAGE!
                // Completely remove the ImageView from the screen to close the gap:
                img.setVisibility(View.GONE);
            }

            DatabaseService.getInstance().getCommentList(thePost.getPostId(), new DatabaseService.DatabaseCallback<List<Comment>>() {
                @Override
                public void onCompleted(List<Comment> comments) {
                    // 3. When data arrives, give it to the adapter and show it!
                    if(comments != null) {
                        commentAdapter = new CommentAdapter(comments);
                        rvComments.setAdapter(commentAdapter);
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    Log.d(TAG, "Error"+e.toString());
                }
            });

        }
        else {
            // This will tell you exactly what's wrong in your Logcat
            Log.e(TAG, "ERROR: Post object was null! Check your Intent keys.");
            Toast.makeText(this, "Error loading post", Toast.LENGTH_SHORT).show();
            finish(); // This is why you might be getting sent back
        }



    }

    // A helper method so you don't repeat this line multiple times
    private void updateVoteText() {
        tvNumberOfVotes.setText("number of votes:" + (up - down));
    }

    private void AddCommentDialog() {
        // Building the AlertDialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        // 2. Inflate the custom layout (addcomment.xml)
        View dialogView = getLayoutInflater().inflate(R.layout.addcomment, null);
        builder.setView(dialogView);

        // 3. Create and show the dialog
        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        // 4. Handle the views inside the dialog
        // these views belong to addcomment.xml, so we MUST use dialogView.findViewById()
        EditText EtCommentContent = dialogView.findViewById(R.id.EtCommentContent);
        Button btnSubmit = dialogView.findViewById(R.id.btnAddTheComment);  // The first button in addcomment.xml
        Button btnCancel = dialogView.findViewById(R.id.btnCancelComment); // The second button in addcomment.xml

        // Set what happens when they click Submit
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentContent = EtCommentContent.getText().toString();
                String commentId = databaseService.generateCommentId();
                Date currentDate= new Date();
                String postID = thePost.getPostId();
                Comment newComment = new Comment(commentId,currentDate,commentContent,postID,currentUser);
                databaseService.createNewComment(newComment, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void object) {
                        // נניח ששלפת את יוצר הפוסט (creator) מהדאטה-בייס:
                        String tokenOfPostOwner = postCreator.getFcmToken();
                        String myName = currentUser.getFname(); // השם שלך (המגיב)

                        triggerNotification(
                                tokenOfPostOwner,
                                "תגובה חדשה!",
                                myName + " הגיב על הפוסט שלך"
                        );



                    }

                    @Override
                    public void onFailed(Exception e) {

                            Log.d(TAG, "Error from dialog"+e.toString());
                    }

                });


                Toast.makeText(PostViewAfterClick.this, "comment added", Toast.LENGTH_SHORT).show();

                // Close the dialog when done
                dialog.dismiss();
            }
        });

        // Set what happens when they click Cancel
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Just close the dialog
                dialog.dismiss();
            }
        });
    }
    @Override
    public void onClick(View v) {
        if (v == btnBack) {
            Intent GoBack= new Intent(PostViewAfterClick.this, UserMain.class);
            startActivity(GoBack);
        }
        else if (v == btnUp) {
            if (currentVoteState == 1) {
                // User already upvoted, clicking again cancels their upvote
                up--;
                currentVoteState = 0;
            } else if (currentVoteState == -1) {
                // User had a downvote, changing to an upvote
                down--;
                up++;
                currentVoteState = 1;
            } else {
                // User had no vote, adding upvote
                up++;
                currentVoteState = 1;
            }
            updateVoteText();
        }
        else if (v == btnDown) {
            if (currentVoteState == -1) {
                // User already downvoted, clicking again cancels their downvote
                down--;
                currentVoteState = 0;
            } else if (currentVoteState == 1) {
                // User had an upvote, changing to a downvote
                up--;
                down++;
                currentVoteState = -1;
            } else {
                // User had no vote, adding downvote
                down++;
                currentVoteState = -1;
            }
            updateVoteText();
        }

        if(v==btnGoAddComment){
            AddCommentDialog();


        };

        }
    /**
     * Call this method when a comment is successfully posted.
     * @param targetToken The FCM token of the user who created the post.
     * @param title The title of the notification (e.g., "New Comment!")
     * @param message The body of the notification (e.g., "Ilya replied to your post")
     */
    public void sendPushNotification(String targetToken, String title, String message) {
        if (targetToken == null || targetToken.isEmpty()) {
            return; // Don't try to send if the user has no token
        }

        // Create a new request in a "NotificationRequests" node
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("NotificationRequests").push();

        HashMap<String, String> requestData = new HashMap<>();
        requestData.put("token", targetToken);
        requestData.put("title", title);
        requestData.put("message", message);

        // Save it to the database
        ref.setValue(requestData);
    }
    private void triggerNotification(String targetToken, String title, String message) {
        // בדיקה שיוצר הפוסט אכן מחובר ויש לו טוקן
        if (targetToken == null || targetToken.isEmpty()) {
            android.util.Log.w("NOTIF_DEBUG", "Target token is empty, skipping...");
            return;
        }

        // התחברות לתיקייה המיוחדת שהשרת מקשיב לה
        com.google.firebase.database.DatabaseReference ref =
                com.google.firebase.database.FirebaseDatabase.getInstance()
                        .getReference("NotificationRequests")
                        .push(); // יצירת מזהה ייחודי לבקשה

        // הכנת הנתונים עבור השרת
        java.util.HashMap<String, String> notificationMap = new java.util.HashMap<>();
        notificationMap.put("token", targetToken);
        notificationMap.put("title", title);
        notificationMap.put("message", message);

        // כתיבה ל-DB - ברגע שזה קורה, השרת שוכר העלית שולח את ה-Push!
        ref.setValue(notificationMap);
    }
    }
