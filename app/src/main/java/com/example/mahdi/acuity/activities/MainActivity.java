package com.example.mahdi.acuity.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.example.mahdi.acuity.R;
import com.example.mahdi.acuity.adpaters.PostAdapter;
import com.example.mahdi.acuity.models.Post;
import com.example.mahdi.acuity.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends BaseDrawerActivity implements PostAdapter.OnPostClickListener {


    RecyclerView rvPost;
    CoordinatorLayout clContent;
    List<Post> posts= new ArrayList<>();
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvPost = (RecyclerView) findViewById(R.id.rvFeed);
        clContent= (CoordinatorLayout) findViewById(R.id.content);
        setupFeed();
        updateItems();
    }
    private void setupFeed() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvPost.setLayoutManager(linearLayoutManager);
        postAdapter = new PostAdapter(posts);
        postAdapter.setOnPostClickListener(this);
        rvPost.setAdapter(postAdapter);
    }
    public void updateItems() {
        posts.clear();
                DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://projectacuity.firebaseio.com/users/e8ktZwlphfexnmlYL98KZ9MgtNB2/posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                posts.addAll(Arrays.asList(post));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        postAdapter.notifyItemRangeInserted(0, posts.size());
    }

    @Override
    public void onStart() {
        super.onStart();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }
    @Override
    public void onProfileClick(View v) {
        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}