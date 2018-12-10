package com.example.kristoffer.stockmonitorremake;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static com.example.kristoffer.stockmonitorremake.GlobalVariables.broadcast_results;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.connect;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.extra_company_symbol;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.log_msg_stockDataService;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.notification_channel_name;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.put_extra_broadcast_result;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.stockDataService_destroyed;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.stock_data_service_id;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.symbol;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.url;
import static com.example.kristoffer.stockmonitorremake.GlobalVariables.url_books_suffix;

/**
 *
 *  This service is responsible for getting stock data from https://api.iextrading.com
 *  Also this service is communicating with the local room database.
 *  If the room db is empty, the service will get data on ten predetermined stocks
 *
 */

public class stockDataService extends Service {


    private Handler handler;

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(log_msg_stockDataService, "stockDataservice.onCreate() is called...");

        handler = new Handler();
        executeTask.run();
    }

    //Inspiration for the executeTask method, is found here: https://stackoverflow.com/questions/1921514/how-to-run-a-runnable-thread-in-android-at-defined-intervals
    private Runnable executeTask = new Runnable() {
        @Override
        public void run() {
            try{
                List<Book> books = getBooks(getApplicationContext());
                books.forEach(stock -> backgroundTask(stock.getCompanySymbol()));
            }
            finally {
                //using the handler to update every second minute
                handler.postDelayed(executeTask, 1000*60*2);
            }
        }
    };

    private void backgroundTask(String companySymbol) {
        BookTask bTask = new BookTask();
        bTask.execute(url+symbol+url_books_suffix);
    }

    private class BookTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.d(connect, "Starting background task");

            return connectToURL(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            broadcastResult(result);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        if(intent != null){
            Log.d(log_msg_stockDataService, "stockDataServce osStart called...");
            String companySymbol = intent.getStringExtra(extra_company_symbol);

            //Inspiration for the foreground service is found here: https://stackoverflow.com/questions/5528288/how-do-i-update-the-notification-text-for-a-foreground-service-in-android
            NotificationManager notManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notChannel = new NotificationChannel(notification_channel_name, "books", NotificationManager.IMPORTANCE_LOW);
            notManager.createNotificationChannel(notChannel);

            Notification notification = new NotificationCompat.Builder(this, "books")
                    .setContentTitle(getString(R.string.stockData_Service)).setContentText(getText(R.string.stockData_Service)).
                            setSmallIcon(R.mipmap.ic_stockdataserviceforeground).setTicker(getText(R.string.stockData_Service))
                            .setChannelId("books").build();


            startForeground(stock_data_service_id, notification);
            backgroundTask(companySymbol);

        }
        //The system will try to recreate the service after it is killed:
        return START_STICKY;
    }

    private void broadcastResult(String res) {
        Intent broadcastResultsIntent = new Intent();
        broadcastResultsIntent.putExtra(put_extra_broadcast_result, res);
        broadcastResultsIntent.setAction(broadcast_results);

        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastResultsIntent);
        Log.d(log_msg_stockDataService, "Results from broadcasting: " + res);
    }

    public List<Book> getBooks(Context context){
        return BookDb.getBookDb(context).bookDao().getAllBooks();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //connectToUrl is taken from/inspired by: https://stackoverflow.com/questions/35547375/android-studio-connecting-to-a-url
    private String connectToURL(String callUrl) {
        InputStream is = null;

        try {
            URL url = new URL(callUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoInput(true);
            connection.connect();

            int res = connection.getResponseCode();
            Log.d(connect, "Response: " + res);
            is = connection.getInputStream();

            return streamToString(is);
        } catch (ProtocolException pe) {
            Log.d(connect, "ERROR - ProtocolException");
        } catch (UnsupportedEncodingException uee) {
            Log.d(connect, "ERROR - UnsupportedEncodingException");
        } catch (IOException ioe) {
            Log.d(connect, "ERROR - IOException");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                    Log.d(connect, "ERROR - Could not close stream, IOException");
                }
            }
        }

        return null;
    }

    //streamToString is inspired by/ taken from: https://stackoverflow.com/questions/2492076/android-reading-from-an-input-stream-efficiently
    private String streamToString(InputStream is) {
        StringBuilder returnString = new StringBuilder();
        String line;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

        try {
            while ((line = bufferedReader.readLine()) != null) {
                returnString.append(line);
            }
        } catch (IOException e) {
            Log.e(connect, "Error reading the HTTP response", e);
        }

        return returnString.toString();
    }

    @Override
    public void onDestroy(){
        Log.d(log_msg_stockDataService, stockDataService_destroyed);
        super.onDestroy();
        handler.removeCallbacks(executeTask);
    }
}