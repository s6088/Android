package com.example.jnubus;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class WhereToGoFragment extends Fragment implements BusAdapter.OnBusListener {

    private AutoCompleteTextView actv;
    private List <String> places;
    private List <String> buses;
    private View view;
    private RecyclerView recyclerView;
    private Context mContext;
    private BusAdapter.OnBusListener onBusListener;




    private DatabaseHelper databaseHelper;


    public WhereToGoFragment(){

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_where_to_go, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = view.getContext();
        actv = view.findViewById(R.id.actv);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        onBusListener = this;
        places = new ArrayList<>();
        buses = new ArrayList<>();
        new FatchPlaces().execute();

        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String placeName = actv.getText().toString().trim();
                new SearchBuses(placeName).execute();
            }
        });

    }


    @Override
    public void onBusClick(int position) {
        Intent intent = new Intent(mContext, StoppageActivity.class);
        intent.putExtra("busName", buses.get(position));
        startActivity(intent);
    }

    private class SearchBuses extends AsyncTask<Void, Void, Void> {

        private String placeName;

        public SearchBuses (String placeName){
            this.placeName = placeName;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            databaseHelper = new DatabaseHelper(mContext);
            databaseHelper.openDataBase();
            int ans = databaseHelper.getPlacesByName(placeName);
            if(ans==-1)return null;
            buses = databaseHelper.getListBuses(ans);
            return null;
        }

        @Override
        protected void onPostExecute(Void x) {
            super.onPostExecute(null);
            recyclerView.setAdapter(new BusAdapter(buses, onBusListener));
            databaseHelper.closeDatabase();
        }


    }



    private class FatchPlaces extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            databaseHelper = new DatabaseHelper(mContext);
            databaseHelper.openDataBase();
            places = databaseHelper.getListPlaces();
            return null;
        }

        @Override
        protected void onPostExecute(Void x) {
            super.onPostExecute(null);
            databaseHelper.closeDatabase();
            ArrayAdapter <String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_expandable_list_item_1, places);
            actv.setAdapter(adapter);
        }


    }




}
