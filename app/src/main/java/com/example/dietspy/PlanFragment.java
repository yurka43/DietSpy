package com.example.dietspy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.dietspy.MainActivity.dataStorage;


public class PlanFragment extends Fragment {


    private ArrayList<String> foodNames;
    private ArrayList<Pair<String, Pair<Double, Integer>>> plans;

    public PlanFragment() {

    }

    public static PlanFragment newInstance() {
        PlanFragment fragment = new PlanFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_plan, container, false);

        AutoCompleteTextView foodField = view.findViewById(R.id.autoEnterFoods3);
        foodNames = dataStorage.getFoodNames();
        ArrayAdapter<String> foods = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line,
                foodNames);
        foodField.setAdapter(foods);

        ListView planList = view.findViewById(R.id.planList);
        plans = dataStorage.getPlans();
        PlanAdapter adapter = new PlanAdapter(getContext(), plans);
        planList.setAdapter(adapter);

        Button add = view.findViewById(R.id.add_ftp);

        PlanFragment copy = this;
        add.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String input = foodField.getText().toString();
                if (!foodNames.contains(input)) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Warning")
                            .setMessage("Before you plan this food, add it in the food tab")
                            .setCancelable(true)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).show();
                } else {
                    dataStorage.addPlanIngredientForFood(input);
                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.detach(copy).attach(copy).commit();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    foodField.setText("");
                }
            }
        });


        if(!plans.isEmpty()) {
            planList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Confirmation");
                    alert.setMessage("Bought " + plans.get(i).first + " or already have it?");

                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dataStorage.deletePlan(plans.get(i).first);
                            plans = dataStorage.getPlans();
                            PlanAdapter adapter = new PlanAdapter(getContext(), plans);
                            planList.setAdapter(adapter);
                            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                            ft.detach(copy).attach(copy).commit();
                            dialog.dismiss();
                        }
                    });
                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
            });
        }

        return view;
    }
}