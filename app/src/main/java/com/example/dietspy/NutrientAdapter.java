package com.example.dietspy;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;

import static com.example.dietspy.MainActivity.dataStorage;

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

        double normalizedAmount = dataStorage.getNutrientProgressToday(curNutrient.getName(), curNutrient.getUnitInt());

        row_title.setText(curNutrient.getName());
        BigDecimal bd = new BigDecimal(normalizedAmount);
        bd = bd.round(new MathContext(6));
        normalizedAmount = bd.doubleValue();
        progressText.setText("" + normalizedAmount + "/" + curNutrient.getTarget() + curNutrient.getUnits());
        int progress = (int) (normalizedAmount/curNutrient.getTarget() * 100.0);
        if (progress > 100 && curNutrient.getFlag() == 1) {
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        } else if (progress < 100 && curNutrient.getFlag() == 0) {
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
        }
        progressBar.setProgress(progress, true);

        return row;
    }
}
