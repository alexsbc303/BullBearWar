package com.example.bullbearwar.CurrencyConverter;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bullbearwar.Chatroom.ChatroomActivity;
import com.example.bullbearwar.Firebase.TradingMasterActivity;
import com.example.bullbearwar.R;
import com.example.bullbearwar.Record.SearchRecordAdapter;
import com.example.bullbearwar.Searching.YahooRealtimeDataStruct;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrencyActivity extends android.support.v4.app.Fragment implements Callback<CurrencyExchange>, CurrencyItemClickListener {

    private ListView currencies;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViewsInLayout();
        }
        View mView = inflater.inflate(R.layout.activity_currency, container, false);

        currencies = (ListView) mView.findViewById(R.id.Currencies);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Currency Converter");
        return mView;
    }


    @Override
    public void onStart() {
        super.onStart();
        loadCurrencyExchangeData();
    }

    private void loadCurrencyExchangeData(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.fixer.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CurrencyExchangeService service = retrofit.create(CurrencyExchangeService.class);
        Call<CurrencyExchange> call = service.loadCurrencyExchange();
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<CurrencyExchange> call, Response<CurrencyExchange> response) {
        CurrencyExchange currencyExchange = response.body();
        currencies.setAdapter(new CurrencyAdapter(getActivity(), currencyExchange.getCurrencyList(), this));
    }

    @Override
    public void onFailure(Call<CurrencyExchange> call, Throwable t) {
        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCurrencyItemClick(Currency currency) {

        ConversionActivity chatroomFragment = new ConversionActivity();
        Bundle bundle = new Bundle();
        bundle.putString("currency_name", currency.getName());
        bundle.putDouble("currency_rate", currency.getRate());
        chatroomFragment.setArguments(bundle);
        FragmentManager manager = getActivity().getSupportFragmentManager();

        manager.beginTransaction().replace(R.id.mainLayout, chatroomFragment).commit();
    }
}
