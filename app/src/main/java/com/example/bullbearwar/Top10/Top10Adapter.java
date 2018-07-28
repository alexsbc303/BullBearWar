package com.example.bullbearwar.Top10;

import android.content.Context;
import android.graphics.Color;
import android.service.notification.NotificationListenerService;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bullbearwar.R;

import java.util.List;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class Top10Adapter extends  RecyclerView.Adapter<Top10Adapter.StockViewHolder> {
    private Context mCtx;
    private List<Top10struct> searchRecords;

    public Top10Adapter(Context mCtx, List<Top10struct> searchRecords) {
        this.mCtx = mCtx;
        this.searchRecords = searchRecords;
    }

    @Override
    public Top10Adapter.StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.top10_record, null);
        return new Top10Adapter.StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Top10Adapter.StockViewHolder holder, int position) {
        Top10struct record = searchRecords.get(position);

        holder.rankTextView.setText("#" + record.getStockRank() +" ");

        holder.stockNumTextView.setText(record.getStockSym().substring(1) + ".HK");
        holder.companyNameTextView.setText(record.getStockName());
        holder.volumeTextView.setText(record.getTurnover().substring(0,record.getTurnover().length()-1) + " B");


        if (record.getStockStatus().compareTo("up") == 0){

            holder.changeValueTextView.setText(record.getChange().substring(1));
            holder.changePercentTextView.setText("(" + record.getPercentageChange().substring(1) + ")");
            holder.lastPriceTextView.setText(String.valueOf(record.getStockValue()));
            holder.lastRangeTextView.setText("(" + record.getLowerest() + " - " + record.getHighest() + ")");

            holder.priceImageView.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.arrow_up));
            holder.changeImageView.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.arrow_up));
            holder.changeValueTextView.setTextColor(GREEN);
            holder.changePercentTextView.setTextColor(GREEN);
            holder.lastPriceTextView.setTextColor(GREEN);
            holder.lastRangeTextView.setTextColor(GREEN);

        }else if(record.getStockStatus().compareTo("down") == 0){
            holder.changeValueTextView.setText(record.getChange().substring(1));
            holder.changePercentTextView.setText("(" + record.getPercentageChange().substring(1) + ")");
            holder.lastPriceTextView.setText(String.valueOf(record.getStockValue()));
            holder.lastRangeTextView.setText("(" + record.getLowerest() + " - " + record.getHighest() + ")");

            holder.priceImageView.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.arrow_down));
            holder.changeImageView.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.arrow_down));
            holder.changeValueTextView.setTextColor(RED);
            holder.changePercentTextView.setTextColor(RED);
            holder.lastPriceTextView.setTextColor(RED);
            holder.lastRangeTextView.setTextColor(RED);

        }else{
            holder.changeValueTextView.setText(record.getChange());
            holder.changePercentTextView.setText("(" + record.getPercentageChange() + ")");
            holder.lastPriceTextView.setText(String.valueOf(record.getStockValue()));
            holder.lastRangeTextView.setText("(" + record.getLowerest() + " - " + record.getHighest() + ")");

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
        TextView volumeTextView, rankTextView;
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
            rankTextView = itemView.findViewById(R.id.rankTextView);
        }
    }
}
