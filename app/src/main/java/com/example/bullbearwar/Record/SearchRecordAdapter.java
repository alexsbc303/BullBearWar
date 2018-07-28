package com.example.bullbearwar.Record;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bullbearwar.R;
import com.example.bullbearwar.Searching.YahooRealtimeDataStruct;

import java.util.List;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

/**
 * Created by User on 26 Feb 2018.
 */

public class SearchRecordAdapter extends RecyclerView.Adapter<SearchRecordAdapter.StockViewHolder>{

    private Context mCtx;
    private List<YahooRealtimeDataStruct> searchRecords;

    public SearchRecordAdapter(Context mCtx, List<YahooRealtimeDataStruct> searchRecords) {
        this.mCtx = mCtx;
        this.searchRecords = searchRecords;
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.searching_record, null);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        YahooRealtimeDataStruct record = searchRecords.get(position);

        holder.stockNumTextView.setText(record.getStockSym());
        holder.companyNameTextView.setText(record.getCompanyName());

        String volume = String.format("%.2fM", record.getVolume()/ 1000000.0);
        holder.volumeTextView.setText(String.valueOf(volume));

        String change = record.getChange();
        String[] tokens = change.split(" ");
        char changeSign = tokens[0].charAt(0);
        String changeMagnitude = tokens[0].substring(1);
        String changePercent = tokens[1];

        holder.changeValueTextView.setText(changeMagnitude);
        holder.changePercentTextView.setText(changePercent);
        holder.lastPriceTextView.setText(String.valueOf(record.getCurrent()));
        holder.lastRangeTextView.setText("("+record.getDayRange()+")");

        if (changeSign == '+') {
            holder.priceImageView.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.arrow_up));
            holder.changeImageView.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.arrow_up));
            holder.changeValueTextView.setTextColor(GREEN);
            holder.changePercentTextView.setTextColor(GREEN);
            holder.lastPriceTextView.setTextColor(GREEN);
            holder.lastRangeTextView.setTextColor(GREEN);
        }else if (changeSign == '-'){
            holder.priceImageView.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.arrow_down));
            holder.changeImageView.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.arrow_down));
            holder.changeValueTextView.setTextColor(RED);
            holder.changePercentTextView.setTextColor(RED);
            holder.lastPriceTextView.setTextColor(RED);
            holder.lastRangeTextView.setTextColor(RED);
        }else{
            holder.priceImageView.setImageDrawable(null);
            holder.changeImageView.setImageDrawable(null);
            holder.changeValueTextView.setTextColor(BLACK);
            holder.changePercentTextView.setTextColor(BLACK);
            holder.lastPriceTextView.setTextColor(BLACK);
            holder.lastRangeTextView.setTextColor(BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return searchRecords.size();
    }

    class StockViewHolder extends RecyclerView.ViewHolder {

        TextView stockNumTextView, companyNameTextView;
        TextView volumeTextView;
        TextView lastPriceTextView, lastRangeTextView;
        TextView changeValueTextView, changePercentTextView;
        ImageView priceImageView, changeImageView;

        public StockViewHolder(View itemView) {
            super(itemView);
            stockNumTextView = itemView.findViewById(R.id.stockSymTextView);
            companyNameTextView = itemView.findViewById(R.id.companyNameTextView);
            volumeTextView = itemView.findViewById(R.id.volumeTextView);
            lastPriceTextView = itemView.findViewById(R.id.lastPriceTextView);
            lastRangeTextView = itemView.findViewById(R.id.lastRangeTextView);
            changeValueTextView = itemView.findViewById(R.id.changeValueTextView);
            changePercentTextView = itemView.findViewById(R.id.changePercentTextView);
            priceImageView = itemView.findViewById(R.id.priceImageView);
            changeImageView = itemView.findViewById(R.id.changeImageView);

        }
    }
}
