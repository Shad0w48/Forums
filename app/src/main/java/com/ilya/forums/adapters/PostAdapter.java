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


    private final List<Post> postList;
    private final PostAdapter.OnPostClickListener onPostClickListener;




    public interface OnPostClickListener {
        void onPostClick(Post post);


        void onLongPostClick(Post post);
    }
    public PostAdapter(List<Post> postList, OnPostClickListener onPostClickListener) {
        this.postList = postList;
        this.onPostClickListener = onPostClickListener;
    }




    @Override
    public String toString() {
        return "PostAdapter{" +
                "postList=" + postList +
                ", onPostClickListener=" + onPostClickListener +
                '}';
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
     //   holder.content.setText( post.getContent());
      holder.title.setText(post.getTitle());
    //    holder.upvotes.setText( post.getUpVote()+"");
       //holder.comments.setText("💬 " + post.comments);


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

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView content, title, upvotes, comments;

        PostViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvPostTitle);
          //  content = itemView.findViewById(R.id.tvPostContent);
          //  upvotes = itemView.findViewById(R.id.tvVotes);



        }
    }
}

