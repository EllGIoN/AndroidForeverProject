package com.example.androidforeversource;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    private static final int PERMISSION_REQUEST_CODE =  200;
    ArrayList<structEstimateInfo> infoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Started.");
        ListView mListView = (ListView) findViewById(R.id.listViewInMainActivity); // take from it

        structEstimateInfo firstExample = new structEstimateInfo("Mr. Krab","4000","25-11-1989");
        structEstimateInfo SecondExample = new structEstimateInfo("Riot hospicium","12000","13-10-2010");


        infoList.add(firstExample);
        infoList.add(SecondExample);

        structEstimateInfoAdapter adapter = new structEstimateInfoAdapter(this, R.layout.adapter_view_layout, infoList);
        mListView.setAdapter(adapter);

        registerForContextMenu(mListView);

        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Choose"); // title of context menu

        // context menu choices
        menu.add(0,v.getId(),0,"Share");
        menu.add(0,v.getId(),0,"Create PDF");
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
            _deleteRecordContextBtn(item);

        }
        if(item.getTitle() == "Create PDF"){
            _createPDFileContextBtn(item);
        }
        //adapter.notifyDataSetChanged();
        return  true;
    }


    public void goToAddNewEstimate(View view){
        Intent intent = new Intent(this,addNewEstimate.class);
        startActivity(intent);
    }
    private void _deleteRecordContextBtn(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        infoList.remove(menuInfo.position);

    }
    private void _createPDFileContextBtn(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        structEstimateInfo sei = infoList.get(menuInfo.position);

        File file = new File(Environment.getExternalStorageDirectory(),sei.getName()+sei.getDateOfCreation()+".pdf");
        if(file.exists()){
            Toast.makeText(MainActivity.this,"File Already Exist",Toast.LENGTH_SHORT).show();
        }
        else {
            _createPDFile(sei);
        }
    }

    private void _createPDFile(structEstimateInfo sei){
        int heightPage = 1120;
        int widthPage = 792;

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(widthPage,heightPage,1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(myPageInfo);

        Canvas canvas = myPage.getCanvas();

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));// SETTINGS
        paint.setTextSize(15);                                                // SETTINGS
        paint.setColor(ContextCompat.getColor(this,R.color.black));    // SETTINGS


        canvas.drawText(sei.getName(),0,0,paint);
        canvas.drawText(sei.getCost(),0,20,paint);
        canvas.drawText(sei.getDateOfCreation(),0,40,paint); // WRITE TEXT
        canvas.drawText("Example for Java Project",0,60,paint); // WRITE TEXT

        pdfDocument.finishPage(myPage);

        File file = new File(Environment.getExternalStorageDirectory(),sei.getName()+sei.getDateOfCreation()+".pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(MainActivity.this, sei.getName()+sei.getDateOfCreation()+" File Created Successfully",Toast.LENGTH_SHORT).show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        pdfDocument.close();

    }

    private boolean checkPermission(){
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }
    private  void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults ) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denined.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}