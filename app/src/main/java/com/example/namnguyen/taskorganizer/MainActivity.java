package com.example.namnguyen.taskorganizer;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

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
    private boolean changingData = false;

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    Dialog timeSpanDialog;
    Dialog googleDialog;

    private ExpandableListView expandableListView;
    final List<String> headings = new ArrayList<>();
    HashMap<String, List<String>> childList = new HashMap<>();

    final List<String> thisHeadings = new ArrayList<>();
    HashMap<String, List<String>> thisChildList = new HashMap<>();

    final MyAdapter myAdapter = new MyAdapter(this, headings, childList);
    String heading;
    String child1;
    String child2 = "\nAdd Location?";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Place pl = PlaceAutocomplete.getPlace(this, data);
                address = "\nAddress:" + "\n" + pl.getAddress().toString();
                if(changingData){
                    myAdapter.replaceChildItem(task, address, 1);
                    myAdapter.notifyDataSetChanged();
                }
                else{
                    List<String> childItems = new ArrayList<>();
                    childItems.add(child1);
                    childItems.add(address);

                    myAdapter.addHeader(heading, -1);
                    myAdapter.addChild(heading, childItems);
                    myAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        expandableListView = (ExpandableListView) findViewById(R.id.listView);
        expandableListView.setAdapter(myAdapter);
        expandableListView.setChildDivider(getResources().getDrawable(R.color.transparent));

        GoogleApiClient apiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

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

        //Edit child items
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, final int childPosition, long id) {
                Snackbar.make(v, "Position: " + childPosition, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if (childPosition == 0) {
                    //Show title/description dialog
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.title_description_dialog);
                    Window window = dialog.getWindow();
                    window.setLayout(1000, 800);
                    dialog.show();

                    final EditText title = (EditText) dialog.findViewById(R.id.taskTitle);
                    final EditText description = (EditText) dialog.findViewById(R.id.description);
                    Button okButton = (Button) dialog.findViewById(R.id.ok);
                    Button cancelButton = (Button) dialog.findViewById(R.id.cancel);

                    //Retrieve original header and description
                    String originalHeader = myAdapter.getHeader(groupPosition);
                    String originalDescription = myAdapter.getChild(originalHeader, childPosition);
                    final String headerSplit[] = originalHeader.split("\n");
                    final String descriptionSplit[] = originalDescription.split("\n");

                    title.setText(headerSplit[0]);
                    description.setText(descriptionSplit[1]);

                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Retrieves text from dialog and update lists
                            String task = title.getText().toString() + "\n" + headerSplit[1];
                            String taskDescription = descriptionSplit[0] + "\n" + description.getText().toString();
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
                if (childPosition == 1) {
                    changingData = true;
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                        .build(MainActivity.this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                        task = myAdapter.getHeader(groupPosition);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
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
                        List<String> temp = new ArrayList<String>();
                        task = title.getText().toString();
                        thisHeadings.add(task);
                        taskDescription = description.getText().toString();
                        temp.add(taskDescription);
                        thisChildList.put(task, temp);

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
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
            if (currentDay == selectedDay && currentMonth == selectedMonth) {
                date = "\nToday";
            } else if (selectedDay == currentDay + 1 && currentMonth == selectedMonth || (currentDay == 30 || currentDay == 31) && selectedDay == 1) {
                date = "\nTomorrow";
            } else {
                date = "\n" + (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
            }

            heading = task + date + " at " + time;
            changingData = false;

            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("Add a location for this task?");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                Intent intent =
                                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                                .build(MainActivity.this);
                                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                            } catch (GooglePlayServicesRepairableException e) {
                                e.printStackTrace();
                            } catch (GooglePlayServicesNotAvailableException e) {
                                e.printStackTrace();
                            }
                            googleDialog.dismiss();
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            List<String> childItems = new ArrayList<>();
                            childItems.add(child1);
                            childItems.add(child2);

                            myAdapter.addHeader(heading, -1);
                            myAdapter.addChild(heading, childItems);
                            myAdapter.notifyDataSetChanged();
                            googleDialog.dismiss();
                        }
                    });
            googleDialog = builder1.create();
            googleDialog.show();
            scheduleNotification("Reminder: " + heading, heading + " is coming up soon", 500);
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

    private void scheduleNotification(String title, String message, int delay) {
        //Build notification
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setDefaults(Notification.DEFAULT_ALL);
        Notification notification = builder.build();

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        //TODO Research id relevance
        notificationIntent.putExtra("notification-id", 1);
        notificationIntent.putExtra("notification", notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.contentIntent = pendingIntent;
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        return;
    }
}
