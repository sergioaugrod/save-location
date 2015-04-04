package br.com.sergioaugrod.savelocation.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Rota {
    private final List<LatLng> points;
    private String polyline;

    public Rota() {
        points = new ArrayList<LatLng>();
    }

    public void addPoints(final List<LatLng> points) {
        this.points.addAll(points);
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }

    public String getPolyline() {
        return polyline;
    }
}