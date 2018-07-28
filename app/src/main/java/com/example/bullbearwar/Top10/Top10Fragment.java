package com.example.bullbearwar.Top10;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.bullbearwar.R;
import com.example.bullbearwar.Searching.YahooHsiActivity;
import com.example.bullbearwar.Searching.YahooRealtimeDataStruct;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Top10Fragment extends Fragment {

// top 20 website :
// https://www.etnet.com.hk/www/eng/stocks/realtime/top20.php

    List<Top10struct> top10RecordList;
    RecyclerView recyclerView;
    String tempUrl = "https://www.etnet.com.hk/www/eng/stocks/realtime/top20.php?subtype=turnover";
    Spinner Top10Spinner;

    public Top10Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container != null) {
            container.removeAllViewsInLayout();
        }
        View mView = inflater.inflate(R.layout.activity_top10_record, container, false);
        recyclerView = mView.findViewById(R.id.recyclerView);
        Top10Spinner = mView.findViewById(R.id.Top10Spinner);

        initial();
        new GetYahooRealtimeData().execute("");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Top10Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setUpTop10Spinner();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return mView;
    }

    private class GetYahooRealtimeData extends AsyncTask<String, Void, YahooRealtimeDataStruct> {
        ProgressDialog mYahooRealTimeDialog = new ProgressDialog(getActivity());

        private void getHeadInfo(Document doc) {
            top10RecordList = new ArrayList<>();
            for (int i = 0; i <= 4; i++) {

                Top10struct record = new Top10struct();
                //evenrow (13579)
                Elements stockHead = doc.body().getElementsByClass("evenRow");
                Element s1 = stockHead.get(i);
                //Log.e("testinfo", s1.toString()); //show detailed info

                //Fetch ranking
                String[] tokens = s1.toString().split("</td>");
                String[] rank = tokens[0].split("<td>");
                Log.e("testrank",rank[1]);
                record.setStockRank(rank[1]);

                //Fetch stocksym
                String[] stocksym = tokens[1].split("=");
                String[] stocksym2= stocksym[2].split("\"");
                Log.e("testsym",stocksym2[0]);
                record.setStockSym(stocksym2[0]);

                //Fetch StockName
                String[] stockName = tokens[2].split(">");
                String[] stockName2 = stockName[2].split("<");
                Log.e("teststockName",stockName2[0]);
                record.setStockName(stockName2[0]);

                //Fetch up down status and stockValue
                String[] stockStatus=tokens[4].split("\"");
                Log.e("teststockStatus",stockStatus[3]);
                record.setStockStatus(stockStatus[3]);
                String[] stockValue=stockStatus[4].split(">");
                Log.e("stockValue",stockValue[1]);
                record.setStockValue(Double.parseDouble(stockValue[1]));

                //Fetch change, percentchange, highest, lowerest, turnover
                String[] change=tokens[5].split(">");
                Log.e("testchange",change[1]);
                record.setChange(change[1]);
                String[] percentchange=tokens[6].split(">");
                Log.e("percentchange",percentchange[1]);
                record.setPercentageChange(percentchange[1]);
                String[] highest=tokens[7].split(">");
                Log.e("highest",highest[1]);
                record.setHighest(Double.parseDouble(highest[1]));
                String[] lowerest=tokens[8].split(">");
                Log.e("lowerest",lowerest[1]);
                record.setLowerest(Double.parseDouble(lowerest[1]));
                String[] turnover=tokens[9].split(">");
                Log.e("turnover",turnover[1]);
                record.setTurnover(turnover[1]);

                top10RecordList.add(record);


                //evenrow (246810)

                Top10struct record2 = new Top10struct();
                Elements stockHead2 = doc.body().getElementsByClass("oddRow");
                Element s2 = stockHead2.get(i);
                //Log.e("testinfoforodd", s2.toString()); //show detailed info

                //Fetch ranking
                String[] tokens2 = s2.toString().split("</td>");
                String[] rank2 = tokens2[0].split("<td>");
                Log.e("testrankforodd",rank2[1]);
                record2.setStockRank(rank2[1]);

                //Fetch stocksym
                String[] stocksym3 = tokens2[1].split("=");
                String[] stocksym4= stocksym3[2].split("\"");
                Log.e("testsymforodd",stocksym4[0]);
                record2.setStockSym(stocksym4[0]);


                //Fetch StockName
                String[] stockName3 = tokens2[2].split(">");
                String[] stockName4 = stockName3[2].split("<");
                Log.e("teststockName",stockName4[0]);
                record2.setStockName(stockName4[0]);


                //Fetch up down status and stockValue
                String[] stockStatus2=tokens2[4].split("\"");
                Log.e("teststockStatus2",stockStatus2[3]);
                record2.setStockStatus(stockStatus2[3]);
                String[] stockValue2=stockStatus2[4].split(">");
                Log.e("stockValue2",stockValue2[1]);
                record2.setStockValue(Double.parseDouble(stockValue2[1]));

                //Fetch change, percentchange, highest, lowerest, turnover
                String[] change2=tokens2[5].split(">");
                Log.e("testchange",change2[1]);
                record2.setChange(change2[1]);
                String[] percentchange2=tokens2[6].split(">");
                Log.e("percentchange",percentchange2[1]);
                record2.setPercentageChange(percentchange2[1]);
                String[] highest2=tokens2[7].split(">");
                Log.e("highest",highest2[1]);
                record2.setHighest(Double.parseDouble(highest2[1]));
                String[] lowerest2=tokens2[8].split(">");
                Log.e("lowerest",lowerest2[1]);
                record2.setLowerest(Double.parseDouble(lowerest2[1]));
                String[] turnover2=tokens2[9].split(">");
                Log.e("turnover",turnover2[1]);
                record2.setTurnover(turnover2[1]);

                top10RecordList.add(record2);
            }

            Log.e("testList",top10RecordList.toString());

           }

        protected YahooRealtimeDataStruct doInBackground(String... params) {

//          String tempUrl = "https://www.etnet.com.hk/www/eng/stocks/realtime/top20.php?subtype=turnover";
            Log.e("html", "fetching " + tempUrl);

            try {
                Document doc = Jsoup.connect(tempUrl).get();
                getHeadInfo(doc);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(YahooRealtimeDataStruct result) {
            super.onPostExecute(result);
            mYahooRealTimeDialog.dismiss();
            Top10Adapter adapter = new Top10Adapter(getActivity(), top10RecordList);
            recyclerView.setAdapter(adapter);
        }

        @Override
        protected void onPreExecute() {
            mYahooRealTimeDialog.setMessage("Downloading Yahoo Financial Data ...");
            if (!mYahooRealTimeDialog.isShowing())
                mYahooRealTimeDialog.show();
        }

        @Override
        protected void onCancelled() {
            mYahooRealTimeDialog.dismiss();
        }
    }

    @Override
      public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Top 10");
    }

    private void initial() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.top10sort, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Top10Spinner.setAdapter(adapter);
    }

    private void setUpTop10Spinner(){
        String selectedSort = Top10Spinner.getSelectedItem().toString();
        switch(selectedSort) {
            case "%Gainers":
                tempUrl = "https://www.etnet.com.hk/www/eng/stocks/realtime/top20.php?subtype=up";
                new GetYahooRealtimeData().execute("");
                break;

            case "%Losers":
                tempUrl = "https://www.etnet.com.hk/www/eng/stocks/realtime/top20.php?subtype=down";
                new GetYahooRealtimeData().execute("");
                break;

            case "Volume":
                tempUrl = "https://www.etnet.com.hk/www/eng/stocks/realtime/top20.php?subtype=volume";
                new GetYahooRealtimeData().execute("");
                break;

            case "Turnover":
            default:
                tempUrl = "https://www.etnet.com.hk/www/eng/stocks/realtime/top20.php?subtype=turnover";
                new GetYahooRealtimeData().execute("");
                break;
        }
    }
}
