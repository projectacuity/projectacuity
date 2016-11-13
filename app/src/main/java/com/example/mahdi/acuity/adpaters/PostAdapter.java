package com.example.mahdi.acuity.adpaters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mahdi.acuity.R;
import com.example.mahdi.acuity.models.Post;
import com.example.mahdi.acuity.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Post> posts;
    private OnPostClickListener onPostClickListener;


    public PostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        setupClickableViews(view, myViewHolder);
        return myViewHolder;
    }

    private void setupClickableViews(final View view, final MyViewHolder myViewHolder) {
        myViewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        myViewHolder.btnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        myViewHolder.userProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostClickListener.onProfileClick(view);
            }
        });
        myViewHolder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostClickListener.onProfileClick(view);
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((MyViewHolder) viewHolder).bindView(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setOnPostClickListener(OnPostClickListener onpostClickListener) {
        this.onPostClickListener = onpostClickListener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfilePhoto;
        TextView userName;
        ImageView postPhoto;
        public TextView comment;
        ImageButton btnLike;
        TextView likesNb;
        ImageView btnDislike;
        TextView dislikesNb;
        Post post;

        public MyViewHolder(View view) {
            super(view);
            userProfilePhoto = (ImageView) view.findViewById(R.id.userProfilePhoto);
            userName = (TextView) view.findViewById(R.id.userName);
            postPhoto = (ImageView) view.findViewById(R.id.postPhoto);
            comment = (TextView) view.findViewById(R.id.comment);
            btnLike = (ImageButton) view.findViewById(R.id.btnLike);
            likesNb = (TextView) view.findViewById(R.id.likesNb);
            btnDislike = (ImageButton) view.findViewById(R.id.btnDislike);
            dislikesNb = (TextView) view.findViewById(R.id.dislikesNb);
        }

        public void bindView(Post post) {
            this.post = post;
            comment.setText(post.getComment());
            userName.setText(post.getComment());
            dislikesNb.setText(Integer.toString(post.getDislikesCount()));
            likesNb.setText(Integer.toString(post.getLikesCount()));
        }
    }
    public interface OnPostClickListener {

        void onProfileClick(View v);
    }
}
