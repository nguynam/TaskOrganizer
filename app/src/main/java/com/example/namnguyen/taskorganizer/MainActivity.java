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
    private int currentYear;
    private int currentMonth;
    private int currentDay;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    private int hour;
    private int minute;
    private int deletePosition;
    private int reminder;
    private String task;
    private String taskDescription;
    private String date;
    private String time;
    private String address;

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    Dialog timeSpanDialog;


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

        //Creates alert dialog for deleting a task
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

        //Delete task on long press
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

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, final int childPosition, long id) {
                if(childPosition == 0){
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.title_description_dialog);
                    Window window = dialog.getWindow();
                    window.setLayout(1000, 800);
                    dialog.show();

                    final EditText title = (EditText) dialog.findViewById(R.id.taskTitle);
                    final EditText description = (EditText) dialog.findViewById(R.id.description);
                    Button okButton = (Button) dialog.findViewById(R.id.ok);
                    Button cancelButton = (Button) dialog.findViewById(R.id.cancel);

                    String originalHeader = myAdapter.getHeader(groupPosition);
                    String originalDescription = myAdapter.getChild(originalHeader, childPosition);

                    title.setText(originalHeader);
                    description.setText(originalDescription);

                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Retrieves text from dialog
                            String task = title.getText().toString();
                            String taskDescription = description.getText().toString();
                            myAdapter.addHeader(task, groupPosition);
                            myAdapter.replaceChildItem(task, taskDescription, childPosition);
                            myAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });

                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                }
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get current date
                setCurrentDate();

                //Creates title/description dialog
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
                        //Retrieves text from dialog
                        task = title.getText().toString();
                        taskDescription = description.getText().toString();
                        child1 = "Description: \n" + taskDescription;
                        dialog.dismiss();

                        //Instantiates datepicker and shows it
                        datePickerDialog = new DatePickerDialog(MainActivity.this, datePickerListener, currentYear, currentMonth, currentDay);
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

    public void setCurrentDate() {
        Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);
        currentMonth = c.get(Calendar.MONTH);
        currentDay = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
    }

    //Retrieve date information when "OK" button is pressed
    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int Year,
                              int Month, int Day) {
            selectedYear = Year;
            selectedMonth = Month;
            selectedDay = Day;

            timePickerDialog = new TimePickerDialog(MainActivity.this, timePickerListener, hour, minute, false);
            timePickerDialog.show();
        }
    };

    //Set time
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

            //Create TimeSpan selection dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Set Reminder");
            builder.setCancelable(true);
            builder.setPositiveButton("Okay", timeSpanListener);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    timeSpanDialog.dismiss();
                }
            });
            builder.setSingleChoiceItems(R.array.time_spans, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String selected = getResources().getStringArray(R.array.time_spans)[i];
                }
            });
            timeSpanDialog = builder.create();
            timePickerDialog.dismiss();
            timeSpanDialog.show();

        }
    };
    private DialogInterface.OnClickListener timeSpanListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if(currentDay == selectedDay && currentMonth == selectedMonth){
                date = "\nToday";
            }
            else if(selectedDay == currentDay+1 && currentMonth == selectedMonth || (currentDay == 30 || currentDay == 31) && selectedDay == 1){
                date = "\nTomorrow";
            }
            else{
                date = "\n" + (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
            }

            heading = task + date + " at " + time;

            List<String> childItems = new ArrayList<>();
            childItems.add(child1);
            childItems.add(child2);

            myAdapter.addHeader(heading, -1);
            myAdapter.addChild(heading, childItems);
            myAdapter.notifyDataSetChanged();;
        }
    };
    private DialogInterface.OnClickListener dateCancelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            heading = task;

            //Adds all information to array and pass it to the adapter
            List<String> childItems = new ArrayList<>();
            childItems.add(child1);
            childItems.add(child2);

            myAdapter.addHeader(heading, -1);
            myAdapter.addChild(heading, childItems);
            myAdapter.notifyDataSetChanged();
        }
    };
}
