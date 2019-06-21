package com.example.jnubus;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class StoppageActivity extends AppCompatActivity {


    private ListView listView;
    private List<String> data;
    private String busName;
    private DatabaseHelper databaseHelper;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stoppage);

        data = new ArrayList<>();
        listView = findViewById(R.id.bus_details_route_list_lv);
        busName = getIntent().getStringExtra("busName");

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoppageActivity.this, RouteActivity.class);
                intent.putExtra("busName", busName);
                startActivity(intent);
            }
        });

        new FatchStoppage().execute();
    }

    private class FatchStoppage extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            databaseHelper = new DatabaseHelper(StoppageActivity.this);
            databaseHelper.openDataBase();
            data = databaseHelper.getStoppage(busName);
            return null;
        }

        @Override
        protected void onPostExecute(Void x) {
            super.onPostExecute(null);
            StoppageAdapter stoppageAdapter = new StoppageAdapter(StoppageActivity.this, data);
            listView.setAdapter(stoppageAdapter);
            databaseHelper.closeDatabase();
        }


    }

}
