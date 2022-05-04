package com.example.androidforeversource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DataAccess extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "androidForever";
    private static final String TABLE_PRODUCTS = "products";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DataAccess(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void saveOrUpdateData(){
        List<Product> products = db.collection("Products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("DATABASE", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("DATABASE", "Error getting documents.", task.getException());
                        }
                    }
                }).getResult().toObjects(Product.class);

        SQLiteDatabase db = this.getReadableDatabase();
        for (Product product : products) {
            ContentValues values = new ContentValues();

            values.put("sku", product.sku);
            values.put("name", product.name);
            values.put("url", product.url);
            values.put("imageUrl", product.imageUrl);
            values.put("currentPrice", product.currentPrice);
            values.put("oldPrice", product.oldPrice);
            values.put("category", product.category);

            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + "WHERE sku LIKE " + product.sku + ";", new String[]{});
            if(cursor == null || !cursor.moveToFirst()){
                db.insert(TABLE_PRODUCTS, null, values);
            } else {
                db.update(TABLE_PRODUCTS, values, "sku LIKE ?", new String[] { product.sku });
            }
            db.close();
        }
    }

    public ArrayList<Product> getProducts(){
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + ";", new String[]{});
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
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS
                + " (id INTEGER PRIMARY KEY AUTOINCREMENT, sku TEXT, name TEXT, url TEXT, imageUrl TEXT, currentPrice REAL, oldPrice REAL, category TEXT);";
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS + ";");
        onCreate(db);
    }
}
