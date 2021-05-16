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

public class AddFoodNutrients extends Fragment {


    private static int foodId;

    public AddFoodNutrients(int foodId) {
        this.foodId = foodId;
    }


    public static AddFoodNutrients newInstance() {
        AddFoodNutrients fragment = new AddFoodNutrients(foodId);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_food_nutrients, container, false);

        ArrayList<String> nutrientNames = dataStorage.getNutrientNames();

        AutoCompleteTextView autoCompleteInput = view.findViewById(R.id.autoEnterNutrients);
        ArrayAdapter<String> autoAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line,
                nutrientNames);
        autoCompleteInput.setAdapter(autoAdapter);

        ArrayAdapter<CharSequence> unitNames = ArrayAdapter.createFromResource(container.getContext(),
                R.array.units, android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = view.findViewById(R.id.units2);
        spinner.setAdapter(unitNames);

        Button save = view.findViewById(R.id.save_nutrient_button2);
        Button back = view.findViewById(R.id.back_button);

        save.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String nutrientName = autoCompleteInput.getText().toString();
                if (!nutrientNames.contains(nutrientName)) {
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
                    int unitChoice = spinner.getSelectedItemPosition();
                    int amount = Integer.parseInt(((EditText) container.findViewById(R.id.amount_field)).getText().toString());
                    dataStorage.insertFoodNutrient(foodId, nutrientName, amount, unitChoice);

                    Fragment fragment = new MainFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.controller, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Fragment fragment = new MainFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.controller, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

}