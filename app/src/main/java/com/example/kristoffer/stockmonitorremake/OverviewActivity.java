package com.example.kristoffer.stockmonitorremake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static com.example.kristoffer.stockmonitorremake.GlobalVariables.extra_company_symbol;

public class OverviewActivity extends AppCompatActivity {

    private ListView portfolio;
    private TextView caption;
    private Button btnAdd;
    private List<Book> books;
    private EditText addSymbol;

    private BookAdaptor bookAdaptor;

    private stockDataService stockDataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        stockDataService = new stockDataService();
        books = stockDataService.getBooks(this);

        portfolio = findViewById(R.id.list_stocks);
        caption = findViewById(R.id.textViewCaption);
        btnAdd = findViewById(R.id.buttonAdd);
        addSymbol = findViewById(R.id.editTextaddSymbol);
        
        bookAdaptor = new BookAdaptor(this, books);
        portfolio.setAdapter(bookAdaptor);
        portfolio.setOnItemClickListener((parent, view, position, id) -> {
            Book b = books.get(position);
            
            if(b != null)
                goToDetails(b, position);
        });

        //load books from service

    }

    private void addBook(){
        String symbol = addSymbol.getText().toString();
        String symbolTrim = symbol.trim();

        if(symbolTrim.equals("")){
            Toast.makeText(OverviewActivity.this, "Enter a symbol", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(OverviewActivity.this, stockDataService.class);
        intent.putExtra(extra_company_symbol, symbolTrim);
        startService(intent);


    }

    private void goToDetails(Book b, int position) {
        Intent detailsIntent = new Intent(OverviewActivity.this, DetailsActivity.class);

    }
}
