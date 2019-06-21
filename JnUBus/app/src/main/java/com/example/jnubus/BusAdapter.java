package com.example.jnubus;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {

    private List <String> data;
    private OnBusListener mOnBusListener;

    public BusAdapter(List <String> data, OnBusListener onBusListener)
    {
        this.data = data;
        this.mOnBusListener = onBusListener;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.list_item_layout, viewGroup, false);
        return new BusViewHolder(view, mOnBusListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder BusViewHolder, int i) {
        String busName = data.get(i);
        BusViewHolder.textView.setText(busName);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }



    public class BusViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView;
        OnBusListener onBusListener;

        public BusViewHolder(@NonNull View itemView, OnBusListener onBusListener) {
            super(itemView);
            textView = itemView.findViewById(R.id.busName);
            itemView.setOnClickListener(this);
            this.onBusListener = onBusListener;
        }

        @Override
        public void onClick(View v) {
            onBusListener.onBusClick(getAdapterPosition());
        }
    }

    public interface OnBusListener{
        void onBusClick(int position);
    }

}
