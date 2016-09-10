package georgemcdonnell.com.treasurehunt.hunts;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import georgemcdonnell.com.treasurehunt.R;


public class AddHuntLocationFragment extends Fragment {

    public static enum LocationType {
        LocationTypeStart,
        LocationTypeEnd,
        LocationTypeIntermediate
    }

    private LocationType locationType;
    private String huntName;
    private int lastPosition;
    private String username;


    private class AddHuntLocationTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                return postData(params[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            } else {
                Fragment fragment;
                if (locationType == LocationType.LocationTypeEnd) {
                    fragment = new HuntListFragment().newInstance(username);
                } else {
                    fragment = AddHuntLocationFragment.newInstance(LocationType.LocationTypeIntermediate, huntName, lastPosition + 1, username);
                }

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);

                transaction.commit();
            }
        }

        public String postData(String params) throws IOException {
            URL url = new URL("http://sots.brookes.ac.uk/~p0073862/services/hunt/" + "addlocation");
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
                    return "Invalid data entered";
                }

                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static AddHuntLocationFragment newInstance(LocationType locationType, String huntName, int lastPosition, String username) {

        Bundle args = new Bundle();
        args.putSerializable("locationType", locationType);
        args.putString("huntName", huntName);
        args.putInt("lastPosition", lastPosition);
        args.putString("username", username);

        AddHuntLocationFragment fragment = new AddHuntLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_hunt_location, container, false);
        TextView titleTextView = (TextView) view.findViewById(R.id.add_hunt_location);

        Button addEndLocationButton = (Button) view.findViewById(R.id.add_hunt_end_location_button);

        final LocationType locationType = (LocationType) getArguments().getSerializable("locationType");

        if (locationType == LocationType.LocationTypeStart) {
            this.locationType = LocationType.LocationTypeStart;
            titleTextView.setText(R.string.add_hunt_start_location);
        } else {
            this.locationType = LocationType.LocationTypeIntermediate;
            titleTextView.setText(R.string.add_hunt_location);
            addEndLocationButton.setVisibility(View.VISIBLE);
        }

        huntName = getArguments().getString("huntName");
        lastPosition = getArguments().getInt("lastPosition");
        username = getArguments().getString("username");

        Button addLocationButton = (Button) view.findViewById(R.id.add_hunt_location_button);
        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddLocationTask();
            }
        });

        addEndLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocationType(LocationType.LocationTypeEnd);
                startAddLocationTask();
            }
        });

        return view;
    }

    private void startAddLocationTask() {
        // add location to hunt
        String huntName = "huntname=" + this.huntName;
        String locationName = "&locationname=" + ((EditText) getView().findViewById(R.id.location_name_field)).getText();
        String position = "&position=" + String.valueOf(lastPosition + 1);
        String description = "&description=" + ((EditText) getView().findViewById(R.id.location_description_field)).getText();
        String latitude = "&latitude=" + ((EditText) getView().findViewById(R.id.location_latitude_field)).getText();
        String longitude = "&longitude=" + ((EditText) getView().findViewById(R.id.location_longitude_field)).getText();
        String question = "&question=" + ((EditText) getView().findViewById(R.id.location_question_field)).getText();
        String answer = "&answer=" + ((EditText) getView().findViewById(R.id.location_answer_field)).getText();
        String clue = "&clue=" + ((EditText) getView().findViewById(R.id.location_clue_field)).getText();

        String params = huntName + locationName + position + description + latitude + longitude + question + answer + clue;

        AddHuntLocationTask task = new AddHuntLocationTask();
        task.execute(params);
    }
}
