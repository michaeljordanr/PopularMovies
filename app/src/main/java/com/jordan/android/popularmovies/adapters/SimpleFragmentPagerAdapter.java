package com.jordan.android.popularmovies.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jordan.android.popularmovies.R;
import com.jordan.android.popularmovies.fragment.RecyclerViewFragment;
import com.jordan.android.popularmovies.utilities.Filter;
import com.jordan.android.popularmovies.utilities.Keys;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = new RecyclerViewFragment();
        Bundle args = new Bundle();

        if (position == 0) {
            args.putInt(Keys.FILTER_KEY, Filter.POPULAR.getValue());
        } else if (position == 1){
            args.putInt(Keys.FILTER_KEY, Filter.TOP_RATED.getValue());
        }

        frag.setArguments(args);

        return frag;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.popular);
            case 1:
                return mContext.getString(R.string.top_rated);
            default:
                return null;
        }
    }
}
