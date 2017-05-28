package com.lyzz.bbcampus.order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/5/17.
 */

public class OrderRepachage {
    public static OrderClass[] Repachage(JSONArray OrderArray)
    {
        OrderClass[] orderclass=new OrderClass[10];
        JSONObject jsonobject=null;
        for(int i=0;i<10;i++)
        {
            try {
                jsonobject=OrderArray.getJSONObject(i);
                orderclass[i]=new OrderClass(jsonobject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return orderclass;
    }
}
