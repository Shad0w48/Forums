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
    private ImageView ivIPic;

    private Button btnBack;
    Timestamp timestamp;


    /// Activity result launcher for capturing image from camera
    private ActivityResultLauncher<Intent> captureImageLauncher;


    // constant to compare
    // the activity result code
    int SELECT_PICTURE = 200;
    private String forumId="";

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

        forumId = getIntent().getStringExtra("forumId");

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
        btnGallery = findViewById(R.id.btnGallery);
        btnTakePic = findViewById(R.id.btnTakePic);

        ivIPic = findViewById(R.id.imgNewPost);


        /// register the activity result launcher for capturing image from camera
        captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        ivIPic.setImageBitmap(bitmap);
                    }
                });


        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();


            }
        });

        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImageFromCamera();

            }
        });


    }

    @Override
    public void onClick(View v) {

        timestamp = Timestamp.now();

        String imagePic = ImageUtil.convertTo64Base(ivIPic);
        title = etPostTitle.getText().toString();
        description = etPostInfo.getText().toString();
        /// Validate input
        Log.d(TAG, "onClick: Registering user...");
        String postId=databaseService.generatePostId()    ;
        Post newPost=new Post(postId,title,description,currentUser,timestamp.toString(),forumId,imagePic);

        databaseService.createNewPost(newPost, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Log.d(TAG, "Post Added");
                Intent go=new Intent(CreateNewPost.this,UserMain.class);
                startActivity(go);

            }

            @Override
            public void onFailed(Exception e) {
                Log.d(TAG, "Error"+e.toString());

            }
        });


    }












/// select image from gallery
private void selectImageFromGallery() {

    imageChooser();
}

/// capture image from camera
private void captureImageFromCamera() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    captureImageLauncher.launch(takePictureIntent);
}


void imageChooser() {

    // create an instance of the
    // intent of the type image
    Intent i = new Intent();
    i.setType("image/*");
    i.setAction(Intent.ACTION_GET_CONTENT);

    // pass the constant to compare it
    // with the returned requestCode
    startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
}

// this function is triggered when user
// selects the image from the imageChooser
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode == RESULT_OK) {

        // compare the resultCode with the
        // SELECT_PICTURE constant
        if (requestCode == SELECT_PICTURE) {
            // Get the url of the image from data
            Uri selectedImageUri = data.getData();
            if (null != selectedImageUri) {
                // update the preview image in the layout
                ivIPic.setImageURI(selectedImageUri);
            }
        }
    }
}


}






