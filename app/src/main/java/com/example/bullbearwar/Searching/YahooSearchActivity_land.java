package com.example.bullbearwar.Searching;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bullbearwar.R;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class YahooSearchActivity_land extends FragmentActivity {

    private AutoCompleteTextView autoCompleteTextView;
    private Spinner rangeSpinner, intervalSpinner;
    CandleStickChart searchCandleStick_land;
    Button refreshBtn;
    StockHintAdapter stockHintAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candle_stick_land_searching);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        findViews();
        initial();
        initialSetUpSpinners();

        Intent intent = getIntent();
        if (intent.hasExtra("stockSymName")) {
            autoCompleteTextView.setText(intent.getExtras().getString("stockSymName"));
            Log.e("intent",intent.getExtras().getString("stockSymName"));
        }

        String range = rangeSpinner.getSelectedItem().toString();
        String interval = intervalSpinner.getSelectedItem().toString();
        new GetHistoricalDataByJson_land().execute(intent.getStringExtra("stockSymName"), range, interval);

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("info", "search button clicked");
                Log.e("inputtext", String.valueOf(autoCompleteTextView.getText()));
                if (stockHintAdapter.getCount() == 0) {
                    Log.e("noRecord", "No Record. Try Again.");
                    Toast.makeText(YahooSearchActivity_land.this, "No Record. Try Again.", Toast.LENGTH_SHORT).show();
                } else {
                    String selectedStockSym = "";
                    String inputText = autoCompleteTextView.getText().toString();
                    if (!inputText.contains(".")) {
                        selectedStockSym = stockHintAdapter.getItem(0).toString();
                        autoCompleteTextView.setText(selectedStockSym);
                    } else {
                        selectedStockSym = inputText;
                    }
                    Log.e("selectedStockSym", selectedStockSym);
                    String range = rangeSpinner.getSelectedItem().toString();
                    String interval = intervalSpinner.getSelectedItem().toString();
                    new GetHistoricalDataByJson_land().execute(selectedStockSym, range, interval);

                    try {
                        InputMethodManager imm = (InputMethodManager) YahooSearchActivity_land.this.getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(YahooSearchActivity_land.this.getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        rangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < 2) {
                    ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.intradayInterval, android.R.layout.simple_spinner_item);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    intervalSpinner.setAdapter(adapter2);
                } else if (position == 2) {
                    ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.intradayInterval1Month, android.R.layout.simple_spinner_item);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    intervalSpinner.setAdapter(adapter2);
                } else {
                    ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.longInterval, android.R.layout.simple_spinner_item);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    intervalSpinner.setAdapter(adapter2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        autoCompleteTextView.setOnKeyListener(new TextView.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            refreshBtn.performClick();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    private void findViews() {
        autoCompleteTextView = findViewById(R.id.autoInputNum_land);
        rangeSpinner = findViewById(R.id.rangeSpinner_land);
        intervalSpinner = findViewById(R.id.intervalSpinner_land);
        searchCandleStick_land = findViewById(R.id.searchCandleStick_land);
        refreshBtn = findViewById(R.id.refreshBtn_land);

    }

    private void initial() {
        String[] stockHints_SZ = getResources().getStringArray(R.array.StockHints_SZ);
        String[] stockHints_NASDAQ = getResources().getStringArray(R.array.StockHints_NASDAQ);
        String[] stockHints_NYSE = getResources().getStringArray(R.array.StockHints_NYSE);
        String[] stockHints_HK = getResources().getStringArray(R.array.StockHints_HK);
        List<String> hintList = Arrays.asList(stockHints_HK);
        stockHintAdapter = new StockHintAdapter(this, android.R.layout.simple_list_item_1, hintList);
        autoCompleteTextView.setThreshold(0);
        autoCompleteTextView.setDropDownWidth(350);
        autoCompleteTextView.setAdapter(stockHintAdapter);

    }
    private void initialSetUpSpinners() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.range, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rangeSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.intradayInterval, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        intervalSpinner.setAdapter(adapter2);
    }
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        super.onConfigurationChanged(newConfig);
    }

    private class GetHistoricalDataByJson_land extends AsyncTask<String, Void, Void> {
        List<Quote> quotes;

        private String getJSONData(String tempUrl) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String result = "";
            try {
                URL url = new URL(tempUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                result = buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        private void generateQuotes(String rawData) {
            quotes = new ArrayList<>();

            try {
                JSONObject parentObject = new JSONObject(rawData);
                JSONObject chart = parentObject.getJSONObject("chart");
                JSONObject result = chart.getJSONArray("result").getJSONObject(0);
                Log.e("result", result.toString());

                JSONArray timeStamp = result.getJSONArray("timestamp");
                Log.e("timeStamp", timeStamp.toString());

                JSONObject indicators = result.getJSONObject("indicators");
                JSONObject quote = indicators.getJSONArray("quote").getJSONObject(0);

                JSONArray volume = quote.getJSONArray("volume");
                Log.e("volume", volume.toString());

                JSONArray high = quote.getJSONArray("high");
                Log.e("high", high.toString());

                JSONArray close = quote.getJSONArray("close");
                Log.e("close", close.toString());

                JSONArray open = quote.getJSONArray("open");
                Log.e("open", open.toString());

                JSONArray low = quote.getJSONArray("low");
                Log.e("low", low.toString());

                for (int i = 0; i < timeStamp.length(); i++) {
                    Quote tempQuote = new Quote();
                    if (!timeStamp.isNull(i)) {
                        tempQuote.timeStamp = timeStamp.getLong(i);
                    } else {
                        tempQuote.timeStamp = 0;
                    }
                    if (!volume.isNull(i)) {
                        tempQuote.volume = volume.getInt(i);
                    } else {
                        tempQuote.volume = 0;
                    }

                    if (!high.isNull(i)) {
                        tempQuote.high = high.getDouble(i);
                    } else {
                        tempQuote.high = 0;
                    }
                    if (!close.isNull(i)) {
                        tempQuote.close = close.getDouble(i);
                    } else {
                        tempQuote.close = 0;
                    }

                    if (!open.isNull(i)) {
                        tempQuote.open = open.getDouble(i);
                    } else {
                        tempQuote.open = 0;
                    }
                    if (!low.isNull(i)) {
                        tempQuote.low = low.getDouble(i);
                    } else {
                        tempQuote.low = 0;
                    }

                    quotes.add(tempQuote);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                List<Quote> tempQuotes = new ArrayList<>();
                for (int i = 0; i < quotes.size(); i++) {
                    boolean highIsNull = quotes.get(i).high == 0;
                    boolean openIsNull = quotes.get(i).open == 0;
                    boolean closeIsNull = quotes.get(i).close == 0;
                    boolean lowIsNull = quotes.get(i).low == 0;
                    if (!highIsNull && !openIsNull &&
                            !closeIsNull && !lowIsNull)
                        tempQuotes.add(quotes.get(i));
                }
                quotes.clear();
                quotes.addAll(tempQuotes);
                tempQuotes = null;
            }
        }

        private void setupCandleStickChart() {

            searchCandleStick_land.resetTracking();

            ArrayList<CandleEntry> yVals1 = new ArrayList<CandleEntry>();
            ArrayList<String> labels = new ArrayList<>();
            for (int i = 0; i < quotes.size(); i++) {

                long date = quotes.get(i).timeStamp;

                float high = (float) quotes.get(i).high;
                float low = (float) quotes.get(i).low;

                float open = (float) quotes.get(i).open;
                float close = (float) quotes.get(i).close;

                yVals1.add(new CandleEntry(
                        i, high,
                        low,
                        open,
                        close
                ));
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(date * 1000L);
                String dateString = DateFormat.format("dd-MM-yyyy HH:mm", cal).toString();
                labels.add(dateString);
            }

            CandleDataSet set1 = new CandleDataSet(yVals1, "Quotes");

            set1.setDrawIcons(false);
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);

//        set1.setColor(Color.rgb(80, 80, 80));
            set1.setShadowColor(Color.DKGRAY);
            set1.setShadowWidth(0.7f);
            set1.setDecreasingColor(Color.RED);
            set1.setDecreasingPaintStyle(Paint.Style.FILL);
            set1.setIncreasingColor(Color.rgb(122, 242, 84));
            set1.setIncreasingPaintStyle(Paint.Style.FILL_AND_STROKE);
            set1.setNeutralColor(Color.BLUE);
            //set1.setHighlightLineWidth(1f);


            searchCandleStick_land.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

            CandleData data = new CandleData(set1);

            searchCandleStick_land.setData(data);
            searchCandleStick_land.invalidate();
        }

        @Override
        protected Void doInBackground(String... strings) {
            Log.e("yahooData", "params = " + strings[0]);

            String inputStockNumber = strings[0];
            Log.e("params", inputStockNumber);

            String range = strings[1];
            Log.e("params", range);

            String interval = strings[2];
            Log.e("params", interval);

            String tempUrl = "https://query1.finance.yahoo.com/v7/finance/chart/" + inputStockNumber + "?range=" + range + "&interval=" + interval + "&indicators=quote&includeTimestamps=true";
            Log.e("searchingURL", "url: " + tempUrl);

            String rawData = getJSONData(tempUrl);
            Log.e("raw", rawData);

            generateQuotes(rawData);
            Log.e("quotes", quotes.toString());

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            mYahooRealTimeDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setupCandleStickChart();
//            mYahooRealTimeDialog.hide();
        }


    }

    public class Quote {
        long timeStamp;
        long volume;
        double high;
        double close;
        double open;
        double low;

        @Override
        public String toString() {
            return "Quote{" +
                    "timeStamp=" + timeStamp +
                    ", volume=" + volume +
                    ", high=" + high +
                    ", close=" + close +
                    ", open=" + open +
                    ", low=" + low +
                    '}';
        }
    }

    public class StockHintAdapter extends ArrayAdapter implements Filterable {

        List<String> allCodes;
        List<String> originalCodes;
        StringFilter filter;

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
            return new StringFilter();
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
}
