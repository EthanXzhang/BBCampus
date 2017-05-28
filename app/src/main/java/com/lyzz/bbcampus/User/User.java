package com.lyzz.bbcampus.User;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/5/19.
 */

public class User{
    public String usernickname;
    public String useremail;
    public String userId;
    public String userregistertime;
    public String userbonus;
    public String userbangbivalue;
    public String userfollows,userfans;
    public String usercredit;
    public String userreputation;
    public String usersafequestion;
    public String userstatus;
    public String userskill;
    public String userfavor;
    public String userlable;
    public String userfocus;
    public String userlevel;
    public String userorganization;
    public String userschool;
    public String useraddress;
    public String userphone;
    public String userbitrh;
    public String useravatar;
    public String usersex;

    public User(JSONObject object)
    {
        try {
            usernickname=object.getString("userNickName");
            useremail=object.getString("userEmail");
            userId=object.getString("userId");
            userregistertime=object.getString("userRegisterTime");
            userbonus=object.getString("userBonus");
            userbangbivalue=object.getString("userBangBiValue");
            usercredit=object.getString("userCredit");
            userreputation=object.getString("userReputation");
            userskill=object.getString("userSkill");
            userfavor=object.getString("userFavor");
            userlable=object.getString("userLable");
            userfocus=object.getString("userFocus");
            userlevel=object.getString("userLevel");
            userorganization=object.getString("userOrganization");
            userschool=object.getString("userSchool");
            useraddress=object.getString("userAddress");
            userphone=object.getString("userPhone");
            userbitrh=object.getString("userBirth");
            useravatar=object.getString("userAvavtar");
            usersex=object.getString("userSex");
            userstatus=object.getString("userStatus");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
