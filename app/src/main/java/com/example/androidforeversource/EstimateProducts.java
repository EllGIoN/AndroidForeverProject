package com.example.androidforeversource;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EstimateProducts extends AppCompatActivity {
    public static Estimate estimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_products);

        if(estimate != null){
            refreshList();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(estimate != null){
            refreshList();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Remove");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        ListView productsView = this.findViewById(R.id.listViewInMainActivity);
        if (item.getTitle() == "Remove") {
            AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            String productSku = productsView.getAdapter().getItem(menuInfo.position).toString().split("Sku:")[1];
            DataAccess db = new DataAccess(EstimateProducts.this);

            Product product = db.getProduct(productSku);
            if(product != null && estimate.id != -1){
                db.removeProductFromEstimate(estimate.id, product.id);
                refreshList();
            }
        }

        return super.onContextItemSelected(item);
    }

    public void goToSelectProduct(View view){
        if(estimate != null){
            Intent intent = new Intent(this,SelectProduct.class);
            SelectProduct.estimateId = estimate.id;
            startActivity(intent);
        }
    }

    public void refreshList(){
        estimate = new DataAccess(EstimateProducts.this).getEstimate(estimate.id);
        List<String> productsStrings = new ArrayList<>();

        for (Product product : estimate.products) {
            String productString = product.name +
                    "\n\tCena:" +
                    product.currentPrice +
                    "\n\tPoprzednia cena:" +
                    product.oldPrice +
                    "\n\tSku:" +
                    product.sku;
            productsStrings.add(productString);
        }

        ListView productsView = findViewById(R.id.listViewInMainActivity);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, productsStrings);

        productsView.setAdapter(arrayAdapter);
        registerForContextMenu(productsView);
        arrayAdapter.notifyDataSetChanged();
    }
    public void returnToMonkey(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}