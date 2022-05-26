package com.example.androidforeversource;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    private static final int PERMISSION_REQUEST_CODE = 200;
    private ArrayList<Estimate> infoList = new ArrayList<>();
    private EstimateInfoAdapter adapter;
    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU = 1;
    private Estimate sei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Started.");
        ListView mListView = findViewById(R.id.listViewInMainActivity); // take from it

        infoList = new DataAccess(MainActivity.this).getEstimates();


        adapter = new EstimateInfoAdapter(this, R.layout.adapter_view_layout, infoList);
        mListView.setAdapter(adapter);

        registerForContextMenu(mListView);

        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Choose"); // title of context menu

        // context menu choices
        menu.add(0, v.getId(), 0, "Share");
        menu.add(0, v.getId(), 0, "Create PDF");
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Share") {
            ShareMethod(item);
        } else if (item.getTitle() == "Edit") {

        }
        if (item.getTitle() == "Delete") {
            _deleteRecordContextBtn(item);

        }
        if (item.getTitle() == "Create PDF") {
            _createPDFileContextBtn(item);
        }

        return true;
    }

    private void ShareMethod(MenuItem item) {

        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Estimate sei = infoList.get(menuInfo.position);
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            Toast.makeText(MainActivity.this, "Bluetooth does not support this device", Toast.LENGTH_LONG).show();
        } else {
            File file = new File(Environment.getExternalStorageDirectory(), sei.getName() + sei.getDateOfCreation() + ".pdf");
            if (file.exists()) {

            } else {
                _createPDFile();
            }
            SendFile();
        }
    }

    private void SendFile() {
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivityForResult(discoveryIntent, REQUEST_BLU);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == DISCOVER_DURATION && requestCode == REQUEST_BLU){

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("application/pdf");
            File file = new File(Environment.getExternalStorageDirectory(),sei.getName()+sei.getDateOfCreation()+".pdf");
            intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(file));
            PackageManager pm = getPackageManager();
            List<ResolveInfo> appsList = pm.queryIntentActivities(intent,0);

            if(appsList.size() > 0){
                String packageName = null;
                String className = null;
                boolean found = false;

                for(ResolveInfo info : appsList){
                    packageName = info.activityInfo.packageName;
                    if(packageName.equals("com.android.bluetooth")){
                        className = info.activityInfo.name;
                        found = true;
                        break;
                    }
                }
                if(!found){
                    Toast.makeText(MainActivity.this, "Bluetooth haven't been found", Toast.LENGTH_SHORT).show();
                }
                else{
                    intent.setClassName(packageName,className);
                    Toast.makeText(MainActivity.this, "File was send", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            }
            else{
                Toast.makeText(MainActivity.this, "Bluetooth is cancelled", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void goToAddNewEstimate(View view){
        Intent intent = new Intent(this, AddNewEstimate.class);
        startActivity(intent);
    }
    private void _deleteRecordContextBtn(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        new DataAccess(MainActivity.this).deleteEstimate(infoList.remove(menuInfo.position).id);
        adapter.notifyDataSetChanged();
    }
    private void _createPDFileContextBtn(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
         sei = infoList.get(menuInfo.position);

        File file = new File(Environment.getExternalStorageDirectory(),sei.getName()+sei.getDateOfCreation()+".pdf");
        if(file.exists()){
            Toast.makeText(MainActivity.this,"File Already Exist",Toast.LENGTH_SHORT).show();
        }
        else {
            _createPDFile();
        }
    }

    private void _createPDFile(){
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