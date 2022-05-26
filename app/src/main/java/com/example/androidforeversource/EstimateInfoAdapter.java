package com.example.androidforeversource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class EstimateInfoAdapter extends ArrayAdapter<Estimate> {

    private static final String TAG = "StructEstimateInfoAdapter";
    private Context mContext;
    int mResource;
    /**
     * Default constructor for the personal StructEstimateInfoAdapter
     * @param context
     * @param resource
     * @param objects
     */

    public EstimateInfoAdapter(Context context, int resource, ArrayList<Estimate> objects){
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
