package georgemcdonnell.com.treasurehunt.huntlocations;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import georgemcdonnell.com.treasurehunt.model.Location;

/**
 * Created by George on 05/11/2015.
 */
public class HuntLocationsFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private static final String[] TAB_TITLES = {"Map", "List"};
    private String huntName;
    private String username;
    private ArrayList<Location> locations;

    public HuntLocationsFragmentPagerAdapter(FragmentManager fm, String huntName, String username, ArrayList<Location> locations) {
        super(fm);

        this.huntName = huntName;
        this.username = username;
        this.locations = locations;
    }


    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return HuntLocationMapFragment.newInstance(huntName, locations);
        } else {
            return HuntLocationListFragment.newInstance(huntName, locations);
        }
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }
}
