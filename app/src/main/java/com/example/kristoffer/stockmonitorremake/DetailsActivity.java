package com.example.kristoffer.stockmonitorremake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import static com.example.kristoffer.stockmonitorremake.GlobalVariables.broadcast_background_service_result;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.extra_details_company_symbol;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.go_to_edit;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.log_msg_stockDataService;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.put_extra_broadcast_result;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.response_delete;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.response_save;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.symbol_from_delete;

public class DetailsActivity extends AppCompatActivity {

    private TextView textViewCompanyName;
    private TextView textViewStockPrice;
    private TextView textViewAmount;
    private TextView textViewAmountOwned;
    private TextView textViewSector;
    private TextView textViewSectorDisplayed;
    private TextView textViewTimestamp;
    private TextView textViewTimestampDisplay;
    private TextView textViewPrimaryEx;
    private TextView getTextViewPrimaryExDisplay;
    private Button back, edit, delete;
    private Intent intent;
    private stockDataService service;

    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        service = new stockDataService();
        intent = getIntent();

        String companySymbol = intent.getStringExtra(extra_details_company_symbol);
        book = service.getBook(this, companySymbol);

        textViewCompanyName = findViewById(R.id.textViewNameDisplay);
        textViewStockPrice = findViewById(R.id.textViewPriceDisplay);
        textViewAmountOwned = findViewById(R.id.textViewAmountDisplay);
        textViewSectorDisplayed = findViewById(R.id.textViewSectorDisplay);
        textViewTimestampDisplay = findViewById(R.id.textViewTimestampDisplay);
        getTextViewPrimaryExDisplay = findViewById(R.id.textViewPrimaryExDisplay);

        back = findViewById(R.id.buttonBack);
        edit = findViewById(R.id.buttonEdit);
        delete = findViewById(R.id.buttonDelete);

        textViewCompanyName.setText(book.getCompanyName());
        textViewStockPrice.setText(String.valueOf(book.getLatestValue()));
        textViewAmountOwned.setText(String.valueOf(book.getAmount()));
        textViewSectorDisplayed.setText(book.getSector());
        textViewTimestampDisplay.setText(book.getTimeStamp());
        getTextViewPrimaryExDisplay.setText(book.getPrimaryExchange());

        back.setOnClickListener(l -> back());
        delete.setOnClickListener(l -> delete());
        edit.setOnClickListener(l -> edit());
    }

    private void back(){
        finish();
    }

    private void edit(){
        Intent intent = new Intent(DetailsActivity.this, EditActivity.class);
        intent.putExtra(extra_details_company_symbol, book.getCompanySymbol());
        startActivityForResult(intent, go_to_edit);
    }

    private void delete(){
        Intent intent = new Intent();
        String companySymb = book.getCompanySymbol();
        intent.putExtra(symbol_from_delete, companySymb);
        setResult(response_delete, intent);
        back();
    }

    @Override
    protected void onStart(){
        super.onStart();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(broadcast_background_service_result);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
        Log.d(log_msg_stockDataService, "Registered receiver on detailsActivity");
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receivedData = intent.getStringExtra(put_extra_broadcast_result);

            Book tempBook = BookJSONParser.parseBookJson(receivedData);

            String receivedSymbol = tempBook.getCompanySymbol();
            Double updatedValue = tempBook.getLatestValue();
            String updatedTimestamp = tempBook.getTimeStamp();

            if(receivedSymbol.equals(book.getCompanySymbol())){
                book.setLatestValue(updatedValue);
                book.setTimeStamp(updatedTimestamp);

                textViewTimestampDisplay.setText(updatedTimestamp);

                service.updateBook(context, book);
            }
        }
    };

    @Override
    protected void onActivityResult(int reqcode, int rescode, @Nullable Intent data){
        super.onActivityResult(reqcode, rescode, data);

        if(rescode == RESULT_OK && reqcode == go_to_edit){
            if (data != null) {
                Intent intent = new Intent();
                setResult(response_save, intent);
                finish();
            }
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        Log.d(log_msg_stockDataService, "Receiver unregistered from detailsActivity");
    }
}
