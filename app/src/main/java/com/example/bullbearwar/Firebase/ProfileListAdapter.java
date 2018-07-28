package com.example.bullbearwar.Firebase;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bullbearwar.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by User on 14 Mar 2018.
 */

public class ProfileListAdapter extends RecyclerView.Adapter<ProfileListAdapter.StockViewHolder>{
    private Context mCtx;
    private List<StockProfileStruct> stockProfileList;

    public ProfileListAdapter(Context mCtx, List<StockProfileStruct> stockProfileList) {
        this.mCtx = mCtx;
        this.stockProfileList = stockProfileList;
    }

    @Override
    public ProfileListAdapter.StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.profile, null);
        return new ProfileListAdapter.StockViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ProfileListAdapter.StockViewHolder holder, int position) {
        StockProfileStruct profile = stockProfileList.get(position);
        holder.avgPrice.setText('$' + NumberFormat.getNumberInstance(Locale.US).format(profile.getAveragePrice()));

        String date = DateFormat.format("dd/MM/yyyy", profile.getLastTradeDateTime()).toString();
        String time = DateFormat.format("HH:mm:ss", profile.getLastTradeDateTime()).toString();

        holder.dataTime.setText(date+'\n'+time);

        holder.stockSymTextView.setText(profile.getStockSym());
        holder.volumeTextView.setText( String.valueOf(profile.getTotalShares()));

    }

    @Override
    public int getItemCount() {
        return stockProfileList.size();
    }

    class StockViewHolder extends RecyclerView.ViewHolder {

        TextView stockSymTextView, volumeTextView;
        TextView avgPrice, dataTime;


        public StockViewHolder(View itemView) {
            super(itemView);
            stockSymTextView = itemView.findViewById(R.id.stockSymTextView);
            avgPrice = itemView.findViewById(R.id.avgPrice);
            volumeTextView = itemView.findViewById(R.id.volumeTextView);
            dataTime = itemView.findViewById(R.id.dataTime);
        }
    }
}
