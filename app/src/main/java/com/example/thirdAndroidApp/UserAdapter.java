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
    private List<UserModel> userList;
    private Context context;

    public UserAdapter(List<UserModel> userList,Context context) {
        this.userList = userList;
        this.context=context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel userModel = userList.get(position);
        holder.textViewName.setText(userModel.getFirstName() +" "+userModel.getMaidenName() + " " + userModel.getLastName());
        holder.textViewHairColor.setText(userModel.getHairColor());
        holder.textViewAddress.setText(userModel.getAddressAddress());
        holder.textViewCompanyDepartment.setText(userModel.getCompanyDepartment());
        holder.textViewCompanyAddress.setText(userModel.getCompanyAddressAddress());

        // Load image using Glide
        Glide.with(context)
                .load(userModel.getImageUrl())
                .into(holder.userImageView);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateUsers(List<UserModel> users) {
        this.userList.addAll(users);
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName, textViewMaidenName, textViewHairColor, textViewAddress, textViewCompanyDepartment, textViewCompanyAddress;
        public ImageView userImageView;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewHairColor = itemView.findViewById(R.id.textViewHairColor);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewCompanyDepartment = itemView.findViewById(R.id.textViewCompanyDepartment);
            textViewCompanyAddress = itemView.findViewById(R.id.textViewCompanyAddress);
            userImageView=itemView.findViewById(R.id.userImageView);
        }
    }
}
