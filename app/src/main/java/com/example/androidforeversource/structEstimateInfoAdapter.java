package com.example.androidforeversource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class structEstimateInfoAdapter extends ArrayAdapter<structEstimateInfo> {

    private static final String TAG = "StructEstimateInfoAdapter";
    private Context mContext;
    int mResource;
    /**
     * Default constructor for the personal StructEstimateInfoAdapter
     * @param context
     * @param resource
     * @param objects
     */

    public structEstimateInfoAdapter(Context context, int resource, ArrayList<structEstimateInfo> objects){
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //get all the estimate's info
        String name = getItem(position).getName();
        String cost = getItem(position).getCost();
        String dateOfCreation = getItem(position).getDateOfCreation();

        // create object with getted info
        structEstimateInfo struct = new structEstimateInfo(name,cost,dateOfCreation);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.textView1);
        TextView tvCost = (TextView) convertView.findViewById(R.id.textView2);
        TextView tvDayOfCreation = (TextView) convertView.findViewById(R.id.textView3);

        tvName.setText(name);
        tvCost.setText(cost);
        tvDayOfCreation.setText(dateOfCreation);

        return convertView;
    }

}
