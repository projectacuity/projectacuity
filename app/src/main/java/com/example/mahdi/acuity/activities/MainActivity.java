package com.example.mahdi.acuity.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.mahdi.acuity.R;
import com.example.mahdi.acuity.activities.fragments.MyFlopPostsFragment;
import com.example.mahdi.acuity.activities.fragments.MyTopPostsFragment;
import com.example.mahdi.acuity.activities.fragments.RecentPostsFragment;


public class MainActivity extends BaseDrawerActivity {

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
                    new RecentPostsFragment(),
                    new MyTopPostsFragment(),
                    new MyFlopPostsFragment()
            };
            private final String[] mFragmentNames = new String[] {
                    "Recent",
                    "Top posts",
                    "Flop posts"
            };
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }
            @Override
            public int getCount() {
                return mFragments.length;
            }
            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }
}