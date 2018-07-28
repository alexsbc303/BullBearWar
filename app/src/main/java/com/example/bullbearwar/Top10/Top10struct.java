package com.example.bullbearwar.Top10;

public class Top10struct {
    private String stockRank;
    private String stockSym;
    private String stockStatus;
    private String stockName;
    private double stockValue;
    private String change;
    private String percentageChange;
    private double highest;
    private double lowerest;
    private String turnover;

    public Top10struct() {
    }

    public Top10struct(String stockRank, String stockSym, String stockStatus, String stockName, double stockValue, String change, String percentageChange, double highest, double lowerest, String turnover) {
        this.stockRank = stockRank;
        this.stockSym = stockSym;
        this.stockStatus = stockStatus;
        this.stockName = stockName;
        this.stockValue = stockValue;
        this.change = change;
        this.percentageChange = percentageChange;
        this.highest = highest;
        this.lowerest = lowerest;
        this.turnover = turnover;
    }

    public String getStockRank() {
        return stockRank;
    }

    public void setStockRank(String stockRank) {
        this.stockRank = stockRank;
    }

    public String getStockSym() {
        return stockSym;
    }

    public void setStockSym(String stockSym) {
        this.stockSym = stockSym;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public double getStockValue() {
        return stockValue;
    }

    public void setStockValue(double stockValue) {
        this.stockValue = stockValue;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getPercentageChange() {
        return percentageChange;
    }

    public void setPercentageChange(String percentageChange) {
        this.percentageChange = percentageChange;
    }

    public double getHighest() {
        return highest;
    }

    public void setHighest(double highest) {
        this.highest = highest;
    }

    public double getLowerest() {
        return lowerest;
    }

    public void setLowerest(double lowerest) {
        this.lowerest = lowerest;
    }

    public String getTurnover() {
        return turnover;
    }

    public void setTurnover(String turnover) {
        this.turnover = turnover;
    }

    @Override
    public String toString() {
        return "Top10struct{" +
                "stockRank='" + stockRank + '\'' +
                ", stockSym='" + stockSym + '\'' +
                ", stockStatus='" + stockStatus + '\'' +
                ", stockName='" + stockName + '\'' +
                ", stockValue=" + stockValue +
                ", change='" + change + '\'' +
                ", percentageChange='" + percentageChange + '\'' +
                ", highest=" + highest +
                ", lowerest=" + lowerest +
                ", turnover='" + turnover + '\'' +
                '}';
    }
}
