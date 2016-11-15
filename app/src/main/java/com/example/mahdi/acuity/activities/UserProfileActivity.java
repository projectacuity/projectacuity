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
import com.example.mahdi.acuity.models.Post;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserProfileActivity extends BaseDrawerActivity {
    private static final String TAG = "UserProfileActivity";


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
        userDetails = findViewById(R.id.userDetails);
        btnFollow = (Button) findViewById(R.id.btnFollow);
        userStats = findViewById(R.id.userStats);
        vUserProfileRoot = findViewById(R.id.vUserProfileRoot);
        clContent = (CoordinatorLayout) findViewById(R.id.content);

    }
}

