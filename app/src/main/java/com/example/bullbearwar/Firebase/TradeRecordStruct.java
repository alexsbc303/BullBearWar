package com.example.bullbearwar.Firebase;

import java.util.Date;
import java.util.List;

/**
 * Created by User on 5 Mar 2018.
 */

public class TradeRecordStruct {

    private Date tradeDateTime;
    private String tradeDate;
    private String tradeTime;
    private String stockSym;
    private int shares;
    private double profit;
    private double transactionPrice;
    private boolean transactionType;  // 0 => buy ; 1 => sell

    public TradeRecordStruct() {
    }

    public TradeRecordStruct(String stockSym,
                             boolean transactionType,
                             double transactionPrice,
                             int shares,
                             Date tradeDateTime) {
        this.stockSym = stockSym;
        this.transactionType = transactionType;
        this.transactionPrice = transactionPrice;
        this.shares = shares;
        this.tradeDateTime = tradeDateTime;
        this.profit = 0;
    }

    public boolean hasEnoughCashToBuy(double cash) {
        return cash >= transactionPrice;
    }

    public boolean isAlreadyBroughtThisStock(List<StockProfileStruct> profileList, String stockSym) {
        boolean found = false;
        if (profileList != null && profileList.size() != 0) {
            for (StockProfileStruct profile : profileList) {
                if (profile.getStockSym().compareTo(stockSym) == 0) {
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    public int getProfileIndex(List<StockProfileStruct> profileList, String stockSym) {
        int index = 999;

        if (profileList != null) {
            for (int i = 0; i < profileList.size(); i++) {
                if (profileList.get(i).getStockSym().compareTo(stockSym) == 0) {
                    index = i;
                    break;
                }
            }
        }

        return index;
    }

    public boolean hasEnoughSharesToSell(List<StockProfileStruct> profileList, int position, int shares){
        return profileList.get(position).getTotalShares() >= shares;
    }

    public void buyStock(double askPrice, int shares, int tradeTime) {
        // double tradingPrice = askPrice * shares;
        // this.tradeTime = tradeTime;
    }

    public Date getTradeDateTime() {
        return tradeDateTime;
    }

    public void setTradeDateTime(Date tradeDateTime) {
        this.tradeDateTime = tradeDateTime;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getStockSym() {
        return stockSym;
    }

    public void setStockSym(String stockSym) {
        this.stockSym = stockSym;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public double getTransactionPrice() {
        return transactionPrice;
    }

    public void setTransactionPrice(double transactionPrice) {
        this.transactionPrice = transactionPrice;
    }

    public boolean getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(boolean transactionType) {
        this.transactionType = transactionType;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }


    @Override
    public String toString() {
        return "TradeRecordStruct{" +
                "tradeDateTime=" + tradeDateTime +
                ", tradeDate='" + tradeDate + '\'' +
                ", tradeTime='" + tradeTime + '\'' +
                ", stockSym='" + stockSym + '\'' +
                ", shares=" + shares +
                ", profit=" + profit +
                ", transactionPrice=" + transactionPrice +
                ", transactionType=" + transactionType +
                '}';
    }
}
