package com.example.bullbearwar.CurrencyConverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.bullbearwar.R;

import java.util.List;

public class CurrencyAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<Currency> currencyList;
    private CurrencyItemClickListener currencyItemClickListener;


    public CurrencyAdapter(Context context, List<Currency> currencyList, CurrencyItemClickListener currencyItemClickListener) {
        this.context = context;
        this.currencyList = currencyList;
        this.currencyItemClickListener = currencyItemClickListener;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return currencyList.size();
    }

    @Override
    public Object getItem(int position) {
        return currencyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View currencyItemView = layoutInflater.inflate(R.layout.currency_item, null);
        TextView Nametextview = (TextView) currencyItemView.findViewById(R.id.Nametextview);
        TextView Ratetextview = (TextView) currencyItemView.findViewById(R.id.Ratetextview);

        final Currency currency = currencyList.get(position);
        Nametextview.setText(currency.getName());
        Ratetextview.setText(Double.toString(currency.getRate()));

        currencyItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View convertView) {
                currencyItemClickListener.onCurrencyItemClick(currency);
            }
        });
        return currencyItemView;
    }
}
