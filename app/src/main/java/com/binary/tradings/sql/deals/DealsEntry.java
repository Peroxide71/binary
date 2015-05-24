package com.binary.tradings.sql.deals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.binary.tradings.model.Deal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by stas on 03.05.15.
 */
public class DealsEntry {
    private final static String DATABASE_TABLE = "deals_table";
    private DealsHelper mHelper;
    private final Context mContext;
    private SQLiteDatabase mDatabase;

    public DealsEntry(Context mContext) {
        this.mContext = mContext;
    }

    public DealsEntry open() {
        mHelper = new DealsHelper(mContext);
        mDatabase = mHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if(mHelper != null){
            mHelper.close();
        }
        mDatabase.close();
    }

    public void clearTable(){
        mDatabase.execSQL("delete from "+ DATABASE_TABLE);
    }

    public long addDeal(Deal deal) {
        ContentValues cv = new ContentValues();
        cv.put(DealsHelper.KEY_ASSET_NAME, deal.getAssetName());
        cv.put(DealsHelper.KEY_EXPIRY_DATE, deal.getExpiryDate().getTime());
        cv.put(DealsHelper.KEY_PAYOUT, deal.getPayout());
        cv.put(DealsHelper.KEY_START_DATE, deal.getStartDate().getTime());
        return mDatabase.insert(DATABASE_TABLE, null, cv);
    }

    public List<Deal> getDeals() {
        Deal deal;
        String[] columns = new String[] {DealsHelper.KEY_ASSET_NAME, DealsHelper.KEY_EXPIRY_DATE,
                DealsHelper.KEY_START_DATE, DealsHelper.KEY_PAYOUT};
        Cursor c = mDatabase.query(DATABASE_TABLE, columns, null, null, null,
                null, null, null);
        List<Deal> result =  new ArrayList<>();
        int iName = c.getColumnIndex(DealsHelper.KEY_ASSET_NAME);
        int iDate = c.getColumnIndex(DealsHelper.KEY_EXPIRY_DATE);
        int iPayout = c.getColumnIndex(DealsHelper.KEY_PAYOUT);
        int iStartDate = c.getColumnIndex(DealsHelper.KEY_START_DATE);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            deal = new Deal();
            deal.setAssetName(c.getString(iName));
            deal.setExpiryDate(new Date(c.getLong(iDate)));
            deal.setPayout(c.getInt(iPayout));
            deal.setStartDate(new Date(c.getLong(iStartDate)));
            result.add(deal);
        }
        c.close();
        return result;
    }

    public boolean hasDealsInDB(){
        String[] columns = new String[] {DealsHelper.KEY_ASSET_NAME};
        Cursor c = mDatabase.query(DATABASE_TABLE, columns, null, null, null,
                null, null, null);
        return c.getCount() > 0;
    }
}
