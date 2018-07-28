package com.example.bullbearwar.AutoTrading;

import com.example.bullbearwar.Searching.YahooRealtimeDataStruct;

import java.util.Calendar;
import java.util.Date;

public class AutoTradingRecordStruct {
    private String targetStockSym;
    private String tradeType;
    private double targetAskPrice, targetBidPrice;
    private int targetAskVolume, targetBidVolume;
    private double actualAskPrice, actualBidPrice;
    private long buyTime, sellTime, lastCheckTime;
    private long startingTime, endingTime;
    private String status;

    public AutoTradingRecordStruct() {
        this.targetStockSym = "";
        this.tradeType = "";
        this.targetAskPrice = 0;
        this.targetBidPrice = 0;
        this.targetAskVolume = 0;
        this.targetBidVolume = 0;
        this.actualAskPrice = 0;
        this.actualBidPrice = 0;
        this.buyTime = 0;
        this.sellTime = 0;
        this.lastCheckTime = 0;
        this.startingTime = 0;
        this.endingTime = 0;
        this.status = "In Progress";
    }

    public void setStatusToInProgress(){
        this.status = "In Progress";
    }

    public void setStatusToSuccessful(){
        this.status = "Successful";
    }
    public void setStatusToUnsuccessful(){
        this.status = "Unsuccessful";
    }

    public boolean isInProgress(){
        return status.compareTo("In Progress") == 0;
    }

    public boolean isTradeTypeAutoBuy(){
        return tradeType.compareTo("Auto Buy") == 0;
    }

    public void setTradeTypeAsAutoBuy(){
        this.tradeType = "Auto Buy";
    }

    public void setTradeTypeAsAutoSell(){
        this.tradeType = "Auto Sell";
    }

    public String getTargetStockSym() {
        return targetStockSym;
    }

    public void setTargetStockSym(String targetStockSym) {
        this.targetStockSym = targetStockSym;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public int getTargetAskVolume() {
        return targetAskVolume;
    }

    public void setTargetAskVolume(int targetAskVolume) {
        this.targetAskVolume = targetAskVolume;
    }

    public int getTargetBidVolume() {
        return targetBidVolume;
    }

    public void setTargetBidVolume(int tartgetBidVolume) {
        this.targetBidVolume = tartgetBidVolume;
    }

    public double getTargetAskPrice() {
        return targetAskPrice;
    }

    public void setTargetAskPrice(double targetAskPrice) {
        this.targetAskPrice = targetAskPrice;
    }

    public double getTargetBidPrice() {
        return targetBidPrice;
    }
    public void setTargetBidPrice(double targetBidPrice) {
        this.targetBidPrice = targetBidPrice;
    }

    public double getActualAskPrice() {
        return actualAskPrice;
    }

    public void setActualAskPrice(double actualAskPrice) {
        this.actualAskPrice = actualAskPrice;
    }

    public double getActualBidPrice() {
        return actualBidPrice;
    }

    public void setActualBidPrice(double actualBidPrice) {
        this.actualBidPrice = actualBidPrice;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(long buyTime) {
        this.buyTime = buyTime;
    }

    public long getSellTime() {
        return sellTime;
    }

    public void setSellTime(long sellTime) {
        this.sellTime = sellTime;
    }

    public long getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(long lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }

    public long getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(long startingTime) {
        this.startingTime = startingTime;
    }

    public long getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(long endingTime) {
        this.endingTime = endingTime;
    }

    @Override
    public String toString() {
        return "AutoTradingRecordStruct{" +
                "targetStockSym='" + targetStockSym + '\'' +
                ", tradeType='" + tradeType + '\'' +
                ", targetAskPrice=" + targetAskPrice +
                ", targetBidPrice=" + targetBidPrice +
                ", targetAskVolume=" + targetAskVolume +
                ", targetBidVolume=" + targetBidVolume +
                ", actualAskPrice=" + actualAskPrice +
                ", actualBidPrice=" + actualBidPrice +
                ", buyTime=" + buyTime +
                ", sellTime=" + sellTime +
                ", lastCheckTime=" + lastCheckTime +
                ", startTime=" + startingTime +
                ", endTime=" + endingTime +
                ", status='" + status + '\'' +
                '}';
    }
}
