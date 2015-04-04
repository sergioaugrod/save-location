package br.com.sergioaugrod.savelocation.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import br.com.sergioaugrod.savelocation.R;
import br.com.sergioaugrod.savelocation.dao.LocalDAO;
import br.com.sergioaugrod.savelocation.model.Local;
import br.com.sergioaugrod.savelocation.util.Notificacao;

public class CadastroLocalActivity extends FragmentActivity implements LocationListener {

    private GoogleMap mMap;
    private LocalDAO dao;
    private double latitude;
    private double longitude;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dao = new LocalDAO(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_local);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setLocalAtual() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 0, this);
        List<String> providers = locationManager.getProviders(true);
        Location location = null;
        for (int i = 0; i < providers.size(); i++) {
            location = locationManager.getLastKnownLocation(providers.get(i));
            if (location != null)
                break;
        }
        if (location != null) {
            latitude = location.getLatitude();
            longitude  = location.getLongitude();
        }
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        setLocalAtual();
        LatLng latLng = new LatLng(latitude, longitude);
        //Marcador do Local Atual.
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng).title("SaveLocation").snippet("Local Atual");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
        mMap.addMarker(markerOptions);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.moveCamera(update);
    }

    private boolean verificarGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsHabilitado = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return gpsHabilitado;
    }

    public void onClickCadastrar(View view) {
        if(verificarGPS()) {
            SharedPreferences pref = getSharedPreferences("configuracoes", MODE_PRIVATE);
            if(pref.getBoolean("limite", false) && (dao.buscarTodos().size() == Integer.parseInt(pref.getString("numLocais", "0")))) {
                Toast.makeText(getApplicationContext(), "Não foi possível cadastrar, pois o limite de locais foi atingido.", Toast.LENGTH_SHORT).show();
            } else {
                EditText etDescricao = (EditText) findViewById(R.id.etDescricao);
                String descricao = etDescricao.getText().toString();
                if(!descricao.isEmpty()) {
                    Local local = new Local(descricao, latitude, longitude);
                    dao.inserir(local);
                    Toast.makeText(getApplicationContext(), "Local cadastrado com sucesso.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction("ACAO_TELA_INICIAL");
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Digite a descrição do local.", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Para cadastrar um local, ative o GPS.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dao.fechar();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

}