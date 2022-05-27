package com.example.androidforeversource;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SetEstimateName extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.set_estimate_name);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.8),(int)(height*0.3));
    }
    public void goToAddNewEstimate(View view){
        EditText et = findViewById(R.id.estimateNewName);
        if(!et.getText().toString().equals("")) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            EstimateProducts.estimate = new DataAccess(SetEstimateName.this).createEstimate(et.getText().toString(),format1.format(cal.getTime()));
            Intent intent = new Intent(this, EstimateProducts.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(this,"Name is empty", Toast.LENGTH_SHORT).show();
        }
    }
}
