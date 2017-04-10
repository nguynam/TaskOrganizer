package com.example.namnguyen.taskorganizer;

import android.util.Base64;
import android.util.Xml;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Josh on 4/8/17.
 */

public class ExpandableListWrapper {
    public List<String> headers;
    //private HashMap<String,List<String>> children;
    public String children;
    public void setHeaders(List<String> headers){
        this.headers = headers;
    }
    public List<String> getHeaders(){return headers;}
    public void encodeChildren(HashMap<String, List<String>> children) {
        /*for (String key: children.keySet()){
            String temp = Base64.encodeToString(key.getBytes(),Base64.URL_SAFE);
            key = temp;
        }
        this.children = children;*/
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = null;
            so = new ObjectOutputStream(bo);
            so.writeObject(children);
            so.close();
            String serializedObject = bo.toString();
            //String encoded = Base64.encodeToString(serializedObject.getBytes(),Base64.URL_SAFE);
            this.children = serializedObject;
        }catch (Exception e) {
            System.out.println(e);
        }
    }
    public HashMap<String,List<String>> decodeChildren(){
        try {
            //byte b[] = Base64.decode(children.getBytes(),Base64.URL_SAFE);
            ByteArrayInputStream bi = new ByteArrayInputStream(children.getBytes());
            ObjectInputStream si = new ObjectInputStream(bi);
            HashMap<String,List<String>> obj = (HashMap<String,List<String>>) si.readObject();
            si.close();
            return obj;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
/*
ByteArrayOutputStream bo = new ByteArrayOutputStream();
     ObjectOutputStream so = new ObjectOutputStream(bo);
     so.writeObject(myObject);
     so.flush();
     serializedObject = bo.toString();
 } catch (Exception e) {
     System.out.println(e);
 }

 // deserialize the object
 try {
     byte b[] = serializedObject.getBytes();
     ByteArrayInputStream bi = new ByteArrayInputStream(b);
     ObjectInputStream si = new ObjectInputStream(bi);
     MyObject obj = (MyObject) si.readObject();
 } catch (Exception e) {
     System.out.println(e);
 }
 */