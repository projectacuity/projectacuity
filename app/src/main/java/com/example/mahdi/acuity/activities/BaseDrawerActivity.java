package com.example.mahdi.acuity.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mahdi.acuity.R;
import com.example.mahdi.acuity.models.User;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class BaseDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected static final String TAG = "BaseDrawerActivity";
    protected SessionManager session;
    protected DrawerLayout drawerLayout;
    protected NavigationView vNavigation;
    protected android.support.v7.widget.Toolbar toolbar;
    protected ImageView userProfilePhoto;
    protected TextView userName;
    protected FirebaseAuth mAuth;
    protected DatabaseReference mUserRef;
    protected ProgressDialog mProgress;
    protected FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_drawer);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.flContentRoot);
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);
        session = new SessionManager(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        vNavigation = (NavigationView) findViewById(R.id.vNavigation);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Signing out...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        mAuth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setupNavHeader();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        vNavigation.setNavigationItemSelectedListener(this);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_new_post);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),UploadPostActivity.class);
                startActivity(intent);
            }
        });

    }
    public void signOut() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.alert)
                .setTitle("Sign out")
                .setMessage("Are you sure you want to sign out from your Acuity account?")
                .setPositiveButton("SIGN OUT", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mProgress.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mAuth.signOut();
                                session.logoutUser();
                                finish();
                                mProgress.dismiss();
                            }
                        },2000);
                    }

                })
                .setNegativeButton("CANCEL", null)
                .show();
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
        } else if (id == R.id.nav_account) {
            Intent Profile = new Intent(getApplicationContext(),UserProfileActivity.class);
            startActivity(Profile);
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            signOut();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void setupNavHeader() {
        View headerView = vNavigation.getHeaderView(0);
        headerView.findViewById(R.id.vGlobalMenuHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
        userName = (TextView) headerView.findViewById(R.id.userName);
        userProfilePhoto = (ImageView) headerView.findViewById(R.id.userProfilePhoto);
        setNavHeaderInfo();
    }
    private void setNavHeaderInfo() {
        mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                userName.setText(user.getUsername());
                if (user.getPhotoUrl()!=null) {
                    Glide.with(userProfilePhoto.getContext()).load(user.getPhotoUrl()).centerCrop().into(userProfilePhoto);
                }
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    }
