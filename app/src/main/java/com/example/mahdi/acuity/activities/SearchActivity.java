package com.example.mahdi.acuity.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.mahdi.acuity.R;
import com.example.mahdi.acuity.adpaters.SearchViewHolder;
import com.example.mahdi.acuity.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class SearchActivity extends AppCompatActivity {

    private FirebaseRecyclerAdapter<User, SearchViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private MyLinearLayoutManager mManager;
    private DatabaseReference mDatabase;
    protected FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar toolbar;
    private String mInput;
    MenuItem searchItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mRecycler = (RecyclerView) findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);
        mManager = new MyLinearLayoutManager(this);
        mRecycler.setLayoutManager(mManager);

    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchItem.expandActionView();
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mInput=searchView.getQuery().toString();
                if (mAdapter!=null) {
                    mAdapter.cleanup();
                }
                if (mInput.equals("")) {
                    mRecycler.setAdapter(null);
                }
                else {
                    mAdapter = new FirebaseRecyclerAdapter<User, SearchViewHolder>(User.class, R.layout.item_search,
                            SearchViewHolder.class, mDatabase.child("users").orderByChild("username").startAt(mInput).endAt(mInput+"~")) {
                        @Override
                        protected void populateViewHolder(final SearchViewHolder viewHolder, final User model, final int position) {
                            setUserPhoto(viewHolder, model.photoUrl);
                            viewHolder.bindToSearch(model, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                        Intent intent = new Intent(getApplicationContext(),UserProfileActivity.class);
                                        intent.putExtra("uid",model.uid);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                }
                            });
                        }
                    };
                    mRecycler.setAdapter(mAdapter);
                }
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }


        return super.onOptionsItemSelected(item);
    }


    private void setUserPhoto(final SearchViewHolder viewHolder, String authorUrl) {
        final Context contextUserPhoto = viewHolder.userProfilePhoto.getContext();
        if (authorUrl != null) {
            Glide.with(contextUserPhoto).load(authorUrl).bitmapTransform(new CropCircleTransformation(contextUserPhoto)).into(viewHolder.userProfilePhoto);
        }
    }
    public class MyLinearLayoutManager extends LinearLayoutManager {

        public MyLinearLayoutManager(Context context) {
            super(context);
        }

        public MyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public MyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

}

