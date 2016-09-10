package georgemcdonnell.com.treasurehunt.huntlocations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import georgemcdonnell.com.treasurehunt.R;
import georgemcdonnell.com.treasurehunt.dialogs.LocationDialogFragment;
import georgemcdonnell.com.treasurehunt.model.Location;


public class HuntLocationListFragment extends ListFragment {

    private ArrayList<Location> locations;
    private ArrayAdapter<Location> locationsAdapter;
    private String huntName;

    public static HuntLocationListFragment newInstance(String huntName, ArrayList<Location> locations) {

        Bundle args = new Bundle();
        args.putString("huntName", huntName);
        args.putParcelableArrayList("locations", locations);

        HuntLocationListFragment fragment = new HuntLocationListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_list, container, false);

        huntName = getArguments().getString("huntName");
        locations = getArguments().getParcelableArrayList("locations");

        if (locations != null) {
            locationsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, locations);
            setListAdapter(locationsAdapter);

            locationsAdapter.notifyDataSetChanged();
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogFragment locationDialogFragment = LocationDialogFragment.newInstance(getString(R.string.hunt_name) + huntName + "\n" + locations.get(position).getFullDetails());
                locationDialogFragment.show(getFragmentManager(), "dialog");
            }
        });
    }
}
