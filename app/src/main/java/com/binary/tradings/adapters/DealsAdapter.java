package com.binary.tradings.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.binary.tradings.R;
import com.binary.tradings.model.Deal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by stas on 03.05.15.
 */
public class DealsAdapter extends BaseAdapter {
    private List<Deal> dataList;
    private LayoutInflater mLayoutInflater;

    public DealsAdapter(List<Deal> dataList, Context ctx) {
        this.dataList = dataList;
        mLayoutInflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dataList.get(position).getDealId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DealsViewHolder holder;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.deal_item, null);
            holder = new DealsViewHolder();
            holder.symbolTextView = (TextView)convertView.findViewById(R.id.textViewSymbol);
            holder.expiryDateTextView = (TextView)convertView.findViewById(R.id.textViewExpiryDate);
            holder.payoutTextView = (TextView)convertView.findViewById(R.id.textViewPayout);
            convertView.setTag(holder);
        } else {
            holder = (DealsViewHolder)convertView.getTag();
        }
        Deal deal = dataList.get(position);
        if(deal != null){
            DateFormat df = new SimpleDateFormat("HH:mm:ss");
            String expiryDateString = deal.getExpiryDate() != null ? df.format(deal.getExpiryDate()) : "";
            holder.symbolTextView.setText(deal.getAssetName());
            holder.expiryDateTextView.setText(expiryDateString);
            holder.payoutTextView.setText(String.format("Payout (%d%%)", deal.getPayout()));
        }
        return convertView;
    }

    static class DealsViewHolder{
        TextView symbolTextView;
        TextView expiryDateTextView;
        TextView payoutTextView;

    }
}
