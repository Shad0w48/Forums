package com.ilya.forums.services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ilya.forums.model.Forum;
import com.ilya.forums.model.User;
import com.ilya.forums.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;


/// a service to interact with the Firebase Realtime Database.
/// this class is a singleton, use getInstance() to get an instance of this class
/// @see #getInstance()
/// @see FirebaseDatabase
public class DatabaseService {

    /// tag for logging
    /// @see Log
    private static final String TAG = "DatabaseService";

    /// paths for different data types in the database
    /// @see DatabaseService#readData(String)
    private static final String USERS_PATH = "users",
                                POSTS_PATH = "forums_posts",
                                FORUMS_PATH = "forums";

    /// callback interface for database operations
    /// @param <T> the type of the object to return
    /// @see DatabaseCallback#onCompleted(Object)
    /// @see DatabaseCallback#onFailed(Exception)
    public interface DatabaseCallback<T> {
        /// called when the operation is completed successfully
        public void onCompleted(T object);

        /// called when the operation fails with an exception
        public void onFailed(Exception e);
    }

    /// the instance of this class
    /// @see #getInstance()
    private static DatabaseService instance;

    /// the reference to the database
    /// @see DatabaseReference
    /// @see FirebaseDatabase#getReference()
    private final DatabaseReference databaseReference;

