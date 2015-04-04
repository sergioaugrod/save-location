package br.com.sergioaugrod.savelocation.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Locale;

import br.com.sergioaugrod.savelocation.model.Rota;
import br.com.sergioaugrod.savelocation.util.GeoParser;

public class RotaAsyncTask extends
        AsyncTask<Double, Void, Void> {

    private ProgressDialog dialog;
    private GoogleMap mapView;
    private Context context;
    private Rota rota;

    public RotaAsyncTask(Context ctx, GoogleMap mapa) {
        mapView = mapa;
        context = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Double... params) {
        rota = directions(
                new LatLng(params[0], params[1]),
                new LatLng(params[2], params[3]));
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        PolylineOptions options = new PolylineOptions()
                .width(5)
                .color(Color.RED)
                .visible(true);
        for (LatLng latlng : rota.getPoints()) {
            options.add(latlng);
        }
        mapView.addPolyline(options);
    }

    private Rota directions(final LatLng start, final LatLng dest) {
        String urlRota = String.format(Locale.US,
                "http://maps.googleapis.com/maps/api/"+
                        "directions/json?origin=%f,%f&"+
                        "destination=%f,%f&" +
                        "sensor=true&mode=driving",
                start.latitude,
                start.longitude,
                dest.latitude,
                dest.longitude);
        GeoParser parser;
        parser = new GeoParser(urlRota);
        return parser.parse();
    }

}