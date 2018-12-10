package com.example.kristoffer.stockmonitorremake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class OverviewActivity extends AppCompatActivity {

    private ListView portfolio;
    private TextView caption;
    private Button btnAdd;
    private List<Book> books;

    private BookAdaptor bookAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        portfolio = findViewById(R.id.list_stocks);
        caption = findViewById(R.id.textViewCaption);
        btnAdd = findViewById(R.id.buttonAdd);
        
        bookAdaptor = new BookAdaptor(this, books);
        portfolio.setAdapter(bookAdaptor);
        portfolio.setOnItemClickListener((parent, view, position, id) -> {
            Book b = books.get(position);
            
            if(b != null)
                goToDetails(b, position);
        });

        //load books from service

    }

    private void goToDetails(Book b, int position) {
        Intent detailsIntent = new Intent(OverviewActivity.this, DetailsActivity.class);

    }
}
