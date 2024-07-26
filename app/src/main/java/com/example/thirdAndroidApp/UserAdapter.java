package com.example.thirdAndroidApp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firstandroidapp.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<UserModel> userList;

    public UserAdapter(Context context, List<UserModel> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // Adjust position to ensure it loops correctly
        if (userList.isEmpty()) {
            return; // Avoid binding if the list is empty
        }
        int adjustedPosition = position % userList.size();
        UserModel userModel = userList.get(adjustedPosition);

        holder.textViewId.setText(String.valueOf(userModel.getId()));
        holder.textViewName.setText(userModel.getFirstName() + " " + userModel.getMaidenName() + " " + userModel.getLastName());
        holder.textViewAddress.setText(userModel.getAddressAddress());
        holder.textViewCompanyDepartment.setText(userModel.getCompanyDepartment());
        holder.textViewCompanyAddress.setText(userModel.getCompanyAddressAddress());

        // Load image using Glide
        Glide.with(context)
                .load(userModel.getImageUrl())
                .placeholder(R.drawable.profile_picture) // Add a placeholder image if needed
                .error(R.drawable.profile_picture) // Add an error image if needed
                .into(holder.userImageView);
    }

    @Override
    public int getItemCount() {
        // Return a large number for infinite scroll simulation
        return userList.size();// Avoid infinite loop if list is empty
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewId, textViewName, textViewAddress, textViewCompanyDepartment, textViewCompanyAddress;
        ImageView userImageView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewId = itemView.findViewById(R.id.userId);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewCompanyDepartment = itemView.findViewById(R.id.textViewCompanyDepartment);
            textViewCompanyAddress = itemView.findViewById(R.id.textViewCompanyAddress);
            userImageView = itemView.findViewById(R.id.userImageView);
        }
    }
}
