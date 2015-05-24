package com.binary.tradings.loaders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.binary.tradings.model.Deal;
import com.binary.tradings.model.Rate;
import com.binary.tradings.sql.rates.RatesEntry;

import java.util.List;

/**
 * Created by stas on 04.05.15.
 */
public class RatesLoader extends AsyncTaskLoader<List<Rate>> {
    private Context context;
    private Deal deal;
    public static final String FILTER = "content_updated";

    /*Loader class for Details observer. This class is responsible for retrieving data from DB once it is saved.*/

    public RatesLoader(Context context, Deal deal) {
        super(context);
        this.context = context;
        this.deal = deal;
    }

    @Override
    public List<Rate> loadInBackground() {
        RatesEntry entry = new RatesEntry(context);
        entry.open();
        List<Rate> result = entry.getRates(deal);
        entry.close();
        return result;
    }

    @Override
    public void deliverResult(List<Rate> pList) {
        if (isStarted()) {
            super.deliverResult(pList);
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        createObserver();
    }

    private BroadcastReceiver createObserver(){
        //Here we create the actual observer which will be notified, when dataset in DB changed.
        return new Observer(this, FILTER);
    }
}
