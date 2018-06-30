package com.example.android.kys;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


public class DatabaseHelper extends SQLiteAssetHelper {
    public static final String DBNAME = "pictures.db";
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public DatabaseHelper(Context context) {
        super(context, DBNAME, null, 1);
    }
}