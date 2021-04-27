package com.example.dietspy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.dietspy.MainActivity.dataStorage;

public class FoodFragment extends Fragment {


    ListView foods;
    ArrayList<String> foodList;


    public FoodFragment() {
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food, container, false);
        foods = view.findViewById(R.id.foodList);
        foodList = dataStorage.getFoodNames();


        if (!foodList.isEmpty()) {
            ArrayAdapter<String> foodAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, foodList);
            foods.setAdapter(foodAdapter);


            foods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   /* Nutrient selectedFood = foodList.get(position);
                    Fragment fragment = new EditNutrientFragment(selectedFood.getName(), selectedFood.getUnitInt(),
                            selectedFood.getFlag(), selectedFood.getTarget());
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.controller, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit(); */
                    System.out.println("to do");
                }
            });
        }

        Button add = view.findViewById(R.id.add_food);
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment = new AddFoodFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.controller, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }
}