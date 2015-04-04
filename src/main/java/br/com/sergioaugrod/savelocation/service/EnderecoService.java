package br.com.sergioaugrod.savelocation.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.util.List;


public class EnderecoService extends IntentService {

    public static final String ACTION_EnderecoService = "br.com.sergioaugrod.EnderecoResponse";

    public EnderecoService() {
        super("EnderecoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<Address> enderecos;
        Geocoder geocoder = new Geocoder(this);
        try {
            double latitude = intent.getDoubleExtra("latitude", 0);
            double longitude =intent.getDoubleExtra("longitude", 0);
            enderecos = geocoder.getFromLocation(latitude, longitude, 1);
            String endereco = enderecos.get(0).getAddressLine(0);
            String cidade;
            if(enderecos.get(0).getMaxAddressLineIndex() > 3) {
                cidade = enderecos.get(0).getAddressLine(2);
            } else {
                cidade = enderecos.get(0).getAddressLine(1);
            }
            Intent enderecoBroadcast = new Intent();
            enderecoBroadcast.setAction(ACTION_EnderecoService);
            enderecoBroadcast.putExtra("endereco", endereco);
            enderecoBroadcast.putExtra("cidade", cidade);
            sendBroadcast(enderecoBroadcast);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
