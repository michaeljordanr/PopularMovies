package com.jordan.android.popularmovies.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jordan.android.popularmovies.R;
import com.jordan.android.popularmovies.adapters.SimpleFragmentPagerAdapter;
import com.jordan.android.popularmovies.fragment.RecyclerViewFragment;
import com.jordan.android.popularmovies.utilities.Constants;
import com.jordan.android.popularmovies.utilities.Filter;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.sliding_tabs)
    TabLayout mTabLayout;

    private Filter filterSelected;
    private SimpleFragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAdapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());

        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                Log.i("onPageSelected", "onPageSelected " + arg0);

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                Log.i("onPageScrolled", "onPageScrolled " + arg0);

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                Log.i("Doing something here", "On Scroll state changed " + arg0);
            }
        });

        mTabLayout.setupWithViewPager(mViewPager);

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (intent.hasExtra(Constants.FILTER_KEY)) {
                    filterSelected = Filter.fromValue(
                            intent.getIntExtra(Constants.FILTER_KEY, Filter.POPULAR.getValue()));

                    mViewPager.setAdapter(mAdapter);

                    new Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    mTabLayout.getTabAt(filterSelected.getValue() -1).select();
                                }
                            }, 100);
                }
            }
        }
    }
}
