package com.example.jnubus;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class StoppageAdapter extends ArrayAdapter<String> {

    public List<String> arrayList = new ArrayList();
    public Activity activity;

    public StoppageAdapter(Activity activity, List<String> arrayList) {
        super(activity, R.layout.bus_details_list, arrayList);
        this.activity = activity;
        this.arrayList = arrayList;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = this.activity.getLayoutInflater().inflate(R.layout.bus_details_list, viewGroup, false);

        ((TextView) view.findViewById(R.id.item_route_name_tv)).setText( this.arrayList.get(i));
        View upLine = view.findViewById(R.id.up_line);
        View downLine = view.findViewById(R.id.down_line);
        ImageView imageView =  view.findViewById(R.id.item_dot_iv);

        if (i == 0) {
            upLine.setVisibility(8);
            imageView.setImageResource(R.drawable.ic_dot_green);
        } else if (i != this.arrayList.size() - 1) {
            upLine.setVisibility(0);
            imageView.setImageResource(R.drawable.ic_dot_blue_grey);
        }

        if (i == this.arrayList.size() - 1) {
            downLine.setVisibility(8);
            imageView.setImageResource(R.drawable.ic_dot_red);
        } else if (i != 0) {
            downLine.setVisibility(0);
            imageView.setImageResource(R.drawable.ic_dot_blue_grey);
        }

        return view;
    }
}
