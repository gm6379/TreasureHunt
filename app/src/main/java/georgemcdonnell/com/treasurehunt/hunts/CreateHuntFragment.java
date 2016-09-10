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
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import georgemcdonnell.com.treasurehunt.R;


public class CreateHuntFragment extends Fragment {

    private String username;

    public static CreateHuntFragment newInstance(String username) {

        Bundle args = new Bundle();
        args.putString("username", username);

        CreateHuntFragment fragment = new CreateHuntFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private class CreateHuntAsyncTask extends AsyncTask<String, Void, String> {

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

            if (result == null) {
                EditText huntNameField = (EditText) getView().findViewById(R.id.hunt_name_field);
                AddHuntLocationFragment newFragment = AddHuntLocationFragment.newInstance(AddHuntLocationFragment.LocationType.LocationTypeStart, huntNameField.getText().toString(), 0, username);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);

                transaction.commit();
            } else {
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }

        }

        public String postData(String params) throws IOException {
            URL url = new URL(getString(R.string.base_url) + getString(R.string.create_hunt_uri));

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
                    return getString(R.string.hunt_exists_error);
                } else {
                    return null;
                }
            } catch (MalformedURLException e) {
                return "MalformedURL";
            } catch (IOException e) {
                return "IOException";
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_create_hunt, container, false);
        username = getArguments().getString("username");

        Button button = (Button)  view.findViewById(R.id.create_hunt_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText huntNameField = (EditText) getView().findViewById(R.id.hunt_name_field);
                String huntName = "huntname=" + huntNameField.getText();
                String params = "username=" + username + "&" + huntName;
                CreateHuntAsyncTask task = new CreateHuntAsyncTask();
                task.execute(params);
            }
        });
        return view;
    }


}
