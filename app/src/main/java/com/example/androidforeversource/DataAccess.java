package com.example.androidforeversource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

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
    private static final String TABLE_ESTIMATES = "estimates";
    private static final String TABLE_ESTIMATES_PRODUCTS = "estimatesProducts";
    private static SQLiteDatabase sqlDBInstance;

    public DataAccess(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void saveOrUpdateData(){
        sqlDBInstance = this.getReadableDatabase();
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

            dbTask.onSuccessTask(queryDocumentSnapshots -> {
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

                    try{
                        sqlDBInstance.insert(TABLE_PRODUCTS, null, values);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                sqlDBInstance.close();
                return null;
            });
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.terminate();
        }
    }

    public Product getProduct(String sku){
        SQLiteDatabase sqlDB = this.getReadableDatabase();

        Cursor cursor = sqlDB.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE sku LIKE '" + sku + "';", new String[]{});
        if(cursor != null){
            if(cursor.moveToFirst()) {
                Product product = new Product();
                product.id = Integer.parseInt(cursor.getString(0));
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
        sqlDB.close();
        return null;
    }

    public ArrayList<Product> getProducts(){
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase sqlDB = this.getReadableDatabase();

        Cursor cursor = sqlDB.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + ";", new String[]{});
        if(cursor != null){
            if(cursor.moveToFirst()) {
                do{
                    Product product = new Product();
                    product.id = Integer.parseInt(cursor.getString(0));
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
        sqlDB.close();
        return products;
    }

    public Estimate createEstimate(String name, String date){
        SQLiteDatabase sqlDB = this.getReadableDatabase();
        Estimate estimate = new Estimate();

        Cursor cursor = sqlDB.rawQuery("SELECT * FROM " + TABLE_ESTIMATES + " WHERE name LIKE '" + name + "';", new String[]{});
        if(cursor != null && cursor.moveToFirst()){
            estimate.id = Integer.parseInt(cursor.getString(0));
            estimate.name = cursor.getString(1);
            estimate.date = cursor.getString(2);

            cursor = sqlDB.rawQuery("SELECT p.* FROM " + TABLE_ESTIMATES + " AS e"
                    + " JOIN " + TABLE_ESTIMATES_PRODUCTS + " AS ep ON ep.estimateId = e.id"
                    + " JOIN " + TABLE_PRODUCTS + " AS p ON ep.productId = p.id"
                    + " WHERE e.id = " + estimate.id + ";", new String[]{});
            if(cursor != null && cursor.moveToFirst()){
                do{
                    Product product = new Product();
                    product.id = Integer.parseInt(cursor.getString(0));
                    product.sku = cursor.getString(1);
                    product.name = cursor.getString(2);
                    product.url = cursor.getString(3);
                    product.imageUrl = cursor.getString(4);
                    product.currentPrice = Double.parseDouble(cursor.getString(5));
                    product.oldPrice = Double.parseDouble(cursor.getString(6));
                    product.category = cursor.getString(7);
                    estimate.products.add(product);
                } while(cursor.moveToNext());
            }
        } else {
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("date", date);
            sqlDB.insert(TABLE_ESTIMATES, null, values);

            cursor = sqlDB.rawQuery("SELECT * FROM " + TABLE_ESTIMATES + " WHERE name LIKE '" + name + "';", new String[]{});
            if(cursor != null && cursor.moveToFirst()){
                estimate.id = Integer.parseInt(cursor.getString(0));
                estimate.name = cursor.getString(1);
                estimate.date = cursor.getString(2);
            }
        }

        if(cursor != null){
            cursor.close();
        }
        sqlDB.close();

        return estimate.name.isEmpty() ? null : estimate;
    }

    public Estimate getEstimate(int id){
        SQLiteDatabase sqlDB = this.getReadableDatabase();
        Estimate estimate = new Estimate();

        Cursor estimatesCursor = sqlDB.rawQuery("SELECT * FROM " + TABLE_ESTIMATES + " WHERE id = " + id + ";", new String[]{});
        if(estimatesCursor != null && estimatesCursor.moveToFirst()){
            estimate.id = Integer.parseInt(estimatesCursor.getString(0));
            estimate.name = estimatesCursor.getString(1);
            estimate.date = estimatesCursor.getString(2);

            Cursor cursor = sqlDB.rawQuery("SELECT p.* FROM " + TABLE_ESTIMATES + " AS e"
                    + " JOIN " + TABLE_ESTIMATES_PRODUCTS + " AS ep ON ep.estimateId = e.id"
                    + " JOIN " + TABLE_PRODUCTS + " AS p ON ep.productId = p.id"
                    + " WHERE e.id = " + estimate.id + ";", new String[]{});
            if(cursor != null && cursor.moveToFirst()){
                do{
                    Product product = new Product();
                    product.id = Integer.parseInt(cursor.getString(0));
                    product.sku = cursor.getString(1);
                    product.name = cursor.getString(2);
                    product.url = cursor.getString(3);
                    product.imageUrl = cursor.getString(4);
                    product.currentPrice = Double.parseDouble(cursor.getString(5));
                    product.oldPrice = Double.parseDouble(cursor.getString(6));
                    product.category = cursor.getString(7);
                    estimate.products.add(product);
                } while(cursor.moveToNext());
            }

            if(cursor != null){
                cursor.close();
            }
        }

        if(estimatesCursor != null){
            estimatesCursor.close();
        }

        sqlDB.close();
        return estimate.name.isEmpty() ? null : estimate;
    }

    public Estimate getEstimate(String name){
        SQLiteDatabase sqlDB = this.getReadableDatabase();
        Estimate estimate = new Estimate();

        Cursor estimatesCursor = sqlDB.rawQuery("SELECT * FROM " + TABLE_ESTIMATES + " WHERE name LIKE '" + name + "';", new String[]{});
        if(estimatesCursor != null && estimatesCursor.moveToFirst()){
            estimate.id = Integer.parseInt(estimatesCursor.getString(0));
            estimate.name = estimatesCursor.getString(1);
            estimate.date = estimatesCursor.getString(2);

            Cursor cursor = sqlDB.rawQuery("SELECT p.* FROM " + TABLE_ESTIMATES + " AS e"
                    + " JOIN " + TABLE_ESTIMATES_PRODUCTS + " AS ep ON ep.estimateId = e.id"
                    + " JOIN " + TABLE_PRODUCTS + " AS p ON ep.productId = p.id"
                    + " WHERE e.id = " + estimate.id + ";", new String[]{});
            if(cursor != null && cursor.moveToFirst()){
                do{
                    Product product = new Product();
                    product.id = Integer.parseInt(cursor.getString(0));
                    product.sku = cursor.getString(1);
                    product.name = cursor.getString(2);
                    product.url = cursor.getString(3);
                    product.imageUrl = cursor.getString(4);
                    product.currentPrice = Double.parseDouble(cursor.getString(5));
                    product.oldPrice = Double.parseDouble(cursor.getString(6));
                    product.category = cursor.getString(7);
                    estimate.products.add(product);
                } while(cursor.moveToNext());
            }

            if(cursor != null){
                cursor.close();
            }
        }

        if(estimatesCursor != null){
            estimatesCursor.close();
        }

        sqlDB.close();
        return estimate.name.isEmpty() ? null : estimate;
    }

    public ArrayList<Estimate> getEstimates(){
        SQLiteDatabase sqlDB = this.getReadableDatabase();
        ArrayList<Estimate> estimates = new ArrayList<>();

        Cursor estimatesCursor = sqlDB.rawQuery("SELECT * FROM " + TABLE_ESTIMATES + " ;", new String[]{});
        if(estimatesCursor != null && estimatesCursor.moveToFirst()){
            do{
                Estimate estimate = new Estimate();
                estimate.id = Integer.parseInt(estimatesCursor.getString(0));
                estimate.name = estimatesCursor.getString(1);
                estimate.date = estimatesCursor.getString(2);

                Cursor cursor = sqlDB.rawQuery("SELECT p.* FROM " + TABLE_ESTIMATES + " AS e"
                        + " JOIN " + TABLE_ESTIMATES_PRODUCTS + " AS ep ON ep.estimateId = e.id"
                        + " JOIN " + TABLE_PRODUCTS + " AS p ON ep.productId = p.id"
                        + " WHERE e.id = " + estimate.id + ";", new String[]{});
                if(cursor != null && cursor.moveToFirst()){
                    do{
                        Product product = new Product();
                        product.id = Integer.parseInt(cursor.getString(0));
                        product.sku = cursor.getString(1);
                        product.name = cursor.getString(2);
                        product.url = cursor.getString(3);
                        product.imageUrl = cursor.getString(4);
                        product.currentPrice = Double.parseDouble(cursor.getString(5));
                        product.oldPrice = Double.parseDouble(cursor.getString(6));
                        product.category = cursor.getString(7);
                        estimate.products.add(product);
                    } while(cursor.moveToNext());
                }

                if(cursor != null){
                    cursor.close();
                }

                estimates.add(estimate);
            } while (estimatesCursor.moveToNext());
        }

        if(estimatesCursor != null){
            estimatesCursor.close();
        }

        sqlDB.close();
        return estimates;
    }

    public void deleteEstimate(int id){
        SQLiteDatabase sqlDB = this.getReadableDatabase();
        sqlDB.delete(TABLE_ESTIMATES_PRODUCTS, "estimateId =?", new String[]{" " + id});
        sqlDB.delete(TABLE_ESTIMATES, "id =?", new String[]{" " + id} );
        sqlDB.close();
    }

    public void addProductToEstimate(int estimateId, int productId){
        SQLiteDatabase sqlDB = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put("estimateId", estimateId);
        values.put("productId", productId);
        sqlDB.insert(TABLE_ESTIMATES_PRODUCTS, null, values);

        sqlDB.close();
    }

    public void removeProductFromEstimate(int estimateId, int productId){
        SQLiteDatabase sqlDB = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put("estimateId", estimateId);
        values.put("productId", productId);
        sqlDB.rawQuery("DELETE FROM " + TABLE_ESTIMATES_PRODUCTS
                + " WHERE id IN (SELECT id FROM " + TABLE_ESTIMATES_PRODUCTS
                + " WHERE estimateId = " + estimateId + " AND productId = " + productId + " LIMIT 1)", new String[]{});

        sqlDB.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqldb) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS
                + " (id INTEGER PRIMARY KEY AUTOINCREMENT, sku TEXT, name TEXT, url TEXT, imageUrl TEXT, currentPrice REAL, oldPrice REAL, category TEXT);";
        sqldb.execSQL(CREATE_PRODUCTS_TABLE);

        String CREATE_ESTIMATES_TABLE = "CREATE TABLE " + TABLE_ESTIMATES
                + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, date TEXT);";
        sqldb.execSQL(CREATE_ESTIMATES_TABLE);

        String CREATE_ESTIMATES_PRODUCTS = "CREATE TABLE " + TABLE_ESTIMATES_PRODUCTS
                + " (id INTEGER PRIMARY KEY AUTOINCREMENT, estimateId INTEGER, productId INTEGER,"
                + " FOREIGN KEY(estimateId) REFERENCES " + TABLE_ESTIMATES + ","
                + " FOREIGN KEY(productId) REFERENCES " + TABLE_PRODUCTS + ");";
        sqldb.execSQL(CREATE_ESTIMATES_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqldb, int oldVersion, int newVersion) {
        sqldb.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS + ";");
        sqldb.execSQL("DROP TABLE IF EXISTS " + TABLE_ESTIMATES + ";");
        sqldb.execSQL("DROP TABLE IF EXISTS " + TABLE_ESTIMATES_PRODUCTS + ";");
        onCreate(sqldb);
    }
}