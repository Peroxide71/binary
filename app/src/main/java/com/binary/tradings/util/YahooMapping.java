package com.binary.tradings.util;

/**
 * Created by stas on 06.05.15.
 */
public enum YahooMapping {
    AP1USD("AAPL"),
    CC1USD("CCE"),
    EB1USD("EBAY"),
    FB1USD("FB"),
    FO1USD("F"),
    GO1USD("GOOG"),
    IB1USD("IBM");

    private final String name;
    private YahooMapping(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

}
