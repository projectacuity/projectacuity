package com.example.mahdi.acuity.activities;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import com.example.mahdi.acuity.R;



public class MainActivity extends BaseDrawerActivity {


    RecyclerView rvPost;
    CoordinatorLayout clContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clContent= (CoordinatorLayout) findViewById(R.id.content);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.my_feed, new MyPostsFragment());
        ft.commit();
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
}