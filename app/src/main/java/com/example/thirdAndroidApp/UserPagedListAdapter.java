package com.example.thirdAndroidApp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstandroidapp.R;

public class UserPagedListAdapter extends PagingDataAdapter<UserModel, UserPagedListAdapter.UserViewHolder> {

    protected UserPagedListAdapter(@NonNull DiffUtil.ItemCallback<UserModel> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel userModel = getItem(position);
        if (userModel != null) {
            holder.textViewId.setText(userModel.getId());
            holder.textViewName.setText(userModel.getFirstName() + " " + userModel.getLastName());
            holder.textViewMaidenName.setText(userModel.getMaidenName());
            holder.textViewHairColor.setText(userModel.getHairColor());
            holder.textViewAddress.setText(userModel.getAddressAddress());
            holder.textViewCompanyDepartment.setText(userModel.getCompanyDepartment());
            holder.textViewCompanyAddress.setText(userModel.getCompanyAddressAddress());
        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewId;
        TextView textViewName;
        TextView textViewMaidenName;
        TextView textViewHairColor;
        TextView textViewAddress;
        TextView textViewCompanyDepartment;
        TextView textViewCompanyAddress;

        UserViewHolder(View itemView) {
            super(itemView);
            textViewId=itemView.findViewById(R.id.userId);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewAddress=itemView.findViewById(R.id.textViewAddress);
            textViewCompanyDepartment=itemView.findViewById(R.id.textViewCompanyDepartment);
            textViewCompanyAddress=itemView.findViewById(R.id.textViewCompanyAddress);
        }
    }

    public static final DiffUtil.ItemCallback<UserModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<UserModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull UserModel oldItem, @NonNull UserModel newItem) {
            return oldItem.getFirstName().equals(newItem.getFirstName()) &&
                    oldItem.getLastName().equals(newItem.getLastName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull UserModel oldItem, @NonNull UserModel newItem) {
            return oldItem.equals(newItem);
        }
    };
}
