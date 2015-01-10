package com.coderobot.autocaller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Tony on 7/23/14.
 */
public class CallerDB extends SQLiteOpenHelper {

    private final static String Tag = "CallerDB";

    private final static int database_version = 1;
    private final static String database_name = "Number.db";

    private static CallerDB mInstance = null;

    private CallerDB(Context context) {
        super(context, database_name, null, database_version);
    }

    public static CallerDB getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CallerDB(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL = "CREATE TABLE Number (" + "id INTEGER," + "number TEXT, path TEXT" + ");";
        db.execSQL(SQL);
    }

    public String getPhoneNum(int id) {
        String phoneNum = "0000000000";
        SQLiteDatabase db = mInstance.getReadableDatabase();

        Cursor c = db.rawQuery("select * from Number where id=" + id, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            phoneNum = c.getString(1);
        }
        db.close();

        return phoneNum;
    }

    public ArrayList<PhoneSet> getAllPhoneNum() {
        SQLiteDatabase db = mInstance.getReadableDatabase();

        ArrayList<PhoneSet> phoneSet = new ArrayList<PhoneSet>();
        Cursor c = db.rawQuery("select * from Number", null);
        while (c.moveToNext()) {
            phoneSet.add(new PhoneSet(c.getInt(0), c.getString(1), c.getString(2)));
        }
        db.close();
        return phoneSet;
    }

    public void updatePhoneNumber(PhoneSet phoneSet) {
        SQLiteDatabase db = mInstance.getWritableDatabase();

        db.delete("Number", "id=?", new String[]{phoneSet.id + ""});
        ContentValues values = new ContentValues();
        values.put("id", phoneSet.id);
        values.put("number", phoneSet.phoneNum);
        values.put("path", phoneSet.path);
        db.insert("Number", null, values);
        db.close();
    }

    public void deleteId(int id) {
        SQLiteDatabase db = mInstance.getWritableDatabase();

        db.delete("Number", "id=?", new String[]{id + ""});
        db.close();
    }

    public void printDB() {
        SQLiteDatabase db = mInstance.getReadableDatabase();

        Cursor c = db.rawQuery("select * from Number", null);
        while (c.moveToNext()) {
            log(c.getInt(0) + " " + c.getString(1));
        }
        db.close();
    }

    public void log(String msg) {
        Log.d(Tag, msg);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
