package com.example.namnguyen.taskorganizer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private int year;
    private int month;
    private int day;
    private boolean datePicked = false;

    static final int DATE_DIALOG_ID = 999;

    private ExpandableListView expandableListView;
    final List<String> headings = new ArrayList<String>();
    List<String> childItems = new ArrayList<String>();
    HashMap<String, List<String>> childList = new HashMap<String, List<String>>();
    final MyAdapter myAdapter = new MyAdapter(this, headings, childList);
    String heading;
    String child1 = "Description: Buy Chicken, Ham, Bacon";
    String child2 = "Additional: Buy store brand";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setCurrentDateOnView();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        expandableListView = (ExpandableListView) findViewById(R.id.listView);
        childItems.add(child1);
        childItems.add(child2);
        expandableListView.setAdapter(myAdapter);
        expandableListView.setChildDivider(getResources().getDrawable(R.color.transparent));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                showDialog(DATE_DIALOG_ID);
//                heading = "Buy Food \n" + "Due: " + (month + 1) + "/" + day + "/" + year;
//                myAdapter.addHeader(heading);
//                myAdapter.addChild(heading, childItems);
//                myAdapter.notifyDataSetChanged();
            }
        });
    }

    // display current date
    public void setCurrentDateOnView() {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener,
                        year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            heading = "Buy Food \n" + "Due: " + (month + 1) + "/" + day + "/" + year;
            myAdapter.addHeader(heading);
            myAdapter.addChild(heading, childItems);
            myAdapter.notifyDataSetChanged();
        }
    };

}
