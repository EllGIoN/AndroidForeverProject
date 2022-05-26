package com.example.androidforeversource;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Estimate {
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public int id;
    public String name;
    public String date;

    public ArrayList<Product> products = new ArrayList<>();

    public String getName(){
        return name;
    }
    public String getCost(){
        double price = 0;
        for (Product product : products) {
            price += product.currentPrice;
        }
        return df.format(price);
    }

    public String getDateOfCreation(){
        return date;
    }
}
