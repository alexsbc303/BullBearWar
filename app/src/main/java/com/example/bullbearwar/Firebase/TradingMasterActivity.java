package com.example.bullbearwar.Firebase;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bullbearwar.AutoTrading.AskNowFragment;
import com.example.bullbearwar.AutoTrading.AutoAskFragment;
import com.example.bullbearwar.AutoTrading.AutoBidFragment;
import com.example.bullbearwar.AutoTrading.AutoTradingRecordStruct;
import com.example.bullbearwar.AutoTrading.BidNowFragment;
import com.example.bullbearwar.AutoTrading.TimeService;
import com.example.bullbearwar.MainActivity;
import com.example.bullbearwar.Navigation.NavigationActivity;
import com.example.bullbearwar.R;
import com.example.bullbearwar.Searching.YahooRealtimeDataStruct;
import com.example.bullbearwar.Searching.YahooSearchActivity;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

import android.os.Handler;

public class TradingMasterActivity extends AppCompatActivity {

    public static TabHost tabHost;
    TableLayout tableLayout;
    TextView displayName, askPrice, bidPrice;
    TextView cashFlow, stockValue, totalAsset;
    TextView totalBuyPrice, totalSellPrice;
    TextView buyTime, sellTime;
    EditText buyShares, sellShares;
    Button search, buy, sell;
    RadioButton radioBuy, radioSell;
    RadioGroup radioGP;
    LinearLayout buyLinearLayout, sellLinearLayout;
    RecyclerView stcckProfileRecyclerView, tradingRecordRecycleView, autoTradingRecordRecyclerView;
    AutoCompleteTextView autoCompleteTextView;
    StockHintAdapter stockHintAdapter;

    TextView QuoteNum, QuoteName, LastUpdatedTime;
    TextView PreviousCloseNum, OpenNum, HighNum, LowNum, VolumnNum;
    TextView currentPrice, currentPriceChange;
    TextView askPriceNum, bidPriceNum;
    RelativeLayout detailedRelativeLayout;
    FrameLayout frameLayout;


    RadioButton askNowRadioButton, bidNowRadioButton;
    RadioButton autoAskRadioButton, autoBidRadioButton;
    private static FragmentManager fragmentManager;

    private double askPriceInMarket, bidPriceInMarket;

    private PieChart mChart;

    public static DatabaseReference mDatabase;
    FirebaseUser user;
    public static UserStruct currentUser;

