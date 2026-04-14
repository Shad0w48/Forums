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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_comment layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        // Get the current comment
        Comment comment = commentList.get(position);

        // Put the data into the TextViews
        holder.tvAuthor.setText(comment.getAuthor().getFname()+" "+comment.getAuthor().getLname());
        holder.tvText.setText(comment.getText());
        if (comment.getDate() != null) {
            // Choose how you want the date to look.
            // "dd/MM/yyyy HH:mm" will look like: 25/10/2023 14:30
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            // Convert the Date object to a String
            String formattedDate = sdf.format(comment.getDate());

            // Set the String into the TextView
            holder.tvTime.setText(formattedDate);
        } else {
            holder.tvTime.setText(""); // Just in case the date is missing
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    // ViewHolder class holds the references to the UI elements
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor, tvText, tvTime;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tvCommentAuthor);
            tvText = itemView.findViewById(R.id.tvCommentText);
            tvTime = itemView.findViewById(R.id.tvCommentTime);
        }
    }
}
