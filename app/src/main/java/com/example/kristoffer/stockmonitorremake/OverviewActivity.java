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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static com.example.kristoffer.stockmonitorremake.GlobalVariables.broadcast_background_service_result;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.extra_company_symbol;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.extra_details_company_symbol;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.go_to_details;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.log_msg_stockDataService;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.put_extra_broadcast_result;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.response_delete;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.response_save;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.symbol_from_delete;

public class OverviewActivity extends AppCompatActivity {

    private ListView portfolio;
    private TextView caption;
    private Button btnAdd;
    private Button update;
    private List<Book> books;
    private EditText addSymbol;

    private BookAdaptor bookAdaptor;

    private stockDataService stockDataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        stockDataService = new stockDataService();

        portfolio = findViewById(R.id.list_stocks);
        caption = findViewById(R.id.textViewCaption);
        btnAdd = findViewById(R.id.buttonAdd);
        addSymbol = findViewById(R.id.editTextaddSymbol);
        update = findViewById(R.id.buttonUpdate);

        bookAdaptor = new BookAdaptor(this, books);
        portfolio.setAdapter(bookAdaptor);
        portfolio.setOnItemClickListener((parent, view, position, id) -> {
            Book b = books.get(position);
            
            if(b != null)
                goToDetails(b);
        });

        btnAdd.setOnClickListener(listener -> addBook());
        update.setOnClickListener(listener -> updatePressed());

        //refresh portfolio list (from db - not server)
        loadBooksFromRoomAndRefreshList();

        /**
        Create testdata:
        Book testBook = new Book();
        testBook.setAmount(10);
        testBook.setTimeStamp(String.valueOf(System.currentTimeMillis()));
        testBook.setLatestValue(100);
        testBook.setBuyingPrice(100);
        testBook.setCompanyName("Facebook");
        testBook.setCompanySymbol("fb");
        testBook.setSector("Technology");
        testBook.setPrimaryExchange("");
        books.add(testBook);
        stockDataService.insertBook(this ,testBook);
         **/

        //prepopulate portfolio with books, if empty:
        if(books.isEmpty()){
            String[] defaultStocks = {"fb","tsla","ge","gs","grub","f","c","bac","g","h"};
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
                for (Book book: books) {
                    if(book.getCompanySymbol().equals(resultBook.getCompanySymbol())){
                        //find that book in the db and update latestValue and timestamp
                        book.setLatestValue(resultBook.getLatestValue());
                        book.setTimeStamp(resultBook.getTimeStamp());
                        stockDataService.updateBook(context, book);

                        for (Book b: books) {
                            if(b.getCompanySymbol().equals(resultBook.getCompanyName())){
                                b = resultBook;
                            }
                        }

                        return;
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

    //refresh portfolio list (from db - not server)
    private void loadBooksFromRoomAndRefreshList(){
        books = stockDataService.getBooks(this);
        if(books != null || books.size()!= 0){
            bookAdaptor.setStocks(books);
            bookAdaptor.notifyDataSetChanged();
            }
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
        addSymbol.setText("");
    }

    private void goToDetails(Book b) {
        Intent detailsIntent = new Intent(OverviewActivity.this, DetailsActivity.class);
        detailsIntent.putExtra(extra_details_company_symbol, b.getCompanySymbol());

        startActivityForResult(detailsIntent, go_to_details);
    }

    private void updatePressed() {
        for (Book b:books) {
            String symbol = b.getCompanySymbol();
            Intent intent = new Intent(OverviewActivity.this, stockDataService.class);
            intent.putExtra(extra_company_symbol, symbol);
            startService(intent);
        }
    }

    @Override
    protected void onActivityResult(int reqcode, int rescode, @Nullable Intent data){
        super.onActivityResult(reqcode, rescode, data);

            if (rescode == response_delete && reqcode == go_to_details) {
                if(data != null) {
                    //delete the book with the right symbol from the db
                    String symbolForDelete = data.getStringExtra(symbol_from_delete);

                    for (Book b : books) {
                        if (b.getCompanySymbol().equals(symbolForDelete)) {
                            stockDataService.deleteBook(this, b);
                            books.remove(b);
                            loadBooksFromRoomAndRefreshList();
                            return;
                        }
                    }
                }
            }

            //if a book has been edited, we want to refresh to list
            if (rescode == response_save && reqcode == go_to_details) {
                if(data != null) {
                    loadBooksFromRoomAndRefreshList();
                }
            }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(log_msg_stockDataService, "Registering broadcastReceiver");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(broadcast_background_service_result);
        LocalBroadcastManager.getInstance(this).registerReceiver(resultsReceiver, intentFilter);

        loadBooksFromRoomAndRefreshList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(log_msg_stockDataService, "Unregistering broadcastReceiver");

        LocalBroadcastManager.getInstance(this).unregisterReceiver(resultsReceiver);
    }
}
