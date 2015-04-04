package br.com.sergioaugrod.savelocation.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.sergioaugrod.savelocation.R;
import br.com.sergioaugrod.savelocation.dao.LocalDAO;
import br.com.sergioaugrod.savelocation.model.Local;
import br.com.sergioaugrod.savelocation.util.Notificacao;


public class InicioActivity extends ActionBarActivity {

    private LocalDAO dao;
    private List<Local> locais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        dao = new LocalDAO(this);
        listarLocais();
        verificarGPS();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inicio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_configuracoes) {
            Intent intent = new Intent();
            intent.setAction("ACAO_TELA_CONFIGURACOES");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void criarNotificacao() {
        //Intent a ser executada quando se clicar na notificação.
        Intent intent =  new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        //Informações da notificação.
        String tickerText = "SaveLocation";
        String titulo = "SaveLocation";
        String mensagem = "Ative o GPS.";
        Notificacao.criar(this, tickerText, titulo, mensagem, R.drawable.ic_launcher, R.drawable.ic_launcher, intent);
    }

    private void criarAlerta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SaveLocation");
        builder.setMessage("O GPS está desativado, deseja ativa-lo agora?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(callGPSSettingIntent);
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                criarNotificacao();
                Toast.makeText(getApplicationContext(), "Sem o GPS ativado as funcionalidades do aplicativo não irão funcionar.", Toast.LENGTH_LONG).show();
            }
        });
        builder.create().show();
    }

    private void verificarGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsHabilitado = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!gpsHabilitado) {
            criarAlerta();
        }
    }

    private void listarLocais() {
        locais = dao.buscarTodos();
        ListView listView = (ListView) findViewById(R.id.listView);
        if(!locais.isEmpty()) {
            ArrayAdapter<Local> arrayAdapter = new ArrayAdapter<Local>(this, android.R.layout.simple_list_item_1, locais);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Local local = locais.get(position);
                    Intent intent = new Intent();
                    intent.putExtra("id", local.getId());
                    intent.setAction("ACAO_TELA_VER_LOCAL");
                    startActivity(intent);
                }
            });
        } else {
            TextView subTitulo = (TextView) findViewById(R.id.tvSubTitulo);
            listView.setVisibility(View.GONE);
            subTitulo.setText("Não há locais cadastrados.");
        }
    }

    public void onClickCadastrarLocal(View v) {
        Intent intent = new Intent();
        intent.setAction("ACAO_TELA_CADASTRAR_LOCAL");
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dao.fechar();
    }

}
