package br.com.sergioaugrod.savelocation.activity;

import android.content.Intent;
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

import br.com.sergioaugrod.savelocation.R;
import br.com.sergioaugrod.savelocation.dao.LocalDAO;
import br.com.sergioaugrod.savelocation.model.Local;


public class EditarLocalActivity extends FragmentActivity {

    private GoogleMap mMap;
    private LocalDAO dao;
    private Local local;
    private EditText etDescricao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long id = 0;
        dao = new LocalDAO(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getLong("id");
        }
        local = dao.buscar(id);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_local);
        etDescricao = (EditText) findViewById(R.id.etDescricaoEdit);
        setDescricao();
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        LatLng latLng = new LatLng(local.getLatitude(), local.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng).title("Local Cadastrado").snippet(local.getDescricao());
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
        mMap.addMarker(markerOptions);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.moveCamera(update);
    }

    private void setDescricao() {
        etDescricao.setText(local.getDescricao());
    }

    public void onClickEditarLocal(View view) {
        local.setDescricao(etDescricao.getText().toString());
        dao.atualizar(local);
        Toast.makeText(getApplicationContext(), "Local editado com sucesso.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.putExtra("id", local.getId());
        intent.setAction("ACAO_TELA_INICIAL");
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dao.fechar();
    }

}
