package com.binary.tradings.sql.rates;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.binary.tradings.model.Deal;
import com.binary.tradings.model.Rate;
import com.binary.tradings.sql.deals.DealsHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by stas on 03.05.15.
 */
public class RatesEntry {
    private final static String DATABASE_TABLE = "rates_table";
    private RatesHelper mHelper;
    private final Context mContext;
    private SQLiteDatabase mDatabase;

    public RatesEntry(Context mContext) {
        this.mContext = mContext;
    }

    public RatesEntry open() {
        mHelper = new RatesHelper(mContext);
        mDatabase = mHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if(mHelper != null){
            mHelper.close();
        }
        mDatabase.close();
    }

    public void clearEntries(Integer dealId){
        if(isTableExist()){
            mDatabase.execSQL("delete from "+ DATABASE_TABLE + " where " + RatesHelper.KEY_DEAL_ID + "=" + dealId);
        }
    }

    public long addRate(Rate rate) {
        ContentValues cv = new ContentValues();
        cv.put(RatesHelper.KEY_ASSET_NAME, rate.getRateName());
        cv.put(RatesHelper.KEY_DATE, rate.getRateDate().getTime());
        cv.put(RatesHelper.KEY_VALUE, rate.getRateValue());
        cv.put(RatesHelper.KEY_DEAL_ID, rate.getDealID());
        return mDatabase.insert(DATABASE_TABLE, null, cv);
    }

    public List<Rate> getRates(Deal deal) {
        Integer dealID = deal.getDealId();
        Date startDate = deal.getStartDate();
        Date endDate = deal.getExpiryDate();
        Rate rate;
        String[] columns = new String[] {RatesHelper.KEY_ASSET_NAME, RatesHelper.KEY_DATE,
                RatesHelper.KEY_VALUE, RatesHelper.KEY_DEAL_ID};
        Cursor c = mDatabase.query(DATABASE_TABLE, columns, RatesHelper.KEY_DEAL_ID + "=" + dealID +
                " AND " + RatesHelper.KEY_DATE + ">=" + startDate.getTime() + " AND " + RatesHelper.KEY_DATE + "<=" + endDate.getTime(), null, null,
                null, null, null);
        List<Rate> result =  new ArrayList<>();
        int iName = c.getColumnIndex(RatesHelper.KEY_ASSET_NAME);
        int iDate = c.getColumnIndex(RatesHelper.KEY_DATE);
        int iValue = c.getColumnIndex(RatesHelper.KEY_VALUE);
        int iDealID = c.getColumnIndex(RatesHelper.KEY_DEAL_ID);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            rate = new Rate();
            rate.setRateName(c.getString(iName));
            rate.setRateDate(new Date(c.getLong(iDate)));
            rate.setRateValue(c.getInt(iValue));
            rate.setDealID(c.getInt(iDealID));
            result.add(rate);
        }
        c.close();
        return result;
    }

    private boolean isTableExist()
    {
        if (DATABASE_TABLE == null || mDatabase == null || !mDatabase.isOpen())
        {
            return false;
        }
        Cursor cursor = mDatabase.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", DATABASE_TABLE});
        if (!cursor.moveToFirst())
        {
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    public boolean hasRatesInDB(String symbol){
        String[] columns = new String[] {RatesHelper.KEY_ASSET_NAME};
        Cursor c = mDatabase.query(DATABASE_TABLE, columns, RatesHelper.KEY_ASSET_NAME + "=" + "'" + symbol + "'", null, null,
                null, null, null);
        return c.getCount() > 0;
    }
}
