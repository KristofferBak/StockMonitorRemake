package com.example.kristoffer.stockmonitorremake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;

import static com.example.kristoffer.stockmonitorremake.GlobalVariables.extra_details_company_symbol;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.response_delete;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.symbol_from_delete;

public class DetailsActivity extends AppCompatActivity {

    private TextView textViewName;
    private TextView textViewCompanyName;
    private TextView textViewPrice;
    private TextView textViewStockPrice;
    private TextView textViewAmount;
    private TextView textViewAmountOwned;
    private TextView textViewSector;
    private TextView textViewSectorDisplayed;
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

        textViewName = findViewById(R.id.textViewName);
        textViewCompanyName = findViewById(R.id.textViewNameDisplay);
        textViewCompanyName.setGravity(Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK);
        textViewPrice = findViewById(R.id.textViewPrice);
        textViewStockPrice = findViewById(R.id.textViewPriceDisplay);
        textViewAmount = findViewById(R.id.textViewAmount);
        textViewAmountOwned = findViewById(R.id.textViewAmountDisplay);
        textViewSector = findViewById(R.id.textViewSector);
        textViewSectorDisplayed = findViewById(R.id.textViewSectorDisplay);
        back = findViewById(R.id.buttonBack);
        edit = findViewById(R.id.buttonEdit);
        delete = findViewById(R.id.buttonDelete);

        textViewCompanyName.setText(book.getCompanyName());
        textViewStockPrice.setText(String.valueOf(book.getLatestValue()));
        textViewAmountOwned.setText(String.valueOf(book.getAmount()));
        textViewSectorDisplayed.setText(book.getSector());

        back.setOnClickListener(l -> back());
        delete.setOnClickListener(l -> delete());
        edit.setOnClickListener(l -> edit());

    }

    private void back(){
        finish();
    }

    private void edit(){

    }

    private void delete(){
        Intent intent = new Intent();
        String companySymb = book.getCompanySymbol();
        intent.putExtra(symbol_from_delete, companySymb);
        setResult(response_delete, intent);
        back();
    }
}
