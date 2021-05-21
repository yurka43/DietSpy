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
import java.util.HashMap;
import java.util.ListIterator;

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
                "CONSTRAINT delNutrient FOREIGN KEY(Name) REFERENCES Nutrients(Name) " +
                "ON DELETE CASCADE ON UPDATE CASCADE)";
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS Foods(Id INTEGER PRIMARY KEY, Name TEXT UNIQUE)";
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS FoodNutrientPair(FoodId INTEGER, NutrientName TEXT, Amount INTEGER, Unit INTEGER, PRIMARY KEY (FoodId, NutrientName)," +
                "FOREIGN KEY(FoodId) REFERENCES Foods(Id) ON DELETE CASCADE," +
                "FOREIGN KEY(NutrientName) REFERENCES Nutrients(Name) ON DELETE CASCADE ON UPDATE CASCADE)";
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS Ingredients(IngredientName TEXT PRIMARY KEY, Units Integer)";
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS FoodIngredientPair(FoodId INTEGER, IngredientName TEXT UNIQUE, Amount REAL, PRIMARY KEY(FoodId, IngredientName)," +
                "FOREIGN KEY(FoodId) REFERENCES Foods(Id) ON DELETE CASCADE)";
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS FoodRecord(FoodId INTEGER, Date TEXT, Time TEXT, PRIMARY KEY(FoodId, Date, Time)," +
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

    public boolean insertProgress(int foodId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("FoodId", foodId);
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String dateTime = df.format(Calendar.getInstance().getTime());
        contentValues.put("Date", dateTime.substring(0, 16));
        contentValues.put("Time", dateTime.substring(17, 23));
        db.insert("FoodRecord", null, contentValues);
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

        res.close();
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

        res.close();
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
        String date = df.format(Calendar.getInstance().getTime()).substring(0, 16);
        Cursor res =  db.query("Progress", new String[]{"Amount"}, "Name = ?", new String[]{nutrientName},
                null, null, null);
        int progress = 0;
        res.moveToFirst();

        if (res.isAfterLast() == false) {
            progress = res.getInt(res.getColumnIndex(COLUMN_AMOUNT));
        }

        res.close();
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

        res.close();
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

        res.close();
        return list;
    }

    public ArrayList<Record> getAllRecords(String date) {
        ArrayList<Record> list = new ArrayList<Record>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.query("FoodRecord", new String[]{"FoodId", "Date", "Time"}, "Date = ?",
                new String[]{date}, null, null, null);
        res.moveToFirst();


        while(res.isAfterLast() == false) {
            String fullDateTime = res.getString(res.getColumnIndex("Date")) +
                    res.getString(res.getColumnIndex("Time"));
            int id = res.getInt(res.getColumnIndex("FoodId"));
            System.out.println(id);
            Record rec = new Record(id, fullDateTime);
            list.add(rec);

            res.moveToNext();
        }
        res.close();
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
        db.insertWithOnConflict("FoodNutrientPair", null,
                contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return true;
    }

    public void deleteFoodNutrient(int foodId, String nutrientName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("FoodNutrientPair", "FoodId = ? AND NutrientName = ?",
                new String[]{"" + foodId, nutrientName});
    }

    public void deleteFoodIngredient(int foodId, String ingredientName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("FoodIngredientPair", "FoodId = ? AND IngredientName = ?",
                new String[]{"" + foodId, ingredientName});
    }

    public String getFoodName(int foodId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.query("Foods", new String[]{"Name"}, "Id = ?", new String[]{"" + foodId},
                null, null, null);
        res.moveToFirst();

        String result = "Error";

        while(res.isAfterLast() == false) {
            result = res.getString(res.getColumnIndex(COLUMN_NAME));
            res.moveToNext();
        }

        res.close();
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
        res.close();

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
            res2.close();
        }
        res.close();

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
        res.close();
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
        res.close();

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
        res.close();

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
        long inserted = db.insertWithOnConflict("FoodIngredientPair", null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (inserted == -1) {
            db.update("FoodIngredientPair", contentValues, "FoodId = ?",
                    new String[]{"" + foodId});
        }
        return true;
    }

    public void insertNutrientProgress(String name, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put("Name", name);
        c.put("Amount", amount);
        db.insert("Progress", null, c);
    }

    public void calculateProgress() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Progress", "", null);

        int hashmap[] = new int[this.getMaxId()+1];

        db = this.getReadableDatabase();
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime()).substring(0, 16);
        Cursor res =  db.query("FoodRecord", new String[]{"FoodId"}, "Date = ?",
                new String[]{date}, null, null, null);
        res.moveToFirst();
        int result;

        while(res.isAfterLast() == false) {
            result = res.getInt(res.getColumnIndex("FoodId"));
            hashmap[result] = hashmap[result] + 1;
            res.moveToNext();
        }

        HashMap<String, Integer> nv = new HashMap<String, Integer>();
        for (int i = 0; i < this.getMaxId(); i++) {
            ArrayList<Pair<String, Pair<Integer, Integer>>> foodNutrients =
                    new ArrayList<Pair<String, Pair<Integer,Integer>>>();
            if (hashmap[i] > 0) {
                foodNutrients = getFoodNutrients(i);
                for (int j = 0; j < foodNutrients.size(); j++) {
                    Pair<String, Pair<Integer, Integer>> old = foodNutrients.get(j);

                    Pair<String, Pair<Integer, Integer>> newNutrient = new Pair<String, Pair<Integer, Integer>>(old.first,
                            new Pair<Integer, Integer>(old.second.first*hashmap[i], old.second.second));
                    foodNutrients.set(j, newNutrient);
                }
            }

            for (Pair<String, Pair<Integer, Integer>> n : foodNutrients) {
                if (nv.containsKey(n.first)) {
                    int old = nv.get(n.first);
                    nv.put(n.first, old + n.second.first);
                } else {
                    nv.put(n.first, n.second.first);
                }
            }
        }

        db = this.getWritableDatabase();
        for (String k : nv.keySet()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("Name", k);
            contentValues.put("Amount", nv.get(k));
            db.insert("Progress", null, contentValues);
        }
        res.close();
    }


}
