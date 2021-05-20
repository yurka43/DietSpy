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
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.example.dietspy.MainActivity.dataStorage;

public class AddFoodFragment extends Fragment {

    private static String name;
    private static int foodId;
    private static ArrayList<Pair<String, Pair<Integer, Integer>>> nutrients;
    private static ArrayList<Pair<String, Pair<Double, Integer>>> ingredients;
    private static ArrayList<String> nutrientNames;
    private static ArrayList<String> ingredientNames;
    private boolean newFood = true;

    public AddFoodFragment() {
        this.foodId = dataStorage.getMaxId();
        this.nutrients = dataStorage.getFoodNutrients(foodId);
        this.ingredients = dataStorage.getFoodIngredients(foodId);
        this.nutrientNames = extractNames(this.nutrients);
        this.ingredientNames = extractINames(this.ingredients);
    }

    public AddFoodFragment(int id, String name) {
        this.name = name;
        this.foodId = dataStorage.getFoodId(name);
        this.nutrients = dataStorage.getFoodNutrients(foodId);
        this.ingredients = dataStorage.getFoodIngredients(foodId);
        this.nutrientNames = extractNames(this.nutrients);
        this.ingredientNames = extractINames(this.ingredients);
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
    public static ArrayList<String> extractINames(ArrayList<Pair<String, Pair<Double, Integer>>> nutrientList) {
        ArrayList<String> names = new ArrayList<String>();
        for (Pair<String, Pair<Double, Integer>> i : nutrientList) {
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

        if (!newFood) {
            CharSequence text = this.name;
            foodField.setText(text);
        }

        Button add = view.findViewById(R.id.add_food_nutrient);
        ArrayList<Pair<String,Integer>> nutrientValuePairs = new ArrayList<Pair<String,Integer>>();

        Spinner nutrient_ingredient = view.findViewById(R.id.nutrient_ingredient);

        ArrayAdapter<CharSequence> items = ArrayAdapter.createFromResource(container.getContext(),
                R.array.food_spinner, android.R.layout.simple_spinner_dropdown_item);
        nutrient_ingredient.setAdapter(items);

        ListView nutrientIngredientList = view.findViewById(R.id.foodNutrients);
        ArrayAdapter<String> nutrientAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, nutrientNames);
        ArrayAdapter<String> ingredientAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ingredientNames);

        nutrientIngredientList.setAdapter(nutrientAdapter);

        nutrient_ingredient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    nutrientIngredientList.setAdapter(nutrientAdapter);
                } else if (i == 1) {
                    nutrientIngredientList.setAdapter(ingredientAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        AddFoodFragment copy = this;

        nutrientIngredientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Fragment fragment;
                if (nutrient_ingredient.getSelectedItemPosition() == 1) {
                    fragment = new EditFoodIngredient(foodId, copy, ingredients.get(i));
                } else {
                    fragment = new EditFoodNutrient(foodId, copy, nutrients.get(i));
                }

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(container.getId(), fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Fragment fragment;
                if (nutrient_ingredient.getSelectedItemPosition() == 1) {
                    fragment = new AddFoodIngredients(foodId, copy);
                } else {
                    fragment = new AddFoodNutrients(foodId, copy);
                }

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
                        dataStorage.insertFoodNutrient(foodId, n.first, n.second.first, n.second.second);
                    }

                    for (Pair<String, Pair<Double, Integer>> i  : ingredients) {
                        dataStorage.insertIngredient(i.first, i.second.second);
                        dataStorage.insertFoodIngredient(foodId, i.first, i.second.first);
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


    public boolean addNutrient(Pair<String, Pair<Integer, Integer>> nutrient) {
        for (Pair<String, Pair<Integer, Integer>> n : nutrients) {
            if (n.first.equals(nutrient.first)) {
                return false;
            }
        }
        this.nutrients.add(nutrient);
        this.nutrientNames = extractNames(this.nutrients);
        return true;
    }

    public boolean deleteNutrient(Pair<String, Pair<Integer, Integer>> nutrient) {
        boolean deleted = nutrients.remove(nutrient);
        this.nutrientNames = extractNames(this.nutrients);
        return deleted;
    }

    public boolean addIngredient(Pair<String, Pair<Double, Integer>> ingredient) {
        for (Pair<String, Pair<Double, Integer>> n : ingredients) {
            if (n.first.equals(ingredient.first)) {
                return false;
            }
        }
        this.ingredients.add(ingredient);
        this.ingredientNames = extractINames(this.ingredients);
        return true;
    }

    public boolean deleteIngredient(Pair<String, Pair<Double, Integer>> ingredient) {
        boolean deleted = false;
        for (Pair<String, Pair<Double, Integer>> in : ingredients) {
            if (in.first.equals(ingredient.first)) {
                ingredients.remove(in);
                deleted = true;
                break;
            }
        }

        this.ingredientNames = extractINames(this.ingredients);
        return deleted;
    }

}