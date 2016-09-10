package georgemcdonnell.com.treasurehunt.huntlocations;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import georgemcdonnell.com.treasurehunt.R;
import georgemcdonnell.com.treasurehunt.model.Location;


public class HuntLocationsActivity extends FragmentActivity {

    private ViewPager viewPager;
    private HuntLocationsFragmentPagerAdapter adapter;
    private String huntName;
    private String username;
    private ArrayList<Location> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt_locations);

        huntName = getIntent().getStringExtra("huntName");
        username = getIntent().getStringExtra("username");
        locations = getIntent().getParcelableArrayListExtra("locations");


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new HuntLocationsFragmentPagerAdapter(getSupportFragmentManager(), huntName, username, locations);

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
}
