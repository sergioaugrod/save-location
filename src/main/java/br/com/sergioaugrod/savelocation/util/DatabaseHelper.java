package br.com.sergioaugrod.savelocation.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String NOME_BD = "savelocation";
    private static final int VERSAO_BD = 1;

    public DatabaseHelper(Context context) {
        super(context, NOME_BD, null, VERSAO_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase bd) {
        String tabelaLocal = "CREATE TABLE `local` (" +
                "`_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`descricao` TEXT NOT NULL," +
                "`latitude` REAL NOT NULL," +
                "`longitude` REAL NOT NULL" +
                ");";
        bd.execSQL(tabelaLocal);
    }

    @Override
    public void onUpgrade(SQLiteDatabase bd, int i, int i2) {
        bd.execSQL("DROP TABLE `local`");
        onCreate(bd);
    }

}
