package com.example.mahdi.acuity.activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyPostsFragment extends PostListFragment {

    String uid;

    public MyPostsFragment() {}

    public MyPostsFragment(String uid) {
        this.uid = uid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("user-posts")
                .child(this.uid);
    }

    @Override
    public View.OnClickListener getClick(String uid) {
        return null;
    }
}
