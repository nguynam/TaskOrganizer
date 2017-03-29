package com.example.namnguyen.taskorganizer;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        expandableListView = (ExpandableListView) findViewById(R.id.listView);
        List<String> headings = new ArrayList<String>();
        List<String> childItems = new ArrayList<String>();
        HashMap<String, List<String>> childList = new HashMap<String, List<String>>();
        String heading = "Buy Food \n" + "Due:3/27/2017";
        String child = "Description: Buy Chicken, Ham, Bacon";
        headings.add(heading);
        childItems.add(child);
        childList.put(headings.get(0),childItems);
        MyAdapter myAdapter = new MyAdapter(this, headings, childList);
        expandableListView.setAdapter(myAdapter);
        expandableListView.setChildDivider(getResources().getDrawable(R.color.transparent));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
