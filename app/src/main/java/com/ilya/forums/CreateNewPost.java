package com.ilya.forums;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ilya.forums.R;
import com.ilya.forums.model.Forum;
import com.ilya.forums.model.Post;
import com.ilya.forums.model.User;
import com.ilya.forums.services.DatabaseService;
import com.ilya.forums.utils.ImageUtil;

import java.util.ArrayList;

public class CreateNewPost extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Create Post";

    private DatabaseService databaseService;
    private FirebaseAuth mAuth;
    User currentUser;

    TextView tvCreateNewPost;
    String userId, title, description;
    private EditText etPostTitle, etPostInfo;
    private Button btnGallery, btnTakePic, btnAddPost;
    private ImageView imageView;

    private Button btnBack;
    Timestamp timestamp;


    /// Activity result launcher for capturing image from camera
    private ActivityResultLauncher<Intent> captureImageLauncher;


    // constant to compare
    // the activity result code
    int SELECT_PICTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_new_post);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });databaseService=DatabaseService.getInstance();
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


        tvCreateNewPost=findViewById(R.id.tvNewPostTitle);
        btnAddPost=findViewById(R.id.btnAddNewPost);
        etPostTitle=findViewById(R.id.etNewPostTitle);
        etPostInfo=findViewById(R.id.etNewPostInfo);

        btnAddPost.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        timestamp = Timestamp.now();


        title = etPostTitle.getText().toString();
        description = etPostInfo.getText().toString();
        /// Validate input
        Log.d(TAG, "onClick: Registering user...");
        String postId=databaseService.generatePostId()    ;
//    public Post(String postId, String title, String content, User user, String timestamp, String ForumId, String postPic) {
        Post newPost=new Post(postId,title,description,currentUser,timestamp.toString(),"5454","iuiu");

        databaseService.createNewPost(newPost, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {

            }

            @Override
            public void onFailed(Exception e) {

            }
        });


    }

}






