package com.example.bullbearwar.AutoTrading;

import android.icu.text.PluralRules;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

public class AskNowFragment extends Fragment {

    View view;
    TextView askPriceTextView, totalBuyPriceTextView;
    TextView buyTime;
    EditText buySharesEditText;
    Button buyNowBtn;

    Thread myThread;
    Runnable myRunnableThread;

    Date noteTS;
    String quoteNum;
    private double askPriceInMarket, requiredTotalAskPrice;

    public AskNowFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_ask_now, container, false);

        findView();
        initi();
        runThread();
        onTextchanged();
        onBuyNowBtnClicked();
        return view;
    }

    private void findView(){
        askPriceTextView = view.findViewById(R.id.askPrice);
        totalBuyPriceTextView = view.findViewById(R.id.totalBuyPrice);
        buySharesEditText = view.findViewById(R.id.buyShares);
        buyTime = view.findViewById(R.id.buyTime);
        buyNowBtn = view.findViewById(R.id.buyNowBtn);
    }

    private void initi(){
        askPriceInMarket = getArguments().getDouble("askPriceInMarket");
        quoteNum = getArguments().getString("quoteNum");
        askPriceTextView.setText(String.valueOf("$" + askPriceInMarket));
    }

    public void onBuyNowBtnClicked(){
        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    String stockSym = quoteNum;

                    boolean tradeType = false; // 0 => buy ; 1 => sell
                    Double tempTotalTradePrice = requiredTotalAskPrice;
                    int shares = Integer.parseInt(buySharesEditText.getText().toString());
                    Date now = noteTS;

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

                        Log.e("transaction", "user setting finished");

                        try {
                            ((TradingMasterActivity)getActivity()).updatePortfolioAndViews();

                            myThread.interrupt();
                            Toast.makeText(getContext(), "Successful!", Toast.LENGTH_SHORT).show();
                            Log.e("transaction", "successfully updated firebase");

                            tabHost.setCurrentTab(0);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getContext(), "Sorry, you don't have enough money.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void onTextchanged() {
        buySharesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0) {
                    totalBuyPriceTextView.setText(getFormatedAmount(getTotalTradePrice(s)));
                } else {
                    totalBuyPriceTextView.setText("$0");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    int number = Integer.parseInt(s.toString());
                    String askPriceInYahoo = askPriceTextView.getText().toString();
                    double askPrice = askPriceInMarket;
                    double total = number * askPrice;
                    requiredTotalAskPrice = total;
                    totalBuyPriceTextView.setText(getFormatedAmount(total));
                } else {
                    totalBuyPriceTextView.setText("$0");
                }
            }
        });

    }

    private double getTotalTradePrice(CharSequence s) {
        if (s == null) {
            return 0.0;
        }

        int number = Integer.parseInt(s.toString());
        requiredTotalAskPrice = number * askPriceInMarket;
        return requiredTotalAskPrice;

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
                    buyTime.setText(showTime);
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
