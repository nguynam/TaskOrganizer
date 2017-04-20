package com.example.namnguyen.taskorganizer;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import layout.TasksWidget;

import static android.R.style.Widget;

/**
 * Created by NamNguyen on 4/18/17.
 */

public class WidgetService implements RemoteViewsService.RemoteViewsFactory {
    private Context ctx;
    private Intent intent;
    public static String userId = "4S9lsCglG2cnoYfaFw1dcJHV2Fz1";
    private List<String> headers;
    private int appIds;
    private FirebaseDatabase database;
    private DatabaseReference userData;
    private DatabaseReference firebaseListWrapper;
    private ExpandableListWrapper listWrapper;
    private ValueEventListener firebaseListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            GenericTypeIndicator<ExpandableListWrapper> t = new GenericTypeIndicator<ExpandableListWrapper>() {};
            if(dataSnapshot.exists()){
                listWrapper = dataSnapshot.getValue(t);
            }
            headers = listWrapper.getHeaders();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public WidgetService(Context ctx, Intent intent){
        this.ctx = ctx;
        this.intent = intent;
        this.appIds = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        headers = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        userData = database.getReference("users");
        firebaseListWrapper = userData.child(userId).child("userTasks");
        firebaseListWrapper.addValueEventListener(firebaseListener);
        listWrapper = new ExpandableListWrapper();
    }

    @Override
    public void onDataSetChanged() {
        firebaseListWrapper = userData.child(userId).child("userTasks");
        firebaseListWrapper.addValueEventListener(firebaseListener);
    }

    @Override
    public void onDestroy() {
        headers.clear();
    }

    @Override
    public int getCount() {
        return headers.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(ctx.getPackageName(),
                R.layout.tasks_widget_row);

        row.setTextViewText(R.id.tasksWidgetRow, headers.get(position));
        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
