package com.example.androidforeversource;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SelectProduct extends AppCompatActivity {
    //DataAccess db = new DataAccess(SelectProduct.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);

        addPositionsToView();
    }

    public void updateProductData(View view){
        new DataAccess(SelectProduct.this).saveOrUpdateData();
        addPositionsToView();
    }

    private void addPositionsToView(){
        List<Product> products = new DataAccess(SelectProduct.this).getProducts();
        List<String> productsStrings = new ArrayList<>();

        for (Product product : products) {
            StringBuilder productString = new StringBuilder();
            productString.append(product.name);
            productString.append("\n\tCena:");
            productString.append(product.currentPrice);
            productString.append("\n\tPoprzednia cena:");
            productString.append(product.oldPrice);
            productString.append("\n\tŹródło:");
            productString.append(product.url);
            productsStrings.add(productString.toString());
        };
        ListView productsView = findViewById(R.id.ProductsList);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, productsStrings);

        productsView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }
}
