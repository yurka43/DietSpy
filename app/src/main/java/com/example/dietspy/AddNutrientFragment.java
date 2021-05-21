package com.example.dietspy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import static com.example.dietspy.MainActivity.dataStorage;

public class AddNutrientFragment extends Fragment {

    public AddNutrientFragment() {
    }


    public static AddNutrientFragment newInstance() {
        AddNutrientFragment fragment = new AddNutrientFragment();
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_nutrient, container, false);
        Spinner dropDown = view.findViewById(R.id.limit_target);

        ArrayAdapter<CharSequence> dropItems = ArrayAdapter.createFromResource(container.getContext(),
                R.array.limits, android.R.layout.simple_spinner_dropdown_item);

        dropDown.setAdapter(dropItems);

        Spinner units = view.findViewById(R.id.units);
        ArrayAdapter<CharSequence> unitNames = ArrayAdapter.createFromResource(container.getContext(),
                R.array.units, android.R.layout.simple_spinner_dropdown_item);

        units.setAdapter(unitNames);

        Button save = view.findViewById(R.id.save_nutrient_button);

        save.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String name = ((EditText) container.findViewById(R.id.nutrient_field)).getText().toString();
                if (name.equals("")) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Warning")
                            .setMessage("Do not leave empty name")
                            .setCancelable(true)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).show();
                    return;
                }
                int unitChoice = units.getSelectedItemPosition();
                int flag = dropDown.getSelectedItemPosition();
                String field_value = ((EditText) container.findViewById(R.id.target_field)).getText().toString();
                if (field_value.equals("")) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Warning")
                            .setMessage("Do not leave empty amount")
                            .setCancelable(true)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).show();
                    return;
                }
                double target_value = Double.parseDouble(field_value);
                dataStorage.insertNutrient(name, target_value, flag, unitChoice);

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