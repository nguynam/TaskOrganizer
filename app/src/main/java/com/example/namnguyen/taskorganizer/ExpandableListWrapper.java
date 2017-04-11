package com.example.namnguyen.taskorganizer;

import android.util.Base64;
import android.util.Xml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Josh on 4/8/17.
 */

public class ExpandableListWrapper {
    public List<String> headers;
    public String children;
    public void setHeaders(List<String> headers){
        this.headers = headers;
    }
    public List<String> getHeaders(){
        if(this.headers == null){
            return new ArrayList<String>();
        }else{
            return headers;
        }
    }
    public void encodeChildren(HashMap<String, List<String>> children) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.children = mapper.writeValueAsString(children);
        }catch (Exception e) {
            System.out.println(e);
        }
    }
    public HashMap<String,List<String>> decodeChildren(){
        try {
            if(this.children == null){
                return new HashMap<String,List<String>>();
            }else{
                ObjectMapper mapper = new ObjectMapper();
                HashMap<String,List<String>> tempMap = mapper.readValue(this.children,new TypeReference<Map<String,List<String>>>(){});
                return  tempMap;
            }
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
