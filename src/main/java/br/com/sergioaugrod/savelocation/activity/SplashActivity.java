package br.com.sergioaugrod.savelocation.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import br.com.sergioaugrod.savelocation.R;


public class SplashActivity extends Activity implements Runnable {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        handler.postDelayed(this, 3000);
    }

    public void run(){
        Intent intent = new Intent();
        intent.setAction("ACAO_TELA_INICIAL");
        startActivity(intent);
        finish();
    }

}