    Date noteTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tradingTtoolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);

        tabHost = (TabHost) findViewById(R.id.TabHost);
        tabHost.setup();

        //Wallet

        TabHost.TabSpec spec = tabHost.newTabSpec(getResources().getString(R.string.Wallet));
        spec.setContent(R.id.Wallet);
        spec.setIndicator(getResources().getString(R.string.Wallet));
        tabHost.addTab(spec);


        //New Trade

        spec = tabHost.newTabSpec(getResources().getString(R.string.NewTrade));
        spec.setContent(R.id.NewTrade);
        spec.setIndicator(getResources().getString(R.string.NewTrade));
        tabHost.addTab(spec);

        //Record

        spec = tabHost.newTabSpec(getResources().getString(R.string.Record));
        spec.setContent(R.id.Record);
        spec.setIndicator(getResources().getString(R.string.Record));
        tabHost.addTab(spec);

        //auto Record

        spec = tabHost.newTabSpec("auto");
        spec.setContent(R.id.autotrade);
        spec.setIndicator("auto");
        tabHost.addTab(spec);

        tabHost.setOnTabChangedListener(new AnimationOnTrading(this, tabHost));

        findView();
        initi();

        //onTextchanged();
        updateTime();
        fragmentManager = getSupportFragmentManager();

        final Button searchBtn = findViewById(R.id.search);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stockHintAdapter.getCount() == 0) {
                    Log.e("noRecord", "No Record. Try Again.");
                    Toast.makeText(TradingMasterActivity.this, "No Record. Try Again.", Toast.LENGTH_SHORT).show();
                } else {
                    detailedRelativeLayout.setVisibility(View.VISIBLE);
                    String selectedStockSym = "";
                    String inputText = autoCompleteTextView.getText().toString();
                    if (!inputText.contains(".")) {
                        selectedStockSym = stockHintAdapter.getItem(0).toString();
                        autoCompleteTextView.setText(selectedStockSym);
                        Log.e("selectedStockSym", selectedStockSym);
                    } else {
                        selectedStockSym = inputText.toString();

                    }

                    new TradingMasterActivity.GetYahooRealtimeData().execute(selectedStockSym);

                    try {
                        radioGP.setVisibility(View.VISIBLE);
                        frameLayout.setVisibility(View.VISIBLE);

                        InputMethodManager imm = (InputMethodManager) TradingMasterActivity.this.getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(TradingMasterActivity.this.getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    autoCompleteTextView.setText("");
                }
            }
        });

        autoCompleteTextView.setOnKeyListener(new TextView.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            searchBtn.performClick();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trading_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signOut) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Sign out", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initi() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        radioGP.setVisibility(View.INVISIBLE);
        frameLayout.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();

        if (intent.getBooleanExtra("signedIn", true)) {
            if (user != null) {
                Log.e("user", "user is signed in");
                mDatabase.child("users")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserStruct users = dataSnapshot.child(user.getUid()).getValue(UserStruct.class);
                                currentUser = users;
                                Log.e("database", "onChildAdded:" + users.toString());

                                updateViews();
                                updateRecordList();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }
        } else {
            String userId = intent.getStringExtra("uid");
            String name = intent.getStringExtra("displayName");
            String email = intent.getStringExtra("email");
            writeNewUser(userId, name, email);
            updateViews();
        }

        String[] stockHints_HK = getResources().getStringArray(R.array.StockHints_HK);
        List<String> hintList = Arrays.asList(stockHints_HK);
        stockHintAdapter = new StockHintAdapter(getBaseContext(), android.R.layout.simple_list_item_1, hintList);
        autoCompleteTextView.setThreshold(0);
        autoCompleteTextView.setDropDownWidth(350);
        autoCompleteTextView.setAdapter(stockHintAdapter);


    }

    private void writeNewUser(String userId, String name, String email) {
        UserStruct user = new UserStruct(userId, name, email);
        mDatabase.child("users").child(userId).setValue(user);
    }

    private void findView() {
        displayName = findViewById(R.id.displayName);
        autoCompleteTextView = findViewById(R.id.targetStockSym);
//        askPrice = findViewById(R.id.askPrice);
//        bidPrice = findViewById(R.id.bidPrice);
        search = findViewById(R.id.search);
//        buy = findViewById(R.id.buy);
//        sell = findViewById(R.id.sell);
        cashFlow = findViewById(R.id.cashFlow);
        stockValue = findViewById(R.id.stockValue);
        totalAsset = findViewById(R.id.totalAsset);

//        radioBuy = findViewById(R.id.radioBuy);
//        radioSell = findViewById(R.id.radioSell);

//        buyLinearLayout = findViewById(R.id.buyLinearLayout);
//        sellLinearLayout = findViewById(R.id.sellLinearLayout);
//
//        buyShares = findViewById(R.id.buyShares);
//        sellShares = findViewById(R.id.sellShares);
//
        radioGP = findViewById(R.id.radioGP);
//
//        totalBuyPrice = findViewById(R.id.totalBuyPrice);
//        totalSellPrice = findViewById(R.id.totalSellPrice);

//        buyTime = findViewById(R.id.buyTime);
//        sellTime = findViewById(R.id.sellTime);


        QuoteNum = findViewById(R.id.QuoteNum);
        QuoteName = findViewById(R.id.QuoteName);
        LastUpdatedTime = findViewById(R.id.LastUpdatedTime);
        PreviousCloseNum = findViewById(R.id.PreviousCloseNum);
        OpenNum = findViewById(R.id.OpenNum);
        HighNum = findViewById(R.id.HighNum);
        LowNum = findViewById(R.id.LowNum);
        VolumnNum = findViewById(R.id.VolumnNum);
        currentPrice = findViewById(R.id.currentPrice);
        currentPriceChange = findViewById(R.id.currentPriceChange);
        askPriceNum = findViewById(R.id.askPriceNum);
        bidPriceNum = findViewById(R.id.bidPriceNum);

        detailedRelativeLayout = findViewById(R.id.detailedRelativeLayout);
        detailedRelativeLayout.setVisibility(View.INVISIBLE);

        askNowRadioButton = findViewById(R.id.askNowRB);
        bidNowRadioButton = findViewById(R.id.bidNowRB);
        autoAskRadioButton = findViewById(R.id.autoAskRB);
        autoBidRadioButton = findViewById(R.id.autoBidRB);

        mChart = findViewById(R.id.pieChart);
        frameLayout = findViewById(R.id.frameLayout);
    }

    private void setUpPieChart(float stockValue, float cashFlow){
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
//        mChart.setExtraOffsets(5, 5, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setDrawHoleEnabled(false);
        mChart.setHoleColor(Color.WHITE);

//        mChart.setTransparentCircleColor(Color.WHITE);
//        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(0f);
        mChart.setTransparentCircleRadius(0f);
// mChart.setHoleRadius(58f);
//        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
//        mChart.setOnChartValueSelectedListener(this);

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        entries.add(new PieEntry((float) stockValue,
                "Stock Value",
                getResources().getDrawable(R.drawable.star)));

        entries.add(new PieEntry((float) cashFlow,
                "Cash Flow",
                getResources().getDrawable(R.drawable.star)));

        PieDataSet dataSet = new PieDataSet(entries, "Asset Distribution");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(10f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(15f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        colors.add(Color.RED);
        colors.add(Color.GREEN);
//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.JOYFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.COLORFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.LIBERTY_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.PASTEL_COLORS)
//            colors.add(c);
//
//        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.BLUE);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(5f);
        l.setYEntrySpace(5f);
        l.setYOffset(0f);

        // entry label styling
        mChart.setEntryLabelColor(Color.BLUE);
        mChart.setEntryLabelTextSize(15f);

    }

//       private void setUpPieChart(float stockValue, float cashFlow) {
//
//        RelativeLayout.LayoutParams rlParams =
//                (RelativeLayout.LayoutParams) mChart.getLayoutParams();
//        rlParams.setMargins(0, 0, 0, 0);
//
//        mChart.setLayoutParams(rlParams);
//
//        mChart.setBackgroundColor(Color.WHITE);
//
//        mChart.setUsePercentValues(true);
//        mChart.getDescription().setEnabled(false);
//
//        mChart.setDrawHoleEnabled(true);
//        mChart.setHoleColor(Color.WHITE);
//
//        mChart.setTransparentCircleColor(Color.WHITE);
//        mChart.setTransparentCircleAlpha(110);
//
//        mChart.setHoleRadius(58f);
//        mChart.setTransparentCircleRadius(61f);
//
//        mChart.setDrawCenterText(true);
//
//        mChart.setRotationEnabled(false);
//        mChart.setHighlightPerTapEnabled(true);
//
//        mChart.setMaxAngle(180f); // HALF CHART
//        mChart.setRotationAngle(180f);
//        mChart.setCenterTextOffset(0, -20);
//
//        ArrayList<PieEntry> values = new ArrayList<PieEntry>();
//
//        values.add(new PieEntry(stockValue));
//        values.add(new PieEntry(cashFlow));
//
//        PieDataSet dataSet = new PieDataSet(values, "Stock Value / Cash Flow");
//        dataSet.setSliceSpace(3f);
//        dataSet.setSelectionShift(5f);
//
//        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
//        //dataSet.setSelectionShift(0f);
//
//        PieData data = new PieData(dataSet);
//        data.setValueFormatter(new PercentFormatter());
//        data.setValueTextSize(15f);
//        data.setValueTextColor(Color.BLUE);
//        mChart.setData(data);
//
//        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
//
//        Legend l = mChart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(false);
//        l.setXEntrySpace(7f);
//        l.setYEntrySpace(0f);
//        l.setYOffset(0f);
//
//        // entry label styling
//        mChart.setEntryLabelColor(Color.WHITE);
//        mChart.setEntryLabelTextSize(12f);
//
//        mChart.invalidate();
//    }

    public void updateViews() {
        if (currentUser != null) {
            displayName.setText(currentUser.getUsername());
            double cash = currentUser.getCashFlow();
            double stock = currentUser.getStockValue();

            cashFlow.setText(getFormatedAmount(cash));
            stockValue.setText(getFormatedAmount(stock));
            totalAsset.setText(getFormatedAmount(currentUser.getTotalAsset()));

            setUpPieChart((float) stock, (float) cash);
        }
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, NavigationActivity.class);
        startActivity(intent);
    }

    public void updateTime() {
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                noteTS = Calendar.getInstance().getTime();

                                // 01 January 2013
                                String date = DateFormat.format("dd/MM/yyyy", noteTS).toString();

                                // 12:00:00
                                String time = DateFormat.format("HH:mm:ss", noteTS).toString();

                                String showTime = date + " " + time;


//                                buyTime.setText(showTime);
//                                sellTime.setText(showTime);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        t.start();
    }

    private String getFormatedAmount(Double amount) {
        return '$' + NumberFormat.getNumberInstance(Locale.US).format(amount);
    }

    private double getTotalTradePrice(CharSequence s, boolean tradeType) {
        if (s == null) {
            return 0.0;
        }

        int number = Integer.parseInt(s.toString());

        if (tradeType == false) {
            String askPriceInYahoo = askPrice.getText().toString();
            double askPrice = Double.parseDouble(askPriceInYahoo);
            return number * askPrice;
        } else {
            String bidPriceInYahoo = bidPrice.getText().toString();
            double bidPrice = Double.parseDouble(bidPriceInYahoo);
            return number * bidPrice;
        }

    }

    public void updateRecordList() {
        // portfolio list
        stcckProfileRecyclerView = findViewById(R.id.recyclerView);
        stcckProfileRecyclerView.setHasFixedSize(true);
        stcckProfileRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ProfileListAdapter adapter = new ProfileListAdapter(this, currentUser.getStockProfileList());
        stcckProfileRecyclerView.setAdapter(adapter);

        // trading record list
        tradingRecordRecycleView = findViewById(R.id.tradingRecordRecyclerView);
        tradingRecordRecycleView.setHasFixedSize(true);
        tradingRecordRecycleView.setLayoutManager(new LinearLayoutManager(this));

        TradingRecordAdapter adapter2 = new TradingRecordAdapter(this, currentUser.getTradeRecordList());
        tradingRecordRecycleView.setAdapter(adapter2);


        // trading record list
        tradingRecordRecycleView = findViewById(R.id.tradingRecordRecyclerView);
        tradingRecordRecycleView.setHasFixedSize(true);
        tradingRecordRecycleView.setLayoutManager(new LinearLayoutManager(this));

        adapter2 = new TradingRecordAdapter(this, currentUser.getTradeRecordList());
        tradingRecordRecycleView.setAdapter(adapter2);

        // trading record list
        autoTradingRecordRecyclerView = findViewById(R.id.autoTradingRecordRecyclerView);
//        autoTradingRecordRecyclerView.setHasFixedSize(true);
//        autoTradingRecordRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TradingRecrodAdapter_Auto adapter4 = new TradingRecrodAdapter_Auto(this, currentUser.getAt_recordList());
        autoTradingRecordRecyclerView.setAdapter(adapter4);


    }


    private class GetYahooRealtimeData extends AsyncTask<String, Void, YahooRealtimeDataStruct> {
        private ProgressDialog mYahooRealTimeDialog_Trading;

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

            Log.e("map", dataMap.toString());
            Log.e("map", Integer.toString(dataMap.size()));
        }

        private JSONArray appendSearchRecordPreferences(YahooRealtimeDataStruct record) {
            // tutorial of sharedpreference: https://stackoverflow.com/questions/28107647/how-to-save-listobject-to-sharedpreferences

            Gson gson = new Gson();
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

            String jsonSaved = sharedPref.getString(MainActivity.SEARCHRECORD, "");
            String jsonNewRecordToAdd = gson.toJson(record);

            JSONArray jsonArrayStockRecordList = new JSONArray();

            try {
                if (jsonSaved != "") {
                    jsonArrayStockRecordList = new JSONArray(jsonSaved);
                }
                jsonArrayStockRecordList.put(new JSONObject(jsonNewRecordToAdd));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //SAVE NEW ARRAY
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(MainActivity.SEARCHRECORD, String.valueOf(jsonArrayStockRecordList));
            editor.commit();
            editor.apply();

            return jsonArrayStockRecordList;
        }

        private void assignDataToView(YahooRealtimeDataStruct stock) {

            askPriceNum.setText(String.valueOf(stock.getAsk()));
            bidPriceNum.setText(String.valueOf(stock.getBid()));

//            askPrice.setText(String.valueOf(stock.getAsk()));
//            bidPrice.setText(String.valueOf(stock.getBid()));
            askPriceInMarket = stock.getAsk();
            bidPriceInMarket = stock.getBid();

            QuoteNum.setText(String.valueOf(stock.getStockSym()));
            QuoteName.setText(String.valueOf(stock.getCompanyName()));
            LastUpdatedTime.setText(String.valueOf(stock.getLastUpdatedTime()));

            PreviousCloseNum.setText(String.valueOf(stock.getPreviousClose()));
            OpenNum.setText(String.valueOf(stock.getOpen()));
            HighNum.setText(String.valueOf(stock.getHigh()));
            LowNum.setText(String.valueOf(stock.getLow()));
            VolumnNum.setText(String.valueOf(stock.getVolume()));

            currentPrice.setText(String.valueOf(stock.getCurrent()));
            currentPriceChange.setText(String.valueOf(stock.getChange()));

            char changeSign = String.valueOf(stock.getChange()).charAt(0);

            if (changeSign == '+') {
                currentPriceChange.setTextColor(GREEN);
            } else if (changeSign == '-') {
                currentPriceChange.setTextColor(RED);
            } else {
                currentPriceChange.setTextColor(BLACK);
            }
        }

        protected YahooRealtimeDataStruct doInBackground(String... params) {

            YahooRealtimeDataStruct realTimeDataSet = new YahooRealtimeDataStruct();
            Map dataMap = new HashMap();

            String inputStockNumber = params[0];

            try {
                String tempUrl = "https://finance.yahoo.com/quote/" + inputStockNumber + "?p=" + inputStockNumber;
                Log.e("html", "fetching " + tempUrl);

                Document doc = Jsoup.connect(tempUrl).get();

                getHeadInfo(doc, dataMap);
                getTableValues(doc, dataMap);

                generateRealTimeDataSet(realTimeDataSet, dataMap);

                appendSearchRecordPreferences(realTimeDataSet);

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
            mYahooRealTimeDialog_Trading.dismiss();
            Log.e("endtask", result.toString());
            assignDataToView(result);
        }

        @Override
        protected void onPreExecute() {

            mYahooRealTimeDialog_Trading = new ProgressDialog(TradingMasterActivity.this);
            mYahooRealTimeDialog_Trading.setMessage("Downloading Yahoo Financial Data ...");
            mYahooRealTimeDialog_Trading.show();
        }

        @Override
        protected void onCancelled() {
            if (mYahooRealTimeDialog_Trading.isShowing()) {
                mYahooRealTimeDialog_Trading.dismiss();
            }
        }
    }

    private String getRevisedStockNum(String input, String domain) {
        String inputStockNumber = input;
        switch (domain) {
            case "hsi":
                inputStockNumber = "%5EHSI";
            case "hk":
            default:
                int tempLength = inputStockNumber.length();
                if (inputStockNumber.length() > 0 && inputStockNumber.length() < 5) {
                    for (int i = 0; i < (4 - tempLength); i++) {
                        inputStockNumber = "0" + inputStockNumber;
                    }
                } else {
                    Log.e("error", "wrong stock quote");
                    inputStockNumber = "0005";
                }
                inputStockNumber += ".HK";
                break;
        }

        return inputStockNumber;
    }

    public class StockHintAdapter extends ArrayAdapter implements Filterable {

        List<String> allCodes;
        List<String> originalCodes;
        StockHintAdapter.StringFilter filter;

        public StockHintAdapter(Context context, int resource, List<String> keys) {
            super(context, resource, keys);
            allCodes = keys;
            originalCodes = keys;
        }

        public int getCount() {
            if (allCodes == null) {
                return 0;
            }
            return allCodes.size();
        }

        public Object getItem(int position) {
            return allCodes.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        private class StringFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = "";
                if (constraint != null) {
                    filterString = constraint.toString().toLowerCase();
                }

                FilterResults results = new FilterResults();
                final List<String> list = originalCodes;

                int count = list.size();
                final ArrayList<String> nlist = new ArrayList<String>(count);
                String filterableString;

                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i);
                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist.add(filterableString);
                    }
                }

                String a = getRevisedStockNum(filterString, "hk");
                for (int i = 0; i < nlist.size(); i++) {
                    if (nlist.get(i).compareTo(a) == 0) {
                        nlist.remove(i);
                        nlist.add(0, a);
                    }
                }

                results.values = nlist;
                results.count = nlist.size();
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                allCodes = (ArrayList<String>) results.values;
                notifyDataSetChanged();
            }
        }

        @Override
        public Filter getFilter() {
            return new StockHintAdapter.StringFilter();
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.askNowRB:
                if (checked) {
                    bidNowRadioButton.setChecked(false);
                    autoAskRadioButton.setChecked(false);
                    autoBidRadioButton.setChecked(false);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("askPriceInMarket", askPriceInMarket);
                    bundle.putString("quoteNum", QuoteNum.getText().toString());
                    loadTransactionFragment(new AskNowFragment(), bundle);
                    break;
                }

            case R.id.bidNowRB:
                if (checked) {
                    askNowRadioButton.setChecked(false);
                    autoAskRadioButton.setChecked(false);
                    autoBidRadioButton.setChecked(false);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("bidPriceInMarket", bidPriceInMarket);
                    bundle.putString("quoteNum", QuoteNum.getText().toString());
                    loadTransactionFragment(new BidNowFragment(), bundle);
                    break;
                }
            case R.id.autoAskRB:
                if (checked) {
                    bidNowRadioButton.setChecked(false);
                    askNowRadioButton.setChecked(false);
                    autoBidRadioButton.setChecked(false);
                    Bundle bundle = new Bundle();
                    bundle.putString("quoteNum", QuoteNum.getText().toString());
                    loadTransactionFragment(new AutoAskFragment(), bundle);
                    break;
                }
            case R.id.autoBidRB:
                if (checked) {
                    bidNowRadioButton.setChecked(false);
                    askNowRadioButton.setChecked(false);
                    autoAskRadioButton.setChecked(false);

                    Bundle bundle = new Bundle();
                    bundle.putString("quoteNum", QuoteNum.getText().toString());
                    loadTransactionFragment(new AutoBidFragment(), bundle);
                    break;
                }
        }
    }

    private void loadTransactionFragment(Fragment fragment, Bundle bundle) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePortfolioAndViews();

        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("my-event"));
    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateViews();
            updateRecordList();
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    public void updatePortfolioAndViews() {
        if (currentUser != null) {
            List<StockProfileStruct> stockProfileList = currentUser.getStockProfileList();

            if (stockProfileList == null || stockProfileList.size() == 0) {
                currentUser.setStockValue(0);
                currentUser.setTotalAsset(currentUser.getCashFlow());
                mDatabase.child("users").child(currentUser.getUID()).setValue(currentUser);
                updateViews();
                updateRecordList();
            } else {
                new GetRegularStockValue().execute(stockProfileList);
            }
        }
    }

    private class GetRegularStockValue extends AsyncTask<List<StockProfileStruct>, Void, Void> {

        List<StockProfileStruct> stockProfileList;
        double totalStockValue;

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

            Log.e("map", dataMap.toString());
            Log.e("map", Integer.toString(dataMap.size()));
        }

        protected Void doInBackground(List<StockProfileStruct>... params) {

            stockProfileList = params[0];
            totalStockValue = 0;

            for (StockProfileStruct stock : stockProfileList) {

                YahooRealtimeDataStruct realTimeDataSet = new YahooRealtimeDataStruct();
                Map dataMap = new HashMap();

                String inputStockNumber = stock.getStockSym();

                try {
                    String tempUrl = "https://finance.yahoo.com/quote/" + inputStockNumber + "?p=" + inputStockNumber;
                    Log.e("html", "fetching " + tempUrl);

                    Document doc = Jsoup.connect(tempUrl).get();

                    getHeadInfo(doc, dataMap);
                    getTableValues(doc, dataMap);

                    generateRealTimeDataSet(realTimeDataSet, dataMap);

                    totalStockValue += realTimeDataSet.getBid() * stock.getTotalShares();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            currentUser.setStockValue(totalStockValue);
            currentUser.setTotalAsset(totalStockValue + currentUser.getCashFlow());
            mDatabase.child("users").child(currentUser.getUID()).setValue(currentUser);
            updateViews();
            updateRecordList();
        }
    }

}
