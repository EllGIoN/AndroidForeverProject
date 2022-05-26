package com.example.androidforeversource;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddNewEstimate extends AppCompatActivity {
    private Estimate estimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_estimate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EditText editText = this.findViewById(R.id.editTextTextPersonName);
        if(!editText.getText().toString().isEmpty()){
            estimate = new DataAccess(AddNewEstimate.this).getEstimate(editText.getText().toString());
            List<String> productsStrings = new ArrayList<>();

            for (Product product : estimate.products) {
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

            ListView productsView = findViewById(R.id.listViewInMainActivity);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_list_item_1, productsStrings);

            productsView.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    public void goToSelectProduct(View view){
        if(estimate == null){
            EditText editText = this.findViewById(R.id.editTextTextPersonName);
            estimate = new DataAccess(AddNewEstimate.this)
                    .createEstimate(editText.getText().toString(), Calendar.getInstance().getTime().toString());
        }

        if(estimate != null){
            Intent intent = new Intent(this,SelectProduct.class);
            SelectProduct.estimateId = estimate.id;
            startActivity(intent);
        }
    }
}