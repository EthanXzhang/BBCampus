package com.lyzz.bbcampus.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.lyzz.bbcampus.R;
import com.lyzz.bbcampus.User.User;
import com.lyzz.bbcampus.order.OrderClass;
import com.lyzz.bbcampus.service.AysncTaskPost;
import com.lyzz.bbcampus.service.ImageDownload;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.R.attr.order;

public class MainList extends AppCompatActivity {
    private ListView listview;
    private SimpleAdapter orderadapter;
    private List<Map<String,Object>> listmap;
    private OrderClass[] orderpackage=new OrderClass[10];
    private ArrayList<OrderClass> list;
    private User[] userpackage=new User[10];
    private String[] imagepackage=new String[10];
    private Drawable[] drawable=new Drawable[10];
    private ImageView map,avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        list=getIntent().getParcelableArrayListExtra("order");
        listview=(ListView)findViewById(R.id.orderlistview);
        map=(ImageView)findViewById(R.id.mapButton);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainList.this,MainLBS.class);
                startActivity(intent);
                finish();
            }
        });
        avatar=(ImageView)findViewById(R.id.owerButton);
        setAvatar();
        getOrderPackage();
        init();
    }
    private void getOrderPackage()
    {
        Iterator it=list.iterator();
        int i=0;
        while(it.hasNext()&&i<10)
        {
            orderpackage[i]=(OrderClass) it.next();
            i++;
        }
//        for(int j=0;j<orderpackage.length;j++)
//        {
//            if(orderpackage[j]!=null)
//            {
//                getUserInfoByOrder(orderpackage[j].posterId,j);
//            }
//        }
    }
    private void init()
    {
        listmap=new ArrayList<Map<String,Object>>();
        for(int i=0;i<orderpackage.length;i++)
        {
            if(orderpackage[i]!=null)
            {
                putOrderInfo(listmap,i);
            }
        }
        orderadapter=new SimpleAdapter(this,listmap,R.layout.mission_listview_item,new String[]{"avatar","ordertitle","ordercontext","location","bonus","distance"},new int[]{R.id.avatar,R.id.ordertitle,R.id.ordercontext,R.id.locationdetail,R.id.bonus,R.id.distance});
        listview.setAdapter(orderadapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(MainList.this,MissionDetail.class);
                intent.putExtra("order",orderpackage[i]);
                startActivity(intent);
                finish();
            }
        });
    }
    private void putOrderInfo(List list,int i)
    {

        Map<String,Object> map=new HashMap<String,Object>();
        switch (orderpackage[i].ordertype)
        {
            case "解题":map.put("avatar",R.drawable.p1);break;
            case "带饭":map.put("avatar",R.drawable.p2);break;
            case "维修":map.put("avatar",R.drawable.p3);break;
            case "打水":map.put("avatar",R.drawable.p4);break;
            case "洗涤":map.put("avatar",R.drawable.p5);break;
            case "留座":map.put("avatar",R.drawable.p6);break;
            case "媒体制作":map.put("avatar",R.drawable.p7);break;
            case "资料":map.put("avatar",R.drawable.p8);break;
            case "快递":map.put("avatar",R.drawable.p9);break;
            case "借":map.put("avatar",R.drawable.p0);break;
            default:map.put("avatar",R.drawable.send);
        }
        map.put("ordertitle",orderpackage[i].ordertitle);
        map.put("ordercontext",orderpackage[i].ordercontext);
        if(orderpackage[i].orderloactiondetail!=null)
        {
            map.put("location",orderpackage[i].orderloactiondetail);
        }else
        {
            map.put("location","任务未设置详细地理信息");
        }
        if(orderpackage[i].orderbonusvalue!=null)
        {
            map.put("bonus","赏金："+orderpackage[i].orderbonusvalue);
        }else if(orderpackage[i].orderbangbivalue!=null)
        {
            map.put("bonus","帮币："+orderpackage[i].orderbangbivalue);
        }else
        {
            map.put("bonus","积分奖励");
        }
        map.put("distance","计算中");
        list.add(map);
    }
    /*
    *设置用户头像
    */
    private void setAvatar()
    {
        SharedPreferences userstate=getApplicationContext().getSharedPreferences("userstate",0);
        int loginInt=userstate.getInt("login",0);
        if(loginInt==2)
        {
            String avatarpath;
            SharedPreferences path=getSharedPreferences("system",0);
            avatarpath=path.getString("avatarpath","");
            if(!(avatarpath==""))
            {
                Bitmap bitmap = null;
                try
                {
                    File file = new File(avatarpath);
                    if(file.exists())
                    {
                        bitmap = BitmapFactory.decodeFile(avatarpath);
                        avatar.setImageBitmap(bitmap);
                        Log.d("checkUserState", "加载按钮");
                    }
                } catch (Exception e)
                {
                    // TODO: handle exception
                }
            }
        }
    }
