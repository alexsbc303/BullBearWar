package com.example.bullbearwar.Firebase;

/**
 * Created by User on 14 Mar 2018.
 */

import java.util.Date;

/**
 * Created by Bobby on 8/3/2018.
 */

public class StockProfileStruct {
    private String stockSym;
    private double averagePrice;
    private int totalShares;
    private Date LastTradeDateTime;

    public StockProfileStruct() {
    }

    public StockProfileStruct(String stockSym, double averagePrice, int totalShares) {
        this.stockSym = stockSym;
        this.averagePrice = averagePrice;
        this.totalShares = totalShares;
    }

    public StockProfileStruct(String stockSym, double averagePrice, int totalShares, Date lastTradeDateTime) {
        this.stockSym = stockSym;
        this.averagePrice = averagePrice;
        this.totalShares = totalShares;
        LastTradeDateTime = lastTradeDateTime;
    }

    public StockProfileStruct(String stockSym) {
        this.stockSym = stockSym;
    }

    public String getStockSym() {
        return stockSym;
    }

    public void setStockSym(String stockSym) {
        this.stockSym = stockSym;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public int getTotalShares() {
        return totalShares;
    }

    public void setTotalShares(int totalShares) {
        this.totalShares = totalShares;
    }

    public Date getLastTradeDateTime() {
        return LastTradeDateTime;
    }

    public void setLastTradeDateTime(Date lastTradeDateTime) {
        LastTradeDateTime = lastTradeDateTime;
    }
}