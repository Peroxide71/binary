package com.binary.tradings.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.binary.tradings.adapters.DealsAdapter;
import com.binary.tradings.loaders.DealsLoader;
import com.binary.tradings.model.Deal;
import com.binary.tradings.sql.deals.DealsEntry;
import com.binary.tradings.ui.activity.MainActivity;
import com.binary.tradings.util.NetworkRequestHelper;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DealsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Deal>>  {
    private MainActivity mActivity;
    private boolean reload;
    private static OpenTimer timer;
    private TimerTask timerTask;
    final Handler handler = new Handler();
    private int updatePeriod;
    private static DealsFragment currentEntity;

    private OnFragmentInteractionListener mListener;

    public static DealsFragment newInstance(boolean reload) {
        DealsFragment fragment = new DealsFragment();
        fragment.setReload(reload);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DealsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentEntity = this;
        mActivity.getSupportActionBar().setTitle("Binary");
        new LoadDealsTask().execute();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity)activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void startTimer() {
        //Stop already existing timer if any.
        stopTimerTask();
        //set a new Timer
        timer = new OpenTimer();

        //initialize the TimerTask's job
        initializeTimerTask();
        SharedPreferences sPrefs = mActivity.getSharedPreferences(MainActivity.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        updatePeriod = sPrefs.getInt(MainActivity.UPDATE_PERIOD_KEY, 60);

        timer.schedule(timerTask, 0, updatePeriod * 1000); //
    }

    public void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        SharedPreferences sPrefs = mActivity.getSharedPreferences(MainActivity.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
                        updatePeriod = sPrefs.getInt(MainActivity.UPDATE_PERIOD_KEY, 60);
                        if(timer.getPeriod()/1000 != updatePeriod &&
                                timer.getPeriod() <= 300000 && timer.getPeriod() >= 30000){
                            startTimer();
                        } else if(currentEntity.isVisible()){
                            mActivity.updateDealsFragmentContent();
                        } else {
                            stopTimerTask();
                        }
                    }
                });
            }
        };
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            stopTimerTask();
            mListener.onFragmentInteraction(((Deal)getListAdapter().getItem(position)));
        }
    }

    //For loading and caching Deals we will use Observer pattern.

    @Override
    public Loader<List<Deal>> onCreateLoader(int i, Bundle bundle) {
        return new DealsLoader(mActivity);
    }

    @Override
    public void onLoadFinished(Loader<List<Deal>> listLoader, List<Deal> deals) {
        DealsAdapter adapter = new DealsAdapter(deals, mActivity);

        setListAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<List<Deal>> listLoader) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Deal deal);
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }

    private class LoadDealsTask extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] params) {
            DealsEntry entry = new DealsEntry(mActivity);
            entry.open();
            if(!entry.hasDealsInDB() || reload){
                NetworkRequestHelper.getInstane(mActivity).getDealsFromServer();
                reload = false;
            }
            entry.close();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            //Done caching, now starting to load content from DB to UI.
            mActivity.getSupportLoaderManager().initLoader(0, null, mActivity.getListLoaderCallbacks());
            Intent broadcast = new Intent();
            broadcast.setAction(DealsLoader.DEALS_FILTER);
            //Sending broadcast to notify the observer, it's time to re-load content from DB.
            mActivity.sendBroadcast(broadcast);
            if(timer == null){
                startTimer();
            }
        }
    }

    //As standard Android timer does not have getter for period, we will implement our own.

    private class OpenTimer extends Timer{
        private long period;

        @Override
        public void schedule(TimerTask task, long delay, long period) {
            super.schedule(task, delay, period);
            this.period = period;
        }

        public long getPeriod() {
            return period;
        }
    }

}
