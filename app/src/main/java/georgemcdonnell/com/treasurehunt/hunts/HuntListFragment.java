package georgemcdonnell.com.treasurehunt.hunts;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import georgemcdonnell.com.treasurehunt.dialogs.HuntOptionsDialogFragment;
import georgemcdonnell.com.treasurehunt.dialogs.HuntOptionsDialogFragment.HuntState;
import georgemcdonnell.com.treasurehunt.R;
import georgemcdonnell.com.treasurehunt.adaptors.HuntArrayAdapter;
import georgemcdonnell.com.treasurehunt.dialogs.AsyncResponse;
import georgemcdonnell.com.treasurehunt.huntlocations.HuntLocationsTask;
import georgemcdonnell.com.treasurehunt.model.Hunt;
import georgemcdonnell.com.treasurehunt.model.Location;

public class HuntListFragment extends Fragment implements AsyncResponse {

    private ListView huntListView;
    private ArrayList<Hunt> hunts = new ArrayList<>();
    private HuntArrayAdapter huntsAdapter;
    private String username;

    public static HuntListFragment newInstance(String username) {

        Bundle args = new Bundle();
        args.putString("username", username);

        HuntListFragment fragment = new HuntListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private class LocationsVisitedTask extends HuntLocationsTask {

        private ArrayList<Location> huntLocations;
        private Hunt hunt;

        @Override
        protected void onPostExecute(ArrayList<Location> locationsVisited) {

            HuntState huntState;
            Location currentLocation;
            Location nextLocation;
            String clueAfterNextLocation;
            HuntOptionsDialogFragment huntOptionsDialogFragment;
            if (huntLocations.size() > 0) {
                if (locationsVisited.size() == huntLocations.size()) {
                    // hunt completed
                    huntState = HuntState.HuntStateCompleted;
                    locationsVisited = huntLocations;
                    currentLocation = locationsVisited.get(locationsVisited.size() - 1);
                    nextLocation = currentLocation;

                    huntOptionsDialogFragment = HuntOptionsDialogFragment.newInstance(username, hunt.getName(), hunt.isCreatorCurrentUser(), huntState, currentLocation, nextLocation, locationsVisited, huntLocations, null);
                    huntOptionsDialogFragment.show(getFragmentManager(), "dialog");

                } else if (locationsVisited.size() > 0) {
                    // hunt has been started
                    huntState = HuntState.HuntStateStarted;
                    currentLocation = locationsVisited.get(locationsVisited.size() - 1);
                    nextLocation = huntLocations.get(locationsVisited.size());

                    updateVisitedLocationDetails(locationsVisited);

                    if (locationsVisited.size() < huntLocations.size() - 1) {
                        clueAfterNextLocation = huntLocations.get(locationsVisited.size() + 1).getClue();
                        huntOptionsDialogFragment = HuntOptionsDialogFragment.newInstance(username, hunt.getName(), hunt.isCreatorCurrentUser(), huntState, currentLocation, nextLocation, locationsVisited, huntLocations, clueAfterNextLocation);
                    } else {
                        huntOptionsDialogFragment = HuntOptionsDialogFragment.newInstance(username, hunt.getName(), hunt.isCreatorCurrentUser(), huntState, currentLocation, nextLocation, locationsVisited, huntLocations, null);
                    }

                    huntOptionsDialogFragment.show(getFragmentManager(), "dialog");
                } else {
                    // no locations have been visited yet
                    nextLocation = huntLocations.get(0);

                    HasRegisteredOnHuntTask hasRegisteredOnHuntTask = new HasRegisteredOnHuntTask();
                    hasRegisteredOnHuntTask.hunt = hunt;
                    hasRegisteredOnHuntTask.nextLocation = nextLocation;
                    if (huntLocations.size() > 1) {
                        hasRegisteredOnHuntTask.clueAfterNextLocation = huntLocations.get(1).getClue();
                    }
                    hasRegisteredOnHuntTask.huntLocations = huntLocations;
                    try {
                        URL url = new URL(getString(R.string.base_url) + getString(R.string.players_uri) + hunt.getName());
                        hasRegisteredOnHuntTask.execute(url);
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                    }
                }

            } else {
                // hunt has no locations
                huntState = HuntState.HuntStateNoLocations;
                huntOptionsDialogFragment = HuntOptionsDialogFragment.newInstance(username, hunt.getName(), hunt.isCreatorCurrentUser(), huntState, null, null, null, null, null);
                huntOptionsDialogFragment.show(getFragmentManager(), "dialog");
            }
        }

        private void updateVisitedLocationDetails(ArrayList<Location> locationsVisited) {
            // replace locations visited with all details from hunt locations
            for (int i = 0; i < locationsVisited.size(); i++) {
                Location location = locationsVisited.get(i);
                for (Location huntLocation : huntLocations) {
                    if (huntLocation.getName().equals(location.getName())) {
                        locationsVisited.set(i, huntLocation);
                    }
                }
            }
        }
    }

    private class CompletedHuntTask extends HuntLocationsTask {

        private Hunt hunt;

        @Override
        protected void onPostExecute(ArrayList<Location> locations) {
            LocationsVisitedTask huntLocationsTask = new LocationsVisitedTask();
            huntLocationsTask.huntLocations = locations;
            huntLocationsTask.hunt = hunt;
            try {
                URL url = new URL(getString(R.string.base_url) + getString(R.string.reached_uri) + hunt.getName() + "/" + username);
                huntLocationsTask.execute(url);
            } catch (MalformedURLException ex) {
                Log.d("Malformed URL exception", ex.getMessage());
            }
        }
    }

    private class HasRegisteredOnHuntTask extends AsyncTask<URL, Void, Boolean> {

        private Hunt hunt;
        private Location nextLocation;
        private ArrayList<Location> huntLocations;
        private String clueAfterNextLocation;

        @Override
        protected Boolean doInBackground(URL... params) {
            try {
                URLConnection connection = params[0].openConnection();
                HttpURLConnection httpConnection =
                        (HttpURLConnection) connection;
                int responseCode = httpConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = httpConnection.getInputStream();
                    DocumentBuilderFactory dbf = DocumentBuilderFactory
                            .newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document dom = db.parse(in);

                    Element ele = dom.getDocumentElement();
                    NodeList userNodes = ele.getElementsByTagName("user");
                    for (int i = 0; i < userNodes.getLength(); i++) {
                        Element usernameEle = (Element) userNodes.item(i);
                        String name = usernameEle.getTextContent();
                        if (name.equals(username)) {
                            return true;
                        }
                    }

                    return false;
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean registered) {
            HuntState huntState;
            HuntOptionsDialogFragment huntOptionsDialogFragment;
            if (registered) { // user has registered but not started
                huntState = HuntState.HuntStateRegistered;
            } else {
                huntState = HuntState.HuntStateNotRegistered;
            }
            huntOptionsDialogFragment = HuntOptionsDialogFragment.newInstance(username, hunt.getName(), hunt.isCreatorCurrentUser(), huntState, null, nextLocation, null, huntLocations, clueAfterNextLocation);
            huntOptionsDialogFragment.delegate = HuntListFragment.this;
            huntOptionsDialogFragment.show(getFragmentManager(), "dialog");
        }

    }

    private class HuntsTask extends AsyncTask<URL, Void, ArrayList<Hunt>> {

        @Override
        protected ArrayList<Hunt> doInBackground(URL... params) {
            ArrayList<Hunt> hunts = new ArrayList<>();
            try {
                URLConnection connection = params[0].openConnection();
                HttpURLConnection httpConnection =
                        (HttpURLConnection) connection;
                int responseCode = httpConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = httpConnection.getInputStream();
                    DocumentBuilderFactory dbf = DocumentBuilderFactory
                            .newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document dom = db.parse(in);

                    Element ele = dom.getDocumentElement();
                    NodeList huntNodes = ele.getElementsByTagName("hunt");
                    for (int i = 0; i < huntNodes.getLength(); i++) {
                        Element huntEl = (Element) huntNodes.item(i);
                        String huntName = huntEl.getFirstChild().getTextContent();
                        String huntCreator =  huntEl.getLastChild().getTextContent();
                        boolean isCurrentUser = false;

                        if (huntCreator.equals(username)) {
                            isCurrentUser = true;
                        }
                        Hunt hunt = new Hunt(huntCreator, huntName, isCurrentUser);
                        hunts.add(hunt);
                    }
                }
                return hunts;
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Hunt> result) {
            hunts.clear();
            hunts.addAll(result);
            huntsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hunt_list, container, false);
        setHasOptionsMenu(true);
        username = getArguments().getString("username");

        huntListView = (ListView) view.findViewById(R.id.hunt_list_view);
        huntListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Hunt hunt = hunts.get(position);

                CompletedHuntTask completedHuntTask = new CompletedHuntTask();
                completedHuntTask.hunt = hunt;
                try {
                    URL url = new URL(getString(R.string.base_url) + getString(R.string.locations_uri) + hunt.getName());
                    completedHuntTask.execute(url);
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }

            }
        });

        huntsAdapter = new HuntArrayAdapter(getContext(), android.R.layout.simple_list_item_1, hunts);
        huntListView.setAdapter(huntsAdapter);

        HuntsTask huntsTask = new HuntsTask();
        try {
            URL url = new URL(getString(R.string.base_url) + getString(R.string.hunts_uri));
            huntsTask.execute(url);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        huntsAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Create hunt fragment
        CreateHuntFragment newFragment = CreateHuntFragment.newInstance(username);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
        item.setVisible(false);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void processFinish(HuntOptionsFragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    @Override
    public void processFinish(String output) {
        Toast.makeText(getActivity(), output, Toast.LENGTH_LONG).show();
    }
}
