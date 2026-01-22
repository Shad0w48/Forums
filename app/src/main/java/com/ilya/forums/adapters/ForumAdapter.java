package com.ilya.forums.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.ilya.forums.R;
import com.ilya.forums.model.Forum;

import java.util.ArrayList;
import java.util.List;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {


    public interface OnForumClickListener {
        void onForumClick(Forum forum);
        void onLongForumClick(Forum forum);
    }

    private final List<Forum> forumList;
    private final ForumAdapter.OnForumClickListener onForumClickListener;
    public ForumAdapter(@Nullable final ForumAdapter.OnForumClickListener onForumClickListener) {
        forumList = new ArrayList<>();
        this.onForumClickListener = onForumClickListener;
    }

    public ForumAdapter(List<Forum> forumList, OnForumClickListener onForumClickListener) {
        this.forumList = forumList;
        this.onForumClickListener = onForumClickListener;
    }

    @NonNull
    @Override
    public ForumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forum, parent, false);
        return new ForumAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForumAdapter.ViewHolder holder, int position) {
        Forum forum = forumList.get(position);
        if (forum == null) return;

        holder.tvName.setText(forum.getName());
        holder.tvDescription.setText(forum.getDescription());

        // Set initials
        String initials = "";
        if (forum.getName() != null && !forum.getName().isEmpty()) {
            initials += forum.getName().charAt(0);
        }
        if (forum.getDescription() != null && !forum.getDescription().isEmpty()) {
            initials += forum.getDescription().charAt(0);
        }


        // Show admin chip if forum is admin
//        if (forum.getIsAdmin()) {
//            holder.chipRole.setVisibility(View.VISIBLE);
//            holder.chipRole.setText("Admin");
//        } else {
//            holder.chipRole.setVisibility(View.GONE);
//        }

        holder.itemView.setOnClickListener(v -> {
            if (onForumClickListener != null) {
                onForumClickListener.onForumClick(forum);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onForumClickListener != null) {
                onForumClickListener.onLongForumClick(forum);
            }
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return forumList.size();
    }

    public void setForumList(List<Forum> forums) {
        forumList.clear();
        forumList.addAll(forums);
        notifyDataSetChanged();
    }

    public void addForum(Forum forum) {
        forumList.add(forum);
        notifyItemInserted(forumList.size() - 1);
    }
    public void updateForum(Forum forum) {
        int index = forumList.indexOf(forum);
        if (index == -1) return;
        forumList.set(index, forum);
        notifyItemChanged(index);
    }

    public void removeForum(Forum forum) {
        int index = forumList.indexOf(forum);
        if (index == -1) return;
        forumList.remove(index);
        notifyItemRemoved(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvForumName);

            tvDescription = itemView.findViewById(R.id.tvForumDescription);

        }
    }
}
