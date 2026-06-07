package com.ilya.forums.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ilya.forums.R;
import com.ilya.forums.model.User;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

// ============================================================================
// USER ADAPTER
// This acts as the bridge for your user management list (e.g., an Admin panel
// or a list of friends/members). It converts a User object into a formatted row.
// ============================================================================
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    // --- INTERCOM ---
    // A standard interface to notify the Activity when a user is clicked/long-clicked.
    public interface OnUserClickListener {
        void onUserClick(User user);
        void onLongUserClick(User user);
    }

    private final List<User> userList;
    private final OnUserClickListener onUserClickListener;

    public UserAdapter(@Nullable final OnUserClickListener onUserClickListener) {
        userList = new ArrayList<>();
        this.onUserClickListener = onUserClickListener;
    }

    // ============================================================================
    // STEP 1: CREATE THE VISUAL "BOX"
    // ============================================================================
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    // ============================================================================
    // STEP 2: BIND DATA & LOGIC
    // ============================================================================
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        if (user == null) return;

        // Set basic contact info
        holder.tvEmail.setText(user.getEmail());
        holder.tvPhone.setText(user.getPhone());

        // --- INITIALS LOGIC ---
        // Dynamically creates a badge with the first letter of first/last name
        String initials = "";
        if (user.getFname() != null && !user.getFname().isEmpty()) {
            initials += user.getFname().charAt(0);
        }
        if (user.getLname() != null && !user.getLname().isEmpty()) {
            initials += user.getLname().charAt(0);
        }
        holder.tvInitials.setText(initials.toUpperCase());
        holder.tvName.setText(user.getFname() + " " + user.getLname());

        // --- ADMIN CHIP (Optional) ---
        // You've got the logic here ready to uncomment if you want to highlight admins!
        /*
        if (user.getIsAdmin()) {
            holder.chipRole.setVisibility(View.VISIBLE);
            holder.chipRole.setText("Admin");
        } else {
            holder.chipRole.setVisibility(View.GONE);
        }
        */

        // Attach click listeners to the whole row
        holder.itemView.setOnClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onUserClick(user);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onLongUserClick(user);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // ============================================================================
    // HELPER METHODS
    // These manage the data safely so the UI stays in sync with the database.
    // ============================================================================

    public void setUserList(List<User> users) {
        userList.clear();
        userList.addAll(users);
        notifyDataSetChanged(); // Refresh the whole list
    }

    public void addUser(User user) {
        userList.add(user);
        notifyItemInserted(userList.size() - 1); // Animate the addition of one row
    }

    public void updateUser(User user) {
        int index = userList.indexOf(user);
        if (index == -1) return;
        userList.set(index, user);
        notifyItemChanged(index); // Refresh only the row that changed
    }

    public void removeUser(User user) {
        int index = userList.indexOf(user);
        if (index == -1) return;
        userList.remove(index);
        notifyItemRemoved(index); // Animate the removal
    }

    // ============================================================================
    // VIEW HOLDER
    // ============================================================================
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvPhone, tvInitials;
        Chip chipRole; // Using Material Chips for a clean, modern UI

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_user_name);
            tvEmail = itemView.findViewById(R.id.tv_item_user_email);
            tvPhone = itemView.findViewById(R.id.tv_item_user_phone);
            tvInitials = itemView.findViewById(R.id.tv_user_initials);
            chipRole = itemView.findViewById(R.id.chip_user_role);
        }
    }
}