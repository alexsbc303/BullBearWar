package com.example.bullbearwar.AutoTrading;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.bullbearwar.Firebase.StockProfileStruct;
import com.example.bullbearwar.Firebase.TradingMasterActivity;
import com.example.bullbearwar.MainActivity;
import com.example.bullbearwar.R;
import com.example.bullbearwar.Searching.YahooRealtimeDataStruct;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static com.example.bullbearwar.Firebase.TradingMasterActivity.currentUser;
import static com.example.bullbearwar.Firebase.TradingMasterActivity.mDatabase;
import static com.example.bullbearwar.Firebase.TradingMasterActivity.tabHost;

import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AutoAskFragment extends Fragment {

    View view;
    private Button startDateBtn, endDateBtn, autoBuyBtn;
    private Calendar startCalendarFinal, endCalendarFinal;
    private EditText autoAskPrice, autoBuyShares;
    private TextView autoTotalBuyPrice,autoAskShares;
    Thread myThread;
    Runnable myRunnableThread;

    String quoteSym;

    public AutoAskFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_ask_auto, container, false);
        findView();
        initi();
        startDateBtnActions();
        endDateBtnActions();
        autoBuyBtnActions();
        runThread();
        onTextchanged();
        return view;
    }

    private void findView() {
        startDateBtn = view.findViewById(R.id.startDateBtn);
        endDateBtn = view.findViewById(R.id.endDateBtn);
        autoBuyBtn = view.findViewById(R.id.autoBuyBtn);
        autoAskPrice = view.findViewById(R.id.autoAskPrice);
        autoAskShares = view.findViewById(R.id.autoAskShares);
        autoTotalBuyPrice = view.findViewById(R.id.autoTotalBuyPrice);
    }

    private void initi(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        quoteSym = getArguments().getString("quoteNum");

    }

    private void autoBuyBtnActions() {
        autoBuyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDateValidation() && checkPriceVolume()) {

                    myThread.interrupt();
                    AutoTradingRecordStruct newATRecord = new AutoTradingRecordStruct();
                    newATRecord.setTargetStockSym(quoteSym);
                    newATRecord.setTradeTypeAsAutoBuy();
                    newATRecord.setTargetAskPrice(Double.parseDouble(autoAskPrice.getText().toString()));
                    newATRecord.setTargetAskVolume(Integer.parseInt(autoAskShares.getText().toString()));
                    newATRecord.setLastCheckTime(Calendar.getInstance().getTimeInMillis());
                    newATRecord.setStartingTime(startCalendarFinal.getTimeInMillis());
                    newATRecord.setEndingTime(endCalendarFinal.getTimeInMillis());
                    newATRecord.setStatus("In Progress");

                    List<AutoTradingRecordStruct> tempATList = currentUser.getAt_recordList();

                    if (tempATList == null || tempATList.size() == 0) {
                         tempATList = new ArrayList<>();
                    }

                    tempATList.add(newATRecord);

                    currentUser.setAt_recordList(tempATList);

                    // update firebase
//                    mDatabase.child("users").child(currentUser.getUID()).setValue(currentUser);

                    getActivity().startService(new Intent(getActivity(), TimeService.class));

                    Toast.makeText(getContext(), "Start Auto-Trading Service", Toast.LENGTH_SHORT).show();
                    Log.e("testing", "start service");

                    ((TradingMasterActivity)getActivity()).updatePortfolioAndViews();

                    tabHost.setCurrentTab(0);
                }
            }
        });
    }

    private void startDateBtnActions() {
        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

                        final int dateYear = year;
                        final int datemonth = month;
                        final int datedayOfMonth = dayOfMonth;

                        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                                Calendar startCalendar = Calendar.getInstance();
                                startCalendar.set(dateYear, datemonth, datedayOfMonth, hour, minute);

                                String outputText = String.format("%1$tY-%1$tm-%1$td at %1$tI:%1$tM %1$Tp", startCalendar);

                                // format: 2018-04-15 at 07:39 PM
                                Log.e("testing", outputText);
                                startDateBtn.setText(outputText);
                                startCalendarFinal = startCalendar;
                            }
                        };
                        Calendar now = Calendar.getInstance();
                        int hour = now.get(Calendar.HOUR_OF_DAY);
                        int min = now.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, hour, min, true);
                        timePickerDialog.setTitle("Please select starting time.");
                        timePickerDialog.show();
                    }
                };

                Calendar now = Calendar.getInstance();
                int year = now.get(java.util.Calendar.YEAR);
                int month = now.get(java.util.Calendar.MONTH);
                int day = now.get(java.util.Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), onDateSetListener, year, month, day);
