package com.wapazock.solveit.sql_helper;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class helperSQL extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "config.db" ;

    public helperSQL(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE configurations (id INTEGER PRIMARY KEY NOT NULL,name TEXT NOT NULL, varue TEXT NOT NULL)");
    }

    public void writeSQL(String sql){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL(sql);
    }

    public Cursor readSQL(String sql){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(sql,null);
        return cursor;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
