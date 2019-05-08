package com.example.savemynote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{


    public static final String DATABASE_NAME = "mylist_db.db";
    public static final String TABLE_NAME = "mylist_data";
    public static final String NOTE_ID = "ID";
    public static final String NOTE_TEXT = "NOTE";
    public static final String NOTE_IMAGES = "PATH";


    public DatabaseHelper( Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NOTE TEXT, PATH TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData(String Note, String path)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues  = new ContentValues();
        contentValues.put(NOTE_TEXT, Note);
        contentValues.put(NOTE_IMAGES, path);
        long res = db.insert(TABLE_NAME, null, contentValues);
        if(res == -1)
            return false;
        return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);
        return res;
    }

    public Integer deleteData (String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[] {id});
    }

    public boolean updateNote(String id, String note, String path){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues  = new ContentValues();

        contentValues.put(NOTE_ID, id);
        contentValues.put(NOTE_TEXT, note);
        contentValues.put(NOTE_IMAGES, path);
        db.update(TABLE_NAME,contentValues,"ID = ?", new String[] {id});
        return true;
    }
}