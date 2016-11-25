package com.example.mahdi.acuity.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mahdi.acuity.R;
import com.example.mahdi.acuity.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class BaseDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected Activity activity=this;
    protected DrawerLayout drawerLayout;
    protected NavigationView vNavigation;
    protected android.support.v7.widget.Toolbar toolbar;
    protected ImageView userProfilePhoto;
    protected TextView userName;
    protected FirebaseAuth mAuth;
    protected DatabaseReference mUserRef;
    protected ValueEventListener userListner;
    protected ProgressDialog mProgress;
    protected FloatingActionButton floatingActionButton;
    protected static final int REQUEST_PERMISSIONS = 1;

    public Activity getActivity() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_drawer);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.flContentRoot);
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);
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
                allowPermissions();
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
                                Intent intent= new Intent(getApplicationContext(),SplashActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                                mProgress.dismiss();
                            }
                        },2000);
                    }

                })
                .setNegativeButton("CANCEL", null)
                .show();
    }
    private void allowPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.CAMERA)) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Please Grant Permissions",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(BaseDrawerActivity.this,
                                        new String[]{Manifest.permission
                                                .READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                        REQUEST_PERMISSIONS);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(BaseDrawerActivity.this,
                        new String[]{Manifest.permission
                                .READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        REQUEST_PERMISSIONS);
            }
        }
        else {
            Intent intent = new Intent(getApplicationContext(),UploadPostActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if ((grantResults.length > 0) && (grantResults[0] +
                        grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getApplicationContext(),UploadPostActivity.class);
                    startActivity(intent);

                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Enable Permissions from settings",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(intent);
                                }
                            }).show();
                }
                return;
            }
        }
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (this instanceof UserProfileActivity){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if  (item.getItemId()==R.id.action_search) {
            Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.nav_account) {
            Intent intent = new Intent(getApplicationContext(),UserProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_logout) {
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
        userListner = mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                userName.setText(user.getUsername());
                if (user.getPhotoUrl()!=null) {
                    Glide.with(getApplicationContext()).load(user.getPhotoUrl()).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(userProfilePhoto);
                }
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    }