//    private void getUserInfoByOrder(String orderid,int i)
//    {
//        String str="userId="+orderid;
//        getuserinfo[i] = new getUserInfo(str,i);
//        getuserinfo[i].execute((Void) null);
//    }
    /*
    异步队列，请求服务器posetuser数据;
    */
//    public class getUserInfo extends AsyncTask<Void, Void, String> {
//        private String context;
//        private String url="http://112.74.41.59:3000/v1/user/getUserInfosByUserId";
//        private int i;
//        getUserInfo(String out,int i)
//        {
//            context=out;
//            this.i=i;
//        }
//        @Override
//        protected String doInBackground(Void... params) {
//            String result;
//            try {
//                // Simulate network access.
//                //服务器112.74.41.59:3000/v1/user/login
//                result = AysncTaskPost.postUserLogin(url,context);
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                return "error";
//            }
//            // TODO: register the new account here.
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(String success) {
//            getuserinfo[i] = null;
//            if (success.equals("error")) {
//                Toast.makeText(MainList.this,"网络未连接", Toast.LENGTH_SHORT).show();
//            }
//            else if(success.equals("false"))
//            {
//                Toast.makeText(MainList.this,"遇到异常，请尝试重启或联系我们", Toast.LENGTH_SHORT).show();
//            }else
//            {
//                JSONObject object=null;
//                JSONObject detail=null;
//                String code;
//                try {
//                    object=new JSONObject(success);
//                    code=object.getString("code");
//                    if(code.equals("201"))
//                    {
//                        JSONArray array;
//                        array=new JSONArray(object.getString("message"));
//                        detail=array.getJSONObject(0);
//                        userpackage[i]=new User(detail);
//                        downloadimgtask[i]=new DownloadImage(userpackage[i].useravatar,userpackage[i].usernickname,i);
//                        downloadimgtask[i].execute((Void) null);
//                        //添加处理函数
//
//                    }else
//                    {
//                        Toast.makeText(MainList.this,object.getString("message"), Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Log.d("JSON success",success);
//                }
//            }
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onCancelled() {
//            getuserinfo[i] = null;
//        }
//    }
//    /*
//下载头像
//*/
//    private class DownloadImage extends AsyncTask<Void,Void,String>
//    {
//        String imgurl;
//        String filename;
//        int i;
//        DownloadImage(String url,String name,int i)
//        {
//            imgurl=url;
//            filename=name;
//            this.i=i;
//        }
//        @Override
//        protected String doInBackground(Void... params)
//        {
//            String imgpath= ImageDownload.getNetWorkBitmap(imgurl,filename).toString();
//            return imgpath;
//        }
//        @Override
//        protected void onPostExecute(final String imgpath)
//        {
//            /*
//            存储头像路径，写入SP
//             */
//            downloadimgtask[i]=null;
//            imagepackage[i]=imgpath;
//            Log.d("checkUserState", "下载并保存头像成功");
//        }
//        @Override
//        protected void onCancelled() {
//            downloadimgtask[i]=null;
//        }
//    }
}
