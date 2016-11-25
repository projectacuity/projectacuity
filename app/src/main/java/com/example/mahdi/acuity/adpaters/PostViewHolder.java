package com.example.mahdi.acuity.adpaters;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mahdi.acuity.R;
import com.example.mahdi.acuity.models.Post;


public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView authorView;
    public ImageView userPhotoView;
    public ImageView postPhotoView;
    public TextView commentView;
    public ImageView likesView;
    public ImageView dislikesView;
    public TextView numlikesView;
    public TextView numdislikesView;
    public ImageView deleteView;

    public PostViewHolder(View itemView) {
        super(itemView);
        authorView = (TextView) itemView.findViewById(R.id.userName);
        userPhotoView = (ImageView) itemView.findViewById(R.id.userProfilePhoto);
        postPhotoView = (ImageView) itemView.findViewById(R.id.postPhoto);
        commentView = (TextView) itemView.findViewById(R.id.comment);
        likesView = (ImageView) itemView.findViewById(R.id.btnLike);
        dislikesView = (ImageView) itemView.findViewById(R.id.btnDislike);
        numlikesView = (TextView) itemView.findViewById(R.id.likesNb);
        numdislikesView = (TextView) itemView.findViewById(R.id.dislikesNb);
        deleteView = (ImageView) itemView.findViewById(R.id.btnDelete);
    }

    public void bindToPost(Post post, View.OnClickListener likeClickListener, View.OnClickListener dislikeClickListener, View.OnClickListener userClickListener, boolean myAccount, View.OnClickListener deleteClickListener) {
        authorView.setText(post.author);
        if (post.comment.equals("")) {
            commentView.setVisibility(View.GONE);
        }
        else commentView.setText(post.comment);
        numlikesView.setText(String.valueOf(post.likesCount));
        numdislikesView.setText(String.valueOf(post.dislikesCount));
        userPhotoView.setOnClickListener(userClickListener);
        commentView.setOnClickListener(userClickListener);
        likesView.setOnClickListener(likeClickListener);
        dislikesView.setOnClickListener(dislikeClickListener);
        if (myAccount) {
            deleteView.setVisibility(View.VISIBLE);
            deleteView.setOnClickListener(deleteClickListener);
        }
    }
}
