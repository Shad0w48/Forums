package com.ilya.forums;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.ilya.forums.model.Forum;
import com.ilya.forums.model.Post;
import com.ilya.forums.model.User;
import com.ilya.forums.services.DatabaseService;

import java.util.ArrayList;

public class CreateNewForum extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Create Forum";
    TextView tvCreateForumtitle;
    Button btnCreateForum;
    EditText etTitle,etContent;



    String userId, title, description,forumId;

    Timestamp timestamp;
    private DatabaseService databaseService;
    private FirebaseAuth mAuth;
    User currentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_new_forum);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService=DatabaseService.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId=mAuth.getUid();

        databaseService.getUser(userId,  new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                currentUser= new User (user.getId(), user.getFname(), user.getLname());

            }

            @Override
            public void onFailed(Exception e) {

            }
        });


        tvCreateForumtitle=findViewById(R.id.tvCreateForum);
        btnCreateForum=findViewById(R.id.btnCreateForum);
        etTitle=findViewById(R.id.etForumTitle);
        etContent=findViewById(R.id.etForumDescription);

        btnCreateForum.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        timestamp = Timestamp.now();


        title = etTitle.getText().toString();
        description = etContent.getText().toString();
        /// Validate input
        Log.d(TAG, "onClick: Registering user...");
        String forumId=databaseService.generateForumId()    ;

        Forum newForum=new Forum(forumId,title,description,currentUser,new ArrayList<>(),timestamp.toString());

        databaseService.createNewForum(newForum, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {

            }

            @Override
            public void onFailed(Exception e) {

            }
        });


        }

}