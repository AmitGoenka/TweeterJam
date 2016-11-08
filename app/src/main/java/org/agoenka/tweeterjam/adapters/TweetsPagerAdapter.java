package org.agoenka.tweeterjam.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.agoenka.tweeterjam.fragments.HomeTimelineFragment;
import org.agoenka.tweeterjam.fragments.MentionsTimelineFragment;

/**
 * Author: agoenka
 * Created At: 11/5/2016
 * Version: ${VERSION}
 */
// Return the oder of the fragments in the view pager
public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {

    private final String tabTitles[] = {"Home", "Mentions"};

    // Adapter gets the manager insert or remove fragment from activity
    public TweetsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    // The order and creation of fragments within the pager
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeTimelineFragment();
            case 1:
                return new MentionsTimelineFragment();
            default:
                return null;
        }
    }

    // Return the tab title
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    // How many fragments there are to swipe between?
    @Override
    public int getCount() {
        return tabTitles.length;
    }
}