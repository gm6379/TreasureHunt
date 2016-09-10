package georgemcdonnell.com.treasurehunt.adaptors;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import georgemcdonnell.com.treasurehunt.model.Hunt;

/**
 * Created by George on 06/11/2015.
 */
public class HuntArrayAdapter extends ArrayAdapter<Hunt> {

    public HuntArrayAdapter(Context context, int resource, ArrayList<Hunt> hunts) {
        super(context, resource, hunts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        Hunt hunt = super.getItem(position);

        if (hunt.isCreatorCurrentUser()) {
            view.setBackgroundColor(Color.CYAN);
        } else {
            view.setBackgroundColor(Color.WHITE);
        }

        return view;
    }
}
