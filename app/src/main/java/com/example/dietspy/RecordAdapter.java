package com.example.dietspy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class RecordAdapter extends ArrayAdapter<Record> {

    private Context context;
    private ArrayList<Record> records;

    public RecordAdapter (Context c, ArrayList<Record> records) {
        super(c, R.layout.record_row, R.id.dateText, records);
        this.context = c;
        this.records = records;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.record_row, parent, false);
        }
        TextView row_title = convertView.findViewById(R.id.record_food_name);
        TextView dateText = convertView.findViewById(R.id.dateText);

        Record curRecord = records.get(position);
       // System.out.println("This " + position);

        row_title.setText(curRecord.foodName());
        dateText.setText(curRecord.getDate());
        return convertView;
    }
}

