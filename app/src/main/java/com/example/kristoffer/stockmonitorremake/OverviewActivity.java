package com.example.kristoffer.stockmonitorremake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class OverviewActivity extends AppCompatActivity {

    private ListView portfolio;
    private TextView caption;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

    }
}
