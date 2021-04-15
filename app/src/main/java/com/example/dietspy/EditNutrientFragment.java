package com.example.dietspy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import static com.example.dietspy.MainActivity.dataStorage;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditNutrientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditNutrientFragment extends Fragment {

    private String oldName;
    private int oldUnit, oldFlag, oldTarget;
    public EditNutrientFragment(String name, int unit, int flag, int target_value) {
        this.oldName = name;
        this.oldUnit = unit;
        this.oldFlag = flag;
        this.oldTarget = target_value;
    }

    public static EditNutrientFragment newInstance(String name, int unit, int flag, int target_value) {
        EditNutrientFragment fragment = new EditNutrientFragment(name, unit, flag, target_value);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_nutrient, container, false);

        Spinner dropDown = view.findViewById(R.id.limit_edit_target);

        ArrayAdapter<CharSequence> dropItems = ArrayAdapter.createFromResource(container.getContext(),
                R.array.limits, android.R.layout.simple_spinner_dropdown_item);

        dropDown.setAdapter(dropItems);
        dropDown.setSelection(oldFlag);

        Spinner units = view.findViewById(R.id.edit_units);
        ArrayAdapter<CharSequence> unitNames = ArrayAdapter.createFromResource(container.getContext(),
                R.array.units, android.R.layout.simple_spinner_dropdown_item);

        units.setAdapter(unitNames);
        units.setSelection(oldUnit);

        EditText nutrientName = view.findViewById(R.id.nutrient_edit_field);
        nutrientName.setText(oldName);

        EditText nutrientTarget = view.findViewById(R.id.target_edit_field);
        nutrientTarget.setText((new Integer(oldTarget)).toString());

        Button okButton = view.findViewById(R.id.ok_nutrient_button);

        okButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String newName = ((EditText) container.findViewById(R.id.nutrient_edit_field)).getText().toString();
                int unitChoice = units.getSelectedItemPosition();
                int flag = dropDown.getSelectedItemPosition();
                int target_value = Integer.parseInt(((EditText) container.findViewById(R.id.target_edit_field)).getText().toString());
                dataStorage.updateNutrient(oldName, newName, target_value, flag, unitChoice);

                Fragment fragment = new MainFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.controller, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        Button deleteButton = view.findViewById(R.id.delete_nutrient_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dataStorage.deleteNutrient(oldName);

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