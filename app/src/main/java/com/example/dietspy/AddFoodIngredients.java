package com.example.dietspy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import static com.example.dietspy.MainActivity.dataStorage;

public class AddFoodIngredients extends Fragment {


    private static int foodId;
    private static AddFoodFragment parent;

    public AddFoodIngredients(int foodId, AddFoodFragment parent) {
        this.foodId = foodId;
        this.parent = parent;
    }


    public static AddFoodIngredients newInstance() {
        AddFoodIngredients fragment = new AddFoodIngredients(foodId, parent);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_food_ingredients, container, false);

        ArrayList<String> ingredientNames = dataStorage.getIngredientNames();

        AutoCompleteTextView autoCompleteInput = view.findViewById(R.id.autoEnterIngredient);
        ArrayAdapter<String> autoAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line,
                ingredientNames);
        autoCompleteInput.setAdapter(autoAdapter);

        ArrayAdapter<CharSequence> unitNames = ArrayAdapter.createFromResource(container.getContext(),
                R.array.i_units, android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = view.findViewById(R.id.units3);
        spinner.setAdapter(unitNames);

        Button save = view.findViewById(R.id.save_ingredient_button);
        Button back = view.findViewById(R.id.back_button3);
        EditText amount_field = view.findViewById(R.id.amount_field3);

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String i_name = autoCompleteInput.getText().toString().trim();
                if (i_name.equals("")) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Warning")
                            .setMessage("Please enter the name of the ingredient to add")
                            .setCancelable(true)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).show();
                } else {
                    double amount = Double.parseDouble(amount_field.getText().toString());
                    int unit = spinner.getSelectedItemPosition();
                    Pair<String, Pair<Double, Integer>> ingredientPair =
                            new Pair<String, Pair<Double, Integer>>(i_name,
                                    new Pair<Double, Integer>(amount, unit));
                    int check_unit = dataStorage.ingredientUnit(i_name);
                    if (check_unit != -1 && check_unit != unit) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Warning")
                                .setMessage("This ingredient uses " + unitNames.getItem(check_unit) + " as units")
                                .setCancelable(true)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                }).show();
                        return;
                    }
                    if (!parent.addIngredient(ingredientPair)) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Warning")
                                .setMessage("This ingredient is already added. Click on it to edit it!")
                                .setCancelable(true)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                }).show();
                    } else {
                        getParentFragmentManager().popBackStack();
                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

}