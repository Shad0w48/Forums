package com.ilya.forums.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

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
    private List<Forum> forumListFull;
    private final ForumAdapter.OnForumClickListener onForumClickListener;

    public ForumAdapter(@Nullable final ForumAdapter.OnForumClickListener onForumClickListener) {
        forumList = new ArrayList<>();
        forumListFull = new ArrayList<>();
        this.onForumClickListener = onForumClickListener;
    }

    public ForumAdapter(List<Forum> forumList, OnForumClickListener onForumClickListener) {
        this.forumList = forumList;
        this.forumListFull = new ArrayList<>(forumList);
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

        // NEW: Set the post count
        // We will add getPostCount() to your Forum model in Step 2!
        holder.tvPostCount.setText(forum.getPostCount() + " posts");

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

    public void filter(String text) {
        forumList.clear();
        if (text.isEmpty()) {
            forumList.addAll(forumListFull);
        } else {
            String query = text.toLowerCase().trim();
            for (Forum item : forumListFull) {
                if (item.getName() != null && item.getName().toLowerCase().contains(query)) {
                    forumList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setForumList(List<Forum> forums) {
        forumList.clear();
        forumList.addAll(forums);
        forumListFull = new ArrayList<>(forums);
        notifyDataSetChanged();
    }

    public void addForum(Forum forum) {
        forumList.add(forum);
        forumListFull.add(forum);
        notifyItemInserted(forumList.size() - 1);
    }

    public void updateForum(Forum forum) {
        int index = forumList.indexOf(forum);
        if (index != -1) {
            forumList.set(index, forum);
            notifyItemChanged(index);
        }
        int fullIndex = forumListFull.indexOf(forum);
        if (fullIndex != -1) {
            forumListFull.set(fullIndex, forum);
        }
    }

    public void removeForum(Forum forum) {
        int index = forumList.indexOf(forum);
        if (index != -1) {
            forumList.remove(index);
            notifyItemRemoved(index);
        }
        forumListFull.remove(forum);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // NEW: Add tvPostCount here
        TextView tvName, tvDescription, tvPostCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvForumName);
            tvDescription = itemView.findViewById(R.id.tvForumDescription);

            // NEW: Bind it to the ID from your XML
            tvPostCount = itemView.findViewById(R.id.tvPostCount);
        }
    }
}