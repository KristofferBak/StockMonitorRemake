package com.example.kristoffer.stockmonitorremake;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by leafcastle on 01/05/17 - modified by kristofferbak on 11/12/18.
 */

class BookJSONParser {
    static Book parseBookJson(String jsonString) {
        Book book;

        try {
            JSONObject fullJson = new JSONObject(jsonString);
            JSONObject bookJson = fullJson.getJSONObject("quote");
            book = new Book("", "", "", 0, 0, 1, "", "");

            book.setCompanyName(bookJson.getString("companyName"));
            book.setCompanySymbol(bookJson.getString("symbol"));
            book.setPrimaryExchange(bookJson.getString("primaryExchange"));
            book.setLatestValue(bookJson.getDouble("latestPrice"));
            book.setTimeStamp(bookJson.getString("latestUpdate"));
            book.setSector(bookJson.getString("sector"));
            book.setBuyingPrice(bookJson.getDouble("latestPrice"));
        } catch (JSONException e) {
            book = null;
            e.printStackTrace();
        }

        return book;
    }
}
