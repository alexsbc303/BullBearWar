package com.example.bullbearwar.Searching;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.bullbearwar.R;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

/**
 * Created by User on 26 Feb 2018.
 */

public class YahooHsiActivity extends Fragment {
    private TextView titleTextView;
    private TextView lastPriceTextView, lastPriceChangeTextView, priceRangeTextView;
    private ImageView priceImageView;
    private LineChart lineChart;

    private String domain;
    private CandleStickChart mChart;

    private ViewGroup container;
    private LayoutInflater inflater;

    private Spinner rangeSpinner, intervalSpinner;
    Spinner indexSpinner;
    private Button refreshBtn;

    private TextView hsiLineChartLabel, hsiCandelStickLabel;

    String stockNum = "%5EHSI";

    public YahooHsiActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.container = container;
        this.inflater = inflater;
        return initializeUserInterface();
    }

    public View initializeUserInterface() {
        View view;
        if (container != null) {
            container.removeAllViewsInLayout();
        }

        // Get the screen orientation.
        int orientation = getActivity().getResources().getConfiguration().orientation;

        // Inflate the appropriate layout based on the screen orientation.
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = inflater.inflate(R.layout.searching_hsi_home, container, false);

            findViews(view);
            initial();

        } else { // orientation == Configuration.ORIENTATION_LANDSCAPE
            view = inflater.inflate(R.layout.activity_candle_stick_land_hsi, container, false);
            findViews_land(view);
            initialSetUpSpinners();

            refreshBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String range = rangeSpinner.getSelectedItem().toString();
                    String interval = intervalSpinner.getSelectedItem().toString();
                    new YahooHsiActivity.GetHistoricalDataByJson_land().execute(stockNum, range, interval);

                }
            });

            refreshBtn.performClick();
            rangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position < 2) {
                        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(),
                                R.array.intradayInterval, android.R.layout.simple_spinner_item);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        intervalSpinner.setAdapter(adapter2);
                    }else if (position == 2) {
                        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(),
                                R.array.intradayInterval1Month, android.R.layout.simple_spinner_item);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        intervalSpinner.setAdapter(adapter2);
                    } else {
                        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(),
                                R.array.longInterval, android.R.layout.simple_spinner_item);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        intervalSpinner.setAdapter(adapter2);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        indexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setUpIndexSpinner();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        // Create the new layout.
        View view = initializeUserInterface();

        // Display the new layout on the screen.
        container.addView(view);

        // Call the default method to cover our bases.
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Home");
    }

    private void findViews(View mView) {
        titleTextView = mView.findViewById(R.id.titleTextView);
        lastPriceTextView = mView.findViewById(R.id.lastPriceTextView);
        lastPriceChangeTextView = mView.findViewById(R.id.lastPriceChangeTextView);
        priceRangeTextView = mView.findViewById(R.id.priceRangeTextView);
        priceImageView = mView.findViewById(R.id.priceImageView);
        mChart = mView.findViewById(R.id.hsiCandleStick);
        lineChart = mView.findViewById(R.id.lineChart);

        hsiCandelStickLabel = mView.findViewById(R.id.hsiCandleStick_label);
        hsiLineChartLabel = mView.findViewById(R.id.hsiLineChart_label);
        indexSpinner = mView.findViewById(R.id.indexSpinner);
    }

    private class GetYahooRealtimeData extends AsyncTask<String, Void, Void> {
        IndexQuote realTimeDataSet;
        ProgressDialog mYahooRealTimeDialog = new ProgressDialog(getActivity());

        private void generateRealtimeDataSet(String... params) {
            realTimeDataSet = new IndexQuote();
            realTimeDataSet.indexQuote = Double.parseDouble(params[0].replaceAll(",", ""));
            realTimeDataSet.change = params[1];
            realTimeDataSet.updatedTime = params[2];
            realTimeDataSet.range = params[3];
        }

        protected Void doInBackground(String... params) {
            Log.e("yahooData", "params = " + params[0]);

            String inputStockNumber = params[0];
            Log.e("params", inputStockNumber);

            try {
                String tempUrl = "https://finance.yahoo.com/quote/" + inputStockNumber + "?p=" + inputStockNumber;
                Log.e("html", "fetching " + tempUrl);

                Document doc = Jsoup.connect(tempUrl).get();

                Element indexSummary = doc.body().getElementsByClass("D(ib) Mend(20px)").first();
                Log.e("index", indexSummary.toString());

                Elements indexQuoteTag = indexSummary.getElementsByAttributeValue("data-reactid", String.valueOf(35));
                String indexQuote = indexQuoteTag.first().text();
                Log.e("index", indexQuote);

                Elements changesTag = indexSummary.getElementsByAttributeValue("data-reactid", String.valueOf(37));
                String changes = changesTag.first().text();
                Log.e("index", changes);

                Elements updatedTimeTage = indexSummary.getElementsByAttributeValue("data-reactid", String.valueOf(40));
                if (updatedTimeTage == null) {
                    updatedTimeTage = indexSummary.getElementsByAttributeValue("data-reactid", String.valueOf(41));
                }
                String updatedTime = updatedTimeTage.first().text();
                Log.e("index", updatedTime);

                // range value
                Elements rangeTag = doc.body().getElementsByAttributeValue("data-test", "DAYS_RANGE-value");
                String range = rangeTag.first().text();

                generateRealtimeDataSet(indexQuote, changes, updatedTime, range);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                if (mYahooRealTimeDialog.isShowing())
                    mYahooRealTimeDialog.hide();
            } catch (Exception e) {
                e.printStackTrace();
            }


            Log.e("endtask", realTimeDataSet.toString());

            //titleTextView.setTextColor(realTimeDataSet.indexQuote);
            lastPriceTextView.setText(String.valueOf(realTimeDataSet.indexQuote));
            lastPriceChangeTextView.setText(realTimeDataSet.change);

            char changeSign = realTimeDataSet.change.charAt(0);

            if (changeSign == '+') {
                priceImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.arrow_up));
                lastPriceTextView.setTextColor(GREEN);
                lastPriceChangeTextView.setTextColor(GREEN);
            } else if (changeSign == '-') {
                priceImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.arrow_down));
                lastPriceTextView.setTextColor(RED);
                lastPriceChangeTextView.setTextColor(RED);
            } else {
                priceImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.star));
                lastPriceTextView.setTextColor(BLACK);
                lastPriceChangeTextView.setTextColor(BLACK);
            }

            priceRangeTextView.setText("(" + realTimeDataSet.range + ")");

        }

        @Override
        protected void onPreExecute() {
            mYahooRealTimeDialog.setMessage("Downloading Yahoo Financial Data ...");

            try {
                if (!mYahooRealTimeDialog.isShowing())
                    mYahooRealTimeDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
           }

        }

        @Override
        protected void onCancelled() {
            mYahooRealTimeDialog.dismiss();
        }
    }

    private class GetHistoricalDataByJson extends AsyncTask<String, Void, Void> implements OnChartGestureListener, OnChartValueSelectedListener {
        List<YahooHsiActivity.Quote> quotes;

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
                    YahooHsiActivity.Quote tempQuote = new YahooHsiActivity.Quote();
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
                List<YahooHsiActivity.Quote> tempQuotes = new ArrayList<>();
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

            mChart.resetTracking();

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
                String dateString = DateFormat.format("HH:mm", cal).toString();
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

            mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

            CandleData data = new CandleData(set1);

            mChart.setData(data);
            mChart.invalidate();


            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            long date;
            if (quotes.size() > 0){
                date = quotes.get(0).timeStamp;
                cal.setTimeInMillis(date * 1000L);
            }


            String dateString = "1 day candle stick chart on "+DateFormat.format("dd-MM-yyyy", cal).toString() + " :";
            hsiCandelStickLabel.setText(dateString);
        }

        private void setlineChart() {

            lineChart.setOnChartGestureListener(this);
            lineChart.setOnChartValueSelectedListener(this);
            lineChart.setDrawGridBackground(false);

            // no description text
            lineChart.getDescription().setEnabled(false);

            // enable touch gestures
            lineChart.setTouchEnabled(true);

            // enable scaling and dragging
            lineChart.setDragEnabled(true);
            lineChart.setScaleEnabled(true);
            // lineChart.setScaleXEnabled(true);
            // lineChart.setScaleYEnabled(true);

            // if disabled, scaling can be done on x- and y-axis separately
            lineChart.setPinchZoom(true);

            // set an alternative background color
            // lineChart.setBackgroundColor(Color.GRAY);

            // create a custom MarkerView (extend MarkerView) and specify the layout
            // to use for it
//        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//        mv.setChartView(lineChart); // For bounds control
//        lineChart.setMarker(mv); // Set the marker to the chart

            // x-axis limit line
            LimitLine llXAxis = new LimitLine(10f, "Index 10");
            llXAxis.setLineWidth(4f);
            llXAxis.enableDashedLine(10f, 10f, 0f);
            llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            llXAxis.setTextSize(10f);

            XAxis xAxis = lineChart.getXAxis();
            xAxis.enableGridDashedLine(10f, 10f, 0f);

            xAxis.setValueFormatter(new IndexAxisValueFormatter());
            //xAxis.addLimitLine(llXAxis); // add x-axis limit line

            LimitLine ll1 = new LimitLine(150f, "Upper Limit");
            ll1.setLineWidth(4f);
            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(10f);

            LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
            ll2.setLineWidth(4f);
            ll2.enableDashedLine(10f, 10f, 0f);
            ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            ll2.setTextSize(10f);

            YAxis leftAxis = lineChart.getAxisLeft();
            leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
            leftAxis.addLimitLine(ll1);
            leftAxis.addLimitLine(ll2);
//            leftAxis.setAxisMaximum(40000f);
//            leftAxis.setAxisMinimum(20000f);
            //leftAxis.setYOffset(20f);
            leftAxis.enableGridDashedLine(10f, 10f, 0f);
            leftAxis.setDrawZeroLine(false);

            // limit lines are drawn behind data (and not on top)
            leftAxis.setDrawLimitLinesBehindData(true);

            lineChart.getAxisRight().setEnabled(false);

            //lineChart.getViewPortHandler().setMaximumScaleY(2f);
            //lineChart.getViewPortHandler().setMaximumScaleX(2f);

            // add data
            setData(quotes.size());

            //lineChart.setVisibleXRange(20);
            //lineChart.setVisibleYRange(20f, AxisDependency.LEFT);
            //lineChart.centerViewTo(20, 50, AxisDependency.LEFT);

            lineChart.animateX(2500);
            //lineChart.invalidate();

            // get the legend (only possible after setting data)
            Legend l = lineChart.getLegend();

            // modify the legend ...
            l.setForm(Legend.LegendForm.LINE);

            // // dont forget to refresh the drawing
            // lineChart.invalidate();
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            long date;
            if (quotes.size() > 0){
                date = quotes.get(0).timeStamp;
                cal.setTimeInMillis(date * 1000L);
            }

            String dateString = "1 day line chart on "+DateFormat.format("dd-MM-yyyy", cal).toString() + " :";
            hsiLineChartLabel.setText(dateString);

        }

        private void setData(int count) {

            ArrayList<Entry> values = new ArrayList<Entry>();
            ArrayList<String> labels = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                float val = (float) quotes.get(i).close;
                long date = quotes.get(i).timeStamp;
                values.add(new Entry(i, val, getResources().getDrawable(R.drawable.star)));
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(date * 1000L);
                String dateString = DateFormat.format("HH:mm", cal).toString();
                labels.add(dateString);
            }

            LineDataSet set1;

            if (lineChart.getData() != null &&
                    lineChart.getData().getDataSetCount() > 0) {
                set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
                set1.setValues(values);
                lineChart.getData().notifyDataChanged();
                lineChart.notifyDataSetChanged();
            } else {
                // create a dataset and give it a type
                set1 = new LineDataSet(values, "Quotes");

                set1.setDrawIcons(false);

                // set the line to be drawn like this "- - - - - -"
                set1.disableDashedLine();
                set1.setColor(Color.BLUE);
                set1.setLineWidth(2f);
                set1.setDrawCircles(false);
                set1.setDrawValues(false);
                set1.setDrawFilled(true);
                set1.setFormLineWidth(1f);
                set1.setFormSize(15.f);

                if (Utils.getSDKInt() >= 18) {
                    // fill drawable only supported on api level 18 and above
                    Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_blue);
                    set1.setFillDrawable(drawable);
                } else {
                    set1.setFillColor(Color.BLACK);
                }

                ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                dataSets.add(set1); // add the datasets

                // create a data object with the datasets
                LineData data = new LineData(dataSets);

                // set data
                lineChart.setData(data);
                lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

            }
        }

        @Override
        protected Void doInBackground(String... strings) {
            Log.e("yahooData", "params = " + strings[0]);

            String inputStockNumber = strings[0];
            Log.e("params", inputStockNumber);

            //String tempUrl = "https://query1.finance.yahoo.com/v7/finance/chart/" + inputStockNumber + "?range=max&interval=1mo&indicators=quote&includeTimestamps=true";
            String tempUrl = "https://query1.finance.yahoo.com/v7/finance/chart/" + inputStockNumber + "?range=1d&interval=1m&indicators=quote&includeTimestamps=true";
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
            setlineChart();
//            mYahooRealTimeDialog.hide();
        }

        @Override
        public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
        }

        @Override
        public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

            // un-highlight values after the gesture is finished and no single-tap
            if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
                lineChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
        }

        @Override
        public void onChartLongPressed(MotionEvent me) {
            Log.i("LongPress", "Chart longpressed.");
        }

        @Override
        public void onChartDoubleTapped(MotionEvent me) {
            Log.i("DoubleTap", "Chart double-tapped.");
        }

        @Override
        public void onChartSingleTapped(MotionEvent me) {
            Log.i("SingleTap", "Chart single-tapped.");
        }

        @Override
        public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
            Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
        }

        @Override
        public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
            Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
        }

        @Override
        public void onChartTranslate(MotionEvent me, float dX, float dY) {
            Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
        }

        @Override
        public void onValueSelected(Entry e, Highlight h) {
            Log.i("Entry selected", e.toString());
            Log.i("LOWHIGH", "low: " + lineChart.getLowestVisibleX() + ", high: " + lineChart.getHighestVisibleX());
            Log.i("MIN MAX", "xmin: " + lineChart.getXChartMin() + ", xmax: " + lineChart.getXChartMax() + ", ymin: " + lineChart.getYChartMin() + ", ymax: " + lineChart.getYChartMax());
        }

        @Override
        public void onNothingSelected() {
            Log.i("Nothing selected", "Nothing selected.");
        }
    }

    public class IndexQuote {
        String updatedTime;
        double indexQuote;
        String change;
        String range;

        @Override
        public String toString() {
            return "IndexQuote{" +
                    "updatedTime='" + updatedTime + '\'' +
                    ", indexQuote=" + indexQuote +
                    ", change='" + change + '\'' +
                    ", range='" + range + '\'' +
                    '}';
        }
    }

    public class Quote {
        long timeStamp;
        int volume;
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

    private void findViews_land(View mView) {
        mChart = mView.findViewById(R.id.hsiCandleStick_land);
        rangeSpinner = mView.findViewById(R.id.rangeSpinner);
        intervalSpinner = mView.findViewById(R.id.intervalSpinner);
        refreshBtn = mView.findViewById(R.id.refreshBtn);
    }

    private void initialSetUpSpinners() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.range, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rangeSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(),
                R.array.intradayInterval, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        intervalSpinner.setAdapter(adapter2);
    }

    private class GetHistoricalDataByJson_land extends AsyncTask<String, Void, Void> implements OnChartGestureListener, OnChartValueSelectedListener {
        List<YahooHsiActivity.Quote> quotes;

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
                    YahooHsiActivity.Quote tempQuote = new YahooHsiActivity.Quote();
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
                List<YahooHsiActivity.Quote> tempQuotes = new ArrayList<>();
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

            mChart.resetTracking();

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


            mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));


            CandleData data = new CandleData(set1);

            mChart.setData(data);
            mChart.invalidate();
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

        @Override
        public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
        }

        @Override
        public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

            // un-highlight values after the gesture is finished and no single-tap
            if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
                lineChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
        }

        @Override
        public void onChartLongPressed(MotionEvent me) {
            Log.i("LongPress", "Chart longpressed.");
        }

        @Override
        public void onChartDoubleTapped(MotionEvent me) {
            Log.i("DoubleTap", "Chart double-tapped.");
        }

        @Override
        public void onChartSingleTapped(MotionEvent me) {
            Log.i("SingleTap", "Chart single-tapped.");
        }

        @Override
        public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
            Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
        }

        @Override
        public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
            Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
        }

        @Override
        public void onChartTranslate(MotionEvent me, float dX, float dY) {
            Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
        }

        @Override
        public void onValueSelected(Entry e, Highlight h) {
            Log.i("Entry selected", e.toString());
            Log.i("LOWHIGH", "low: " + lineChart.getLowestVisibleX() + ", high: " + lineChart.getHighestVisibleX());
            Log.i("MIN MAX", "xmin: " + lineChart.getXChartMin() + ", xmax: " + lineChart.getXChartMax() + ", ymin: " + lineChart.getYChartMin() + ", ymax: " + lineChart.getYChartMax());
        }

        @Override
        public void onNothingSelected() {
            Log.i("Nothing selected", "Nothing selected.");
        }
    }

    private void initial() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.indexs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        indexSpinner.setAdapter(adapter);
    }

    private void setUpIndexSpinner(){
        String selectedIndex = indexSpinner.getSelectedItem().toString();
        switch(selectedIndex) {
            case "DJI":
                stockNum = "%5EDJI";
                new YahooHsiActivity.GetYahooRealtimeData().execute(stockNum);
                new YahooHsiActivity.GetHistoricalDataByJson().execute(stockNum);
                titleTextView.setText("Dow Jones Industrial Average (DJI)");
                break;

            case "IXIC":
                stockNum = "%5EIXIC";
                new YahooHsiActivity.GetYahooRealtimeData().execute(stockNum);
                new YahooHsiActivity.GetHistoricalDataByJson().execute(stockNum);
                titleTextView.setText("Nasdaq GIDS (IXIC)");
                break;

            case "HSI":
            default:
                stockNum = "%5EHSI";
                new YahooHsiActivity.GetYahooRealtimeData().execute(stockNum);
                new YahooHsiActivity.GetHistoricalDataByJson().execute(stockNum);
                titleTextView.setText("Hang Seng Index (HSI)");
                break;
        }
    }
}
