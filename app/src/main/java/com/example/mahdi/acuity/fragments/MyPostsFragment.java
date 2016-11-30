package com.example.mahdi.acuity.fragments;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.example.mahdi.acuity.R;
import com.example.mahdi.acuity.adpaters.PostViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyPostsFragment extends PostListFragment {

    String uid;

    public MyPostsFragment() {}

    public MyPostsFragment(String uid) {
        this.uid = uid;
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("user-posts")
                .child(this.uid);
    }

    @Override
    public View.OnClickListener postClick(String uid) {
        return null;
    }

    @Override
    public void deleteMyPost(PostViewHolder viewHolder, final DatabaseReference globalPostRef, final DatabaseReference userPostRef, final String imageRef) {
        viewHolder.deleteView.setVisibility(View.VISIBLE);
        viewHolder.deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.alert)
                        .setTitle("Delete post")
                        .setMessage("Are you sure you want to delete this post?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletePost(globalPostRef, userPostRef, imageRef);
                            }
                        })
                        .setNegativeButton("CANCEL", null)
                        .show();
            }
        });
    }
}
