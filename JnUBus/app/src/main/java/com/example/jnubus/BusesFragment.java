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

import java.util.ArrayList;
import java.util.List;

public class BusesFragment extends Fragment implements BusAdapter.OnBusListener {

    private List <String> buses;
    private View view;
    private RecyclerView recyclerView;
    private Context mContext;
    DatabaseHelper databaseHelper;
    private BusAdapter.OnBusListener onBusListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_buses, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = view.getContext();
        recyclerView = view.findViewById(R.id.busRecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        onBusListener = this;
        buses = new ArrayList<>();
        new FatchBuses().execute();
    }

    @Override
    public void onBusClick(int position) {

        Intent intent = new Intent(mContext, StoppageActivity.class);
        intent.putExtra("busName", buses.get(position));
        startActivity(intent);

    }

    private class FatchBuses extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            databaseHelper = new DatabaseHelper(mContext);
            databaseHelper.openDataBase();
            buses = databaseHelper.getListBuses();
            return null;
        }

        @Override
        protected void onPostExecute(Void x) {
            super.onPostExecute(null);
            recyclerView.setAdapter(new BusAdapter(buses, onBusListener));
            databaseHelper.closeDatabase();
        }


    }
}
