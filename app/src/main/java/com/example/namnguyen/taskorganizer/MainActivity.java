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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int deletePosition;
    private String task;
    private String taskDescription;
    private String date;
    private String time;

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    private ExpandableListView expandableListView;
    final List<String> headings = new ArrayList<>();
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

        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
                setCurrentDateOnView();

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.title_description_dialog);
                Window window = dialog.getWindow();
                window.setLayout(1000, 800);
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
                        datePickerDialog = new DatePickerDialog(MainActivity.this, datePickerListener, year, month, day);
                        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "CANCEL", dateCancelListener);
                        datePickerDialog.show();
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

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            timePickerDialog = new TimePickerDialog(MainActivity.this, timePickerListener, hour, minute, false);
            timePickerDialog.show();
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay) {
            if (hourOfDay > 12) {
                hour = hourOfDay - 12;
            } else {
                hour = hourOfDay;
            }
            minute = minuteOfDay;
            time = String.format(Locale.US, "%02d:%02d %s", hour, minute,
                    hourOfDay < 12 ? "AM" : "PM");
            date = "\n" + (month + 1) + "/" + day + "/" + year;

            heading = task + date + " at " + time;

            List<String> childItems = new ArrayList<>();
            childItems.add(child1);
            childItems.add(child2);

            myAdapter.addHeader(heading);
            myAdapter.addChild(heading, childItems);
            myAdapter.notifyDataSetChanged();
        }
    };

    private DialogInterface.OnClickListener dateCancelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            heading = task;

            List<String> childItems = new ArrayList<>();
            childItems.add(child1);
            childItems.add(child2);

            myAdapter.addHeader(heading);
            myAdapter.addChild(heading, childItems);
            myAdapter.notifyDataSetChanged();
        }
    };
}
