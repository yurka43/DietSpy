package com.example.dietspy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Pair;

import java.sql.PreparedStatement;
import java.sql.Statement;
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
    private static final String COLUMN_ID = "Id";


    public AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.getWritableDatabase().execSQL("PRAGMA foreign_keys=ON");
    }

    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS Nutrients(Name TEXT PRIMARY KEY, Target INTEGER, Flag Integer, Units Integer)";
        db.execSQL(query);
        query = "PRAGMA foreign_keys = ON";
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS Progress(Name TEXT PRIMARY KEY, Amount INTEGER, " +
                "Date TEXT, Time Text," +
                "CONSTRAINT delNutrient FOREIGN KEY(Name) REFERENCES Nutrients(Name) " +
                "ON DELETE CASCADE ON UPDATE CASCADE)";
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS Foods(Id INTEGER PRIMARY KEY, Name TEXT UNIQUE)";
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS FoodNutrientPair(FoodId INTEGER, NutrientName TEXT, Amount INTEGER, Unit INTEGER, PRIMARY KEY (FoodId, NutrientName)," +
                "FOREIGN KEY(FoodId) REFERENCES Foods(Id) ON DELETE CASCADE," +
                "FOREIGN KEY(NutrientName) REFERENCES Nutrients(Name) ON DELETE CASCADE)";
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS Ingredients(IngredientName TEXT PRIMARY KEY, Units Integer)";
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS FoodIngredientPair(FoodId INTEGER, IngredientName TEXT UNIQUE, Amount REAL, PRIMARY KEY(FoodId, IngredientName)," +
                "FOREIGN KEY(FoodId) REFERENCES Foods(Id) ON DELETE CASCADE)";
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

    public boolean deleteNutrient (String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Nutrients", "Name= ?", new String[] {name}) > 0;
    }

    public boolean updateNutrient (String oldName, String newName, int target, int flag, int units) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Name", newName);
        contentValues.put("Target", target);
        contentValues.put("Flag", flag);
        contentValues.put("Units", units);
        return db.update("Nutrients", contentValues, "Name= ?", new String[]{oldName}) > 0;
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

    public boolean insertFood(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, id);
        contentValues.put(COLUMN_NAME, name);
        db.insert("Foods", null, contentValues);
        return true;
    }

    public int getMaxId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT MAX(Id) FROM Foods", null);
        res.moveToFirst();
        int maxId = 0;
        if (res.getCount() > 0) {
            maxId = res.getInt(res.getColumnIndex("MAX(Id)")) + 1;
        }
        return maxId;
    }

    public boolean updateFood(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        db.update("Foods", contentValues, "Id = " + id, null);
        return true;
    }

    public int getFoodId(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String args[] = {name};
        Cursor res =  db.rawQuery("SELECT F.Id FROM Foods F WHERE Name = ?", args);
        res.moveToFirst();
        int id = 0;
        if (res.getCount() > 0) {
            id = res.getInt(res.getColumnIndex("Id"));
        }
        return id;
    }

    public boolean deleteFood(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Foods", "Id = " + id, null);
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

    public ArrayList<String> getFoodNames() {
        ArrayList<String> list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT F.name from Foods F", null );
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            list.add(res.getString(res.getColumnIndex(COLUMN_NAME)));
            res.moveToNext();
        }

        return list;
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

    public ArrayList<String> getNutrientNames() {
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<Nutrient> nutrients = getAllNutrients();
        for (Nutrient n : nutrients) {
            names.add(n.getName());
        }
        return names;
    }

    public boolean insertFoodNutrient(int foodId, String nutrientName, int amount, int unit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("FoodId", foodId);
        contentValues.put("NutrientName", nutrientName);
        contentValues.put("Amount", amount);
        contentValues.put("Unit", unit);
        db.insert("FoodNutrientPair", null, contentValues);
        return true;
    }

    public String getFoodName(int foodId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT F.name from Foods F", null );
        res.moveToFirst();

        String result = "Error";

        while(res.isAfterLast() == false) {
            result = res.getString(res.getColumnIndex(COLUMN_NAME));
            res.moveToNext();
        }

        return result;
    }

    public ArrayList<Pair<String, Pair<Integer, Integer>>> getFoodNutrients(int id) {
        ArrayList<Pair<String, Pair<Integer, Integer>>> foodNutrients =
                new ArrayList<Pair<String, Pair<Integer, Integer>>>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * from FoodNutrientPair F WHERE F.FoodId = " + id, null );
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            Pair<Integer, Integer> amount_unit = new Pair<Integer, Integer>(res.getInt(res.getColumnIndex(COLUMN_AMOUNT)),
                    res.getInt(res.getColumnIndex("Unit")));

            Pair<String, Pair<Integer, Integer>> result =
            new Pair<String, Pair<Integer,Integer>>(res.getString(res.getColumnIndex("NutrientName")), amount_unit);
            foodNutrients.add(result);
            res.moveToNext();
        }

        return foodNutrients;
    }

    public ArrayList<Pair<String, Pair<Double, Integer>>> getFoodIngredients(int id) {
        ArrayList<Pair<String, Pair<Double, Integer>>> foodIngredients =
                new ArrayList<Pair<String, Pair<Double, Integer>>>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * from FoodIngredientPair F WHERE F.FoodId = " + id, null );
        res.moveToFirst();
        Cursor res2;

        while(res.isAfterLast() == false) {
            String ingredientName = res.getString(res.getColumnIndex("IngredientName"));
            res2 = db.query("Ingredients", new String[]{"Units"}, "IngredientName = ?",
                    new String[]{ingredientName}, null, null, null);
            res2.moveToFirst();
            int unit = res2.getInt(res2.getColumnIndex("Units"));
            Pair<Double, Integer> amount_unit = new Pair<Double, Integer>(res.getDouble(res.getColumnIndex(COLUMN_AMOUNT)),
                    unit);

            Pair<String, Pair<Double, Integer>> result =
                    new Pair<String, Pair<Double,Integer>>(ingredientName, amount_unit);
            foodIngredients.add(result);
            res.moveToNext();
        }

        return foodIngredients;
    }

    public ArrayList<String> getIngredientNames() {
        ArrayList<String> ingredients = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT I.IngredientName from Ingredients I", null );
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            String result = res.getString(res.getColumnIndex("IngredientName"));
            ingredients.add(result);
            res.moveToNext();
        }
        return ingredients;
    }

    public int ingredientUnit(String name) {
        int result = -1;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.query("Ingredients", new String[]{"Units"}, "IngredientName = ?",
                new String[]{name}, null, null, null);
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            result = res.getInt(res.getColumnIndex("Units"));
            res.moveToNext();
        }

        return result;
    }

    public boolean insertIngredient(String name, int unit) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.query("Ingredients", new String[]{"IngredientName"}, "IngredientName = ?",
                new String[]{name}, null, null, null);
        res.moveToFirst();
        if (res.getCount() > 0) {
            return false;
        }

        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("IngredientName", name);
        contentValues.put("Units", unit);
        db.insert("Ingredients", null, contentValues);
        return true;
    }

    public boolean insertFoodIngredient(int foodId, String ingredientName, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("FoodId", foodId);
        contentValues.put("IngredientName", ingredientName);
        contentValues.put("Amount", amount);
        db.insert("FoodIngredientPair", null, contentValues);
        return true;
    }


}
