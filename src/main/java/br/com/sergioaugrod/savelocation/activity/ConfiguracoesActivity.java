package br.com.sergioaugrod.savelocation.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import br.com.sergioaugrod.savelocation.R;


public class ConfiguracoesActivity extends Activity {

    private ToggleButton tbLimite;
    private EditText etNumLocais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);
        tbLimite = (ToggleButton) findViewById(R.id.tbLimite);
        etNumLocais = (EditText) findViewById(R.id.etNumLocais);
        eventoSwitchLimite();
        carregarConfiguracoes();
    }

    public void eventoSwitchLimite() {
        tbLimite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                etNumLocais.setVisibility(View.VISIBLE);
            } else {
                etNumLocais.setVisibility(View.GONE);
                etNumLocais.setText("");
            }
            }
        });
    }

    public void carregarConfiguracoes() {
        SharedPreferences pref = getSharedPreferences("configuracoes", MODE_PRIVATE);
        tbLimite.setChecked(pref.getBoolean("limite", false));
        etNumLocais.setText(pref.getString("numLocais", ""));
    }

    public void onClickCadastrarConfiguracoes(View view) {
        if(tbLimite.isChecked() && etNumLocais.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Digite o número limite de cadastros.", Toast.LENGTH_LONG).show();
        } else {
            SharedPreferences pref = getSharedPreferences("configuracoes", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("limite", tbLimite.isChecked());
            editor.putString("numLocais", etNumLocais.getText().toString());
            editor.commit();
            Toast.makeText(getApplicationContext(), "Configurações armazenadas com sucesso.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setAction("ACAO_TELA_INICIAL");
            startActivity(intent);
        }
    }

}