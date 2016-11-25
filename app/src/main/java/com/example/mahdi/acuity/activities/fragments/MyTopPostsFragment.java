package com.example.mahdi.acuity.activities.fragments;

import android.content.Intent;
import android.view.View;

import com.example.mahdi.acuity.activities.UserProfileActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyTopPostsFragment extends PostListFragment {

    public MyTopPostsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {

        return databaseReference.child("posts").orderByChild("likesCount");
    }

    @Override
    public View.OnClickListener getClick(final String uid) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserProfileActivity.class);
                intent.putExtra("uid",uid);
                startActivity(intent);
            }
        };
    }
}
