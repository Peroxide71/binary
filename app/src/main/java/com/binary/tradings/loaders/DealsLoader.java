package com.binary.tradings.loaders;

import android.content.BroadcastReceiver;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import com.binary.tradings.model.Deal;
import com.binary.tradings.sql.deals.DealsEntry;

import java.util.List;

/**
 * Created by stas on 25.04.15.
 */
public class DealsLoader extends AsyncTaskLoader<List<Deal>> {
    private Context context;
    public static final String DEALS_FILTER = "deals_content_updated";

    /*Loader class for Deals observer. This class is responsible for retrieving data from DB once it is saved.*/

    @Override
    public List<Deal> loadInBackground() {
        DealsEntry entry = new DealsEntry(context);
        entry.open();
        List<Deal> result = entry.getDeals();
        entry.close();
        return result;
    }

    public DealsLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void deliverResult(List<Deal> pList) {
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
        return new Observer(this, DEALS_FILTER);
    }

}
