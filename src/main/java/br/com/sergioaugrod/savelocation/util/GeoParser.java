package br.com.sergioaugrod.savelocation.util;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.com.sergioaugrod.savelocation.model.Rota;

public class GeoParser {

    protected URL feedUrl;

    public GeoParser(String feedUrl) {
        try {
            this.feedUrl = new URL(feedUrl);
        } catch (MalformedURLException e) {
        }
    }

    public Rota parse() {
        final Rota rota = new Rota();
        try {
            final String result = convertStreamToString(feedUrl.openConnection().getInputStream());
            JSONObject json = new JSONObject(result);
            JSONObject jsonRoute = json.getJSONArray("routes").getJSONObject(0);
            JSONObject leg = jsonRoute.getJSONArray("legs").getJSONObject(0);
            JSONArray steps = leg.getJSONArray("steps");
            final int numSteps = steps.length();
            JSONObject step;
            for (int i = 0; i < numSteps; i++) {
                step = steps.getJSONObject(i);
                rota.addPoints(decodePolyLine(step.getJSONObject("polyline").getString("points")));
            }
        } catch (Exception e) {
        }
        return rota;
    }

    private String convertStreamToString(final InputStream input) {
        final BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(input));
        final StringBuilder sBuf = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sBuf.append(line);
            }
        } catch (IOException e) {
        } finally {
            try {
                input.close();
            } catch (IOException e) {
            }
        }
        return sBuf.toString();
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded =
                new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;
        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat =
                    ((result & 1) != 0 ?
                            ~(result >> 1) :
                            (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ?
                    ~(result >> 1) :
                    (result >> 1));
            lng += dlng;
            decoded.add(
                    new LatLng(
                            (float)(lat / 1E5),
                            (float)(lng / 1E5)));
        }
        return decoded;
    }

}