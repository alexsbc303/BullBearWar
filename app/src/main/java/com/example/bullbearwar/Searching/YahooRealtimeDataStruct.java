package com.example.bullbearwar.Searching;

/**
 * Created by User on 25 Feb 2018.
 */

public class YahooRealtimeDataStruct {
    private String companyName;
    private String stockSym;
    private double current;
    private String change;
    private String lastUpdatedTime;

    private double low;

    private double high;
    private double close;
    private double previousClose;
    private double open;
    private double bid;
    private double ask;
    private String dayRange;
    private String weekRange52;
    private int volume;
    private int volumeAvg;
    private String marketCap;
    private String beta;
    private double peRatio;
    private double eps;
    private String earningsDate;
    private String forwardDividend;
    private String exDividendData;
    private String targetEst1year;

    public YahooRealtimeDataStruct() {
        this.companyName = "";
        this.stockSym = "";
        this.current = 0;
        this.change = "";
        this.lastUpdatedTime = "";
        this.low = 0;
        this.previousClose = 0;
        this.open = 0;
        this.bid = 0;
        this.ask = 0;
        this.dayRange = "";
        this.weekRange52 = "";
        this.volume = 0;
        this.volumeAvg = 0;
        this.marketCap = "";
        this.beta = "";
        this.peRatio = 0;
        this.eps = 0;
        this.earningsDate = "";
        this.forwardDividend = "";
        this.exDividendData = "";
        this.targetEst1year = "";
    }

