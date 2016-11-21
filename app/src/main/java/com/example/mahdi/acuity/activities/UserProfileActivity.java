package com.example.mahdi.acuity.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mahdi.acuity.R;
import com.example.mahdi.acuity.adpaters.PostViewHolder;
import com.example.mahdi.acuity.models.Post;
import com.example.mahdi.acuity.models.User;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserProfileActivity extends BaseDrawerActivity {
    private static final String TAG = "UserProfileActivity";

    ImageView ivUserProfilePhoto;
    TextView userName;
    LinearLayout manage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ivUserProfilePhoto = (ImageView) findViewById(R.id.userProfilePhoto);
        userName = (TextView)findViewById(R.id.userName);
        manage = (LinearLayout) findViewById(R.id.btnFollow);
        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(intent);
            }
        });
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.my_feed, new MyPostsFragment());
        ft.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserInfo();
    }

    private void setUserInfo() {
        final Context contextUserPhoto = ivUserProfilePhoto.getContext();
        final DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                if (user.getUsername()!=null) {
                    userName.setText(user.getUsername());
                }
                if (user.getPhotoUrl()!=null) {
                    Glide.with(contextUserPhoto).load(user.getPhotoUrl()).centerCrop().into(ivUserProfilePhoto);
                }
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}

