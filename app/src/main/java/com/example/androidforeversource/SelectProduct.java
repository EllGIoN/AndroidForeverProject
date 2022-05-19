package com.example.androidforeversource;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SelectProduct extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);
        updateProductList("");

        ListView productsView = (ListView) this.findViewById(R.id.ProductsList);
        productsView.setOnItemClickListener((parent, view, position, id) -> {
            String productSku = productsView.getAdapter().getItem(position).toString().split("Sku:")[1];
            addNewEstimate.addProduct(new DataAccess(SelectProduct.this).getProduct(productSku));
            finish();
        });
    }

    public void updateProductData(View view){
        new DataAccess(SelectProduct.this).saveOrUpdateData();
        updateProductList("");
    }

    public void updateProductList(View view){
        EditText filterText = (EditText) this.findViewById(R.id.filterTextLabel);
        updateProductList(filterText.getText().toString());
    }

    private void updateProductList(String filterText){
        List<Product> products = new DataAccess(SelectProduct.this).getProducts();
        List<String> productsStrings = new ArrayList<>();

        for (Product product : products) {
            if(product.name.contains(filterText) || product.category.contains(filterText)){
                String productString = product.name +
                        "\n\tCena:" +
                        product.currentPrice +
                        "\n\tPoprzednia cena:" +
                        product.oldPrice +
                        "\n\tŹródło:" +
                        product.url +
                        "\n\tSku:" +
                        product.sku;
                productsStrings.add(productString);
            }
        }

        ListView productsView = findViewById(R.id.ProductsList);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, productsStrings);

        productsView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }
}
