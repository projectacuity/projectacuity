package com.example.mahdi.acuity.adpaters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mahdi.acuity.R;
import com.example.mahdi.acuity.models.Post;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView authorView;
    public ImageView userPhotoView;
    public ImageView postPhotoView;
    public TextView commentView;
    public ImageView likesView;
    public ImageView dislikesView;
    public TextView numlikesView;
    public TextView numdislikesView;

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
    }

    public void bindToPost(Post post, View.OnClickListener likeClickListener, View.OnClickListener dislikeClickListener) {
        commentView.setText(post.comment);
        numlikesView.setText(String.valueOf(post.likesCount));
        numdislikesView.setText(String.valueOf(post.dislikesCount));
        likesView.setOnClickListener(likeClickListener);
        dislikesView.setOnClickListener(dislikeClickListener);
    }
}
