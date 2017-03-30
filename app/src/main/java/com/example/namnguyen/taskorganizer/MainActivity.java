package com.example.namnguyen.taskorganizer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int deletePosition;
    private String AM_PM;
    private String task;
    private String taskDescription;

    static final int DATE_DIALOG_ID = 999;
    static final int TIME_DIALOG_ID = 0;

    private ExpandableListView expandableListView;
    final List<String> headings = new ArrayList<>();
//    List<String> childItems = new ArrayList<>();
    HashMap<String, List<String>> childList = new HashMap<>();
    final MyAdapter myAdapter = new MyAdapter(this, headings, childList);
    String heading;
    String child1;
    String child2 = "\nAdd Location?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        expandableListView = (ExpandableListView) findViewById(R.id.listView);
        expandableListView.setAdapter(myAdapter);
        expandableListView.setChildDivider(getResources().getDrawable(R.color.transparent));

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Delete this task?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        myAdapter.removeChildren(deletePosition);
                        myAdapter.removeHeader(deletePosition);
                        myAdapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert11 = builder1.create();

        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                alert11.show();
                deletePosition = position;
                Snackbar.make(view, "Position: " + position, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                setCurrentDateOnView();
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.title_description_dialog);
                dialog.show();

                final EditText title = (EditText) dialog.findViewById(R.id.taskTitle);
                final EditText description = (EditText) dialog.findViewById(R.id.description);
                Button okButton = (Button) dialog.findViewById(R.id.ok);
                Button cancelButton = (Button) dialog.findViewById(R.id.cancel);

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        task = title.getText().toString();
                        taskDescription = description.getText().toString();
                        child1 = "Description: " + taskDescription;
                        dialog.dismiss();
                        showDialog(DATE_DIALOG_ID);
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
            }
        });
    }

    public void setCurrentDateOnView() {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener,
                        year, month, day);
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, timePickerListener, hour, minute, false);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            showDialog(TIME_DIALOG_ID);
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay) {
            if (hourOfDay > 12) {
                hour = hourOfDay - 12;
                AM_PM = "PM";
            } else {
                hour = hourOfDay;
                AM_PM = "AM";
            }
            minute = minuteOfDay;
            String time = String.format("%02d:%02d %s", hour, minute,
                    hourOfDay < 12 ? "AM" : "PM");
            heading = task + "\n" + "Due: " + (month + 1) + "/" + day + "/" + year + " at " + time;
            List<String> childItems = new ArrayList<>();
            childItems.add(child1);
            childItems.add(child2);

            myAdapter.addHeader(heading);
            myAdapter.addChild(heading, childItems);
            myAdapter.notifyDataSetChanged();
        }
    };
}
