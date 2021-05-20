package com.example.dietspy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ListAdapter;

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
    private static ArrayList<Pair<String, Pair<Integer, Integer>>> nutrients;
    private static ArrayList<String> nutrientNames;
    private boolean newFood = true;

    public AddFoodFragment() {
        this.foodId = dataStorage.getMaxId();
        this.nutrients = dataStorage.getFoodNutrients(foodId);
        this.nutrientNames = extractNames(this.nutrients);
    }

    public AddFoodFragment(int id, String name) {
        this.name = name;
        this.foodId = dataStorage.getFoodId(name);
        this.nutrients = dataStorage.getFoodNutrients(foodId);
        this.nutrientNames = extractNames(this.nutrients);
        this.newFood = false;
    }

    public static AddFoodFragment newInstance() {
        AddFoodFragment fragment = new AddFoodFragment();
        return fragment;
    }

    public static ArrayList<String> extractNames(ArrayList<Pair<String, Pair<Integer, Integer>>> nutrientList) {
        ArrayList<String> names = new ArrayList<String>();
        for (Pair<String, Pair<Integer, Integer>> i : nutrientList) {
            names.add(i.first);
        }
        return names;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_food, container, false);
        EditText foodField = view.findViewById(R.id.food_field2);

        foodField.setText("");

        if (this.name != null) {
            CharSequence text = this.name;
            foodField.setText(text);
        }

        Button add = view.findViewById(R.id.add_food_nutrient);
        ArrayList<Pair<String,Integer>> nutrientValuePairs = new ArrayList<Pair<String,Integer>>();

        Spinner nutrient_ingredient = view.findViewById(R.id.nutrient_ingredient);
        ArrayAdapter<CharSequence> items = ArrayAdapter.createFromResource(container.getContext(),
                R.array.food_spinner, android.R.layout.simple_spinner_dropdown_item);
        nutrient_ingredient.setAdapter(items);

        ListView nutrientList = view.findViewById(R.id.foodNutrients);
        ArrayAdapter<String> nutrientAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, nutrientNames);
        for (String n  : nutrientNames) {
            System.out.println(n);
        }
        nutrientList.setAdapter(nutrientAdapter);

        AddFoodFragment copy = this;
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Fragment fragment = new AddFoodNutrients(foodId, copy);
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(container.getId(), fragment);
                transaction.addToBackStack(null);
                transaction.commit();
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
                    if (!newFood) {
                        dataStorage.updateFood(foodId, foodName);
                    } else {
                        dataStorage.insertFood(foodId, foodName);
                    }

                    for (Pair<String, Pair<Integer, Integer>> n  : nutrients) {
                        System.out.println(n.first);
                        dataStorage.insertFoodNutrient(foodId, n.first, n.second.first, n.second.second);
                    }
                    getParentFragmentManager().popBackStack();
                }
            }
        });

        Button delete = view.findViewById(R.id.delete_button);

        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!newFood) {
                    System.out.println(newFood);
                    dataStorage.deleteFood(foodId);
                }
                foodField.setText("");
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

    public void updateFragment() {
        this.nutrientNames = extractNames(this.nutrients);
    }



    public boolean addNutrient(Pair<String, Pair<Integer, Integer>> nutrient) {
        for (Pair<String, Pair<Integer, Integer>> n : nutrients) {
            if (n.first.equals(nutrient.first)) {
                return false;
            }
        }
        this.nutrients.add(nutrient);
        return true;
    }

}