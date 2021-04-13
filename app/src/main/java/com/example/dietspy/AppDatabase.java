package com.example.dietspy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AppDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DietBase.db";
    private static final String NUTRIENTS_TABLE = "Nutrients";
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_TARGET = "Target";
    private static final String COLUMN_FLAG = "Flag";
    private static final String COLUMN_UNITS = "Units";
    private static final String COLUMN_AMOUNT = "Amount";


    public AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS Nutrients(Name TEXT PRIMARY KEY, Target INTEGER, Flag Integer, Units Integer)";
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS Progress(Name TEXT PRIMARY KEY, Amount INTEGER, " +
                "Date TEXT, Time Text, FOREIGN KEY(Name) REFERENCES Nutrients(Name))";
        db.execSQL(query);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Nutrients");
        onCreate(db);
    }

    public boolean insertNutrient (String name, int target, int flag, int units) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Name", name);
        contentValues.put("Target", target);
        contentValues.put("Flag", flag);
        contentValues.put("Units", units);
        db.insert("Nutrients", null, contentValues);
        return true;
    }

    public boolean insertProgress(String name, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Name", name);
        contentValues.put("Amount", amount);
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String dateTime = df.format(Calendar.getInstance().getTime());
        contentValues.put("Date", dateTime.substring(0, 15));
        contentValues.put("Time", dateTime.substring(16, 24));
        db.insert("Progress", null, contentValues);
        return true;
    }

    public int getNutrientProgressToday(String nutrientName) {
        SQLiteDatabase db = this.getReadableDatabase();
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime()).substring(0, 15);
        Cursor res =  db.rawQuery("SELECT * FROM Progress P WHERE " +
                "Name=\"" + nutrientName + "\" AND Date=\"" + date + "\"", null);
        int progress = 0;
        if (res.getCount() > 0) {
            progress = res.getInt(res.getColumnIndex(COLUMN_AMOUNT));
        }
        return progress;
    }

    public ArrayList<Nutrient> getAllNutrients() {
        ArrayList<Nutrient> list = new ArrayList<Nutrient>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * from Nutrients", null );
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            Nutrient cur = new Nutrient(res.getString(res.getColumnIndex(COLUMN_NAME)),
                    res.getInt(res.getColumnIndex(COLUMN_TARGET)),
                    res.getInt(res.getColumnIndex(COLUMN_FLAG)),
                    res.getInt(res.getColumnIndex(COLUMN_UNITS)));
            list.add(cur);
            res.moveToNext();
        }
        return list;
    }
}
