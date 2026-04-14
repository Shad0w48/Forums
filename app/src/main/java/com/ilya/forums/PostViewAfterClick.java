package com.ilya.forums;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.ilya.forums.services.DatabaseService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostViewAfterClick extends AppCompatActivity implements View.OnClickListener {

   DatabaseService databaseService;
    private static final String TAG = "Viewing the post";

    CommentAdapter commentAdapter;
    Button btnBack,btnUp,btnDown,btnGoAddComment;
    ImageView img;
    TextView tvTitle, tvContent,tvForumUser,tvTime,tvNumberOfVotes;

    RecyclerView rvComments;
    ArrayList<Comment> commentArrayList=new ArrayList<>();


    int up,down;
    // 0 = no vote, 1 = upvoted, -1 = downvoted
    int currentVoteState = 0;

    Post thePost=null;
    String forumName;

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
           // img.setImageResource(thePost.getPostPic());
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
                Comment newComment = new Comment(commentId,currentDate,commentContent,postID,thePost.getUser());
                databaseService.createNewComment(newComment, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void object) {



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

    }
