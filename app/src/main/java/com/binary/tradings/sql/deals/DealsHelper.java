package com.binary.tradings.sql.deals;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by stas on 25.04.15.
 */
public class DealsHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "deals";
    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_TABLE = "deals_table";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_ASSET_NAME = "asset_name";
    public static final String KEY_EXPIRY_DATE = "expiry_date";
    public static final String KEY_START_DATE = "start_date";
    public static final String KEY_PAYOUT = "key_payout";


    public DealsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + KEY_ROWID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_ASSET_NAME
                + " TEXT NOT NULL, " + KEY_EXPIRY_DATE
                + " LONG, "+ KEY_START_DATE
                + " LONG, " + KEY_PAYOUT
                + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }
}
