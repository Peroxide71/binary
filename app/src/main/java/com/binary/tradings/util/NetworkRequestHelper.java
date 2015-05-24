package com.binary.tradings.util;

import android.util.Log;

import com.binary.tradings.model.Deal;
import com.binary.tradings.model.Rate;
import com.binary.tradings.sql.deals.DealsEntry;
import com.binary.tradings.sql.rates.RatesEntry;
import com.binary.tradings.ui.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by stas on 02.05.15.
 */
public class NetworkRequestHelper {
    private static NetworkRequestHelper instance;
    private static  MainActivity mainActivity;
    private static final int INITIAL_ITEM = 0;

    private NetworkRequestHelper() {

    }

    public static NetworkRequestHelper getInstane(MainActivity mActivity){
        if(instance == null){
            instance = new NetworkRequestHelper();
            mainActivity = mActivity;
        }
        return instance;
    }

    private final String DEALS_URL = "http://api.ubinary.com/trading/affiliate/112233/user/get/demo/trading/options?data=%7B%22UserId%22:%22%22%7D";
    private final String DETAILS_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20%22";
    private final String INTRADAY_DETAILS_URL = "http://chartapi.finance.yahoo.com/instrument/1.0/";
    private final String INTRADAY_DETAILS_URL_ENDING = "/chartdata;type=quote;range=1d/json";
    private final String WHERE_AND_START_DATE_CLAUSE = "%22%20and%20startDate%20%3D%20%22";
    private final String WHERE_END_DATE_CLAUSE = "%22%20and%20endDate%20%3D%20%22";
    private final String END_CLAUSE = "%22&format=json&diagnostics=true&env=http%3A%2F%2Fdatatables.org%2Falltables.env&callback";
    public void getDealsFromServer(){

        try {
            URL url = new URL(DEALS_URL);
            HttpURLConnection connection = ((HttpURLConnection)url.openConnection());
            connection.addRequestProperty("User-Agent", "Mozilla/4.0");
            InputStream input;
            if (connection.getResponseCode() == 200)  // this must be called before 'getErrorStream()' works
                input = connection.getInputStream();
            else input = connection.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String msg = reader.readLine();
            JSONObject deals = new JSONObject(msg);
            Log.i("Deals", deals.toString());
            JSONArray options = deals.getJSONArray("Options");
            Map<String, List<Deal>> optionsMap = parseOptions(options);
            Log.i("Map", optionsMap.toString());
            //Content got from server, now caching it to local DB.
            DealsEntry entry = new DealsEntry(mainActivity);
            entry.open();
            entry.clearTable();
            for(List<Deal> list : optionsMap.values()){
                for(Deal deal : list){
                    entry.addDeal(deal);
                }
            }
            entry.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException jsone) {
            jsone.printStackTrace();
        }
    }

    private Map<String, List<Deal>> parseOptions(JSONArray options) throws JSONException{
        Map<String, List<Deal>> optionsList = new HashMap<>();
        for(int i = 0; i < options.length(); i++){
            JSONObject option = options.getJSONObject(i);
            String symbol = option.getString("Symbol");
            optionsList.put(symbol, parseDeal(option.getJSONArray("Deals"), symbol));
        }
        return optionsList;
    }

    private List<Deal> parseDeal(JSONArray deals, String assetName) throws JSONException{
        List<Deal> dealsList = new ArrayList<>();
        for(int i = 0; i < deals.length(); i++){
            JSONObject dealJSON = deals.getJSONObject(i);
            if(dealJSON != null){
                Deal deal = new Deal();
                deal.setAssetName(assetName);
                deal.setDealId(dealJSON.getInt("DealId"));
                deal.setPayout(dealJSON.getInt("PayMatch"));
                deal.setExpiryDate(parseDate(dealJSON.getString("EndAt")));
                deal.setStartDate(parseDate(dealJSON.getString("StartAt")));
                dealsList.add(deal);
            }
        }
        return dealsList;
    }

    private Date parseDate(String dateString){
        Date result = null;
        try{
            DateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss", Locale.ENGLISH);
            result =  df.parse(dateString);
        } catch(ParseException pe){
            pe.printStackTrace();
        }
        return result;
    }

    private Date parseHistoryDate(String dateString){
        Date result = null;
        try{
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            result =  df.parse(dateString);
        } catch(ParseException pe){
            pe.printStackTrace();
        }
        return result;
    }

    public void getHistoryFromServer(Deal deal){
        String symbol = YahooMapping.valueOf(deal.getAssetName()).getName();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String startDateString = df.format(deal.getStartDate());
        String endDateString = df.format(deal.getExpiryDate());
        try {
            boolean isIntraday = deal.getExpiryDate().getTime() - deal.getStartDate().getTime() < 8 * 60 * 60 * 1000;
            URL url = null;
            if(isIntraday){
                url = new URL(INTRADAY_DETAILS_URL + symbol + INTRADAY_DETAILS_URL_ENDING);

            } else {
                url = new URL(DETAILS_URL + symbol + WHERE_AND_START_DATE_CLAUSE + startDateString + WHERE_END_DATE_CLAUSE + endDateString + END_CLAUSE);

            }
            Log.i("Request", url.toString());
            HttpURLConnection connection = ((HttpURLConnection) url.openConnection());
            connection.addRequestProperty("User-Agent", "Mozilla/4.0");
            InputStream input;
            if (connection.getResponseCode() == 200)  // this must be called before 'getErrorStream()' works
                input = connection.getInputStream();
            else input = connection.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String msg = "";
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                msg = msg + line;
            }
            Log.i("Details", msg);
            JSONArray quote;
            if(isIntraday){
              String clearMsg = msg.substring(msg.indexOf("(") + 1, msg.indexOf(")"));
                JSONObject history = new JSONObject(clearMsg);
                quote = history.getJSONArray("series");
            } else {
                JSONObject history = new JSONObject(msg);
                JSONObject query = history.getJSONObject("query");
                JSONObject results = query.getJSONObject("results");
                quote = results.getJSONArray("quote");
            }

            List<Rate> rateList = parseRates(quote, isIntraday, deal);
            //Content got from server, now caching it to local DB.
            RatesEntry entry = new RatesEntry(mainActivity);
            entry.open();
            entry.clearEntries(deal.getDealId());
            for(Rate rate : rateList){
                entry.addRate(rate);
            }
            entry.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException jsone) {
            jsone.printStackTrace();
        }
    }

    private List<Rate> parseRates(JSONArray quote, boolean isIntraday, Deal deal) throws JSONException{
        List<Rate> dealsList = new ArrayList<>();
        for(int i = 0; i < quote.length(); i++){
            JSONObject dealJSON = quote.getJSONObject(i);
            if(dealJSON != null){
                Rate rate = new Rate();
                rate.setRateValue(isIntraday ? Double.parseDouble(dealJSON.getString("close")) : Double.parseDouble(dealJSON.getString("Close")));
                rate.setRateDate(isIntraday ? new Date(dealJSON.getLong("Timestamp") * 1000) : parseHistoryDate(dealJSON.getString("Date")));
                rate.setRateName(deal.getAssetName());
                rate.setDealID(deal.getDealId());
                dealsList.add(rate);
            }
        }
        return dealsList;
    }
}
