package com.example.bullbearwar.CurrencyConverter;

import java.util.ArrayList;
import java.util.List;

public class CurrencyExchange {

    /**
     * base : HKD
     * date : 2018-04-13
     * rates : {"AUD":0.16342,"BGN":0.20228,"BRL":0.43417,"CAD":0.16012,"CHF":0.1226,"CNY":0.80014,"CZK":2.6174,"DKK":0.77019,"EUR":0.10343,"GBP":0.089361,"HRK":0.76706,"HUF":32.179,"IDR":1752,"ILS":0.448,"INR":8.3068,"ISK":12.577,"JPY":13.718,"KRW":136.14,"MXN":2.3081,"MYR":0.49349,"NOK":0.9892,"NZD":0.17275,"PHP":6.6161,"PLN":0.43194,"RON":0.482,"RUB":7.8831,"SEK":1.0736,"SGD":0.16712,"THB":3.9682,"TRY":0.52138,"USD":0.12739,"ZAR":1.5355}
     */

    private String base;
    private String date;
    private RatesBean rates;
    public List<Currency> getCurrencyList(){
        List<Currency> currencyList = new ArrayList<>();
        currencyList.add(new Currency("AUD", rates.getAUD()));
        currencyList.add(new Currency("BGN", rates.getBGN()));
        currencyList.add(new Currency("BRL", rates.getBRL()));
        currencyList.add(new Currency("CAD", rates.getCAD()));
        currencyList.add(new Currency("CHF", rates.getCHF()));

        currencyList.add(new Currency("CNY", rates.getCNY()));
        currencyList.add(new Currency("CZK", rates.getCZK()));
        currencyList.add(new Currency("DKK", rates.getDKK()));
        currencyList.add(new Currency("EUR", rates.getEUR()));
        currencyList.add(new Currency("GBP", rates.getGBP()));

        currencyList.add(new Currency("HRK", rates.getHRK()));
        currencyList.add(new Currency("HUF", rates.getHUF()));
        currencyList.add(new Currency("IDR", rates.getIDR()));
        currencyList.add(new Currency("ILS", rates.getILS()));
        currencyList.add(new Currency("INR", rates.getINR()));

        currencyList.add(new Currency("ISK", rates.getISK()));
        currencyList.add(new Currency("JPY", rates.getJPY()));
        currencyList.add(new Currency("KRW", rates.getKRW()));
        currencyList.add(new Currency("MXN", rates.getMXN()));
        currencyList.add(new Currency("MYR", rates.getMYR()));

        currencyList.add(new Currency("NOK", rates.getNOK()));
        currencyList.add(new Currency("NZD", rates.getNZD()));
        currencyList.add(new Currency("PHP", rates.getPHP()));
        currencyList.add(new Currency("PLN", rates.getPLN()));
        currencyList.add(new Currency("RON", rates.getRON()));

        currencyList.add(new Currency("RUB", rates.getRUB()));
        currencyList.add(new Currency("SEK", rates.getSEK()));
        currencyList.add(new Currency("SGD", rates.getSGD()));
        currencyList.add(new Currency("THB", rates.getTHB()));
        currencyList.add(new Currency("TRY", rates.getTRY()));

        currencyList.add(new Currency("USD", rates.getUSD()));
        currencyList.add(new Currency("ZAR", rates.getZAR()));

        return currencyList;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public RatesBean getRates() {
        return rates;
    }

    public void setRates(RatesBean rates) {
        this.rates = rates;
    }

    public static class RatesBean  {
        /**
         * AUD : 0.16342
         * BGN : 0.20228
         * BRL : 0.43417
         * CAD : 0.16012
         * CHF : 0.1226
         * CNY : 0.80014
         * CZK : 2.6174
         * DKK : 0.77019
         * EUR : 0.10343
         * GBP : 0.089361
         * HRK : 0.76706
         * HUF : 32.179
         * IDR : 1752.0
         * ILS : 0.448
         * INR : 8.3068
         * ISK : 12.577
         * JPY : 13.718
         * KRW : 136.14
         * MXN : 2.3081
         * MYR : 0.49349
         * NOK : 0.9892
         * NZD : 0.17275
         * PHP : 6.6161
         * PLN : 0.43194
         * RON : 0.482
         * RUB : 7.8831
         * SEK : 1.0736
         * SGD : 0.16712
         * THB : 3.9682
         * TRY : 0.52138
         * USD : 0.12739
         * ZAR : 1.5355
         */

        private double AUD;
        private double BGN;
        private double BRL;
        private double CAD;
        private double CHF;
        private double CNY;
        private double CZK;
        private double DKK;
        private double EUR;
        private double GBP;
        private double HRK;
        private double HUF;
        private double IDR;
        private double ILS;
        private double INR;
        private double ISK;
        private double JPY;
        private double KRW;
        private double MXN;
        private double MYR;
        private double NOK;
        private double NZD;
        private double PHP;
        private double PLN;
        private double RON;
        private double RUB;
        private double SEK;
        private double SGD;
        private double THB;
        private double TRY;
        private double USD;
        private double ZAR;

        public double getAUD() {
            return AUD;
        }

        public void setAUD(double AUD) {
            this.AUD = AUD;
        }

        public double getBGN() {
            return BGN;
        }

        public void setBGN(double BGN) {
            this.BGN = BGN;
        }

        public double getBRL() {
            return BRL;
        }

        public void setBRL(double BRL) {
            this.BRL = BRL;
        }

        public double getCAD() {
            return CAD;
        }

        public void setCAD(double CAD) {
            this.CAD = CAD;
        }

        public double getCHF() {
            return CHF;
        }

        public void setCHF(double CHF) {
            this.CHF = CHF;
        }

        public double getCNY() {
            return CNY;
        }

        public void setCNY(double CNY) {
            this.CNY = CNY;
        }

        public double getCZK() {
            return CZK;
        }

        public void setCZK(double CZK) {
            this.CZK = CZK;
        }

        public double getDKK() {
            return DKK;
        }

        public void setDKK(double DKK) {
            this.DKK = DKK;
        }

        public double getEUR() {
            return EUR;
        }

        public void setEUR(double EUR) {
            this.EUR = EUR;
        }

        public double getGBP() {
            return GBP;
        }

        public void setGBP(double GBP) {
            this.GBP = GBP;
        }

        public double getHRK() {
            return HRK;
        }

        public void setHRK(double HRK) {
            this.HRK = HRK;
        }

        public double getHUF() {
            return HUF;
        }

        public void setHUF(double HUF) {
            this.HUF = HUF;
        }

        public double getIDR() {
            return IDR;
        }

        public void setIDR(double IDR) {
            this.IDR = IDR;
        }

        public double getILS() {
            return ILS;
        }

        public void setILS(double ILS) {
            this.ILS = ILS;
        }

        public double getINR() {
            return INR;
        }

        public void setINR(double INR) {
            this.INR = INR;
        }

        public double getISK() {
            return ISK;
        }

        public void setISK(double ISK) {
            this.ISK = ISK;
        }

        public double getJPY() {
            return JPY;
        }

        public void setJPY(double JPY) {
            this.JPY = JPY;
        }

        public double getKRW() {
            return KRW;
        }

        public void setKRW(double KRW) {
            this.KRW = KRW;
        }

        public double getMXN() {
            return MXN;
        }

        public void setMXN(double MXN) {
            this.MXN = MXN;
        }

        public double getMYR() {
            return MYR;
        }

        public void setMYR(double MYR) {
            this.MYR = MYR;
        }

        public double getNOK() {
            return NOK;
        }

        public void setNOK(double NOK) {
            this.NOK = NOK;
        }

        public double getNZD() {
            return NZD;
        }

        public void setNZD(double NZD) {
            this.NZD = NZD;
        }

        public double getPHP() {
            return PHP;
        }

        public void setPHP(double PHP) {
            this.PHP = PHP;
        }

        public double getPLN() {
            return PLN;
        }

        public void setPLN(double PLN) {
            this.PLN = PLN;
        }

        public double getRON() {
            return RON;
        }

        public void setRON(double RON) {
            this.RON = RON;
        }

        public double getRUB() {
            return RUB;
        }

        public void setRUB(double RUB) {
            this.RUB = RUB;
        }

        public double getSEK() {
            return SEK;
        }

        public void setSEK(double SEK) {
            this.SEK = SEK;
        }

        public double getSGD() {
            return SGD;
        }

        public void setSGD(double SGD) {
            this.SGD = SGD;
        }

        public double getTHB() {
            return THB;
        }

        public void setTHB(double THB) {
            this.THB = THB;
        }

        public double getTRY() {
            return TRY;
        }

        public void setTRY(double TRY) {
            this.TRY = TRY;
        }

        public double getUSD() {
            return USD;
        }

        public void setUSD(double USD) {
            this.USD = USD;
        }

        public double getZAR() {
            return ZAR;
        }

        public void setZAR(double ZAR) {
            this.ZAR = ZAR;
        }
    }
}
