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

public class PlanAdapter extends ArrayAdapter<Pair<String, Pair<Double, Integer>>> {

    private Context context;
    private ArrayList<Pair<String, Pair<Double, Integer>>> plans;

    public PlanAdapter (Context c, ArrayList<Pair<String, Pair<Double, Integer>>> plans) {
        super(c, R.layout.plan_row, R.id.plan_food_name, plans);
        this.context = c;
        this.plans = plans;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.plan_row, parent, false);
        }
        TextView row_title = convertView.findViewById(R.id.plan_food_name);
        TextView dateText = convertView.findViewById(R.id.amountText);

        Pair<String, Pair<Double, Integer>> curPlan = plans.get(position);
        // System.out.println("This " + position);

        String[] units = context.getResources().getStringArray(R.array.i_units);

        row_title.setText(curPlan.first);
        double a = curPlan.second.first;
        dateText.setText("" + ((a == Math.floor(a)) ? ((int) a) : a) +  " " + units[curPlan.second.second]);
        return convertView;
    }
}