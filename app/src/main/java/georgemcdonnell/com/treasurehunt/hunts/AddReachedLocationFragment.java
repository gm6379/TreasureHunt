package georgemcdonnell.com.treasurehunt.hunts;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import georgemcdonnell.com.treasurehunt.R;
import georgemcdonnell.com.treasurehunt.model.Location;


public class AddReachedLocationFragment extends Fragment {

    private String question;
    private String answer;
    private String huntName;
    private String locationName;
    private String username;
    private String nextClue;

    public static AddReachedLocationFragment newInstance(Location location, String huntName, String username, String nextClue) {

        Bundle args = new Bundle();

        args.putString("question", location.getQuestion());
        args.putString("answer", location.getAnswer());
        args.putString("locationName", location.getName());
        args.putString("huntName", huntName);
        args.putString("username", username);
        args.putString("nextClue", nextClue);

        AddReachedLocationFragment fragment = new AddReachedLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_reached_location, container, false);

        question = getArguments().getString("question");
        answer = getArguments().getString("answer");
        huntName = getArguments().getString("huntName");
        locationName = getArguments().getString("locationName");
        username = getArguments().getString("username");
        nextClue = getArguments().getString("nextClue");

        TextView questionTextView = (TextView) view.findViewById(R.id.question_text_view);
        questionTextView.setText(question);

        final EditText answerField = (EditText) view.findViewById(R.id.reached_location_answer_field);

        Button submitReachedLocationButton = (Button) view.findViewById(R.id.submit_reached_location_button);
        submitReachedLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerField.getEditableText().toString().equals(answer)) {
                    // send reached request
                    String huntNameParameter = "huntname=" + huntName;
                    String usernameParameter = "&username=" + username;
                    String locationNameParamter = "&locationname=" + locationName;

                    TimeZone tz = TimeZone.getTimeZone("UTC");
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                    df.setTimeZone(tz);
                    String nowAsISO = df.format(new Date());

                    String dateParameter = "&date=" + nowAsISO;
                    String params = huntNameParameter + usernameParameter + locationNameParamter + dateParameter;

                    ReachedLocationTask task = new ReachedLocationTask();
                    task.execute(params);
                } else {
                    // display incorrect answer message
                    Toast.makeText(getActivity(), getString(R.string.answer_incorrect), Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    private class ReachedLocationTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            if (nextClue != null) {
                Toast.makeText(getActivity(), getString(R.string.answer_correct) + nextClue, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.completed_hunt), Toast.LENGTH_LONG).show();
            }
            getFragmentManager().popBackStack();
            getFragmentManager().popBackStack();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                return postData(params[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private Void postData(String params) throws IOException {
            URL url = new URL("http://sots.brookes.ac.uk/~p0073862/services/hunt/" + "reachlocation");

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

                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {} else {}

                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
