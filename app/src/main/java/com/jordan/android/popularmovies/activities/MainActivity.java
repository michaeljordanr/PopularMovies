package com.jordan.android.popularmovies.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jordan.android.popularmovies.R;
import com.jordan.android.popularmovies.adapters.ViewPagerAdapter;
import com.jordan.android.popularmovies.fragment.RecyclerViewFragment;
import com.jordan.android.popularmovies.utilities.Constants;
import com.jordan.android.popularmovies.utilities.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.sliding_tabs)
    TabLayout mTabLayout;

    private Bundle viewPagerState;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Get ActionBar.
        ActionBar actionBar = getSupportActionBar();

        // Set below attributes to add logo in ActionBar.
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.drawable.ic_launcher);

        actionBar.setTitle(getString(R.string.app_name));

        setupViewPager(mViewPager);

        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(RecyclerViewFragment.newInstance(Constants.POPULAR), getString(R.string.popular));
        adapter.addFragment(RecyclerViewFragment.newInstance(Constants.TOP_RATED), getString(R.string.top_rated));
        adapter.addFragment(RecyclerViewFragment.newInstance(Constants.FAVORITES), getString(R.string.favorites));

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
    }

}
