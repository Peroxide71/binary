package com.binary.tradings.model;

import java.util.Date;

/**
 * Created by stas on 25.04.15.
 */
public class Rate implements Comparable<Rate>{
    private String rateName;
    private double rateValue;
    private Date rateDate;
    private Integer dealID;

    public double getRateValue() {
        return rateValue;
    }

    public void setRateValue(double rateValue) {
        this.rateValue = rateValue;
    }

    public Date getRateDate() {
        return rateDate;
    }

    public void setRateDate(Date rateDate) {
        this.rateDate = rateDate;
    }

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    @Override
    public int compareTo(Rate another) {
        return rateDate.compareTo(another.getRateDate());
    }

    public Integer getDealID() {
        return dealID;
    }

    public void setDealID(Integer dealID) {
        this.dealID = dealID;
    }
}
