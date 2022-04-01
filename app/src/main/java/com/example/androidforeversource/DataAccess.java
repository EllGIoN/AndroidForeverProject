package com.example.androidforeversource;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DataAccess {
    private DatabaseReference database;

    public DataAccess() {
        database = FirebaseDatabase.getInstance().getReference(DataSet.class.getSimpleName());
    }

    public Task<Void> saveRecord(DataSet record) {
        return database.push().setValue(record);
    }
}
