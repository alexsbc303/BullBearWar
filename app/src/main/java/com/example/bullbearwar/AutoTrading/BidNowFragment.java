package com.example.bullbearwar.AutoTrading;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bullbearwar.Firebase.StockProfileStruct;
import com.example.bullbearwar.Firebase.TradeRecordStruct;
import com.example.bullbearwar.Firebase.TradingMasterActivity;
import com.example.bullbearwar.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.bullbearwar.Firebase.TradingMasterActivity.currentUser;
import static com.example.bullbearwar.Firebase.TradingMasterActivity.mDatabase;
import static com.example.bullbearwar.Firebase.TradingMasterActivity.tabHost;

public class BidNowFragment extends Fragment {
    View view;

    TextView bidPriceTextView, totalSellPriceTextView;
    TextView sellTime;
    EditText sellSharesEditText;
    Button sellNowBtn;

    Thread myThread;
    Runnable myRunnableThread;

    Date noteTS;
    String quoteNum;
    private double bidPriceInMarket, requiredTotalBidPrice;


    public BidNowFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_bid_now, container, false);

        findView();
        initi();
        runThread();
        onTextchanged();
        onSellNowBtnClicked();

        return view;
    }

    private void findView(){
        bidPriceTextView = view.findViewById(R.id.bidPrice);
        totalSellPriceTextView = view.findViewById(R.id.totalSellPrice);
        sellSharesEditText = view.findViewById(R.id.sellShares);
        sellTime = view.findViewById(R.id.sellTime);
        sellNowBtn = view.findViewById(R.id.sellNowBtn);
    }

    private void initi(){
        bidPriceInMarket = getArguments().getDouble("bidPriceInMarket");
        quoteNum = getArguments().getString("quoteNum");
        bidPriceTextView.setText(String.valueOf("$" + bidPriceInMarket));
    }

    public void onSellNowBtnClicked(){
        sellNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    String stockSym = quoteNum;

                    boolean tradeType = true; // 0 => buy ; 1 => sell
                    Double tempTotalTradePrice = requiredTotalBidPrice;
                    int shares = Integer.parseInt(sellSharesEditText.getText().toString());
                    Date now = noteTS;

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

                            // update profile list
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
                                ((TradingMasterActivity)getActivity()).updatePortfolioAndViews();

                                myThread.interrupt();
                                Toast.makeText(getContext(), "Successful!", Toast.LENGTH_SHORT).show();
                                Log.e("transaction", "successfully updated firebase");


                                tabHost.setCurrentTab(0);

                                Toast.makeText(getActivity(), "Successful!", Toast.LENGTH_SHORT).show();
                                Log.e("transaction", "successfully updated firebase");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(getActivity(), "You do not have enough shares!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "You do not hold this stock!", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
    }

    public void onTextchanged() {
        sellSharesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0) {
                    totalSellPriceTextView.setText(getFormatedAmount(getTotalTradePrice(s)));
                } else {
                    totalSellPriceTextView.setText("$0");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    int number = Integer.parseInt(s.toString());
                    String askPriceInYahoo = bidPriceTextView.getText().toString();
                    double askPrice = bidPriceInMarket;
                    double total = number * askPrice;
                    requiredTotalBidPrice = total;
                    totalSellPriceTextView.setText(getFormatedAmount(total));
                } else {
                    totalSellPriceTextView.setText("$0");
                }
            }
        });

    }

    private double getTotalTradePrice(CharSequence s) {
        if (s == null) {
            return 0.0;
        }

        int number = Integer.parseInt(s.toString());
        requiredTotalBidPrice = number * bidPriceInMarket;
        return requiredTotalBidPrice;

    }

    private String getFormatedAmount(Double amount) {
        return '$' + NumberFormat.getNumberInstance(Locale.US).format(amount);
    }

    private void runThread() {
        myThread = null;
        myRunnableThread = new CountDownRunner();
        myThread= new Thread(myRunnableThread);
        myThread.start();
    }

    public void doWork() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try{
                    noteTS = Calendar.getInstance().getTime();

                    // 01 January 2013
                    String date = DateFormat.format("dd/MM/yyyy", noteTS).toString();

                    // 12:00:00
                    String time = DateFormat.format("HH:mm:ss", noteTS).toString();

                    String showTime = date + " " + time;
                    sellTime.setText(showTime);
                }catch (Exception e) {}
            }
        });
    }

    class CountDownRunner implements Runnable {
        // @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    doWork();
                    Thread.sleep(1000); // Pause of 1 Second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }catch(Exception e){
                }
            }
        }
    }

}
