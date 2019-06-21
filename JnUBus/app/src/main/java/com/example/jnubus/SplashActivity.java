package com.example.jnubus;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {


    private List <Place> places;
    private List <Relation> relations;
    private List <Bus> buses;
    private FirebaseFirestore db;
    private Timestamp timestamp;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int cnt;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        db              = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        editor            = sharedPreferences.edit();
        places          = new ArrayList<>();
        buses           = new ArrayList<>();
        relations       = new ArrayList<>();
        cnt             = 0;
        if(!createSqliteDatabaseIfNotExist())return;
        checkUpdate();
    }


    private boolean createSqliteDatabaseIfNotExist (){
        databaseHelper = new DatabaseHelper(SplashActivity.this);
        if(!databaseHelper.checkDatabase() || databaseHelper.countTables() < 5){
            try {
                databaseHelper.copyDataBase();
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }

    private void checkUpdate (){

        if(!isNetworkAvailable()){
            goHome();
            return;
        }

        db.collection("update").document("1").get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        timestamp = documentSnapshot.getTimestamp("update");
                        long prev = sharedPreferences.getLong("update", 0);
                        if(prev==0 || new Timestamp(prev, 0).compareTo(timestamp) < 0){
                            fetchPlaces();
                            fetchBuses();
                        }
                        else {
                            goHome();
                        }
                    }
                }
        );
    }

    private void fetchPlaces (){
        db.collection("places")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Place place = new Place(Integer.parseInt(document.getId()), document.getData().get("name").toString());
                            places.add(place);
                        }
                        cnt++;
                        if(cnt==2)update();
                    }
                });
    }

    private void fetchBuses (){
        db.collection("buses")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int x = 0;
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                            Bus bus = new Bus (Integer.parseInt(document.getId()), document.getData().get("name").toString(), Integer.parseInt(document.getData().get("type").toString()));
                            String [] waypoints = document.getData().get("waypoints").toString().trim().split(" ");
                            for(int i=0; i<waypoints.length; i++)relations.add(new Relation(++x, bus.getId(), Integer.parseInt(waypoints[i]), i));
                            buses.add(bus);
                        }
                        cnt++;
                        if(cnt==2)update();
                    }
                });

    }


    public void update (){
        UpdateDatabase updateDatabase =  new UpdateDatabase();
        updateDatabase.execute();
    }

    private class UpdateDatabase extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            databaseHelper.openDataBase();

            databaseHelper.deleteTable("places");
            for(Place place : places)databaseHelper.addPlaces(place);
            databaseHelper.deleteTable("buses");
            for(Bus bus : buses)databaseHelper.addBuses(bus);
            databaseHelper.deleteTable("relation");
            for(Relation relation : relations)databaseHelper.addRelation(relation);


            return null;
        }

        @Override
        protected void onPostExecute(Void x) {
            super.onPostExecute(null);
            databaseHelper.closeDatabase();
            editor.putLong("update", timestamp.getSeconds());
            editor.commit();
            goHome();
        }


    }

    private void goHome (){
        boolean x = sharedPreferences.getInt("type", 0) == 0;
        if(x)
            startActivity( new Intent(SplashActivity.this, TypeActivity.class) );
        else
            startActivity( new Intent(SplashActivity.this, HomeActivity.class) );
    }



    private boolean isNetworkAvailable (){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
