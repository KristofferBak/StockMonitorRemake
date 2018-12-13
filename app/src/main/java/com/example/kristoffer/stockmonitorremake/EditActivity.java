package com.example.kristoffer.stockmonitorremake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.kristoffer.stockmonitorremake.GlobalVariables.extra_details_company_symbol;

public class EditActivity extends AppCompatActivity {

private TextView caption;
private TextView textViewCompanyName;
private EditText editTextBuyingPrice;
private EditText editTextAmount;
private Button save, cancel;
private Intent intent;
private stockDataService service;
private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        service = new stockDataService();

        intent = getIntent();
        String symbol = intent.getStringExtra(extra_details_company_symbol);
        book = service.getBook(this, symbol);

        caption = findViewById(R.id.textViewEditCaption);
        textViewCompanyName = findViewById(R.id.textViewComapanyNamyDisplay);
        editTextBuyingPrice = findViewById(R.id.editTextPrice);
        editTextAmount = findViewById(R.id.editTextAmount);
        cancel = findViewById(R.id.buttonCancel);
        save = findViewById(R.id.buttonEdit);

        cancel.setOnClickListener(l -> cancel());
        save.setOnClickListener(l -> save());

        String amount = String.valueOf(book.getAmount());
        String bPrice = String.valueOf(book.getBuyingPrice());

        textViewCompanyName.setText(book.getCompanyName());
        editTextAmount.setText(amount);
        editTextBuyingPrice.setText(bPrice);
    }

    private void cancel(){
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void save(){
        //toDo : validation

        if(editTextAmount.getText().toString().trim().equals("")|| editTextBuyingPrice.getText().toString().trim().equals("")){

            Toast.makeText(EditActivity.this, "You must input values", Toast.LENGTH_SHORT).show();

            return;
        }

        if(editTextAmount.getText() != null || editTextBuyingPrice.getText() != null || editTextAmount.getText().toString() != "" || editTextBuyingPrice.getText().toString() != ""){
            String editedAmount = editTextAmount.getText().toString();
            int stringTointAmount = Integer.valueOf(editedAmount);

            String editedBuyingPrice = editTextBuyingPrice.getText().toString();
            double stringToDoubleBuyingPrice = Double.valueOf(editedBuyingPrice);

            book.setAmount(stringTointAmount);
            book.setBuyingPrice(stringToDoubleBuyingPrice);
            service.updateBook(this, book);

            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
