package com.example.androidforeversource;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class addNewEstimate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_estimate);
    }
    public void goToSelectProduct(View view){
        Intent intent = new Intent(this,SelectProduct.class);
        startActivity(intent);
    }

    public static void addProduct(Product product){
        if(product != null){

        }
    }
}