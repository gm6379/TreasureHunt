package georgemcdonnell.com.treasurehunt.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import georgemcdonnell.com.treasurehunt.R;


public class LocationDialogFragment extends DialogFragment {
    public static LocationDialogFragment newInstance(String locationDetails) {

        Bundle args = new Bundle();

        LocationDialogFragment fragment = new LocationDialogFragment();
        args.putString("locationDetails", locationDetails);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_dialog, container, false);
        String forecast = getArguments().getString("locationDetails");
        TextView locationTextView = (TextView) view.findViewById(R.id.location);
        locationTextView.setText(forecast);

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Location");
        return dialog;
    }


}
