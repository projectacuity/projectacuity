package com.example.mahdi.acuity.adpaters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mahdi.acuity.R;
import com.example.mahdi.acuity.models.User;


public class SearchViewHolder extends RecyclerView.ViewHolder {

    public TextView userName;
    public ImageView userProfilePhoto;

    public SearchViewHolder(View itemView) {
        super(itemView);
        userName = (TextView) itemView.findViewById(R.id.userName);
        userProfilePhoto = (ImageView) itemView.findViewById(R.id.userProfilePhoto);
    }
    public void bindToSearch(User user, View.OnClickListener clickListener) {
        userName.setText(user.getUsername());
        userName.setOnClickListener(clickListener);
        userProfilePhoto.setOnClickListener(clickListener);
    }
}
