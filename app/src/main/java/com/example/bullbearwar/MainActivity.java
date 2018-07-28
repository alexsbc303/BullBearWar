package com.example.bullbearwar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.Toast;

import com.example.bullbearwar.Firebase.FirebaseMaster;
import com.example.bullbearwar.Firebase.TradingMasterActivity;


import com.example.bullbearwar.Record.SearchRecordActivity;
import com.example.bullbearwar.Top10.Top10Fragment;
import com.example.bullbearwar.News.NewsActivity;
import com.example.bullbearwar.Searching.YahooHsiActivity;
import com.example.bullbearwar.Searching.YahooSearchActivity;


public class MainActivity extends AppCompatActivity {

    public static final String DOMAIN = "DOMAIN";
    public static final String SEARCHRECORD = "searchRecord";
    public static final String SEARCHRECORD_SINGLERECORD = "searchRecord_singleRecord";
    public static final String SEARCHRECORD_SINGLEHISTORICALRECORD = "searchRecord_singleHistoricalRecord";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSearchActivity_hsi(View view) {
        Intent intent = new Intent(this, YahooHsiActivity.class);
        String domain = "hsi";
        intent.putExtra(DOMAIN, domain);
        startActivity(intent);
    }

    public void onSearchActivity_yahoo(View view) {
        Intent intent = new Intent(this, YahooSearchActivity.class);
        String domain = "hk";
        intent.putExtra(DOMAIN, domain);
        startActivity(intent);
    }

    public void onSearchActivity_record(View view) {
        Intent intent = new Intent(this, SearchRecordActivity.class);
        startActivity(intent);
    }


    public void onClearSharedPreference(View view){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(this, "SharedPreference clear", Toast.LENGTH_SHORT).show();
    }

    public void onFirebaseMaster(View view) {
        Intent intent = new Intent(this, FirebaseMaster.class);
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void onNewsActivity (View view) {
        Intent intent = new Intent(this, NewsActivity.class);
        startActivity(intent);
    }




    public void Top10(View view) {
        Intent intent = new Intent(this, Top10Fragment.class);
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void Trading(View view) {
        Intent intent = new Intent(this, TradingMasterActivity.class);
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}

