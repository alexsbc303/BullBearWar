package com.example.bullbearwar.AutoTrading;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bullbearwar.Firebase.StockProfileStruct;
import com.example.bullbearwar.Firebase.TradeRecordStruct;
import com.example.bullbearwar.Firebase.TradingMasterActivity;
import com.example.bullbearwar.MainActivity;
import com.example.bullbearwar.R;
import com.example.bullbearwar.Searching.YahooRealtimeDataStruct;
import com.github.mikephil.charting.formatter.IFillFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.acl.LastOwnerException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.bullbearwar.Firebase.TradingMasterActivity.currentUser;
import static com.example.bullbearwar.Firebase.TradingMasterActivity.mDatabase;

public class TimeService extends Service {
    // constant
    public static final long NOTIFY_INTERVAL = 10 * 1000; // 10 seconds
    public static final String NOTIFICATION_RUNNING_TITLE = "Auto-Trading Service";
    public static final String NOTIFICATION_RUNNING_MSG = "Your Auto-Trading Service is running.";
    public static final String NOTIFICATION_NOT_ENOUGHT_CASH_TITLE = "No Enough Cash";
    public static final String NOTIFICATION_NOT_ENOUGHT_CASH_MSG = "You don't have enough cash.";
    public static final String NOTIFICATION_SUCCESSFUL_TITLE = "Auto-Trading Service";

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    public static Timer mTimer = null;

    private NotificationManager autoTradingNotificationManager;

