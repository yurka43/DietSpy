package com.example.dietspy;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class FoodAdapter extends ArrayAdapter<Pair<String,Integer>> {

    private Context context;
    private ArrayList<Pair<String,Integer>> nutrientValuePairs;

    public FoodAdapter (Context c, ArrayList<Pair<String,Integer>> nutrientValuePairs) {
        super(c, R.layout.nutrient_row, R.id.nutrient_name, nutrientValuePairs);
        this.context = c;
        this.nutrientValuePairs = nutrientValuePairs;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.nutrient_food_row, parent, false);
        TextView row_title = row.findViewById(R.id.nutrient_name);
        TextView progressText = row.findViewById(R.id.progressText);
        Pair<String,Integer> curPair = nutrientValuePairs.get(position);

        row_title.setText(curPair.first);
        progressText.setText("" + curPair.second);
        return row;
    }
}
