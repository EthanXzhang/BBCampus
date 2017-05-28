package com.lyzz.bbcampus.order;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/17.
 */

public class OrderClass implements Parcelable{
    public String orderId;
    public String ordertitle;
    public String ordercontext;
    public String ordertype;
    public String orderlocationlatitude,orderlocationlongitude;
    public String orderloactiondetail;
    public String orderbuylocationlatitude,orderbuylocationlongitude;
    public String orderbuylocationdetail;
    public String orderimg;
    public String orderbangbivalue;
    public String orderbonusvalue;
    public String posterId;
    public String orderstatus;
    public String orderdonetime;
    public String verification;

    OrderClass(JSONObject object)
    {
        try {
            verification=object.getString("verification");
            orderdonetime=object.getString("orderDoneTime");
            orderstatus=String.valueOf(object.getInt("orderStatus"));
            posterId=String.valueOf(object.getInt("posterId"));
            orderId=object.getString("orderId");
            ordertitle=object.getString("orderTitle");
            ordercontext=object.getString("orderContext");
            ordertype=object.getString("orderType");
            setLocation(object.getJSONArray("orderLocation"),"my");
            if(object.getString("orderLocationDetail")!=null)
            {
                orderloactiondetail=object.getString("orderLocationDetail");
            }
            if(object.getString("orderBangBiValue")!=null)
            {
                orderbangbivalue=String.valueOf(object.getInt("orderBangBiValue"));
            }
            if(object.getString("orderBonusValue")!=null)
            {
                orderbonusvalue=String.valueOf(object.getInt("orderBonusValue"));
            }
            if(object.getString("orderBuyLocation")!=null||!object.getString("orderBuyLocation").equals(""))
            {
                setLocation(object.getJSONArray("orderBuyLocation"),"buy");
                if(object.getString("orderBuyLocationDetail")!=null)
                {
                    orderbuylocationdetail=object.getString("orderBuyLocationDetail");
                }
            }
                if(object.getJSONArray("orderImg")!=null)
                {
                    getImg(object.getString("orderImg"));
                }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected OrderClass(Parcel in) {
        orderId = in.readString();
        ordertitle = in.readString();
        ordercontext = in.readString();
        ordertype = in.readString();
        orderlocationlatitude = in.readString();
        orderlocationlongitude = in.readString();
        orderloactiondetail = in.readString();
        orderbuylocationlatitude = in.readString();
        orderbuylocationlongitude = in.readString();
        orderbuylocationdetail = in.readString();
        orderimg = in.readString();
        orderbangbivalue = in.readString();
        orderbonusvalue = in.readString();
        posterId = in.readString();
        orderstatus = in.readString();
        orderdonetime = in.readString();
        verification = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        dest.writeString(ordertitle);
        dest.writeString(ordercontext);
        dest.writeString(ordertype);
        dest.writeString(orderlocationlatitude);
        dest.writeString(orderlocationlongitude);
        dest.writeString(orderloactiondetail);
        dest.writeString(orderbuylocationlatitude);
        dest.writeString(orderbuylocationlongitude);
        dest.writeString(orderbuylocationdetail);
        dest.writeString(orderimg);
        dest.writeString(orderbangbivalue);
        dest.writeString(orderbonusvalue);
        dest.writeString(posterId);
        dest.writeString(orderstatus);
        dest.writeString(orderdonetime);
        dest.writeString(verification);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderClass> CREATOR = new Creator<OrderClass>() {
        @Override
        public OrderClass createFromParcel(Parcel in) {
            return new OrderClass(in);
        }

        @Override
        public OrderClass[] newArray(int size) {
            return new OrderClass[size];
        }
    };

    public void setLocation(JSONArray array, String flag)
    {
        try {
            if(flag.equals("buy"))
            {
                orderbuylocationlatitude=array.getString(1);
                orderbuylocationlongitude=array.getString(0);
            }else
            {
                orderlocationlatitude=array.getString(1);
                orderlocationlongitude=array.getString(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getImg(String str)
    {
        JSONArray array;
        try {
            array=new JSONArray(str);
            orderimg=array.getString(0)+","+array.getString(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public Map getLocation()
    {
        if(orderlocationlatitude!=null&&orderlocationlongitude!=null)
        {
            Map<String,String> map=new HashMap<>();
            map.put("latitude",orderlocationlatitude);
            map.put("longitude",orderlocationlongitude);
            return map;
        }
        else
        {
            return null;
        }
    }
//    public Map getLocation()
//    {
//        if(orderlocation==null)
//        {
//            return null;
//        }
//        else
//        {
//            Map<String,String> map=new HashMap<>();
//            String str="";
//            for(int i=0;i<orderlocation.length();i++)
//            {
//
//                if(orderlocation.charAt(i)==44)
//                {
//                    map.put("latitude",str);
//                    str="";
//                }else
//                {
//                    str+=orderlocation.charAt(i);
//                }
//            }
//            map.put("longitude",str);
//            return map;
//        }
//    }
}
