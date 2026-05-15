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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final List<Post> postListFull; // Original full list for searching
    private List<Post> postList; // List currently being displayed
    private final OnPostClickListener onPostClickListener;

    public interface OnPostClickListener {
        void onPostClick(Post post);
        void onLongPostClick(Post post);
    }

    public PostAdapter(List<Post> postList, OnPostClickListener onPostClickListener) {
        this.postList = postList;
        // Create a deep copy of the list for filtering purposes
        this.postListFull = new ArrayList<>(postList);
        this.onPostClickListener = onPostClickListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        if (post == null) return;

        // Set Title
        holder.title.setText(post.getTitle());

        // 1. Set Date (Formatting the Date object to a readable string)
        if (post.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            holder.tvPostDate.setText(sdf.format(post.getDate()));
        } else {
            holder.tvPostDate.setText("");
        }

        // 2. Set Image Indicator icon visibility
        // Logic: Show the icon if the base64 string is not null or empty
        if (post.getPostPic() != null && !post.getPostPic().trim().isEmpty()) {
            holder.ivHasImageIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.ivHasImageIndicator.setVisibility(View.GONE);
        }

        // Click Handling
        holder.itemView.setOnClickListener(v -> {
            if (onPostClickListener != null) {
                onPostClickListener.onPostClick(post);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onPostClickListener != null) {
                onPostClickListener.onLongPostClick(post);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    /**
     * Method used by InsideTheForum to filter posts based on search input
     */
    public void filter(String text) {
        List<Post> filteredList = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            filteredList.addAll(postListFull);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (Post item : postListFull) {
                // Filter by title or content
                if (item.getTitle().toLowerCase().contains(filterPattern) ||
                        item.getContent().toLowerCase().contains(filterPattern)) {
                    filteredList.add(item);
                }
            }
        }
        postList = filteredList;
        notifyDataSetChanged();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView title, tvPostDate;
        ImageView ivHasImageIndicator;

        PostViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvPostTitle);
            // Initialize new IDs from your item_post.xml
            tvPostDate = itemView.findViewById(R.id.tvPostDate);
            ivHasImageIndicator = itemView.findViewById(R.id.ivHasImageIndicator);
        }
    }
}