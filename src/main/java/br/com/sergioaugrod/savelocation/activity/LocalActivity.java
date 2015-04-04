package br.com.sergioaugrod.savelocation.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import br.com.sergioaugrod.savelocation.R;
import br.com.sergioaugrod.savelocation.dao.LocalDAO;
import br.com.sergioaugrod.savelocation.model.Local;
import br.com.sergioaugrod.savelocation.service.EnderecoService;
import br.com.sergioaugrod.savelocation.task.RotaAsyncTask;
import br.com.sergioaugrod.savelocation.util.Notificacao;

public class LocalActivity extends FragmentActivity {

    private GoogleMap mMap;
    private LocalDAO dao;
    private Local local;
    private TextView tvDescricao;
    private TextView tvCidade;
    private TextView tvEndereco;
    private double latitudeAtual;
    private double longitudeAtual;
    private EnderecoReceiver enderecoReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long id = 0;
        dao = new LocalDAO(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getLong("id");
        }
        setContentView(R.layout.activiy_local);
        local = dao.buscar(id);
        setLocalAtual();
        tvDescricao = (TextView) findViewById(R.id.tvDescricao);
        tvCidade = (TextView) findViewById(R.id.tvCidade);
        tvEndereco = (TextView) findViewById(R.id.tvEndereco);
        //DEFINE A SERVICE E REGISTRA UM RECEIVER NESTA SERVICE.
        final Intent intentService = new Intent(this, EnderecoService.class);
        intentService.putExtra("latitude", local.getLatitude());
        intentService.putExtra("longitude", local.getLongitude());
        enderecoReceiver = new EnderecoReceiver();
        IntentFilter intentFilter = new IntentFilter(EnderecoService.ACTION_EnderecoService);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(enderecoReceiver, intentFilter);
        //SE TIVER CONEXAO COM A INTERNET INICIA A SERVICE PARA RECUPERAR AS INFORMACOES DA LOCALIDADE.
        if(verificarConexao()) {
            startService(intentService);
        }
        setDescricao();
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_local, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_editar) {
            editarLocal();
        } else if(id == R.id.action_remover) {
            removerLocal();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean verificarConexao() {
        ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void editarLocal() {
        Intent intent = new Intent();
        intent.putExtra("id", local.getId());
        intent.setAction("ACAO_TELA_EDITAR_LOCAL");
        startActivity(intent);
    }

    private void removerLocal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SaveLocation");
        builder.setMessage("Você tem certeza que deseja remover este local?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                dao.remover(local);
                Intent intent = new Intent();
                intent.setAction("ACAO_TELA_INICIAL");
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        builder.create().show();
    }

    private void setLocalAtual() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location location = null;
        for (int i = 0; i < providers.size(); i++) {
            location = locationManager.getLastKnownLocation(providers.get(i));
            if (location != null)
                break;
        }
        if (location != null) {
            latitudeAtual = location.getLatitude();
            longitudeAtual  = location.getLongitude();
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
        LatLng latLng = new LatLng(local.getLatitude(), local.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng).title("Local Cadastrado").snippet(local.getDescricao());
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
        mMap.addMarker(markerOptions);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.moveCamera(update);
        mMap.setMyLocationEnabled(true);
    }

    private void setEnderecoVisivel() {
        final View layoutCidade = findViewById(R.id.layoutCidade);
        final View layoutEndereco = findViewById(R.id.layoutEndereco);
        layoutCidade.setVisibility(View.VISIBLE);
        layoutEndereco.setVisibility(View.VISIBLE);
    }

    private void setEndereco(String endereco, String cidade) {
        tvEndereco.setText(endereco);
        tvCidade.setText(cidade);
        setEnderecoVisivel();
    }

    private void setDescricao() {
        tvDescricao.setText(local.getDescricao());
    }

    private void criarNotificacao() {
        //Intent a ser executada quando se clicar na notificação.
        Intent intent =  new Intent(Settings.ACTION_WIFI_SETTINGS);
        //Informações da notificação.
        String tickerText = "SaveLocation";
        String titulo = "SaveLocation";
        String mensagem = "Ative a INTERNET para traçar a rota.";
        Notificacao.criar(this, tickerText, titulo, mensagem, R.drawable.ic_launcher, R.drawable.ic_launcher, intent);
    }

    private void criarAlerta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SaveLocation");
        builder.setMessage("O INTERNET está desativada, deseja ativa-la agora?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(callGPSSettingIntent);
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                criarNotificacao();
                Toast.makeText(getApplicationContext(), "Sem a internet não será possível traçar a rota.", Toast.LENGTH_LONG).show();
            }
        });
        builder.create().show();
    }

    public void onClickTracarRota(View view) {
        if(verificarConexao()) {
            new RotaAsyncTask(this, mMap).execute(latitudeAtual, longitudeAtual, local.getLatitude(), local.getLongitude());
        } else {
            criarAlerta();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dao.fechar();
        unregisterReceiver(enderecoReceiver);
    }

    public class EnderecoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context c, Intent intent) {
            String cidade = intent.getStringExtra("cidade");
            String endereco = intent.getStringExtra("endereco");
            Log.i("TRETA", "cidade=" + cidade);
            setEndereco(endereco, cidade);
        }
    }

}