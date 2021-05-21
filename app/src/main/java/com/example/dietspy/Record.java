package com.example.dietspy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.dietspy.MainActivity.dataStorage;

public class Record {

    private String date;
    private int foodId;

    public Record(int foodId) {
        this.foodId = foodId;
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        date = df.format(Calendar.getInstance().getTime());
    }

    public Record(int foodId, String date) {
        this.foodId = foodId;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public int getFoodId() {
        return foodId;
    }

    public String foodName() {
        return dataStorage.getFoodName(foodId);
    }

}
