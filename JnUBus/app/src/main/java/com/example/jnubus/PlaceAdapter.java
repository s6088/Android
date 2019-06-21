package com.example.jnubus;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private List <String> data;

    public PlaceAdapter(List <String> data){
        this.data = data;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.list_stoppage_layout, viewGroup, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder placeViewHolder, int i) {
        String placeName = data.get(i);
        placeViewHolder.textView.setText(placeName);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.placeName);
        }
    }

}
