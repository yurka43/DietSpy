package com.example.dietspy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NutrientAdapter extends ArrayAdapter<String> {

    private Context context;
    private String rTitle[];

    public NutrientAdapter (Context c, String titles[]) {
        super(c, R.layout.nutrient_row, R.id.nutrient_name, titles);
        this.context = c;
        this.rTitle = titles;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.nutrient_row, parent, false);
        TextView row_title = row.findViewById(R.id.nutrient_name);
        row_title.setText(rTitle[position]);

        return row;
    }
}
