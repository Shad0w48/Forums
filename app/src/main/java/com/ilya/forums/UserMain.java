package com.ilya.forums;

import static android.widget.Toast.LENGTH_LONG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
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

public class  UserMain extends AppCompatActivity implements View.OnClickListener {
    private DatabaseService databaseService;
    private FirebaseAuth mAuth;
    String userId;
    Button btnToAdminPage,btnGoToEdit;

    RecyclerView rvForum, rvLastPost;

    ArrayList<Forum> forumArrayList=new ArrayList<>();
    ArrayList<Post> postArrayList=new ArrayList<>();
    PostAdapter postAdapter;
    private ForumAdapter forumAdapter;
    String forumId, forumName="";
    SearchView searchView;




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
         searchView = findViewById(R.id.searchViewForums);

         searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                forumAdapter.filter(newText); // This triggers the filter in ForumAdapter
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
        });

         btnGoToEdit=findViewById(R.id.btnEditProfileUser);
         btnGoToEdit.setOnClickListener(this);
        databaseService = DatabaseService.getInstance();
        mAuth = FirebaseAuth.getInstance();
        btnToAdminPage=findViewById(R.id.btnToAdminPage2);
        btnToAdminPage.setOnClickListener(this);
        userId = mAuth.getCurrentUser().getUid();


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








        rvForum.setLayoutManager(new LinearLayoutManager(this));
        forumAdapter = new ForumAdapter(forumArrayList, new ForumAdapter.OnForumClickListener() {
            @Override
            public void onForumClick(Forum forum) {
                if (forum == null) {
                    Log.e("TAG", "The forum object is NULL!");
                    return;
                }

                forumId = forum.getForumId();
                Log.d("TAG", "Moving to InsideTheForum with ID: " + forumId);

                Intent goToForum = new Intent(UserMain.this, InsideTheForum.class);
                goToForum.putExtra("ForumId", forumId);
                // הצעה: תעביר גם את השם ליתר ביטחון אם תרצה להציג אותו למעלה
                goToForum.putExtra("ForumName", forum.getName());
                startActivity(goToForum);
            }

            @Override
            public void onLongForumClick(Forum forum) {

            }



        });
        rvForum.setAdapter(forumAdapter);



        databaseService.getForumList(new DatabaseService.DatabaseCallback<List<Forum>>() {
            @Override
            public void onCompleted(List<Forum> forums) {

                if (forumAdapter != null && forums != null) {
                    forumAdapter.setForumList(forums); // זה מעדכן את האדפטר והגיבוי!
                }

                Log.d("gg", forums.toString());
            }

            @Override
            public void onFailed(Exception e) {
                Log.d("error", e.toString());
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
            go.putExtra("Own_USER_UID",userId);
            startActivity(go);
        }
        if(v==btnGoToEdit){
            Intent goEdit = new Intent(this, UserProfileActivity.class);
            goEdit.putExtra("USER_UID",userId);
            goEdit.putExtra("Own_USER_UID",userId);
            startActivity(goEdit);
        }
    }
}