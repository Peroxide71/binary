package com.binary.tradings.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.binary.tradings.R;
import com.binary.tradings.loaders.RatesLoader;
import com.binary.tradings.model.Deal;
import com.binary.tradings.model.Rate;
import com.binary.tradings.sql.rates.RatesEntry;
import com.binary.tradings.ui.activity.MainActivity;
import com.binary.tradings.util.NetworkRequestHelper;
import com.binary.tradings.util.YahooMapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Rate>>{

    private DealsFragment.OnFragmentInteractionListener mListener;
    private Deal deal;
    private MainActivity mActivity;
    private GraphicalView mChart;
    private LinearLayout plotLayout;

    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

    private XYSeries mCurrentSeries;

    private TimeSeries time_series;

    private XYSeriesRenderer mCurrentRenderer;
    List<Rate> plotData;

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    public DetailsFragment() {
        // Required empty public constructor
    }

    private void initChart() {
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mCurrentSeries = new XYSeries("Sample Data");
        time_series = new TimeSeries("Price");
        mDataset.addSeries(time_series);
        mCurrentRenderer = new XYSeriesRenderer();
        mCurrentRenderer.setDisplayChartValues(true);
        mCurrentRenderer.setChartValuesSpacing(10f);
        mCurrentRenderer.setColor(Color.BLUE);
        mCurrentRenderer.setLineWidth(4f);
        mCurrentRenderer.setFillBelowLine(true);
        mCurrentRenderer.setChartValuesTextSize(20f);
        mRenderer.addSeriesRenderer(mCurrentRenderer);
        mRenderer.setShowGrid(true);
        mRenderer.setShowLegend(false);
        mRenderer.setLabelsTextSize(15f);
        mRenderer.setBackgroundColor(Color.WHITE);
        mRenderer.setMarginsColor(Color.WHITE);
    }

    private void addSampleData() {
        for (int i = 0; i < plotData.size(); i++) {
            time_series.add(plotData.get(i).getRateDate(), plotData.get(i).getRateValue());
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        new LoadHistoryTask().execute();
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        plotLayout = (LinearLayout) rootView.findViewById(R.id.linearLayoutPlot);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;

    }

    @Override
    public Loader<List<Rate>> onCreateLoader(int id, Bundle args) {
        return new RatesLoader(mActivity, deal);
    }

    @Override
    public void onLoadFinished(Loader<List<Rate>> loader, List<Rate> data) {
        plotData = data;
        Collections.sort(plotData);
        if (mChart == null) {
            initChart();
            addSampleData();
            mChart = ChartFactory.getTimeChartView(mActivity, mDataset, mRenderer, "dd.MM.yyyy");
            mChart.setBackgroundColor(Color.WHITE);
            mActivity.getSupportActionBar().setTitle("Details");
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            View convertView = inflater.inflate(R.layout.deal_item, null);

            DateFormat df = new SimpleDateFormat("HH:mm:ss");
            String expiryDateString = deal.getExpiryDate() != null ? df.format(deal.getExpiryDate()) : "";
            ((TextView)convertView.findViewById(R.id.textViewSymbol)).setText(deal.getAssetName());
            ((TextView)convertView.findViewById(R.id.textViewExpiryDate)).setText(expiryDateString);
            ((TextView)convertView.findViewById(R.id.textViewPayout)).setText(String.format("Payout (%d%%)", deal.getPayout()));
            plotLayout.addView(convertView);
            plotLayout.addView(mChart);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)convertView.getLayoutParams();
            params.height = 150;
            convertView.setLayoutParams(params);
        } else {
            mChart.repaint();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Rate>> loader) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setDeal(Deal deal) {
        this.deal = deal;
    }

    private class LoadHistoryTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            RatesEntry entry = new RatesEntry(mActivity);
            entry.open();
            String mappingString = YahooMapping.valueOf(deal.getAssetName()).getName();
            Log.i("MappingString", mappingString);
            if(!entry.hasRatesInDB(YahooMapping.valueOf(deal.getAssetName()).getName())){
                NetworkRequestHelper.getInstane(mActivity).getHistoryFromServer(deal);
            }
            entry.close();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            //Done caching, now starting to load content from DB to UI.
            if(mActivity.getSupportLoaderManager().getLoader(1) == null){
                mActivity.getSupportLoaderManager().initLoader(1, null, mActivity.getHistoryLoaderCallbacks());
            } else {
                mActivity.getSupportLoaderManager().restartLoader(1, null, mActivity.getHistoryLoaderCallbacks());
            }


            Intent broadcast = new Intent();
            //Sending broadcast to notify the observer, it's time to re-load content from DB.
            broadcast.setAction(RatesLoader.FILTER);
            mActivity.sendBroadcast(broadcast);
        }
    }

}
