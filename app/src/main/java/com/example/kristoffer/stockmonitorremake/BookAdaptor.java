package com.example.kristoffer.stockmonitorremake;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by leafcastle on 24/04/16 - modified by kristoffer bak on 12/12/18.
 */

public class BookAdaptor extends BaseAdapter {

    private List<Book> books;
    private Context context;

    public BookAdaptor(Context context, List<Book> list) {
        this.context = context;
        books = list;
    }

    public void setStocks(List<Book> stocks) {
        this.books = stocks;
    }

    @Override
    public int getCount() {
        if (books == null) {
            return 0;
        }

        return books.size();
    }

    @Override
    public Object getItem(int position) {
        if (books != null && books.size() > position) {
            return books.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.portfolio, null);
        }

        if (books != null && books.size() > position) {
            Book tempBook = books.get(position);

            TextView txtName = convertView.findViewById(R.id.txtCompName);
            txtName.setText(tempBook.getCompanyName());
            TextView txtCurrentPrice = convertView.findViewById(R.id.txtCurrentPrice);
            txtCurrentPrice.setText(String.valueOf(tempBook.getLatestValue()));

            TextView txtDiffPrice = convertView.findViewById(R.id.txtDiffPrice);
            double latest = tempBook.getLatestValue();
            double current = tempBook.getLatestValue();

            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);
            String diff = String.valueOf(df.format(latest - current));

            txtDiffPrice.setText(diff);

            return convertView;
        }

        return null;
    }
}
