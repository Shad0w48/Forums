package com.ilya.forums.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ilya.forums.R;
import com.ilya.forums.model.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// ============================================================================
// POST ADAPTER
// This class is the waiter that takes our raw list of Posts from a specific forum
// and serves them up as the visual "cards" the user scrolls through on the screen.
// ============================================================================
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    // --- VARIABLES ---
    private final List<Post> postListFull; // The "secret backup" list holding ALL posts (used for searching)
    private List<Post> postList;           // The active list currently being displayed on the screen
    private final OnPostClickListener onPostClickListener; // The "intercom" to talk to the Activity

    // ============================================================================
    // CLICK LISTENER INTERFACE
    // The adapter's job is just to display the visual cards. When a user clicks a card,
    // we use this intercom to shout back to the Activity: "Hey, open this post!"
    // ============================================================================
    public interface OnPostClickListener {
        void onPostClick(Post post);       // Normal tap (Open the post)
        void onLongPostClick(Post post);   // Press and hold (Maybe delete/edit)
    }

    // --- CONSTRUCTOR ---
    // This runs when we first create the adapter. We hand it the list of posts and the click listener.
    public PostAdapter(List<Post> postList, OnPostClickListener onPostClickListener) {
        this.postList = postList;
        // Create a deep copy of the list into our backup variable so we don't lose data when filtering
        this.postListFull = new ArrayList<>(postList);
        this.onPostClickListener = onPostClickListener;
    }

    // ============================================================================
    // STEP 1: CREATE THE VISUAL "BOX"
    // Android needs an empty layout to put the post text and icons into.
    // ============================================================================
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate our item_post.xml layout file into a real Java View
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    // ============================================================================
    // STEP 2: BIND THE DATA & THE CLICKS
    // Fill the empty box with the specific post's title, date, vote counts, and clicks.
    // ============================================================================
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        // Get the specific post at this exact position in the list
        Post post = postList.get(position);
        if (post == null) return; // Safety check to prevent crashes

        // Set the main title of the post
        holder.title.setText(post.getTitle());

        // 1. SET THE DATE
        if (post.getDate() != null) {
            // Format the raw computer Date object into a nice string like "Oct 25, 2026"
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            holder.tvPostDate.setText(sdf.format(post.getDate()));
        } else {
            holder.tvPostDate.setText(""); // Fallback if no date exists
        }

        // 2. IMAGE INDICATOR LOGIC
        // If the post has a Base64 image string attached, we show a little picture icon!
        if (post.getPostPic() != null && !post.getPostPic().trim().isEmpty()) {
            holder.ivHasImageIndicator.setVisibility(View.VISIBLE); // Show the icon
        } else {
            holder.ivHasImageIndicator.setVisibility(View.GONE);    // Hide the icon
        }

        // 3. CALCULATE & SET NET SCORE
        // We do the math right here: Upvotes minus Downvotes = Total Score
        int netScore = post.getUpVote() - post.getDownVote();
        holder.tvVoteCount.setText(String.valueOf(netScore));

        // 4. SET COMMENT COUNT
        holder.tvCommentCount.setText(String.valueOf(post.getCommentCount()));

        // --- ATTACH CLICK LISTENERS ---
        // Normal click
        holder.itemView.setOnClickListener(v -> {
            if (onPostClickListener != null) {
                onPostClickListener.onPostClick(post);
            }
        });

        // Long press
        holder.itemView.setOnLongClickListener(v -> {
            if (onPostClickListener != null) {
                onPostClickListener.onLongPostClick(post);
            }
            return true; // 'true' means we handled the click successfully
        });
    }

    // ============================================================================
    // STEP 3: COUNT THE ITEMS
    // Tell Android exactly how many posts we need to draw on the screen.
    // ============================================================================
    @Override
    public int getItemCount() {
        return postList.size(); // Always returns the size of the ACTIVE list
    }

    // ============================================================================
    // SEARCH BAR MAGIC (Filtering)
    // This method is called from the Activity whenever the user types in the search bar.
    // ============================================================================
    public void filter(String text) {
        List<Post> filteredList = new ArrayList<>(); // Create a temporary empty list

        if (text == null || text.isEmpty()) {
            // If the search bar is empty, restore ALL posts from our secret backup
            filteredList.addAll(postListFull);
        } else {
            // Convert search text to lowercase so we don't worry about Capital letters
            String filterPattern = text.toLowerCase().trim();

            // Loop through every single post in our backup
            for (Post item : postListFull) {
                // If either the TITLE or the CONTENT contains what the user typed, keep it!
                if (item.getTitle().toLowerCase().contains(filterPattern) ||
                        item.getContent().toLowerCase().contains(filterPattern)) {
                    filteredList.add(item);
                }
            }
        }

        // Update our active list to be the new filtered list
        postList = filteredList;
        // Tell the screen to refresh with the new data
        notifyDataSetChanged();
    }

    // ============================================================================
    // VIEW HOLDER CLASS
    // Grabs the specific UI text boxes and images from the XML layout so we can reuse them
    // instantly as the user scrolls, saving battery and preventing lag.
    // ============================================================================
    static class PostViewHolder extends RecyclerView.ViewHolder {
        // Variables representing the UI elements in item_post.xml
        TextView title, tvPostDate, tvVoteCount, tvCommentCount;
        ImageView ivHasImageIndicator;

        PostViewHolder(View itemView) {
            super(itemView);
            // Link the Java variables to their specific XML IDs
            title = itemView.findViewById(R.id.tvPostTitle);
            tvPostDate = itemView.findViewById(R.id.tvPostDate);
            ivHasImageIndicator = itemView.findViewById(R.id.ivHasImageIndicator);
            tvVoteCount = itemView.findViewById(R.id.tvVoteCount);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
        }
    }
}