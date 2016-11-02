package com.gircenko.gabriel.run4est.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.gircenko.gabriel.run4est.fragments.JogFragment;

/**
 * Created by Gabriel Gircenko on 30-Oct-16.
 */

public class JogPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    public static final int PAGE_COUNT = 7;
    private JogFragment[] fragments = new JogFragment[PAGE_COUNT];

    public JogPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments[position] == null) {
            fragments[position] = new JogFragment();
        }

        return fragments[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
