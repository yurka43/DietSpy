package com.example.dietspy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.example.dietspy.MainActivity.dataStorage;

public class AddFoodFragment extends Fragment {

    public AddFoodFragment() {
    }


    public static AddFoodFragment newInstance() {
        AddFoodFragment fragment = new AddFoodFragment();
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_food, container, false);
        ArrayList<String> nutrientNames = dataStorage.getNutrientNames();
        AutoCompleteTextView autoCompleteInput = view.findViewById(R.id.autoEnterNutrients);
        ArrayAdapter<String> autoAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line,
                nutrientNames);
        autoCompleteInput.setAdapter(autoAdapter);

        Button add = view.findViewById(R.id.add_food_nutrient);
        ArrayList<Pair<String,Integer>> nutrientValuePairs = new ArrayList<Pair<String,Integer>>();
        ListView foodNutrients = view.findViewById(R.id.foodNutrients);


        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String nutrient = autoCompleteInput.getText().toString();
                if (!nutrientNames.contains(nutrient)) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Warning")
                            .setMessage("Before you use this nutrient, please add it in the nutrient tab")
                            .setCancelable(true)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).show();
                } else {

                }
            }
        });
        return view;
    }

    public boolean isNutrientAdded(ArrayList<Pair<String,Integer>> nutrientValuePairs, String name) {
        for (Pair<String,Integer> p : nutrientValuePairs) {
            if (p.first.equals(name)) {
                return true;
            }
        }
        return false;
    }
}