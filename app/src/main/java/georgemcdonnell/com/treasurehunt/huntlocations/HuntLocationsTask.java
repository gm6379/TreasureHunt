package georgemcdonnell.com.treasurehunt.huntlocations;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import georgemcdonnell.com.treasurehunt.model.Location;

/**
 * Created by George on 04/11/2015.
 */
public class HuntLocationsTask extends AsyncTask<URL, Void, ArrayList<Location>> {

    @Override
    protected ArrayList<Location> doInBackground(URL... params) {
        ArrayList<Location> locations = new ArrayList<>();
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
                NodeList locationNodes = ele.getElementsByTagName("location");
                if (params[0].toString().contains("/reached/")) {
                    for (int i = 0; i < locationNodes.getLength(); i++) {
                        Element locationEl = (Element) locationNodes.item(i);
                        NodeList locationChildren = locationEl.getChildNodes();
                        String name = locationChildren.item(0).getTextContent();

                        Location location = new Location(name);
                        locations.add(location);
                    }
                } else {
                    for (int i = 0; i < locationNodes.getLength(); i++) {
                        Element locationEl = (Element) locationNodes.item(i);
                        NodeList locationChildren = locationEl.getChildNodes();
                        String name = locationChildren.item(0).getTextContent();
                        int position =  Integer.parseInt(locationChildren.item(1).getTextContent());
                        String description = locationChildren.item(2).getTextContent();
                        double latitude = Double.parseDouble(locationChildren.item(3).getTextContent());
                        double longitude = Double.parseDouble(locationChildren.item(4).getTextContent());
                        LatLng coordinates = new LatLng(latitude, longitude);
                        String question = locationChildren.item(5).getTextContent();
                        String answer = locationChildren.item(6).getTextContent();
                        String clue = locationChildren.item(7).getTextContent();

                        Location location = new Location(name, position, description, coordinates, question, answer, clue);
                        locations.add(location);
                    }

                    Collections.sort(locations, new Comparator<Location>() {
                        @Override
                        public int compare(Location left, Location right) {
                            Integer leftPosition = new Integer(left.getPosition());
                            Integer rightPosition = new Integer(right.getPosition());
                            return leftPosition.compareTo(rightPosition);
                        }
                    });
                }

            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    return locations;
    }

}
