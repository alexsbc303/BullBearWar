package com.example.bullbearwar.CurrencyConverter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bullbearwar.R;

import java.text.DecimalFormat;

public class ConversionActivity extends Fragment {

    private TextView tvTitle, tvSubTitle, tvOutputName, tvOutputRate;
    private EditText etInput;
    private Button btnCalculate;

    private String currencyName;
    private double currencyRate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViewsInLayout();
        }
        View mView = inflater.inflate(R.layout.activity_conversion, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Currency Converter");


        tvTitle = (TextView) mView.findViewById(R.id.tvTitle);
        tvSubTitle = (TextView) mView.findViewById(R.id.tvSubTitle);
        tvOutputName = (TextView) mView.findViewById(R.id.tvOutputName);
        tvOutputRate = (TextView) mView.findViewById(R.id.tvOutputRate);

        etInput = (EditText) mView.findViewById(R.id.etInput);
        btnCalculate = (Button) mView.findViewById(R.id.btnCalculate);

        if (getArguments() != null) {
            currencyName = getArguments().getString("currency_name","HKD");
            currencyRate = getArguments().getDouble("currency_rate",0);
        }

        tvTitle.setText("HKD to " + currencyName.toUpperCase());
        tvSubTitle.setText("[  1 : " + currencyRate + "  ]");
        tvOutputName.setText("   " + currencyName.toUpperCase() + ": ");

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etInput.getText().toString().length() == 0){
                    return;
                }

                double input;
                try{
                    input = Double.parseDouble(etInput.getText().toString());
                }
                catch (NumberFormatException e){
                    etInput.setText("");
                    return;
                }

                double output = input * currencyRate;
                DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
                tvOutputRate.setText(decimalFormat.format(output));
            }
        });

        return mView;
    }
}
