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

    private static String name;
    private static int foodId;

    public AddFoodFragment() {
        this.foodId = dataStorage.insertFood();
    }

    public AddFoodFragment(int id) {
        this.foodId = id;
        this.name = dataStorage.getFoodName(id);
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
        EditText foodField = view.findViewById(R.id.food_field);

        if (this.name != null) {
            CharSequence text = this.name;
            foodField.setText(text);
        }

        Button add = view.findViewById(R.id.add_food_nutrient);
        ArrayList<Pair<String,Integer>> nutrientValuePairs = new ArrayList<Pair<String,Integer>>();
        ListView foodNutrients = view.findViewById(R.id.foodNutrients);

        Spinner nutrient_ingredient = view.findViewById(R.id.nutrient_ingredient);
        ArrayAdapter<CharSequence> items = ArrayAdapter.createFromResource(container.getContext(),
                R.array.food_spinner, android.R.layout.simple_spinner_dropdown_item);
        nutrient_ingredient.setAdapter(items);

        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });

        Button save = view.findViewById(R.id.save_food_button);


        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String foodName = foodField.getEditableText().toString();
                if (foodName.equals("")) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Warning")
                            .setMessage("Please enter food name, before saving it")
                            .setCancelable(true)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).show();
                } else {
                    dataStorage.updateFood(foodId, foodName);

                    Fragment fragment = new MainFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.controller, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

        Button delete = view.findViewById(R.id.delete_button);

        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dataStorage.deleteFood(foodId);

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