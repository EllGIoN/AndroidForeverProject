package com.example.androidforeversource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DataAccess extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "androidForever";
    private static final String TABLE_PRODUCTS = "products";
    private static SQLiteDatabase sqldb;

    public DataAccess(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqldb = this.getReadableDatabase();
    }

    public void saveOrUpdateData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            Task<QuerySnapshot> dbTask = db.collection("Products")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("DATABASE", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("DATABASE", "Error getting documents.", task.getException());
                        }
                    });

            dbTask.onSuccessTask(new SuccessContinuation<QuerySnapshot, Object>() {

                @NonNull
                @Override
                public Task<Object> then(QuerySnapshot queryDocumentSnapshots) throws Exception {
                    List<Product> products = new ArrayList<>();

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Product product = document.toObject(Product.class);
                        product.sku = document.getId();
                        products.add(product);
                    }

                    for (Product product : products) {
                        ContentValues values = new ContentValues();

                        values.put("sku", product.sku);
                        values.put("name", product.name);
                        values.put("url", product.url);
                        values.put("imageUrl", product.imageUrl);
                        values.put("currentPrice", product.currentPrice);
                        values.put("oldPrice", product.oldPrice);
                        values.put("category", product.category);

                        Cursor cursor = sqldb.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE sku LIKE '" + product.sku + "';", new String[]{});
                        if(cursor == null || !cursor.moveToFirst()){
                            sqldb.insert(TABLE_PRODUCTS, null, values);
                        } else {
                            sqldb.update(TABLE_PRODUCTS, values, "sku LIKE ?", new String[] { product.sku });
                        }
                        if(cursor != null){
                            cursor.close();
                        }
                    }
                    sqldb.close();
                    return null;
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.terminate();
        }
    }

    public Product getProduct(String sku){
        SQLiteDatabase sqldb = this.getReadableDatabase();

        Cursor cursor = sqldb.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE sku LIKE '" + sku + "';", new String[]{});
        if(cursor != null){
            if(cursor.moveToFirst()) {
                Product product = new Product();
                product.sku = cursor.getString(1);
                product.name = cursor.getString(2);
                product.url = cursor.getString(3);
                product.imageUrl = cursor.getString(4);
                product.currentPrice = Double.parseDouble(cursor.getString(5));
                product.oldPrice = Double.parseDouble(cursor.getString(6));
                product.category = cursor.getString(7);
                return product;
            }
            cursor.close();
        }
        return null;
    }

    public ArrayList<Product> getProducts(){
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase sqldb = this.getReadableDatabase();

        Cursor cursor = sqldb.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + ";", new String[]{});
        if(cursor != null){
            if(cursor.moveToFirst()) {
                do{
                    Product product = new Product();
                    product.sku = cursor.getString(1);
                    product.name = cursor.getString(2);
                    product.url = cursor.getString(3);
                    product.imageUrl = cursor.getString(4);
                    product.currentPrice = Double.parseDouble(cursor.getString(5));
                    product.oldPrice = Double.parseDouble(cursor.getString(6));
                    product.category = cursor.getString(7);
                    products.add(product);
                } while(cursor.moveToNext());
            }
            cursor.close();
        }
        return products;
    }

    @Override
    public void onCreate(SQLiteDatabase sqldb) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS
                + " (id INTEGER PRIMARY KEY AUTOINCREMENT, sku TEXT, name TEXT, url TEXT, imageUrl TEXT, currentPrice REAL, oldPrice REAL, category TEXT);";
        sqldb.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqldb, int oldVersion, int newVersion) {
        sqldb.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS + ";");
        onCreate(sqldb);
    }
}
