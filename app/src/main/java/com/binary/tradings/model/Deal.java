package com.binary.tradings.model;

import java.util.Date;
import java.util.List;

/**
 * Created by stas on 25.04.15.
 */
public class Deal {
    private String assetName;
    private int dealId;
    private Date startDate;
    private Date expiryDate;
    private int payout;
    private List<Rate> ratesList;

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getPayout() {
        return payout;
    }

    public void setPayout(int payout) {
        this.payout = payout;
    }

    public List<Rate> getRatesList() {
        return ratesList;
    }

    public int getDealId() {
        return dealId;
    }

    public void setDealId(int dealId) {
        this.dealId = dealId;
    }

    public void setRatesList(List<Rate> ratesList) {
        this.ratesList = ratesList;
    }
}
