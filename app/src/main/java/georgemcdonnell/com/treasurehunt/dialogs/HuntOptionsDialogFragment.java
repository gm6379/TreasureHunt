package georgemcdonnell.com.treasurehunt.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import georgemcdonnell.com.treasurehunt.R;
import georgemcdonnell.com.treasurehunt.huntlocations.HuntLocationsActivity;
import georgemcdonnell.com.treasurehunt.hunts.AddHuntLocationFragment;
import georgemcdonnell.com.treasurehunt.hunts.HuntOptionsFragment;
import georgemcdonnell.com.treasurehunt.model.Location;


public class HuntOptionsDialogFragment extends DialogFragment implements AsyncResponse {

    private Location currentLocation;
    private Location nextLocation;
    private String clueAfterNextLocation;
    private String huntName;
    private String username;
    private ArrayList<Location> locationsVisited;
    private ArrayList<Location> huntLocations;
    public AsyncResponse delegate;

    public enum HuntState {
        HuntStateCompleted,
        HuntStateStarted,
        HuntStateRegistered,
        HuntStateNotRegistered,
        HuntStateNoLocations
    }

    public static HuntOptionsDialogFragment newInstance(String username, String huntName, boolean isCreatorCurrentUser, HuntState huntState, Location currentLocation, Location nextLocation, ArrayList<Location> locationsVisited, ArrayList<Location> huntLocations, String clueAfterNextLocation) {

        Bundle args = new Bundle();
        args.putString("username", username);
        args.putString("huntName", huntName);
        args.putSerializable("isCreatorCurrentUser", isCreatorCurrentUser);
        args.putSerializable("huntState", huntState);
        args.putParcelable("currentLocation", currentLocation);
        args.putParcelable("nextLocation", nextLocation);
        args.putParcelableArrayList("huntLocations", huntLocations);
        args.putParcelableArrayList("locationsVisited", locationsVisited);
        args.putString("clueAfterNextLocation", clueAfterNextLocation);

        HuntOptionsDialogFragment fragment = new HuntOptionsDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        username = getArguments().getString("username");
        huntName = getArguments().getString("huntName");
        boolean isCreatorCurrentUser = (boolean) getArguments().getSerializable("isCreatorCurrentUser");
        HuntState huntState = (HuntState) getArguments().getSerializable("huntState");
        currentLocation = getArguments().getParcelable("currentLocation");
        nextLocation = getArguments().getParcelable("nextLocation");
        locationsVisited = getArguments().getParcelableArrayList("locationsVisited");
        huntLocations = getArguments().getParcelableArrayList("huntLocations");
        clueAfterNextLocation = getArguments().getString("clueAfterNextLocation");

        builder.setTitle(huntName);

        String message = "";

        if (isCreatorCurrentUser) {
            message += getString(R.string.hunt_creator);
            builder.setPositiveButton(getString(R.string.view_hunt_locations), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    viewLocations(huntName);
                }
            });
        }

        switch (huntState) {
            case HuntStateCompleted:
                message += getString(R.string.hunt_completed);
                builder.setNeutralButton(getString(R.string.view_reached_locations), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewLocations(huntName);
                    }
                });
                break;
            case HuntStateStarted:
                message += getString(R.string.hunt_started);
                builder.setNeutralButton(getString(R.string.continue_hunt), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // open hunt options screen
                        viewHuntOptions();
                    }
                });
                break;
            case HuntStateRegistered:
                message += getString(R.string.hunt_registered);
                builder.setNeutralButton(getString(R.string.continue_hunt), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // open hunt options screen
                        viewHuntOptions();
                    }
                });
                break;
            case HuntStateNotRegistered:
                message += getString(R.string.hunt_not_started);
                builder.setNeutralButton(getString(R.string.start_hunt), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // start hunt request
                        String huntNameParameter = "huntname=" + huntName;
                        String usernameParameter = "username=" + username;
                        String params = huntNameParameter + "&" + usernameParameter;
                        StartHuntTask startHuntTask = new StartHuntTask();
                        startHuntTask.delegate = HuntOptionsDialogFragment.this;
                        startHuntTask.execute(params);
                    }
                });
                break;
            case HuntStateNoLocations:
                if (isCreatorCurrentUser) {
                    message += getString(R.string.no_locations_creator);

                    builder.setNeutralButton(getString(R.string.add_locations), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AddHuntLocationFragment newFragment = AddHuntLocationFragment.newInstance(AddHuntLocationFragment.LocationType.LocationTypeStart, huntName, 0, username);

                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, newFragment);
                            transaction.addToBackStack(null);

                            transaction.commit();
                        }
                    });

                } else {
                    message += getString(R.string.no_locations_not_creator);

                    builder.setNegativeButton("OK", null);
                }
                break;
        }

        builder.setMessage(message);

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void viewLocations(String huntName) {
        // open view hunt locations
        Intent intent = new Intent(getActivity(), HuntLocationsActivity.class);
        intent.putExtra("huntName", huntName);
        intent.putParcelableArrayListExtra("locations", huntLocations);
        startActivity(intent);
    }

    private void viewHuntOptions() {
        HuntOptionsFragment huntOptionsFragment;
        if (currentLocation == null) {
            huntOptionsFragment = HuntOptionsFragment.newInstance(username, huntName, nextLocation, clueAfterNextLocation);
        } else {
            huntOptionsFragment = HuntOptionsFragment.newInstance(username, huntName, nextLocation, locationsVisited, clueAfterNextLocation);
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, huntOptionsFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    private class StartHuntTask extends AsyncTask<String, Void, String> {
        public AsyncResponse delegate;


        @Override
        protected String doInBackground(String... params) {
            try {
               return postData(params[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String postData(String params) throws IOException {
            URL url = new URL("http://sots.brookes.ac.uk/~p0073862/services/hunt/starthunt");

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");

                DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
                outputStream.writeBytes(params);
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                outputStream.close();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return "Player is already on hunt.";
                }
                return null;
            } catch (MalformedURLException e) {
                return "MalformedURL Exception";
            } catch (IOException e) {
                return "IOException";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            delegate.processFinish(result);
        }
    }

    @Override
    public void processFinish(String output) {
        if (output != null) { // username already registered, display error
            this.delegate.processFinish(output);
        } else {
            HuntOptionsFragment huntOptionsFragment;
            if (currentLocation == null) {
                huntOptionsFragment = HuntOptionsFragment.newInstance(username, huntName, nextLocation, clueAfterNextLocation);
            } else {
                huntOptionsFragment = HuntOptionsFragment.newInstance(username, huntName, nextLocation, locationsVisited, clueAfterNextLocation);
            }
            this.delegate.processFinish(huntOptionsFragment);
        }
    }

    @Override
    public void processFinish(HuntOptionsFragment fragment) {}
}
