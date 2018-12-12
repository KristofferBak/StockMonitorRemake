package com.example.kristoffer.stockmonitorremake;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "book")
public class Book {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "symbol")
    private String companySymbol;

    @ColumnInfo(name = "name")
    private String companyName;

    @ColumnInfo(name = "latestValue")
    private double latestValue;

    @ColumnInfo(name = "sector")
    private String sector;

    @ColumnInfo(name = "amount")
    private int amount;

    @ColumnInfo(name = "buyingPrice")
    private double buyingPrice;

    @ColumnInfo(name = "primaryExchange")
    private String primaryExchange;

    @ColumnInfo(name = "latestTimestamp")
    private String timeStamp;

    public Book(String timeStamp, String companySymbol, String companyName, double latestValue, double buyingPrice, int amount, String primaryExchange, String sector ){
        this.companySymbol = companySymbol;
        this.companyName = companyName;
        this.latestValue = latestValue;
        this.buyingPrice = buyingPrice;
        this.amount = amount;
        this.primaryExchange = primaryExchange;
        this.sector = sector;
        this.timeStamp = timeStamp;
    }

    public String getCompanySymbol(){
        return companySymbol;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getCompanyName(){
        return companyName;
    }

    public String getSector(){
        return sector;
    }

    public double getBuyingPrice() {
        return buyingPrice;
    }

    public double getLatestValue() {
        return latestValue;
    }

    public int getAmount() {
        return amount;
    }

    public String getPrimaryExchange() {
        return primaryExchange;
    }

    public int getUid() {
        return uid;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setBuyingPrice(double buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setCompanySymbol(String companySymbol) {
        this.companySymbol = companySymbol;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setLatestValue(double latestValue) {
        this.latestValue = latestValue;
    }

    public void setPrimaryExchange(String primaryExchange) {
        this.primaryExchange = primaryExchange;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }
}