    public YahooRealtimeDataStruct(String stockSym){
        this.stockSym = stockSym;
        this.companyName = "";
        this.current = 0;
        this.change = "";
        this.lastUpdatedTime = "";
        this.low = 0;
        this.previousClose = 0;
        this.open = 0;
        this.bid = 0;
        this.ask = 0;
        this.dayRange = "";
        this.weekRange52 = "";
        this.volume = 0;
        this.volumeAvg = 0;
        this.marketCap = "";
        this.beta = "";
        this.peRatio = 0;
        this.eps = 0;
        this.earningsDate = "";
        this.forwardDividend = "";
        this.exDividendData = "";
        this.targetEst1year = "";
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStockSym() {
        return stockSym;
    }

    public void setStockSym(String stockSym) {
        this.stockSym = stockSym;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public void setCurrent(String current) {
        this.current = Double.parseDouble(current);
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }
    public void setLow(String low) {
        this.low = Double.parseDouble(low);
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(double previousClose) {
        this.previousClose = previousClose;
    }
    public void setPreviousClose(String previousClose) {
        this.previousClose = Double.parseDouble(previousClose);
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }
    public void setOpen(String open) {
        this.open = Double.parseDouble(open);
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }
    public void setBid(String bid) {
        String[] tokens = bid.split(" x ");
        this.bid = Double.parseDouble(tokens[0]);
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double tempAsk) {
        this.ask = tempAsk;
    }

    public void setAsk(String tempAsk) {
        if (tempAsk.length() != 0 && tempAsk.contains("x")) {
            String[] tokens = tempAsk.split(" x ");
            this.ask = Double.parseDouble(tokens[0]);
        }
    }

    public String getDayRange() {
        return dayRange;
    }

    public void setDayRange(String dayRange) {
        String[] tokens = dayRange.split(" - ");
        this.low = Double.parseDouble(tokens[0]);
        this.high = Double.parseDouble(tokens[1]);
        this.dayRange = dayRange;
    }

    public String getWeekRange52() {
        return weekRange52;
    }

    public void setWeekRange52(String weekRange52) {
        this.weekRange52 = weekRange52;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
    public void setVolume(String volume) {
        this.volume = Integer.parseInt(volume.replaceAll(",", ""));
    }

    public int getVolumeAvg() {
        return volumeAvg;
    }

    public void setVolumeAvg(int volumeAvg) {
        this.volumeAvg = volumeAvg;
    }
    public void setVolumeAvg(String volumeAvg) {
        this.volumeAvg = Integer.parseInt(volumeAvg.replaceAll(",", ""));
    }

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }

    public String getBeta() {
        return beta;
    }

    public void setBeta(String beta) {
        this.beta = beta;
    }

    public double getPeRatio() {
        return peRatio;
    }

    public void setPeRatio(double peRatio) {
        this.peRatio = peRatio;
    }
    public void setPeRatio(String peRatio) {
        if (peRatio.compareTo("\"N/A\"") == 0)
            this.peRatio = 0;
        else if (peRatio.compareTo("N/A") == 0)
            this.peRatio = 0;
        else
            this.peRatio = Double.parseDouble(peRatio);
    }

    public double getEps() {
        return eps;
    }

    public void setEps(double eps) {
        this.eps = eps;
    }
    public void setEps(String eps) {

        if (eps .compareTo("\"N/A\"") == 0)
            this.eps  = 0;
        else if (eps .compareTo("N/A") == 0)
            this.eps  = 0;
        else
            this.eps  = Double.parseDouble(eps );
    }

    public String getEarningsDate() {
        return earningsDate;
    }

    public void setEarningsDate(String earningsDate) {
        this.earningsDate = earningsDate;
    }

    public String getForwardDividend() {
        return forwardDividend;
    }

    public void setForwardDividend(String forwardDividend) {
        this.forwardDividend = forwardDividend;
    }

    public String getExDividendData() {
        return exDividendData;
    }

    public void setExDividendData(String exDividendData) {
        this.exDividendData = exDividendData;
    }

    public String getTargetEst1year() {
        return targetEst1year;
    }

    public void setTargetEst1year(String targetEst1year) {
        this.targetEst1year = targetEst1year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        YahooRealtimeDataStruct that = (YahooRealtimeDataStruct) o;

        if (Double.compare(that.current, current) != 0) return false;
        if (Double.compare(that.low, low) != 0) return false;
        if (Double.compare(that.high, high) != 0) return false;
        if (Double.compare(that.close, close) != 0) return false;
        if (Double.compare(that.previousClose, previousClose) != 0) return false;
        if (Double.compare(that.open, open) != 0) return false;
        if (Double.compare(that.bid, bid) != 0) return false;
        if (Double.compare(that.ask, ask) != 0) return false;
        if (volume != that.volume) return false;
        if (volumeAvg != that.volumeAvg) return false;
        if (Double.compare(that.peRatio, peRatio) != 0) return false;
        if (Double.compare(that.eps, eps) != 0) return false;
        if (companyName != null ? !companyName.equals(that.companyName) : that.companyName != null)
            return false;
        if (stockSym != null ? !stockSym.equals(that.stockSym) : that.stockSym != null)
            return false;
        if (change != null ? !change.equals(that.change) : that.change != null) return false;
        if (lastUpdatedTime != null ? !lastUpdatedTime.equals(that.lastUpdatedTime) : that.lastUpdatedTime != null)
            return false;
        if (dayRange != null ? !dayRange.equals(that.dayRange) : that.dayRange != null)
            return false;
        if (weekRange52 != null ? !weekRange52.equals(that.weekRange52) : that.weekRange52 != null)
            return false;
        if (marketCap != null ? !marketCap.equals(that.marketCap) : that.marketCap != null)
            return false;
        if (beta != null ? !beta.equals(that.beta) : that.beta != null) return false;
        if (earningsDate != null ? !earningsDate.equals(that.earningsDate) : that.earningsDate != null)
            return false;
        if (forwardDividend != null ? !forwardDividend.equals(that.forwardDividend) : that.forwardDividend != null)
            return false;
        if (exDividendData != null ? !exDividendData.equals(that.exDividendData) : that.exDividendData != null)
            return false;
        return targetEst1year != null ? targetEst1year.equals(that.targetEst1year) : that.targetEst1year == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = companyName != null ? companyName.hashCode() : 0;
        result = 31 * result + (stockSym != null ? stockSym.hashCode() : 0);
        temp = Double.doubleToLongBits(current);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (change != null ? change.hashCode() : 0);
        result = 31 * result + (lastUpdatedTime != null ? lastUpdatedTime.hashCode() : 0);
        temp = Double.doubleToLongBits(low);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(high);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(close);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(previousClose);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(open);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(bid);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ask);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (dayRange != null ? dayRange.hashCode() : 0);
        result = 31 * result + (weekRange52 != null ? weekRange52.hashCode() : 0);
        result = 31 * result + volume;
        result = 31 * result + volumeAvg;
        result = 31 * result + (marketCap != null ? marketCap.hashCode() : 0);
        result = 31 * result + (beta != null ? beta.hashCode() : 0);
        temp = Double.doubleToLongBits(peRatio);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(eps);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (earningsDate != null ? earningsDate.hashCode() : 0);
        result = 31 * result + (forwardDividend != null ? forwardDividend.hashCode() : 0);
        result = 31 * result + (exDividendData != null ? exDividendData.hashCode() : 0);
        result = 31 * result + (targetEst1year != null ? targetEst1year.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "YahooRealtimeDataStruct{" +
                "companyName='" + companyName + '\'' +
                ", stockSym='" + stockSym + '\'' +
                ", current=" + current +
                ", change='" + change + '\'' +
                ", lastUpdatedTime='" + lastUpdatedTime + '\'' +
                ", low=" + low +
                ", high=" + high +
                ", close=" + close +
                ", previousClose=" + previousClose +
                ", open=" + open +
                ", bid=" + bid +
                ", ask=" + ask +
                ", dayRange='" + dayRange + '\'' +
                ", weekRange52='" + weekRange52 + '\'' +
                ", volume=" + volume +
                ", volumeAvg=" + volumeAvg +
                ", marketCap='" + marketCap + '\'' +
                ", beta='" + beta + '\'' +
                ", peRatio=" + peRatio +
                ", eps=" + eps +
                ", earningsDate='" + earningsDate + '\'' +
                ", forwardDividend='" + forwardDividend + '\'' +
                ", exDividendData='" + exDividendData + '\'' +
                ", targetEst1year='" + targetEst1year + '\'' +
                '}';
    }
}