//                datePickerDialog.setIcon(R.drawable.if_snowman);
                datePickerDialog.setTitle("Please select starting date.");
                datePickerDialog.show();

            }
        });
    }

    private void endDateBtnActions() {
        endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

                        final int dateYear = year;
                        final int datemonth = month;
                        final int datedayOfMonth = dayOfMonth;

                        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                                Calendar endCalendar = Calendar.getInstance();
                                endCalendar.set(dateYear, datemonth, datedayOfMonth, hour, minute);

                                String outputText = String.format("%1$tY-%1$tm-%1$td at %1$tI:%1$tM %1$Tp", endCalendar);
                                // format: 2018-04-15 at 07:39 PM
                                Log.e("testing", outputText);
                                endDateBtn.setText(outputText);
                                endCalendarFinal = endCalendar;
                            }
                        };
                        Calendar now = Calendar.getInstance();
                        int hour = now.get(Calendar.HOUR_OF_DAY);
                        int min = now.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, hour, min, true);
                        timePickerDialog.setTitle("Please select ending time.");
                        timePickerDialog.show();
                    }
                };

                Calendar now = Calendar.getInstance();
                int year = now.get(java.util.Calendar.YEAR);
                int month = now.get(java.util.Calendar.MONTH);
                int day = now.get(java.util.Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), onDateSetListener, year, month, day);
//                datePickerDialog.setIcon(R.drawable.if_snowman);
                datePickerDialog.setTitle("Please select ending date.");
                datePickerDialog.show();

            }
        });
    }

    public void onTextchanged() {
        autoAskShares.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (autoAskPrice.getText().toString().compareTo("") != 0)
                    if (count != 0) {
                        autoTotalBuyPrice.setText(getFormatedAmount(getTotalTradePrice(s)));
                    } else {
                        autoTotalBuyPrice.setText("$0");
                    }
                else {
                    autoTotalBuyPrice.setText("$0");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    int number = Integer.parseInt(s.toString());
                    double askPrice = Double.parseDouble(autoAskPrice.getText().toString());
                    double total = number * askPrice;
                    autoTotalBuyPrice.setText(getFormatedAmount(total));
                } else {
                    autoTotalBuyPrice.setText("$0");
                }
            }
        });

    }

    private double getTotalTradePrice(CharSequence s) {
        if (s == null) {
            return 0.0;
        }

        int number = Integer.parseInt(s.toString());

        if (autoAskPrice.getText().toString().compareTo("") != 0){
            double price = Double.parseDouble(autoAskPrice.getText().toString());
            return number * price;
        }

        return 0.0;
    }

    private String getFormatedAmount(Double amount) {
        return '$' + NumberFormat.getNumberInstance(Locale.US).format(amount);
    }

    private boolean checkDateValidation() {
        boolean valid = false;
        if (startCalendarFinal != null && endCalendarFinal != null) {
            if (startCalendarFinal.getTimeInMillis() > endCalendarFinal.getTimeInMillis()) {
                Toast.makeText(getContext(), "Wrong time setting. The starting time cannot be larger than ending time.", Toast.LENGTH_SHORT).show();
                valid = false;
            } else if (startCalendarFinal.getTimeInMillis() == endCalendarFinal.getTimeInMillis()) {
                Toast.makeText(getContext(), "Wrong time setting. The starting time cannot be equal to ending time.", Toast.LENGTH_SHORT).show();
                valid = false;
            } else {
                valid = true;
            }
        } else {
            Toast.makeText(getContext(), "Wrong time setting. Please double check.", Toast.LENGTH_SHORT).show();
        }
        return valid;
    }

    private boolean checkPriceVolume() {
        String price = autoAskPrice.getText().toString();
        String volume = autoAskShares.getText().toString();
        if (price.compareTo("") == 0 || volume.compareTo("") == 0) {
            Toast.makeText(getActivity(), "Wrong setting of price or volume.", Toast.LENGTH_SHORT).show();
            return false;
        }

        double tempAutoAskPrice = Double.parseDouble(price);
        int tempAutoVolume = (int) Double.parseDouble(volume);

        if (tempAutoAskPrice == 0) {
            Toast.makeText(getActivity(), "Price cannot be 0.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (tempAutoVolume == 0) {
            Toast.makeText(getActivity(), "Volume cannot be 0.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void runThread() {
        myThread = null;
        myRunnableThread = new CountDownRunner();
        myThread = new Thread(myRunnableThread);
        myThread.start();

    }

    public void doWork() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    long now = Calendar.getInstance().getTimeInMillis();
                    if (startCalendarFinal != null) {
                        if (startCalendarFinal.getTimeInMillis() < now) {
                            startCalendarFinal.setTimeInMillis(now);
                            startDateBtn.setText("Now");
                        }


                    }
                    if (endCalendarFinal != null) {
                        if (endCalendarFinal.getTimeInMillis() < now) {
                            endCalendarFinal.setTimeInMillis(now);
                            endDateBtn.setText("Now");
                        }

                    }
                } catch (Exception e) {
                }
            }
        });
    }

    class CountDownRunner implements Runnable {
        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork();
                    Thread.sleep(1000); // Pause of 1 Second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }



}
