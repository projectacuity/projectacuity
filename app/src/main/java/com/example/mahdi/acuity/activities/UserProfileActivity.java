package com.example.mahdi.acuity.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mahdi.acuity.R;
import com.example.mahdi.acuity.adpaters.PostAdapter;
import com.example.mahdi.acuity.models.Post;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserProfileActivity extends BaseDrawerActivity implements PostAdapter.OnPostClickListener{
    private static final String TAG = "UserProflileActivity";


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    RecyclerView rvUserProfile;

    ImageView ivUserProfilePhoto;
    View userDetails;
    Button btnFollow;
    View userStats;
    View vUserProfileRoot;
    CoordinatorLayout clContent;
    List<Post> posts= new ArrayList<>();

    private PostAdapter postAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        rvUserProfile = (RecyclerView) findViewById(R.id.rvUserProfile);
        ivUserProfilePhoto = (ImageView) findViewById(R.id.userProfilePhoto);
//        StorageReference storageRef = storage.getReferenceFromUrl("gs://projectacuity.appspot.com");
//        StorageReference imagesRef = storageRef.child("users").child(mAuth.getCurrentUser().getUid()).child("1.jpg");
//        Glide.with(this).using(new FirebaseImageLoader()).load(imagesRef).centerCrop().into(ivUserProfilePhoto);
        userDetails = findViewById(R.id.userDetails);
        btnFollow = (Button) findViewById(R.id.btnFollow);
        userStats = findViewById(R.id.userStats);
        vUserProfileRoot = findViewById(R.id.vUserProfileRoot);
        clContent = (CoordinatorLayout) findViewById(R.id.content);
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
    rvUserProfile.setLayoutManager(linearLayoutManager);

    postAdapter = new PostAdapter(posts);
    postAdapter.setOnPostClickListener(this);
    rvUserProfile.setAdapter(postAdapter);
}
    public void updateItems() {
        posts.clear();
        posts.addAll(Arrays.asList(
                new Post()
        ));
        postAdapter.notifyItemRangeInserted(0, posts.size());
    }
    @Override
    public void onProfileClick(View v) {
        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}

