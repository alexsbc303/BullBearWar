package com.example.bullbearwar.Record;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

import com.example.bullbearwar.MainActivity;
import com.example.bullbearwar.R;
import com.example.bullbearwar.Searching.YahooRealtimeDataStruct;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 26 Feb 2018.
 */


public class SearchRecordActivity extends Fragment {

    List<YahooRealtimeDataStruct> searchRecords;
    RecyclerView recyclerView;

    public SearchRecordActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViewsInLayout();
        }
        View mView = inflater.inflate(R.layout.activity_search_record, container, false);

        recyclerView = mView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        searchRecords = new ArrayList<>();
        List<YahooRealtimeDataStruct> savedSearchRecord = loadSearchRecordPreferences();

        if (savedSearchRecord != null) {
            searchRecords.addAll(savedSearchRecord);
            SearchRecordAdapter adapter = new SearchRecordAdapter(getActivity(), searchRecords);
            recyclerView.setAdapter(adapter);
        }

        return mView;
    }
    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Search Record");
    }

    private List<YahooRealtimeDataStruct> loadSearchRecordPreferences() {
        Gson gson = new Gson();
        List<YahooRealtimeDataStruct> searchRecordList = new ArrayList<>();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String jsonPreferences = sharedPref.getString(MainActivity.SEARCHRECORD, "");
        Log.e("savedvalue", jsonPreferences);
        Type type = new TypeToken<List<YahooRealtimeDataStruct>>() {
        }.getType();
        searchRecordList = gson.fromJson(jsonPreferences, type);

        if (searchRecordList == null) {
            return searchRecordList;
        }
        Log.e("record", searchRecordList.toString());
        Log.e("revisedrecord", Lists.reverse(searchRecordList).toString());
        return Lists.reverse(searchRecordList);

    }
}