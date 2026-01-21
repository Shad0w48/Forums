package com.ilya.forums.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ilya.forums.R;
import com.ilya.forums.model.Post;
import com.ilya.forums.model.User;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts = new ArrayList<>();

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
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
        Post post = posts.get(position);
     //   holder.subreddit.setText("r/" + post.subreddit);
      //  holder.title.setText(post.getTitle());
     //   holder.upvotes.setText("▲ " + post.upvotes);
     //   holder.comments.setText("💬 " + post.comments);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView content, title, upvotes, comments;

        PostViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvPostTitle);
            content = itemView.findViewById(R.id.tvPostContent);
            upvotes = itemView.findViewById(R.id.tvVotes);
        }
    }
}

