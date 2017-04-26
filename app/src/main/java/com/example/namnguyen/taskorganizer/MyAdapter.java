package com.example.namnguyen.taskorganizer;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ListAdapter;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import layout.TasksWidget;

/**
 * Created by NamNguyen on 3/28/17.
 */


public class MyAdapter extends BaseExpandableListAdapter implements RemoteViewsService.RemoteViewsFactory{
    private List<String> header;
    private HashMap<String, List<String>> child_items;
    private Context ctx;

    Typeface bold, regular;

    // Write a message to the database
    public static String userId;
    private boolean hasPulledData = false;
    private String tempNewToken = null;
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
            header = listWrapper.getHeaders();
            child_items = listWrapper.decodeChildren();
            MyAdapter.super.notifyDataSetChanged();
            if(!hasPulledData && tempNewToken != null){
                //After first pull send new token back to firebase (prevents overriding firebase with null values before pulling them).
                listWrapper.token = tempNewToken;
                tempNewToken = null;
                notifyDataSetChanged();
            }
            hasPulledData = true;
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    MyAdapter(Context ctx, List<String> header, HashMap<String, List<String>> child_items, String userId) {
        this.ctx = ctx;
        this.header = header;
        this.child_items = child_items;
        this.userId = userId;
        WidgetService.userId = userId;
        database = FirebaseDatabase.getInstance();

        userData = database.getReference("users");
        firebaseListWrapper = userData.child(userId).child("userTasks");
        firebaseListWrapper.addValueEventListener(firebaseListener);
        listWrapper = new ExpandableListWrapper();

        bold = Typeface.createFromAsset(ctx.getAssets(), "fonts/OpenSans-Regular.ttf");
        regular = Typeface.createFromAsset(ctx.getAssets(), "fonts/OpenSans-Light.ttf");
    }
    public void updateFCMToken(String token){
        tempNewToken = token;
        //Will be updated in firebase after first pull from firebase.

    }
    public void addHeader(String header, int position) {
        if (position == -1) {
            this.header.add(header);
        } else {
            List<String> temp = child_items.get(this.header.get(position));
            String temp2 = this.header.get(position);

            this.header.set(position, header);
            child_items.remove(temp2);
            child_items.put(header, temp);
        }
    }

    public String getHeader(int position) {
        return header.get(position);
    }

    public void addChild(String key, List<String> children) {
        this.child_items.put(key, children);
    }

    public void replaceChildItem(String key, String change, int position){
        List<String> temp = child_items.get(key);
        child_items.remove(key);

        temp.set(position, change);
        child_items.put(key, temp);
    }

    public String getChild(String header, int index) {
        List<String> temp = child_items.get(header);

        return temp.get(index);
    }

    public void removeHeader(int position) {
        header.remove(position);
    }

    public void removeChildren(int position) {
        child_items.remove(header.get(position));
    }

    @Override
    public void notifyDataSetChanged(){
        Intent intent = new Intent(ctx, TasksWidget.class);
        intent.setAction("UPDATE_LIST");
        int[] ids = {0};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        ctx.sendBroadcast(intent);

        listWrapper.setHeaders(header);
        listWrapper.encodeChildren(child_items);
        firebaseListWrapper.setValue(listWrapper);
        super.notifyDataSetChanged();
    }
    @Override
    public int getGroupCount() {
        return header.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return child_items.get(header.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return header.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return child_items.get(header.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        return null;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String header = (String) this.getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.parent_layout, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.header);
        textView.setTypeface(bold);
        textView.setText(header);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String title = (String) this.getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_layout, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.childItem);
        textView.setTypeface(regular);
        textView.setText(title);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
