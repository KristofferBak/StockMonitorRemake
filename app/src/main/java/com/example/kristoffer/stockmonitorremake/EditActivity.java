package com.example.kristoffer.stockmonitorremake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static com.example.kristoffer.stockmonitorremake.GlobalVariables.extra_details_company_symbol;

public class EditActivity extends AppCompatActivity {

private TextView caption;
private TextView textViewCompanyName;
private EditText editTextBuyingPrice;
private EditText editTextAmount;
private Button save, cancel;
private Intent intent;
private stockDataService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        caption = findViewById(R.id.textViewEditCaption);
        textViewCompanyName = findViewById(R.id.textViewComapanyNamyDisplay);
        editTextBuyingPrice = findViewById(R.id.editTextPrice);
        editTextAmount = findViewById(R.id.editTextAmount);
        cancel = findViewById(R.id.buttonCancel);
        save = findViewById(R.id.buttonEdit);

        cancel.setOnClickListener(l -> cancel());
        save.setOnClickListener(l -> save());

        service = new stockDataService();

        intent = getIntent();
        String symbol = intent.getStringExtra(extra_details_company_symbol);

        Book book = service.getBook(this, symbol);

        textViewCompanyName.setText(book.getCompanyName());
        editTextAmount.setText(book.getAmount());
        editTextBuyingPrice.setText(String.valueOf(book.getBuyingPrice()));
    }

    private void cancel(){
        setResult(RESULT_CANCELED);
        finish();
    }

    private void save(){

    }
}
