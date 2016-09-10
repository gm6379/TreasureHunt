package georgemcdonnell.com.treasurehunt.huntlocations;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import georgemcdonnell.com.treasurehunt.R;
import georgemcdonnell.com.treasurehunt.dialogs.LocationDialogFragment;
import georgemcdonnell.com.treasurehunt.model.Location;


public class HuntLocationMapFragment extends SupportMapFragment {

    private ArrayList<Location> locations;
    private GoogleMap map;
    private String huntName;

    private Map<Marker, Location> locationMap = new HashMap<>();

    public static HuntLocationMapFragment newInstance(String huntName, ArrayList<Location> locations) {
        Bundle args = new Bundle();
        args.putString("huntName", huntName);
        args.putParcelableArrayList("locations", locations);

        HuntLocationMapFragment fragment = new HuntLocationMapFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        huntName = getArguments().getString("huntName");
        locations = getArguments().getParcelableArrayList("locations");

        map = getMap();
        map.getUiSettings().setZoomControlsEnabled(true);

        if (locations != null) {
            for (int i = 0; i < locations.size(); i++) {
                Location location = locations.get(i);
                MarkerOptions opt = new MarkerOptions().position(location.getCoordinates()).title(location.getName());
                Marker marker = map.addMarker(opt);
                if (i == 0) {
                    marker.showInfoWindow();
                }
                locationMap.put(marker, location);
                if (i < locations.size() - 1) {
                    map.addPolyline(new PolylineOptions()
                            .add(location.getCoordinates(), locations.get(i + 1).getCoordinates())
                            .width(4)
                            .color(Color.BLUE).geodesic(true));
                }
            }
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(locations.get(0).getCoordinates(), 9));

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Location location = locationMap.get(marker);
                    DialogFragment locationDialogFragment = LocationDialogFragment.newInstance(getString(R.string.hunt_name) + huntName + "\n" + location.getFullDetails());
                    locationDialogFragment.show(getFragmentManager(), "dialog");

                    return true;
                }
            });
        } else {
            Toast.makeText(getActivity(), getString(R.string.not_reached_all), Toast.LENGTH_LONG).show();
        }

        return view;
    }

}
