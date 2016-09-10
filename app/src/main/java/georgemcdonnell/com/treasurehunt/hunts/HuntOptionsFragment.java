package georgemcdonnell.com.treasurehunt.hunts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import georgemcdonnell.com.treasurehunt.R;
import georgemcdonnell.com.treasurehunt.huntlocations.HuntLocationsActivity;
import georgemcdonnell.com.treasurehunt.model.Location;

public class HuntOptionsFragment extends Fragment {

    private String huntName;
    private String username;
    private ArrayList<Location> locationsVisited;
    private String clueAfterNextLocation;

    public static HuntOptionsFragment newInstance(String username, String huntName, Location nextLocation, ArrayList<Location> locationsVisited, String clueAfterNextLocation) {

        Bundle args = new Bundle();
        args.putParcelable("nextLocation", nextLocation);
        args.putString("username", username);
        args.putString("huntName", huntName);
        args.putParcelableArrayList("locationsVisited", locationsVisited);
        args.putString("clueAfterNextLocation", clueAfterNextLocation);

        HuntOptionsFragment fragment = new HuntOptionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static HuntOptionsFragment newInstance(String username, String huntName, Location nextLocation, String clueAfterNextLocation) {

        Bundle args = new Bundle();
        args.putParcelable("nextLocation", nextLocation);
        args.putString("username", username);
        args.putString("huntName", huntName);
        args.putString("clueAfterNextLocation", clueAfterNextLocation);

        HuntOptionsFragment fragment = new HuntOptionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hunt_options, container, false);

        final Location nextLocation = getArguments().getParcelable("nextLocation");
        huntName = getArguments().getString("huntName");
        username = getArguments().getString("username");
        locationsVisited = getArguments().getParcelableArrayList("locationsVisited");
        clueAfterNextLocation = getArguments().getString("clueAfterNextLocation");

        // display clue to next location
        TextView clueTextView = (TextView) view.findViewById(R.id.clue);
        clueTextView.setText(nextLocation.getClue());

        Button viewReachedLocationsButton = (Button) view.findViewById(R.id.view_reached_locations_button);
        viewReachedLocationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open view hunt locations
                Intent intent = new Intent(getActivity(), HuntLocationsActivity.class);
                intent.putExtra("huntName", huntName);
                intent.putExtra("username", username);
                intent.putParcelableArrayListExtra("locations", locationsVisited);
                startActivity(intent);
            }
        });

        Button addReachedLocationButton = (Button) view.findViewById(R.id.add_reached_location_button);
        addReachedLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddReachedLocationFragment addReachedLocationFragment = AddReachedLocationFragment.newInstance(nextLocation, huntName, username, clueAfterNextLocation);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, addReachedLocationFragment);
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });

        return view;
    }
}