    public static List<AutoTradingRecordStruct> at_recordList;
//    public static List<String> at_waitingForBuyList;
//    public static List<String> at_waitingForSellList;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        initi();

        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);

    }

    private void initi() {
//        at_waitingForBuyList = new ArrayList<>();
//        at_waitingForSellList = new ArrayList<>();

        if (currentUser != null) {
            at_recordList = currentUser.getAt_recordList();

//            // generate buy list and sell list
//            for (AutoTradingRecordStruct tempRecord : at_recordList) {
//                if (tempRecord.isInProgress()){
//                    if (tempRecord.isTradeTypeAutoBuy()){
//                        at_waitingForBuyList.add(tempRecord.getTargetStockSym());
//                    }else{
//                        at_waitingForSellList.add(tempRecord.getTargetStockSym());
//                    }
//                }
//            }
        }


    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // check out-dated trade i.e. current time > ending time

                    for (AutoTradingRecordStruct tempRecord : at_recordList) {

                        if (Calendar.getInstance().getTimeInMillis() > tempRecord.getEndingTime()){

                            tempRecord.setStatusToUnsuccessful();

                        }
                    }

                    boolean running = false;
                    for (AutoTradingRecordStruct waitingStockRecord : at_recordList) {

                        if (waitingStockRecord.isInProgress()) {

                            running = true;

                            if (waitingStockRecord.isTradeTypeAutoBuy()) {
                                // auto buy process
                                new GetYahooRealtimeData_buy().execute(waitingStockRecord);

                            } else {
                                // auto sell process
                                new GetYahooRealtimeData_sell().execute(waitingStockRecord);
                            }
                        }
                    }
                    sendMessage();
                    if (running){

                        // notification
                        String title = NOTIFICATION_RUNNING_TITLE;
                        String msg = NOTIFICATION_RUNNING_MSG;
                        int id = 9999;
                        boolean showTime = false;
                        sendNotification(title,msg,id,showTime);
                    }else{
                        cancelNotification(9999);
                    }
                }

            });
        }

        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
            return sdf.format(new Date());
        }

        private void sendNotification(String title, String msg, int notificationID, boolean showTime) {
            autoTradingNotificationManager = (NotificationManager) getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            //get pending intent
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    new Intent(getApplicationContext(), TradingMasterActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);


            //Create notification
            NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.bullbearwall)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setShowWhen(showTime)
                    .setContentText(msg).setAutoCancel(true);
            alamNotificationBuilder.setContentIntent(contentIntent);

            //notiy notification manager about new notification
            autoTradingNotificationManager.notify(notificationID, alamNotificationBuilder.build());
        }

        public int generateNotificationIDByStockSym(String stockSym){
            return Integer.parseInt(stockSym.substring(0,stockSym.length()-3));
        }

        private void cancelNotification(int NOTIFICATION_ID) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_ID);
        }


        public void onBuyActions(String quoteNum, double requiredTotalAskPrice, int volume) {

            if (currentUser != null) {
                String stockSym = quoteNum;

                boolean tradeType = false; // 0 => buy ; 1 => sell
                double tempTotalTradePrice = requiredTotalAskPrice;
                int shares = volume;
                Date now = Calendar.getInstance().getTime();

                TradeRecordStruct buyTransaction = new TradeRecordStruct(stockSym, tradeType, tempTotalTradePrice, shares, now);

                if (buyTransaction.hasEnoughCashToBuy(currentUser.getCashFlow())) {
                    currentUser.setCashFlow(currentUser.getCashFlow() - tempTotalTradePrice);
                    currentUser.setStockValue(currentUser.getStockValue() + tempTotalTradePrice);
                    currentUser.updateTotalAsset();

                    // update stockProfileList
                    List<StockProfileStruct> list = currentUser.getStockProfileList();
                    if (buyTransaction.isAlreadyBroughtThisStock(list, stockSym)) {
                        // have brought this stock before
                        Log.e("transaction", "have brought this stock before");

                        int position = buyTransaction.getProfileIndex(list, stockSym);
                        StockProfileStruct stockProfile = list.get(position);

                        int newShares = stockProfile.getTotalShares() + shares;
                        double newAvgPrice = (stockProfile.getAveragePrice() * stockProfile.getTotalShares() + tempTotalTradePrice) / newShares;
                        Log.e("price", String.valueOf(stockProfile.getAveragePrice()));
                        Log.e("price", String.valueOf(stockProfile.getTotalShares()));
                        Log.e("price", String.valueOf(stockProfile.getAveragePrice() * stockProfile.getTotalShares()));
                        Log.e("price", String.valueOf(tempTotalTradePrice));

                        stockProfile.setAveragePrice(newAvgPrice);
                        stockProfile.setTotalShares(newShares);
                        stockProfile.setLastTradeDateTime(now);

                        list.remove(position);
                        list.add(stockProfile);

                        currentUser.setStockProfileList(list);
                    } else {
                        // have not brought this stock before
                        Log.e("transaction", "have NOT brought this stock before");
                        list.add(new StockProfileStruct(stockSym, tempTotalTradePrice / shares, shares, now));
                        currentUser.setStockProfileList(list);
                    }

                    // update tradeRecordList
                    List<TradeRecordStruct> stockProfileList = currentUser.getTradeRecordList();
                    if (stockProfileList == null) {
                        stockProfileList = new ArrayList<>();
                    }
                    stockProfileList.add(new TradeRecordStruct(stockSym, tradeType, tempTotalTradePrice, shares, now));
                    currentUser.setTradeRecordList(stockProfileList);

                    // update at_recordList
                    currentUser.setAt_recordList(at_recordList);

                    Log.e("transaction", "user setting finished");

                    try {
                        mDatabase.child("users").child(currentUser.getUID()).setValue(currentUser);
                        Log.e("transaction", "successfully updated firebase");

//                        ((TradingMasterActivity) getActivity()).updateViews();
//                        ((TradingMasterActivity) getActivity()).updateRecordList();
//                        tabHost.setCurrentTab(0);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
//                    Toast.makeText(getContext(), "Sorry, you don't have enough money.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private class GetYahooRealtimeData_buy extends AsyncTask<AutoTradingRecordStruct, Void, YahooRealtimeDataStruct> {
            AutoTradingRecordStruct waitingStockRecord ;

            private void updateMap(Elements rows, Map dataMap) {
                for (int i = 0; i < rows.size(); i++) { //first row is the col names so skip it.
                    Element row = rows.get(i);
                    Elements cols = row.select("td");

                    dataMap.put(cols.get(0).text(), cols.get(1).text());
                }
            }

            private void generateRealTimeDataSet(YahooRealtimeDataStruct realTimeDataSet, Map dataMap) {

                // add from head
                realTimeDataSet.setCompanyName(dataMap.get("companyName").toString());
                realTimeDataSet.setStockSym(dataMap.get("stockSym").toString());
                realTimeDataSet.setCurrent(dataMap.get("stockQuote").toString());
                realTimeDataSet.setChange(dataMap.get("changes").toString());
                realTimeDataSet.setLastUpdatedTime(dataMap.get("updatedTime").toString());

                // add from table
                realTimeDataSet.setPreviousClose(dataMap.get("Previous Close").toString());
                realTimeDataSet.setOpen(dataMap.get("Open").toString());
                realTimeDataSet.setBid(dataMap.get("Bid").toString());
                realTimeDataSet.setAsk(dataMap.get("Ask").toString());
                realTimeDataSet.setDayRange(dataMap.get("Day's Range").toString());
                realTimeDataSet.setWeekRange52(dataMap.get("52 Week Range").toString());
                realTimeDataSet.setVolume(dataMap.get("Volume").toString());
                realTimeDataSet.setVolumeAvg(dataMap.get("Avg. Volume").toString());
                realTimeDataSet.setMarketCap(dataMap.get("Market Cap").toString());
                realTimeDataSet.setBeta(dataMap.get("Beta").toString());
                realTimeDataSet.setPeRatio(dataMap.get("PE Ratio (TTM)").toString());
                realTimeDataSet.setEps(dataMap.get("EPS (TTM)").toString());
                realTimeDataSet.setEarningsDate(dataMap.get("Earnings Date").toString());
                realTimeDataSet.setForwardDividend(dataMap.get("Forward Dividend & Yield").toString());
                realTimeDataSet.setExDividendData(dataMap.get("Ex-Dividend Date").toString());
                realTimeDataSet.setTargetEst1year(dataMap.get("1y Target Est").toString());

                //Sample: [
                // Previous Close 79.550
                // Open 79.900
                // Bid 79.650 x 0
                // Ask 79.700 x 0
                // Day's Range 79.600 - 79.950
                // 52 Week Range 61.800 - 86.000
                // Volume 23,199,947
                // Avg. Volume 33,440,139,
                // Market Cap 1.587T
                // Beta N/A
                // PE Ratio (TTM) 165.94
                // EPS (TTM) 0.480
                // Earnings Date N/A
                // Forward Dividend & Yield 3.99 (5.00%)
                // Ex-Dividend Date 2017-02-23
                // 1y Target Est 81.59]
            }

            private void getHeadInfo(Document doc, Map dataMap) {

                Element stockHead = doc.body().getElementsByClass("D(ib) Fz(18px)").first();
                String[] tokens = stockHead.text().split(" \\(");

                String companyName = tokens[0];
                dataMap.put("companyName", companyName);
                String stockSym = tokens[1].replaceAll("\\)", "");
                dataMap.put("stockSym", stockSym);

                Element stockBody = doc.body().getElementsByClass("D(ib) Mend(20px)").first();

                Elements stockQuoteTag = stockBody.getElementsByAttributeValue("data-reactid", String.valueOf(35));
                String stockQuote = stockQuoteTag.first().text();
                dataMap.put("stockQuote", stockQuote);

                Elements changesTag = stockBody.getElementsByAttributeValue("data-reactid", String.valueOf(37));
                String changes = changesTag.first().text();
                dataMap.put("changes", changes);

                Elements updatedTimeTage = stockBody.getElementsByAttributeValue("data-reactid", String.valueOf(40));
                String updatedTime = updatedTimeTage.first().text();
                dataMap.put("updatedTime", updatedTime);

            }

            private void getTableValues(Document doc, Map dataMap) {
                Element quoteSummary = doc.body().getElementById("quote-summary");
                Element table1 = quoteSummary.select("table").first();
                Element table2 = quoteSummary.select("table").get(1);

                updateMap(table1.select("tr"), dataMap);
                updateMap(table2.select("tr"), dataMap);
            }

            protected YahooRealtimeDataStruct doInBackground(AutoTradingRecordStruct... params) {
                YahooRealtimeDataStruct realTimeDataSet = new YahooRealtimeDataStruct();
                Map dataMap = new HashMap();

                waitingStockRecord = params[0];
                String inputStockNumber = params[0].getTargetStockSym();

                try {
                    String tempUrl = "https://finance.yahoo.com/quote/" + inputStockNumber + "?p=" + inputStockNumber;
                    Log.e("html", "fetching " + tempUrl);

                    Document doc = Jsoup.connect(tempUrl).get();

                    getHeadInfo(doc, dataMap);
                    getTableValues(doc, dataMap);

                    generateRealTimeDataSet(realTimeDataSet, dataMap);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                }

                return realTimeDataSet;
            }

            @Override
            protected void onPostExecute(YahooRealtimeDataStruct result) {
                super.onPostExecute(result);

                double askPriceInMarket = result.getAsk();
                Log.e("autoTrading","checking time: " + getDateTime() + "\n" +
                        "Searching target Sym : " + waitingStockRecord.getTargetStockSym() + "\n" +
                        "target ask price: " + String.valueOf(waitingStockRecord.getTargetAskPrice()) + "\n" +
                        "returned ask price: " + String.valueOf(askPriceInMarket) + "\n"
                );

                if (askPriceInMarket <= waitingStockRecord.getTargetAskPrice()) {

                    //check have enough money
                    double totalCashReqired = askPriceInMarket * waitingStockRecord.getTargetAskVolume();
                    if (currentUser.getCashFlow() >= totalCashReqired) {

                        // successfully bought
                        Log.e("autoTrading", "you have enough money to buy" + waitingStockRecord.getTargetStockSym());

                        waitingStockRecord.setStatusToSuccessful();

                        // notification
                        String title = NOTIFICATION_SUCCESSFUL_TITLE;
                        String msg = "Your stock " + waitingStockRecord.getTargetStockSym() + " has successfully bought.";
                        int id = generateNotificationIDByStockSym(waitingStockRecord.getTargetStockSym());
                        boolean showTime = true;
                        sendNotification(title,msg,id,showTime);

                        // update waiting list
                        waitingStockRecord.setActualAskPrice(result.getAsk());
                        waitingStockRecord.setBuyTime(Calendar.getInstance().getTimeInMillis());


                        // buy action
                        String quoteNum = waitingStockRecord.getTargetStockSym();
                        double requiredTotalAskPrice = totalCashReqired;
                        int volume = waitingStockRecord.getTargetAskVolume();
                        onBuyActions(quoteNum, requiredTotalAskPrice, volume);



                    } else {
                        // do not have enough money
                        // notification
                        String title = NOTIFICATION_NOT_ENOUGHT_CASH_TITLE;
                        String msg = "The price of your stock " + waitingStockRecord.getTargetStockSym() + " is reached. ";
                        int id = generateNotificationIDByStockSym(waitingStockRecord.getTargetStockSym());
                        boolean showTime = true;
                        sendNotification(title,msg,id,showTime);

                        Log.e("autoTrading", "you do not have enough money to buy" + waitingStockRecord.getTargetStockSym());

                    }
                }
            }

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected void onCancelled() {

            }
        }
        //onSellActions(quoteNum, totalCashEarned, volume);

        public void onSellActions(String quoteNum, double totalCashEarned, int volume, double bidPriceInMarket){
                    if (currentUser != null) {
                        String stockSym = quoteNum;

                        boolean tradeType = true; // 0 => buy ; 1 => sell
                        Double tempTotalTradePrice = totalCashEarned;
                        int shares = volume;
                        Date now = Calendar.getInstance().getTime();

                        List<StockProfileStruct> list = currentUser.getStockProfileList();
                        TradeRecordStruct sellTransaction = new TradeRecordStruct(stockSym, tradeType, tempTotalTradePrice, shares, now);

                        if (sellTransaction.isAlreadyBroughtThisStock(list, stockSym)) {
                            int position = sellTransaction.getProfileIndex(list, stockSym);
                            if (sellTransaction.hasEnoughSharesToSell(list, position, shares)) {
                                currentUser.setCashFlow(currentUser.getCashFlow() + tempTotalTradePrice);
                                currentUser.setStockValue(currentUser.getStockValue() - tempTotalTradePrice);
                                currentUser.updateTotalAsset();


                                // update tradeRecordList
                                List<TradeRecordStruct> stockProfileList = currentUser.getTradeRecordList();
                                if (stockProfileList == null) {
                                    stockProfileList = new ArrayList<>();
                                }
                                double profit = -(list.get(position).getAveragePrice() - bidPriceInMarket) * shares;
                                TradeRecordStruct tempTradeRecord = new TradeRecordStruct(stockSym, tradeType, tempTotalTradePrice, shares, now);
                                tempTradeRecord.setProfit(profit);
                                stockProfileList.add(tempTradeRecord);
                                currentUser.setTradeRecordList(stockProfileList);


                                if (shares == currentUser.getStockProfileList().get(position).getTotalShares()) {
                                    list.remove(position);
                                } else {
                                    StockProfileStruct stockProfile = list.get(position);
                                    int newShares = stockProfile.getTotalShares() - shares;
                                    double newAvgPrice = (stockProfile.getAveragePrice() * stockProfile.getTotalShares() - tempTotalTradePrice) / newShares;

                                    stockProfile.setAveragePrice(newAvgPrice);
                                    stockProfile.setTotalShares(newShares);
                                    stockProfile.setLastTradeDateTime(now);

                                    list.remove(position);
                                    list.add(stockProfile);
                                }
                                currentUser.setStockProfileList(list);

                                Log.e("transaction", "user setting finished");

                                try {
                                    mDatabase.child("users").child(currentUser.getUID()).setValue(currentUser);
//                                updateViews();
//                                updateRecordList();

                                    Log.e("transaction", "successfully updated firebase");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                Log.e("autoSell","You do not have enough shares!");
                            }
                        } else {
                            Log.e("autoSell","You do not hold this stock!");
                        }

                    }

        }

        private class GetYahooRealtimeData_sell extends AsyncTask<AutoTradingRecordStruct, Void, YahooRealtimeDataStruct> {
            AutoTradingRecordStruct waitingStockRecord ;

            private void updateMap(Elements rows, Map dataMap) {
                for (int i = 0; i < rows.size(); i++) { //first row is the col names so skip it.
                    Element row = rows.get(i);
                    Elements cols = row.select("td");

                    dataMap.put(cols.get(0).text(), cols.get(1).text());
                }
            }

            private void generateRealTimeDataSet(YahooRealtimeDataStruct realTimeDataSet, Map dataMap) {

                // add from head
                realTimeDataSet.setCompanyName(dataMap.get("companyName").toString());
                realTimeDataSet.setStockSym(dataMap.get("stockSym").toString());
                realTimeDataSet.setCurrent(dataMap.get("stockQuote").toString());
                realTimeDataSet.setChange(dataMap.get("changes").toString());
                realTimeDataSet.setLastUpdatedTime(dataMap.get("updatedTime").toString());

                // add from table
                realTimeDataSet.setPreviousClose(dataMap.get("Previous Close").toString());
                realTimeDataSet.setOpen(dataMap.get("Open").toString());
                realTimeDataSet.setBid(dataMap.get("Bid").toString());
                realTimeDataSet.setAsk(dataMap.get("Ask").toString());
                realTimeDataSet.setDayRange(dataMap.get("Day's Range").toString());
                realTimeDataSet.setWeekRange52(dataMap.get("52 Week Range").toString());
                realTimeDataSet.setVolume(dataMap.get("Volume").toString());
                realTimeDataSet.setVolumeAvg(dataMap.get("Avg. Volume").toString());
                realTimeDataSet.setMarketCap(dataMap.get("Market Cap").toString());
                realTimeDataSet.setBeta(dataMap.get("Beta").toString());
                realTimeDataSet.setPeRatio(dataMap.get("PE Ratio (TTM)").toString());
                realTimeDataSet.setEps(dataMap.get("EPS (TTM)").toString());
                realTimeDataSet.setEarningsDate(dataMap.get("Earnings Date").toString());
                realTimeDataSet.setForwardDividend(dataMap.get("Forward Dividend & Yield").toString());
                realTimeDataSet.setExDividendData(dataMap.get("Ex-Dividend Date").toString());
                realTimeDataSet.setTargetEst1year(dataMap.get("1y Target Est").toString());

                //Sample: [
                // Previous Close 79.550
                // Open 79.900
                // Bid 79.650 x 0
                // Ask 79.700 x 0
                // Day's Range 79.600 - 79.950
                // 52 Week Range 61.800 - 86.000
                // Volume 23,199,947
                // Avg. Volume 33,440,139,
                // Market Cap 1.587T
                // Beta N/A
                // PE Ratio (TTM) 165.94
                // EPS (TTM) 0.480
                // Earnings Date N/A
                // Forward Dividend & Yield 3.99 (5.00%)
                // Ex-Dividend Date 2017-02-23
                // 1y Target Est 81.59]
            }

            private void getHeadInfo(Document doc, Map dataMap) {

                Element stockHead = doc.body().getElementsByClass("D(ib) Fz(18px)").first();
                String[] tokens = stockHead.text().split(" \\(");

                String companyName = tokens[0];
                dataMap.put("companyName", companyName);
                String stockSym = tokens[1].replaceAll("\\)", "");
                dataMap.put("stockSym", stockSym);

                Element stockBody = doc.body().getElementsByClass("D(ib) Mend(20px)").first();

                Elements stockQuoteTag = stockBody.getElementsByAttributeValue("data-reactid", String.valueOf(35));
                String stockQuote = stockQuoteTag.first().text();
                dataMap.put("stockQuote", stockQuote);

                Elements changesTag = stockBody.getElementsByAttributeValue("data-reactid", String.valueOf(37));
                String changes = changesTag.first().text();
                dataMap.put("changes", changes);

                Elements updatedTimeTage = stockBody.getElementsByAttributeValue("data-reactid", String.valueOf(40));
                String updatedTime = updatedTimeTage.first().text();
                dataMap.put("updatedTime", updatedTime);

            }

            private void getTableValues(Document doc, Map dataMap) {
                Element quoteSummary = doc.body().getElementById("quote-summary");
                Element table1 = quoteSummary.select("table").first();
                Element table2 = quoteSummary.select("table").get(1);

                updateMap(table1.select("tr"), dataMap);
                updateMap(table2.select("tr"), dataMap);
            }

            protected YahooRealtimeDataStruct doInBackground(AutoTradingRecordStruct... params) {
                YahooRealtimeDataStruct realTimeDataSet = new YahooRealtimeDataStruct();
                Map dataMap = new HashMap();

                waitingStockRecord = params[0];
                String inputStockNumber = params[0].getTargetStockSym();

                try {
                    String tempUrl = "https://finance.yahoo.com/quote/" + inputStockNumber + "?p=" + inputStockNumber;
                    Log.e("html", "fetching " + tempUrl);

                    Document doc = Jsoup.connect(tempUrl).get();

                    getHeadInfo(doc, dataMap);
                    getTableValues(doc, dataMap);

                    generateRealTimeDataSet(realTimeDataSet, dataMap);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                }

                return realTimeDataSet;
            }

            @Override
            protected void onPostExecute(YahooRealtimeDataStruct result) {
                super.onPostExecute(result);

                double bidPriceInMarket = result.getBid();
                Log.e("autoTrading","checking time: " + getDateTime() + "\n" +
                        "Searching target Sym : " + waitingStockRecord.getTargetStockSym() + "\n" +
                        "target bid price: " + String.valueOf(waitingStockRecord.getTargetBidPrice()) + "\n" +
                        "returned bid price: " + String.valueOf(bidPriceInMarket) + "\n"
                );

                if (bidPriceInMarket >= waitingStockRecord.getTargetBidPrice()) {

//                    //check have enough money
                    double totalCashEarned = bidPriceInMarket * waitingStockRecord.getTargetBidVolume();
//                    if (currentUser.getCashFlow() >= totalCashReqired) {
//
//                        // successfully bought
//                        Log.e("autoTrading", "you have enough money to buy" + waitingStockRecord.getTargetStockSym());
//
                    waitingStockRecord.setStatusToSuccessful();
//
                        // notification
                        String title = NOTIFICATION_SUCCESSFUL_TITLE;
                        String msg = "Your stock " + waitingStockRecord.getTargetStockSym() + " has successfully been sold.";
                        int id = generateNotificationIDByStockSym(waitingStockRecord.getTargetStockSym());
                        boolean showTime = true;
                        sendNotification(title,msg,id,showTime);
//
                        // update waiting list
                        waitingStockRecord.setActualBidPrice(result.getAsk());
                        waitingStockRecord.setSellTime(Calendar.getInstance().getTimeInMillis());
//
//
                        // sell action
                        String quoteNum = waitingStockRecord.getTargetStockSym();
                        int volume = waitingStockRecord.getTargetBidVolume();
                        onSellActions(quoteNum, totalCashEarned, volume, bidPriceInMarket);
//
//
//
                    }
                }


            @Override
            protected void onPreExecute() {

            }

            @Override
            protected void onCancelled() {

            }
        }


        private void sendMessage() {
            Intent intent = new Intent("my-event");
            // add data
            intent.putExtra("message", "data");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }

    }


}