    /// use getInstance() to get an instance of this class
    /// @see DatabaseService#getInstance()
    private DatabaseService() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    /// get an instance of this class
    /// @return an instance of this class
    /// @see DatabaseService
    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }


    // region private generic methods
    // to write and read data from the database

    /// write data to the database at a specific path
    /// @param path the path to write the data to
    /// @param data the data to write (can be any object, but must be serializable, i.e. must have a default constructor and all fields must have getters and setters)
    /// @param callback the callback to call when the operation is completed
    /// @see DatabaseCallback
    private void writeData(@NotNull final String path, @NotNull final Object data, final @Nullable DatabaseCallback<Void> callback) {
        readData(path).setValue(data, (error, ref) -> {
            if (error != null) {
                if (callback == null) return;
                callback.onFailed(error.toException());
            } else {
                if (callback == null) return;
                callback.onCompleted(null);
        }
    });
    }

    /// remove data from the database at a specific path
    /// @param path the path to remove the data from
    /// @param callback the callback to call when the operation is completed
    /// @see DatabaseCallback
    private void deleteData(@NotNull final String path, @Nullable final DatabaseCallback<Void> callback) {
        readData(path).removeValue((error, ref) -> {
            if (error != null) {
                if (callback == null) return;
                callback.onFailed(error.toException());
            } else {
                if (callback == null) return;
                callback.onCompleted(null);
        }
    });
    }

    /// read data from the database at a specific path
    /// @param path the path to read the data from
    /// @return a DatabaseReference object to read the data from
    /// @see DatabaseReference

    private DatabaseReference readData(@NotNull final String path) {
        return databaseReference.child(path);
    }


    /// get data from the database at a specific path
    /// @param path the path to get the data from
    /// @param clazz the class of the object to return
    /// @param callback the callback to call when the operation is completed
    /// @see DatabaseCallback
    /// @see Class
    private <T> void getData(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final DatabaseCallback<T> callback) {
        readData(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            T data = task.getResult().getValue(clazz);
            callback.onCompleted(data);
        });
    }

    /// get a list of data from the database at a specific path
    /// @param path the path to get the data from
    /// @param clazz the class of the objects to return
    /// @param callback the callback to call when the operation is completed
    private <T> void getDataList(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final DatabaseCallback<List<T>> callback) {
        readData(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            List<T> tList = new ArrayList<>();
            task.getResult().getChildren().forEach(dataSnapshot -> {
                T t = dataSnapshot.getValue(clazz);
                tList.add(t);
            });

            callback.onCompleted(tList);
        });
    }

    /// generate a new id for a new object in the database
    /// @param path the path to generate the id for
    /// @return a new id for the object
    /// @see String
    /// @see DatabaseReference#push()

    private String generateNewId(@NotNull final String path) {
        return databaseReference.child(path).push().getKey();
    }


    /// run a transaction on the data at a specific path </br>
    /// good for incrementing a value or modifying an object in the database
    /// @param path the path to run the transaction on
    /// @param clazz the class of the object to return
    /// @param function the function to apply to the current value of the data
    /// @param callback the callback to call when the operation is completed
    /// @see DatabaseReference#runTransaction(Transaction.Handler)
    private <T> void runTransaction(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull UnaryOperator<T> function, @NotNull final DatabaseCallback<T> callback) {
        readData(path).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                T currentValue = currentData.getValue(clazz);
                if (currentValue == null) {
                    currentValue = function.apply(null);
                } else {
                    currentValue = function.apply(currentValue);
                }
                currentData.setValue(currentValue);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    Log.e(TAG, "Transaction failed", error.toException());
                    callback.onFailed(error.toException());
                    return;
                }
                T result = currentData != null ? currentData.getValue(clazz) : null;
                callback.onCompleted(result);
            }
        });

    }

    // endregion of private methods for reading and writing data

    // public methods to interact with the database

    // region User Section

    /// generate a new id for a new user in the database
    /// @return a new id for the user
    /// @see #generateNewId(String)
    /// @see User
   // public String generateUserId() {
    //    return generateNewId(USERS_PATH);
   // }

    /// create a new user in the database
    /// @param user the user object to create (without the id, null)
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive new user id
    ///            if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see User
    public void createNewUser(@NotNull final User user,
                              @Nullable final DatabaseCallback<String> callback) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("TAG", "createUserWithEmail:success");
                    String uid = mAuth.getUid();
                    user.setId(uid);
                    writeData(USERS_PATH + "/" + uid, user, new DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void v) {
                            if (callback != null) callback.onCompleted(uid);
                        }

                        @Override
                        public void onFailed(Exception e) {
                            if (callback != null) callback.onFailed(e);
                        }
                    });
                } else {
                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                    if (callback != null)
                        callback.onFailed(task.getException());
                }
            });
    }

    /// Login with email and password
    /// @param email , password
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive String (user id)
    ///            if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see FirebaseAuth
    public void loginUser(@NotNull final String email,final String password,
                          @Nullable final DatabaseCallback<String> callback) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("TAG", "loginUserWithEmail:success");
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if (callback != null)
                        callback.onCompleted(uid);
                } else {
                    Log.w("TAG", "loginUserWithEmail:failure", task.getException());
                    if (callback != null)
                        callback.onFailed(task.getException());
                }
            });
    }




    /// get a user from the database
    /// @param uid the id of the user to get
    /// @param callback the callback to call when the operation is completed
    ///               the callback will receive the user object
    ///             if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see User
    public void getUser(@NotNull final String uid, @NotNull final DatabaseCallback<User> callback) {
        getData(USERS_PATH + "/" + uid, User.class, callback);
    }

    /// get all the users from the database
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive a list of user objects
    ///            if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see List
    /// @see User
    public void getUserList(@NotNull final DatabaseCallback<List<User>> callback) {
        getDataList(USERS_PATH, User.class, callback);
    }

    /// delete a user from the database
    /// @param uid the user id to delete
    /// @param callback the callback to call when the operation is completed
    public void deleteUser(@NotNull final String uid, @Nullable final DatabaseCallback<Void> callback) {
        deleteData(USERS_PATH + "/" + uid, callback);
    }

    /// get a user by email and password
    /// @param email the email of the user
    /// @param password the password of the user
    /// @param callback the callback to call when the operation is completed
    ///            the callback will receive the user object
    ///          if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see User
    public void getUserByEmailAndPassword(@NotNull final String email, @NotNull final String password, @NotNull final DatabaseCallback<User> callback) {
        readData(USERS_PATH).orderByChild("email").equalTo(email).get()
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "Error getting data", task.getException());
                    callback.onFailed(task.getException());
                    return;
                }
                if (task.getResult().getChildrenCount() == 0) {
                    callback.onFailed(new Exception("User not found"));
                    return;
                }
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user == null || !Objects.equals(user.getPassword(), password)) {
                        callback.onFailed(new Exception("Invalid email or password"));
                        return;
                    }

                    callback.onCompleted(user);
                    return;

                }
            });
    }



    /// check if an email already exists in the database
    /// @param email the email to check
    /// @param callback the callback to call when the operation is completed
    public void checkIfEmailExists(@NotNull final String email, @NotNull final DatabaseCallback<Boolean> callback) {
        readData(USERS_PATH).orderByChild("email").equalTo(email).get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Error getting data", task.getException());
                        callback.onFailed(task.getException());
                        return;
                    }
                    boolean exists = task.getResult().getChildrenCount() > 0;
                    callback.onCompleted(exists);
                });
    }

    public void updateUser(@NotNull final User user, @Nullable final DatabaseCallback<Void> callback) {
        runTransaction(USERS_PATH + "/" + user.getId(), User.class, currentUser -> user, new DatabaseCallback<User>() {
            @Override
            public void onCompleted(User object) {
                if (callback != null) {
                    callback.onCompleted(null);
                }
            }

            @Override
            public void onFailed(Exception e) {
                if (callback != null) {
                    callback.onFailed(e);
                }
            }
        });
    }


    // endregion User Section

    // region post section

    /// create a new post in the database
    /// @param post the post object to create
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive void
    ///             if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see Post
    public void createNewPost(@NotNull final Post post, @Nullable final DatabaseCallback<Void> callback) {
        writeData( POSTS_PATH + "/" + post.getForumId()+"/"+post.getPostId(), post, callback);
    }

    /// get a post from the database
    /// @param postId the id of the post to get
    /// @param callback the callback to call when the operation is completed
    ///               the callback will receive the post object
    ///              if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see Post
    public void getPost(@NotNull final String postId, @NotNull final DatabaseCallback<Post> callback) {
        getData(POSTS_PATH + "/" + postId, Post.class, callback);
    }

    /// get all the posts from the database
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive a list of post objects
    ///            if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see List
    /// @see Post
    public void getPostList(@NotNull final DatabaseCallback<List<Post>> callback) {
        getDataList(POSTS_PATH, Post.class, callback);
    }

    /// generate a new id for a new post in the database
    /// @return a new id for the post
    /// @see #generateNewId(String)
    /// @see Post
    public String generatePostId() {
        return generateNewId(POSTS_PATH);
    }

    /// delete a post from the database
    /// @param postId the id of the post to delete
    /// @param callback the callback to call when the operation is completed
    public void deletePost(@NotNull final String postId, @Nullable final DatabaseCallback<Void> callback) {
        deleteData(POSTS_PATH + "/" + postId, callback);
    }

    // endregion post section

    // region forum section

    /// create a new forum in the database
    /// @param forum the forum object to create
    /// @param callback the callback to call when the operation is completed
    ///               the callback will receive void
    ///              if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see Forum
    public void createNewForum(@NotNull final Forum forum, @Nullable final DatabaseCallback<Void> callback) {
        writeData(FORUMS_PATH + "/" + forum.getForumId(), forum, callback);
    }

    /// get a forum from the database
    /// @param forumId the id of the forum to get
    /// @param callback the callback to call when the operation is completed
    ///                the callback will receive the forum object
    ///               if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see Forum
    public void getForum(@NotNull final String forumId, @NotNull final DatabaseCallback<Forum> callback) {
        getData(FORUMS_PATH + "/" + forumId, Forum.class, callback);
    }

    /// get all the forums from the database
    /// @param callback the callback to call when the operation is completed
    ///               the callback will receive a list of forum objects
    ///
    public void getForumList(@NotNull final DatabaseCallback<List<Forum>> callback) {
        getDataList(FORUMS_PATH, Forum.class, callback);
    }

    /// get all the forums of a specific user from the database
    /// @param uid the id of the user to get the forums for
    /// @param callback the callback to call when the operation is completed
    public void getUserForumList(@NotNull String uid, @NotNull final DatabaseCallback<List<Forum>> callback) {
        getForumList(new DatabaseCallback<>() {
            @Override
            public void onCompleted(List<Forum> forums) {
                forums.removeIf(forum -> !Objects.equals(forum.getForumId(), uid));
                callback.onCompleted(forums);
            }

            @Override
            public void onFailed(Exception e) {
                callback.onFailed(e);
            }
        });
    }


    /// generate a new id for a new forum in the database
    /// @return a new id for the forum
    /// @see #generateNewId(String)
    /// @see Forum
    public String generateForumId() {
        return generateNewId(FORUMS_PATH);
    }

    /// delete a forum from the database
    /// @param forumId the id of the forum to delete
    /// @param callback the callback to call when the operation is completed
    public void deleteForum(@NotNull final String forumId, @Nullable final DatabaseCallback<Void> callback) {
        deleteData(FORUMS_PATH + "/" + forumId, callback);
    }

    // endregion forum section

}
