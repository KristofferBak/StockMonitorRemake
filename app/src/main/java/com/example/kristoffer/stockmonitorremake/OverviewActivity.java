package com.example.kristoffer.stockmonitorremake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.text.PrecomputedTextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static com.example.kristoffer.stockmonitorremake.GlobalVariables.extra_company_symbol;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.log_msg_stockDataService;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.put_extra_broadcast_result;

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
        //load books from service
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

        btnAdd.setOnClickListener(listener -> addBook());

        //prepopulate portfolio with books, if empty:
        if(books.isEmpty()){
            String[] defaultStocks = {"fb","tsla"};
            for (String symbol: defaultStocks) {
                Intent intent = new Intent(OverviewActivity.this, stockDataService.class);
                intent.putExtra(extra_company_symbol, symbol);
                startService(intent);
            }
        }
    }
    //Inspiration for the resultsReceiver method was found here: https://stackoverflow.com/questions/18125241/how-to-get-data-from-service-to-activity
    private BroadcastReceiver resultsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(log_msg_stockDataService, "resultsReceiver activated");
            String result = intent.getStringExtra(put_extra_broadcast_result);

            if(result != null){
                Book resultBook = BookJSONParser.parseBookJson(result);

                //Check to see if stock already exists in portfolio
                boolean stockAlreadyInPortfolio = false;
                for (Book book: books) {
                    if(book.getCompanySymbol().equals(resultBook.getCompanySymbol())){
                        stockAlreadyInPortfolio = true;
                        return; //toDo find that book in the db and update latestValue and timestamp
                        //toDo: find the right indexs in portfolio and replace the old book with the new
                    }
                }

                books.add(resultBook);
                stockDataService.insertBook(context,resultBook);
                bookAdaptor.setStocks(books);
                bookAdaptor.notifyDataSetChanged();
            }
            else{
                    Toast.makeText(OverviewActivity.this,"No result", Toast.LENGTH_SHORT).show();
                }

        }
    };

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

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(log_msg_stockDataService, "Registering broadcastReceiver");

        IntentFilter intentFilter = new IntentFilter();
        LocalBroadcastManager.getInstance(this).registerReceiver(resultsReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(log_msg_stockDataService, "Unregistering broadcastReceiver");

        LocalBroadcastManager.getInstance(this).unregisterReceiver(resultsReceiver);
    }
}
