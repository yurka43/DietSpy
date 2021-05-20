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

public class EditFoodIngredient extends Fragment {


    private static int foodId;
    private static AddFoodFragment parent;
    private static Pair<String, Pair<Double, Integer>> ingredient;

    public EditFoodIngredient(int foodId, AddFoodFragment parent, Pair<String, Pair<Double, Integer>> ingredient) {
        this.foodId = foodId;
        this.parent = parent;
        this.ingredient = ingredient;
    }


    public static EditFoodIngredient newInstance() {
        EditFoodIngredient fragment = new EditFoodIngredient(foodId, parent, ingredient);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_food_ingredient, container, false);

        TextView nutrientNameTag = view.findViewById(R.id.edited_ingredient);
        nutrientNameTag.setText(ingredient.first);

        EditText amountField = view.findViewById(R.id.amount_field4);
        amountField.setText("" + ingredient.second.first);


        ArrayAdapter<CharSequence> unitNames = ArrayAdapter.createFromResource(container.getContext(),
                R.array.i_units, android.R.layout.simple_spinner_dropdown_item);

        Spinner units = view.findViewById(R.id.units5);
        units.setAdapter(unitNames);
        units.setSelection(ingredient.second.second);

        Button save = view.findViewById(R.id.save_ingredient_changes);
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
                    double amount = Double.parseDouble(amountField.getText().toString());
                    int unit = units.getSelectedItemPosition();
                    System.out.println("Delete" + parent.deleteIngredient(ingredient));
                    Pair<Double, Integer> newAmountUnit = new Pair<Double, Integer>(amount, unit);
                    ingredient = new Pair<String, Pair<Double, Integer>>(ingredient.first, newAmountUnit);
                    parent.addIngredient(ingredient);
                    getParentFragmentManager().popBackStack();
                }

            }
        });

        Button delete = view.findViewById(R.id.delete_ingredient_button);
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                parent.deleteIngredient(ingredient);
                dataStorage.deleteFoodIngredient(foodId, ingredient.first);
                getParentFragmentManager().popBackStack();
            }
        });



        return view;
    }

}