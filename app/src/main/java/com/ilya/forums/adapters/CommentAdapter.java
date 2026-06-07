package com.ilya.forums.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ilya.forums.R;
import com.ilya.forums.model.Comment;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

// ============================================================================
// COMMENT ADAPTER
// This class acts as a bridge between our raw data (the list of comments)
// and the UI (the RecyclerView on the screen). It tells Android exactly HOW
// to draw each individual comment bubble.
// ============================================================================
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    // The master list that holds all the comments we want to display
    private List<Comment> commentList;

    // --- CONSTRUCTOR ---
    // When we create this adapter in our Activity, we must pass it a list of comments.
    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    // ============================================================================
    // STEP 1: CREATE THE VISUAL "BOX" (onCreateViewHolder)
    // This runs when the screen needs a new blank bubble to put a comment into.
    // Think of it like buying an empty picture frame before putting a photo inside.
    // ============================================================================
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate (convert from XML to a real Java View) the item_comment layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);

        // Wrap it in our custom ViewHolder (defined at the bottom of this file)
        return new CommentViewHolder(view);
    }

    // ============================================================================
    // STEP 2: BIND THE DATA TO THE "BOX" (onBindViewHolder)
    // This runs for EVERY single comment in our list. It takes the empty picture
    // frame we made in Step 1 and puts the specific user's text and name inside it.
    // ============================================================================
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        // Get the specific comment object based on its position in the list
        Comment comment = commentList.get(position);

        // Put the user's first and last name into the Author TextView
        holder.tvAuthor.setText(comment.getAuthor().getFname() + " " + comment.getAuthor().getLname());

        // Put the actual comment text into the Text TextView
        holder.tvText.setText(comment.getText());

        // Safely check if the date exists before trying to display it
        if (comment.getDate() != null) {
            // Choose how we want the date to look.
            // "dd/MM/yyyy HH:mm" will look like: 25/10/2026 14:30
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            // Convert the raw computer Date object into a readable human String
            String formattedDate = sdf.format(comment.getDate());

            // Set that formatted String into the Time TextView
            holder.tvTime.setText(formattedDate);
        } else {
            // Fallback: Just in case the date is missing, leave it blank so it doesn't crash
            holder.tvTime.setText("");
        }
    }

    // ============================================================================
    // STEP 3: COUNT THE ITEMS (getItemCount)
    // Android asks this method: "How many comments do I need to draw?"
    // If this returns 0, the screen will be empty!
    // ============================================================================
    @Override
    public int getItemCount() {
        return commentList.size(); // Returns the total number of comments in our list
    }

    // ============================================================================
    // VIEW HOLDER CLASS
    // This is a memory-saving helper class. Instead of forcing Android to search
    // for R.id.tvCommentAuthor every single time the user scrolls (which makes
    // the app laggy), we find the IDs ONCE here and save them in memory.
    // ============================================================================
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        // Create variables for the UI elements inside our single comment bubble
        TextView tvAuthor, tvText, tvTime;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            // Link the variables to the specific XML IDs in item_comment.xml
            tvAuthor = itemView.findViewById(R.id.tvCommentAuthor);
            tvText = itemView.findViewById(R.id.tvCommentText);
            tvTime = itemView.findViewById(R.id.tvCommentTime);
        }
    }
}