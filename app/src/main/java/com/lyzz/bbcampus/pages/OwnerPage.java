package com.lyzz.bbcampus.pages;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lyzz.bbcampus.MainActivity;
import com.lyzz.bbcampus.R;
import com.lyzz.bbcampus.service.ImageDownload;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OwnerPage extends AppCompatActivity {
    private boolean loginflag;
    private String history="...";
    private String wallet="...";
    private TextView username;
    private TextView userinfo;
    private ImageView avatar;
    private String avatarpath=null;
    private DownloadImage downloadimgtask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_page);
        /**
         * 返回按钮监听事件——回到LBS主页
         */
        ImageButton backButton=(ImageButton)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OwnerPage.this,MainLBS.class);
                startActivity(intent);
                onPause();
            }
        });
        /*
        头像、用户信息控件初始化
         */
        avatar=(ImageView)findViewById(R.id.dispalyPhoto);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //未登录时跳转登陆界面
                if(!loginflag)
                {
                    Intent intent=new Intent(OwnerPage.this,LoginActivity.class);
                    intent.putExtra("login","avatar");
                    startActivity(intent);
                }
                //已登录提示注销
                else
                {
                    //Dialog窗口提示注销
                    showDialog();
                }
            }
        });
        userinfo=(TextView)findViewById(R.id.userinfo);
        username=(TextView)findViewById(R.id.username);
        /*
        用户状态判断
         */
        loginflag=checkUserState();
        /**
         * 初始化列表
         */
        /**
         * middlelist 订单数及金币栏
         */
        GridView middlelist=(GridView)findViewById(R.id.middlelist);
        SimpleAdapter middlelistAdapter;
        List<Map<String,Object>> mlist=new ArrayList<Map<String,Object>>();
        String keynum[]={history,wallet};
        int iconid[]={R.drawable.book,R.drawable.gold};
        for(int num=0;num<2;num++)
        {
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("icon",iconid[num]);
            map.put("num",keynum[num]);
            mlist.add(map);
        }
        middlelistAdapter=new SimpleAdapter(this,mlist,R.layout.middle_list,new String[]{"icon","num"},new int[]{R.id.middlelistimage,R.id.middlelisttext});
        middlelist.setAdapter(middlelistAdapter);
        /**
         * firstlist 帮助记录、我的钱包
         */
        ListView firstlist=(ListView)findViewById(R.id.firstlist);
        SimpleAdapter firstlistAdapter;
        List<Map<String,Object>> flist=new ArrayList<Map<String,Object>>();
        keynum[0]="订单记录";
        keynum[1]="我的帮助";
        iconid[0]=R.drawable.history;
        iconid[1]=R.drawable.wallet;
        for(int num=0;num<2;num++)
        {
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("icon",iconid[num]);
            map.put("num",keynum[num]);
            flist.add(map);
        }
        firstlistAdapter=new SimpleAdapter(this,flist,R.layout.first_list,new String[]{"icon","num"},new int[]{R.id.firstlistimage,R.id.firstlisttext});
        firstlist.setAdapter(firstlistAdapter);
        firstlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Class num[]=new Class[]{UserOrderHistory.class,UserMissionHistory.class};
                Intent intent=new Intent(OwnerPage.this,num[i]);
                startActivity(intent);
            }
        });
        /**
         * secondlist 我的信用、我的积分
         */
        ListView secondlist=(ListView)findViewById(R.id.secondlist);
        SimpleAdapter secondlistAdapter;
        List<Map<String,Object>> slist=new ArrayList<Map<String,Object>>();
        String secondname[]={"我的信用","我的积分"};
        iconid[0]=R.drawable.credit;
        iconid[1]=R.drawable.point;
        for(int num=0;num<2;num++)
        {
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("icon",iconid[num]);
            map.put("num",secondname[num]);
            slist.add(map);
        }
        secondlistAdapter=new SimpleAdapter(this,slist,R.layout.first_list,new String[]{"icon","num"},new int[]{R.id.firstlistimage,R.id.firstlisttext});
        secondlist.setAdapter(secondlistAdapter);
        /**
         *  thirdlist 系统设定、关于帮帮
         */
        ListView thirdlist=(ListView)findViewById(R.id.thirdlist);
        SimpleAdapter thirdlistAdapter;
        List<Map<String,Object>> tlist=new ArrayList<Map<String,Object>>();
        String thirdname[]={"系统设置","关于帮帮"};
        iconid[0]=R.drawable.setting;
        iconid[1]=R.drawable.about;
        for(int num=0;num<2;num++)
        {
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("icon",iconid[num]);
            map.put("num",thirdname[num]);
            tlist.add(map);
        }
        thirdlistAdapter=new SimpleAdapter(this,tlist,R.layout.first_list,new String[]{"icon","num"},new int[]{R.id.firstlistimage,R.id.firstlisttext});
        thirdlist.setAdapter(thirdlistAdapter);

    }
    private boolean checkUserState()
    {
        //检查登陆状态
        SharedPreferences userstate=getApplicationContext().getSharedPreferences("userstate",0);
        int loginInt=userstate.getInt("login",0);
        //未登录
        if(loginInt==0)
        {
            Toast.makeText(OwnerPage.this,"请点击头像登录", Toast.LENGTH_SHORT).show();
            return false;
        }
        //已登录
        else if(loginInt==1)
        {
            String imgurl=userstate.getString("userAvatar","");
            if(!(imgurl.equals("")))
            {
                downloadimgtask=new DownloadImage(imgurl);
                downloadimgtask.execute((Void) null);
                Log.d("checkUserState", "已登陆，待加载"+imgurl);
                SharedPreferences.Editor editor=userstate.edit();
                editor.putInt("login",2);
                editor.commit();
            }
        }
        /*
        加载头像
         */
        setAvatar();
        setUserText();
        Log.d("checkUserState", "加载用户信息完成");
        return true;
    };
    /*
    加载用户资料
    */
    private void setUserText()
    {
        SharedPreferences userstate=getApplicationContext().getSharedPreferences("userstate",0);
        username.setText(userstate.getString("userNickName",""));
        userinfo.setText(userstate.getString("userOrganization","")+userstate.getString("userSchool",""));
        history=userstate.getString("userBonus","");
        wallet=userstate.getString("userBangBiValue","");
    }
    /*
    加载头像
     */
    private void setAvatar()
    {
        SharedPreferences path=getSharedPreferences("system",0);
        if(avatarpath==null)
        {
            avatarpath=path.getString("avatarpath","");
        }
        Bitmap bitmap = null;
        try
        {
            File file = new File(avatarpath);
            if(file.exists())
            {
                bitmap = BitmapFactory.decodeFile(avatarpath);
                avatar.setImageBitmap(bitmap);
                Log.d("checkUserState", "加载头像完成");
            }
        } catch (Exception e)
        {
            // TODO: handle exception
        }
    }
    /*
    下载头像
     */
    private class DownloadImage extends AsyncTask<Void,Void,String>
    {
        String imgurl;
        DownloadImage(String url)
        {
            imgurl=url;
        }
        @Override
        protected String doInBackground(Void... params)
        {
            String imgpath= ImageDownload.getNetWorkBitmap(imgurl,"useravator.jpg").toString();
            return imgpath;
        }
        @Override
        protected void onPostExecute(final String imgpath)
        {
            /*
            存储头像路径，写入SP
             */
            downloadimgtask=null;
            avatarpath=imgpath;
            SharedPreferences setting=getSharedPreferences("system",0);
            SharedPreferences.Editor editor=setting.edit();
            editor.putString("avatarpath",imgpath);
            editor.commit();
            Log.d("checkUserState", "下载并保存头像成功");
            setAvatar();
        }
        @Override
        protected void onCancelled() {

        }
    }
    private void showDialog() {
  /*
  这里使用了 android.support.v7.app.AlertDialog.Builder
  可以直接在头部写 import android.support.v7.app.AlertDialog
  那么下面就可以写成 AlertDialog.Builder
  */
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("注销用户");
        builder.setMessage("是否确定注销用户");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //注销用户
                SharedPreferences sp = getSharedPreferences("userstate", 0);
                SharedPreferences path=getSharedPreferences("system",0);
                String filepath=path.getString("avatarpath","");
                //删除本地缓存用户头像
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();
                //跳转登陆
                Intent intent=new Intent(OwnerPage.this,LoginActivity.class);
                intent.putExtra("login","cancel");
                startActivity(intent);
            }
        });
        builder.show();
    }
}