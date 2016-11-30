package com.example.mahdi.acuity.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mahdi.acuity.R;
import com.example.mahdi.acuity.fragments.MyPostsFragment;
import com.example.mahdi.acuity.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class UserProfileActivity extends BaseDrawerActivity {

    ImageView ivUserProfilePhoto;
    TextView userName;
    LinearLayout manage;
    String uid;
    private DatabaseReference mDatabase;
    DatabaseReference mUserRef;
    ValueEventListener userListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_new_post);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowPermissions();
            }
        });
        ivUserProfilePhoto = (ImageView) findViewById(R.id.userProfilePhoto);
        userName = (TextView)findViewById(R.id.userName);
        manage = (LinearLayout) findViewById(R.id.btnManage);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent=getIntent();
        if (intent.getStringExtra("uid")==null) {
            manage.setVisibility(View.VISIBLE);
            uid=mAuth.getCurrentUser().getUid();
        }
        else if (intent.getStringExtra("uid").equals(mAuth.getCurrentUser().getUid())) {
            manage.setVisibility(View.VISIBLE);
            uid=intent.getStringExtra("uid");
        }
        else {
            uid=intent.getStringExtra("uid");
        }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.my_feed, new MyPostsFragment(uid));
            ft.commit();
        mUserRef = mDatabase.child("users").child(uid);
        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserInfo();
    }

    private void setUserInfo() {
        final Context contextUserPhoto = ivUserProfilePhoto.getContext();
        userListner = mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                if (user.getUsername()!=null) {
                    userName.setText(user.getUsername());
                }
                if (user.getPhotoUrl()!=null) {
                    Glide.with(contextUserPhoto).load(user.getPhotoUrl()).bitmapTransform(new CropCircleTransformation(contextUserPhoto)).into(ivUserProfilePhoto);
                }
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (userListner!=null) {
            mUserRef.removeEventListener(userListner);
        }
    }
}

