package br.com.sergioaugrod.savelocation.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.sergioaugrod.savelocation.model.Local;
import br.com.sergioaugrod.savelocation.util.DatabaseHelper;

public class LocalDAO {

    private SQLiteDatabase bd;

    public LocalDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        bd = dbHelper.getWritableDatabase();
    }

    public void inserir(Local local) {
        ContentValues valores = new ContentValues();
        valores.put("descricao", local.getDescricao());
        valores.put("latitude", local.getLatitude());
        valores.put("longitude", local.getLongitude());
        bd.insert("local", null, valores);
    }

    public List<Local> buscarTodos() {
        List<Local> locais = new ArrayList<Local>();
        String[] colunas = new String[]{"_id", "descricao", "latitude", "longitude"};
        Cursor cursor = bd.query("local", colunas, null, null, null, null, "_id");
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Local local = new Local();
                local.setId(cursor.getLong(0));
                local.setDescricao(cursor.getString(1));
                local.setLatitude(cursor.getDouble(2));
                local.setLongitude(cursor.getDouble(3));
                locais.add(local);
            } while(cursor.moveToNext());
        }
        return locais;
    }

    public Local buscar(long id) {
        String[] colunas = new String[]{"_id", "descricao", "latitude", "longitude"};
        Cursor cursor = bd.query("local", colunas, "_id=" + id, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
        } else {
            return null;
        }
        Local local = new Local();
        local.setId(cursor.getLong(0));
        local.setDescricao(cursor.getString(1));
        local.setLatitude(cursor.getDouble(2));
        local.setLongitude(cursor.getDouble(3));
        return local;
    }

    public void atualizar(Local local) {
        ContentValues valores = new ContentValues();
        valores.put("descricao", local.getDescricao());
        valores.put("latitude", local.getLatitude());
        valores.put("longitude", local.getLongitude());
        bd.update("local", valores, "_id = ?", new String[]{""+local.getId()});
    }

    public void remover(Local local) {
        bd.delete("local", "_id = " + local.getId(), null);
    }

    public void fechar() {
        bd.close();
    }

}
