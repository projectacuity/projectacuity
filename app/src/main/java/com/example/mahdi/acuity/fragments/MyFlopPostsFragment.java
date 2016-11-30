package com.example.mahdi.acuity.fragments;

import android.content.Intent;
import android.view.View;

import com.example.mahdi.acuity.activities.UserProfileActivity;
import com.example.mahdi.acuity.adpaters.PostViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyFlopPostsFragment extends PostListFragment {

    public MyFlopPostsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("posts").orderByChild("dislikesCount");
    }

    @Override
    public View.OnClickListener postClick(final String uid) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserProfileActivity.class);
                intent.putExtra("uid",uid);
                startActivity(intent);
                getActivity().finish();
            }
        };
    }

    @Override
    public void deleteMyPost(PostViewHolder viewHolder, DatabaseReference globalPostRef, DatabaseReference userPostRef, String imageRef) {
        return;
    }

}
