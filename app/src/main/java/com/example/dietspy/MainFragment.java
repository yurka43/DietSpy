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

public class MainFragment extends Fragment {


    ListView nutrients;
    ArrayList<Nutrient> nutrientList;


    public MainFragment() {
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
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        nutrients = view.findViewById(R.id.nutrientList);
        nutrientList = dataStorage.getAllNutrients();


        if (!nutrientList.isEmpty()) {
            NutrientAdapter nutrientAdapter = new NutrientAdapter(getActivity(), nutrientList);
            nutrients.setAdapter(nutrientAdapter);


            nutrients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Nutrient selectedNutrient = nutrientList.get(position);
                    Fragment fragment = new EditNutrientFragment(selectedNutrient.getName(), selectedNutrient.getUnitInt(),
                            selectedNutrient.getFlag(), selectedNutrient.getTarget());
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.controller, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }

        Button add = view.findViewById(R.id.add_nutrient);
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment = new AddNutrientFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.controller, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    
}