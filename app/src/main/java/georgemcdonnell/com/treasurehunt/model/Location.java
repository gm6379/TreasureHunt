package georgemcdonnell.com.treasurehunt.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by George on 21/10/2015.
 */
public class Location implements Parcelable {

    private String name;
    private int position;
    private String description;
    private LatLng coordinates;
    private String question;
    private String answer;
    private String clue;

    public Location() {}

    public Location(String name, int position, String description, LatLng coordinates, String question, String answer, String clue) {
        this.name = name;
        this.position = position;
        this.description = description;
        this.coordinates = coordinates;
        this.question = question;
        this.answer = answer;
        this.clue = clue;
    }

    public Location(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getPosition() { return position; }

    public String getDescription() {
        return description;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getClue() {
        return clue;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getFullDetails() {
        return "Name: " + name +
                "\n" + "Description: " + description +
                "\nCoordinates: " + coordinates.latitude + ", " + coordinates.longitude +
                "\nPosition: " + position +
                "\nClue: " + clue +
                "\nQuestion: " + question +
                "\nAnswer: " + answer;
    }

    public int describeContents() {
        return 0;
    }


    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeInt(position);
        out.writeString(description);
        out.writeDouble(coordinates.latitude);
        out.writeDouble(coordinates.longitude);
        out.writeString(question);
        out.writeString(answer);
        out.writeString(clue);
    }

    public static final Parcelable.Creator<Location> CREATOR
            = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    private Location(Parcel in) {
        name = in.readString();
        position = in.readInt();
        description = in.readString();
        coordinates = new LatLng(in.readDouble(), in.readDouble());
        question = in.readString();
        answer = in.readString();
        clue = in.readString();
    }

}
