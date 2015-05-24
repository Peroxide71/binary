package com.binary.tradings.loaders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

/**
 * Created by stas on 25.04.15.
 */
public class Observer extends BroadcastReceiver {
        private AsyncTaskLoader loader;

    public Observer(AsyncTaskLoader loader, String filter){
        this.loader = loader;
        IntentFilter intentFilter= new IntentFilter();
        intentFilter.addAction(filter);
        this.loader.getContext().registerReceiver(this, intentFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Once dataset in DB changed, observer notifies its Loader.
            loader.onContentChanged();
        }
}
