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
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.example.dietspy.MainActivity.dataStorage;

public class EditFoodNutrient extends Fragment {


    private static int foodId;
    private static AddFoodFragment parent;
    private static Pair<String, Pair<Integer, Integer>> nutrient;

    public EditFoodNutrient(int foodId, AddFoodFragment parent, Pair<String, Pair<Integer, Integer>> nutrient) {
        this.foodId = foodId;
        this.parent = parent;
        this.nutrient = nutrient;
    }


    public static EditFoodNutrient newInstance() {
        EditFoodNutrient fragment = new EditFoodNutrient(foodId, parent, nutrient);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_food_nutrient, container, false);

        TextView nutrientNameTag = view.findViewById(R.id.edited_nutrient);
        nutrientNameTag.setText(nutrient.first);

        EditText amountField = view.findViewById(R.id.amount_field2);
        amountField.setText("" + nutrient.second.first);


        ArrayAdapter<CharSequence> unitNames = ArrayAdapter.createFromResource(container.getContext(),
                R.array.units, android.R.layout.simple_spinner_dropdown_item);

        Spinner units = view.findViewById(R.id.units4);
        units.setAdapter(unitNames);
        units.setSelection(nutrient.second.second);

        Button save = view.findViewById(R.id.save_nutrient_changes);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (amountField.getText().toString().equals("")) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Warning")
                            .setMessage("Do not leave fields empty")
                            .setCancelable(true)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).show();
                } else {
                    int amount = Integer.parseInt(amountField.getText().toString());
                    int unit = units.getSelectedItemPosition();
                    System.out.println("Deleted1 " + parent.deleteNutrient(nutrient));
                    Pair<Integer, Integer> newAmountUnit = new Pair<Integer, Integer>(amount, unit);
                    nutrient = new Pair<String, Pair<Integer, Integer>>(nutrient.first, newAmountUnit);
                    parent.addNutrient(nutrient);
                    getParentFragmentManager().popBackStack();
                }

            }
        });

        Button delete = view.findViewById(R.id.delete_nutrient_button);
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                parent.deleteNutrient(nutrient);
                dataStorage.deleteFoodNutrient(foodId, nutrient.first);
                getParentFragmentManager().popBackStack();
            }
        });



        return view;
    }

}