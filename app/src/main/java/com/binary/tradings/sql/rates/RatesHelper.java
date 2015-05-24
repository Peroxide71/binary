package com.binary.tradings.sql.rates;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by stas on 25.04.15.
 */
public class RatesHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "rates";
    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_TABLE = "rates_table";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_ASSET_NAME = "asset_name";
    public static final String KEY_DATE = "date";
    public static final String KEY_VALUE = "value";
    public static final String KEY_DEAL_ID = "deal_id";


    public RatesHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DATABASE_TABLE + "(" + KEY_ROWID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_DEAL_ID
                + " INTEGER, " + KEY_ASSET_NAME
                + " TEXT NOT NULL, " + KEY_DATE
                + " LONG, " + KEY_VALUE
                + " REAL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }
}
