package com.example.bullbearwar.Firebase;

import com.example.bullbearwar.AutoTrading.AutoTradingRecordStruct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 5 Mar 2018.
 */

public class UserStruct {
    private String UID;
    private String username;
    private String email;
    private double totalAsset;
    private double cashFlow;
    private double stockValue;
    private List<TradeRecordStruct> tradeRecordList;
    private List<StockProfileStruct> stockProfileList;
    private List<AutoTradingRecordStruct> at_recordList;


    public UserStruct() {
        this.username = "";
        this.email = "";
        this.cashFlow = 0;
        this.stockValue = 0;
        this.totalAsset = 0;
        this.tradeRecordList = new ArrayList<>();
        this.stockProfileList = new ArrayList<>();
        this.at_recordList = new ArrayList<>();
    }

    public UserStruct(String UID, String username, String email) {
        this.UID = UID;
        this.username = username;
        this.email = email;
        this.cashFlow = 1000000;
        this.totalAsset = this.cashFlow + this.stockValue;
        this.tradeRecordList = new ArrayList<>();
        this.stockProfileList = new ArrayList<>();
    }

    public void updateTotalAsset(){
        this.setTotalAsset(this.cashFlow + this.stockValue);
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getTotalAsset() {
        return totalAsset;
    }

    public void setTotalAsset(double totalAsset) {
        this.totalAsset = totalAsset;
    }

    public double getCashFlow() {
        return cashFlow;
    }

    public void setCashFlow(double cashFlow) {
        this.cashFlow = cashFlow;
    }

    public double getStockValue() {
        return stockValue;
    }

    public void setStockValue(double stockValue) {
        this.stockValue = stockValue;
    }

    public List<TradeRecordStruct> getTradeRecordList() {
        return tradeRecordList;
    }

    public void setTradeRecordList(List<TradeRecordStruct> tradeRecordList) {
        this.tradeRecordList = tradeRecordList;
    }

    public List<StockProfileStruct> getStockProfileList() {
        return stockProfileList;
    }

    public void setStockProfileList(List<StockProfileStruct> stockProfile) {
        this.stockProfileList = stockProfile;
    }

    public List<AutoTradingRecordStruct> getAt_recordList() {
        return at_recordList;
    }

    public void setAt_recordList(List<AutoTradingRecordStruct> at_recordList) {
        this.at_recordList = at_recordList;
    }

    @Override
    public String toString() {
        return "UserStruct{" +
                "UID='" + UID + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", totalAsset=" + totalAsset +
                ", cashFlow=" + cashFlow +
                ", stockValue=" + stockValue +
                ", tradeRecordList=" + tradeRecordList +
                ", stockProfileList=" + stockProfileList +
                ", at_recordList=" + at_recordList +
                '}';
    }
}
