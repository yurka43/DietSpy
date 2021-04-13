package com.example.dietspy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class NutrientAdapter extends ArrayAdapter<Nutrient> {

    private Context context;
    private ArrayList<Nutrient> nutrients;

    public NutrientAdapter (Context c, ArrayList<Nutrient> nutrients) {
        super(c, R.layout.nutrient_row, R.id.nutrient_name, nutrients);
        this.context = c;
        this.nutrients = nutrients;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.nutrient_row, parent, false);
        TextView row_title = row.findViewById(R.id.nutrient_name);
        TextView progressText = row.findViewById(R.id.progressText);
        ProgressBar progressBar = row.findViewById(R.id.progressBar);
        Nutrient curNutrient = nutrients.get(position);

        row_title.setText(curNutrient.getName());
        progressText.setText("" + "0/" + curNutrient.getTarget() + curNutrient.getUnits());
        return row;
    }
}
