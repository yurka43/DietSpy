package com.example.dietspy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.dietspy.MainActivity.dataStorage;

public class RecordFragment extends Fragment {

    private static ArrayList<Record> records;
    private static String date;
    public RecordFragment() {
    }

    public static RecordFragment newInstance() {
        RecordFragment fragment = new RecordFragment();
        return fragment;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        ListView recordList = view.findViewById(R.id.recordList2);

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String dateTime = df.format(Calendar.getInstance().getTime());
        date = dateTime.substring(0, 16);
        records = dataStorage.getAllRecords(date);
        ArrayList<String> recordNames = new ArrayList<String>();

        RecordAdapter rec_adapter = new RecordAdapter(getContext(), records);
        recordList.setAdapter(rec_adapter);

        ArrayList<String> foodNames = dataStorage.getFoodNames();
        ArrayAdapter<String> foods = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line,
                foodNames);
        AutoCompleteTextView autoFoods = view.findViewById(R.id.autoEnterFoods);
        autoFoods.setAdapter(foods);

        Button rec_button = view.findViewById(R.id.add_record);

        RecordFragment copy = this;
        rec_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String input = autoFoods.getText().toString();
                if (!foodNames.contains(input)) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Warning")
                            .setMessage("Before you record this food, add it in the food tab")
                            .setCancelable(true)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).show();
                } else {
                    dataStorage.insertProgress(dataStorage.getFoodId(input));
                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                    String dateTime = df.format(Calendar.getInstance().getTime());
                    date = dateTime.substring(0, 16);
                    records = dataStorage.getAllRecords(date);
                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.detach(copy).attach(copy).commit();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    autoFoods.setText("");
                }
            }
        });



        return view;
    }
}