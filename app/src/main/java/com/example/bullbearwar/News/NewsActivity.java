package com.example.bullbearwar.News;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bullbearwar.R;

public class NewsActivity extends Fragment {

    RecyclerView recyclerView;
    public NewsActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViewsInLayout();
        }
        View mView = inflater.inflate(R.layout.activity_news, null);
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerview);
        ReadRss readRss = new ReadRss(getActivity(), recyclerView);
        readRss.execute();

        return mView;
    }
    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("News");
    }

}
