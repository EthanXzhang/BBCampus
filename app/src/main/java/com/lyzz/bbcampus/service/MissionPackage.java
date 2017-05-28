package com.lyzz.bbcampus.service;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/10.
 */

public class MissionPackage {
    public static String getPackage(Map<String,String> map)
    {
        String packagestring="";
        Iterator<Map.Entry<String,String>> it=map.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry<String, String> entry = it.next();
            packagestring=packagestring+entry.getKey()+"="+entry.getValue();
            if(it.hasNext())
            {
                packagestring=packagestring+"&";
            }
        }
        return packagestring;
    }
}
