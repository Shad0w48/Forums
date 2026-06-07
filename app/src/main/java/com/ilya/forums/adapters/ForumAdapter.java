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

// ============================================================================
// FORUM ADAPTER
// This class takes our raw list of Forums from the database and builds the
// visual "cards" or "rows" that the user actually scrolls through on the screen.
// ============================================================================
public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {

    // ============================================================================
    // CLICK LISTENER INTERFACE
    // The adapter's job is just to display things, not to open new screens.
    // So, we create an "intercom" (interface) to shout back to the main Activity
    // "Hey! The user just clicked a forum!" so the Activity can open the next page.
    // ============================================================================
    public interface OnForumClickListener {
        void onForumClick(Forum forum);       // Normal quick tap
        void onLongForumClick(Forum forum);   // Press and hold
    }

    // --- VARIABLES ---
    // forumList is the active list currently shown on the screen.
    private final List<Forum> forumList;

    // forumListFull is a HIDDEN BACKUP. When the user types in the search bar,
    // we delete things from forumList, but we never touch forumListFull so we
    // can always restore the original list when they clear the search!
    private List<Forum> forumListFull;

    // The "intercom" we use to talk to the Activity
    private final ForumAdapter.OnForumClickListener onForumClickListener;

    // --- CONSTRUCTOR 1 (Empty Start) ---
    // Use this when we start with an empty screen and fetch data later.
    public ForumAdapter(@Nullable final ForumAdapter.OnForumClickListener onForumClickListener) {
        forumList = new ArrayList<>();
        forumListFull = new ArrayList<>();
        this.onForumClickListener = onForumClickListener;
    }

    // --- CONSTRUCTOR 2 (Data Ready) ---
    // Use this when we already have the list of forums ready to go.
    public ForumAdapter(List<Forum> forumList, OnForumClickListener onForumClickListener) {
        this.forumList = forumList;
        this.forumListFull = new ArrayList<>(forumList); // Copy data into the backup
        this.onForumClickListener = onForumClickListener;
    }

    // ============================================================================
    // STEP 1: CREATE THE VISUAL "BOX"
    // Android needs an empty layout to put the forum text into.
    // ============================================================================
    @NonNull
    @Override
    public ForumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate our item_forum.xml layout file into a real Java View
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forum, parent, false);
        return new ForumAdapter.ViewHolder(view);
    }

    // ============================================================================
    // STEP 2: BIND THE DATA & THE CLICKS
    // Fill the empty box with the specific forum's name, description, and clicks.
    // ============================================================================
    @Override
    public void onBindViewHolder(@NonNull ForumAdapter.ViewHolder holder, int position) {
        // Get the forum at this exact position in the list
        Forum forum = forumList.get(position);
        if (forum == null) return; // Safety check to prevent crashes

        // Put the data into the TextViews
        holder.tvName.setText(forum.getName());
        holder.tvDescription.setText(forum.getDescription());

        // Set the post count string (e.g., "15 posts")
        holder.tvPostCount.setText(forum.getPostCount() + " posts");

        // --- ATTACH CLICK LISTENERS ---
        // If the user does a normal tap, use the intercom to tell the Activity
        holder.itemView.setOnClickListener(v -> {
            if (onForumClickListener != null) {
                onForumClickListener.onForumClick(forum);
            }
        });

        // If the user does a long press, tell the Activity (maybe to delete or edit it)
        holder.itemView.setOnLongClickListener(v -> {
            if (onForumClickListener != null) {
                onForumClickListener.onLongForumClick(forum);
            }
            return true; // 'true' means we successfully handled the long click
        });
    }

    // ============================================================================
    // STEP 3: COUNT THE ITEMS
    // Tell the screen how many items to prepare.
    // ============================================================================
    @Override
    public int getItemCount() {
        return forumList.size(); // Always return the size of the ACTIVE list
    }

    // ============================================================================
    // SEARCH BAR MAGIC (Filtering)
    // This runs every time the user types a letter in the search bar.
    // ============================================================================
    public void filter(String text) {
        forumList.clear(); // Empty out the screen immediately

        if (text.isEmpty()) {
            // If the search bar is empty, put ALL the items from the backup back on screen
            forumList.addAll(forumListFull);
        } else {
            // Convert the search text to lowercase so "Minecraft" and "minecraft" both work
            String query = text.toLowerCase().trim();

            // Loop through our secret backup list
            for (Forum item : forumListFull) {
                // If the forum name contains the typed letters, add it to the active screen!
                if (item.getName() != null && item.getName().toLowerCase().contains(query)) {
                    forumList.add(item);
                }
            }
        }
        // Tell Android the list changed so it redraws the screen with the filtered items
        notifyDataSetChanged();
    }

    // ============================================================================
    // HELPER METHODS
    // These let us update the list without entirely restarting the app.
    // ============================================================================

    // Replaces the entire list (Useful when pulling fresh data from Firebase)
    public void setForumList(List<Forum> forums) {
        forumList.clear();
        forumList.addAll(forums);
        forumListFull = new ArrayList<>(forums); // Update the backup too!
        notifyDataSetChanged();
    }

    // Adds a single new forum to the bottom of the list with a nice animation
    public void addForum(Forum forum) {
        forumList.add(forum);
        forumListFull.add(forum);
        notifyItemInserted(forumList.size() - 1);
    }

    // Updates a specific forum (e.g., if the post count changes)
    public void updateForum(Forum forum) {
        int index = forumList.indexOf(forum);
        if (index != -1) {
            forumList.set(index, forum);
            notifyItemChanged(index); // Animates just this one row updating
        }

        int fullIndex = forumListFull.indexOf(forum);
        if (fullIndex != -1) {
            forumListFull.set(fullIndex, forum);
        }
    }

    // Removes a single forum from the list with a swipe/delete animation
    public void removeForum(Forum forum) {
        int index = forumList.indexOf(forum);
        if (index != -1) {
            forumList.remove(index);
            notifyItemRemoved(index);
        }
        forumListFull.remove(forum); // Don't forget to delete it from the backup!
    }

    // ============================================================================
    // VIEW HOLDER CLASS
    // Grabs the specific UI text boxes from the XML layout so we can reuse them
    // instantly as the user scrolls, saving battery and preventing lag.
    // ============================================================================
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvPostCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Link the Java variables to the XML IDs
            tvName = itemView.findViewById(R.id.tvForumName);
            tvDescription = itemView.findViewById(R.id.tvForumDescription);
            tvPostCount = itemView.findViewById(R.id.tvPostCount);
        }
    }
}