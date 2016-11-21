package com.example.mahdi.acuity.activities;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyFlopPostsFragment extends PostListFragment {

    public MyFlopPostsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // [START my_top_posts_query]
        // My top posts by number of stars
        String myUserId = getUid();
        Query myTopPostsQuery = databaseReference.child("posts").orderByChild("dislikesCount");
        // [END my_top_posts_query]

        return myTopPostsQuery;
    }
}
