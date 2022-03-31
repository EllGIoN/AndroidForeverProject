package com.example.androidforeversource;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    int heightPage = 1120;
    int widthPage = 792;

    Bitmap bmp,scaledbmp;

    private static final int PERMISSION_REQUEST_CODE =  200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Started.");
        ListView mListView = (ListView) findViewById(R.id.listViewInMainActivity); // take from it

        structEstimateInfo firstExample = new structEstimateInfo("Mr. Krab","4000","25-11-1989");
        structEstimateInfo SecondExample = new structEstimateInfo("Riot hospicium","12000","13-10-2010");

        ArrayList<structEstimateInfo> infoList = new ArrayList<>();

        infoList.add(firstExample);
        infoList.add(SecondExample);

        structEstimateInfoAdapter adapter = new structEstimateInfoAdapter(this, R.layout.adapter_view_layout, infoList);
        mListView.setAdapter(adapter);

        registerForContextMenu(mListView);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Choose"); // title of context menu

        // context menu choices
        menu.add(0,v.getId(),0,"Share");
        menu.add(0,v.getId(),0,"Edit");
        menu.add(0,v.getId(),0,"Delete");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        if(item.getTitle() == "Share"){

        }
        else if(item.getTitle() =="Edit"){

        }
        if(item.getTitle() == "Delete"){

        }
        return  true;
    }

    public void goToAddNewEstimate(View view){
        Intent intent = new Intent(this,addNewEstimate.class);
        startActivity(intent);
    }
}