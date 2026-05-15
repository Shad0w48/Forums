package com.ilya.forums;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.ilya.forums.model.Post;
import com.ilya.forums.model.User;
import com.ilya.forums.services.DatabaseService;
import com.ilya.forums.utils.ImageUtil;

import java.util.Date;

public class CreateNewPost extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Create Post";

    private DatabaseService databaseService;
    private FirebaseAuth mAuth;
    User currentUser;

    TextView tvCreateNewPost;
    String userId, title, description;
    private EditText etPostTitle, etPostInfo;
    private Button btnGallery, btnTakePic, btnAddPost;
    private MaterialButton btnRemovePhoto;
    private ImageView imgNewPost;

    // Step 4 variable: Keep track if the user actually selected an image
    private boolean isImageSelected = false;

    private Button btnBack;
    private String forumId = "";
    int SELECT_PICTURE = 200;

    private ActivityResultLauncher<Intent> captureImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_new_post);

        // Standard setup
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService = DatabaseService.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        forumId = getIntent().getStringExtra("forumId");

        // UI References
        tvCreateNewPost = findViewById(R.id.tvNewPostTitle);
        btnAddPost = findViewById(R.id.btnAddNewPost);
        etPostTitle = findViewById(R.id.etNewPostTitle);
        etPostInfo = findViewById(R.id.etNewPostInfo);
        btnGallery = findViewById(R.id.btnGallery);
        btnTakePic = findViewById(R.id.btnTakePic);
        imgNewPost = findViewById(R.id.imgNewPost);
        btnRemovePhoto = findViewById(R.id.btnRemovePhoto);
        btnBack = findViewById(R.id.btnback6); // Make sure this matches your XML ID

        // Click Listeners
        btnAddPost.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
        btnTakePic.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnRemovePhoto.setOnClickListener(v -> removeSelectedImage());

        // Step 3 (Camera): Update UI state after photo is taken
        captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        imgNewPost.setImageBitmap(bitmap);
                        applySelectedImageState(); // Switch UI from placeholder to real image
                    }
                });

        // Get user info
        databaseService.getUser(userId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                currentUser = new User(user.getId(), user.getFname(), user.getLname());
            }
            @Override
            public void onFailed(Exception e) {}
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnBack) {
            Intent goBack = new Intent(CreateNewPost.this, InsideTheForum.class);
            goBack.putExtra("ForumId", forumId);
            startActivity(goBack);
            return;
        }

        if (v == btnGallery) {
            selectImageFromGallery();
            return;
        }

        if (v == btnTakePic) {
            captureImageFromCamera();
            return;
        }

        if (v == btnAddPost) {
            publishPost();
        }
    }

    private void publishPost() {
        title = etPostTitle.getText().toString();
        description = etPostInfo.getText().toString();

        // Step 4 logic: Only convert image if one was actually selected
        String imagePic = "";
        if (isImageSelected) {
            imagePic = ImageUtil.convertTo64Base(imgNewPost);
        }

        String postId = databaseService.generatePostId();
        Date currentDate = new Date();
        Post newPost = new Post(postId, title, description, currentUser, currentDate, forumId, imagePic);

        databaseService.createNewPost(newPost, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                startActivity(new Intent(CreateNewPost.this, UserMain.class));
                finish();
            }
            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Error: " + e.toString());
            }
        });
    }

    // Step 3 logic: Make the image look "real" and show remove button
    private void applySelectedImageState() {
        isImageSelected = true;
        imgNewPost.setAlpha(1.0f); // Solid photo
        imgNewPost.setScaleType(ImageView.ScaleType.CENTER_CROP); // Fill the box
        btnRemovePhoto.setVisibility(View.VISIBLE);
    }

    private void removeSelectedImage() {
        isImageSelected = false;
        imgNewPost.setImageResource(R.drawable.logo100); // Placeholder
        imgNewPost.setAlpha(0.4f); // Watermark style
        imgNewPost.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        btnRemovePhoto.setVisibility(View.GONE);
    }

    private void selectImageFromGallery() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    private void captureImageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureImageLauncher.launch(takePictureIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                imgNewPost.setImageURI(selectedImageUri);
                applySelectedImageState(); // Step 3 Switch
            }
        }
    }
}