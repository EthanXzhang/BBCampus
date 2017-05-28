package com.lyzz.bbcampus.pages;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lyzz.bbcampus.R;
import com.lyzz.bbcampus.User.User;
import com.lyzz.bbcampus.order.MissionLocationAndRoute;
import com.lyzz.bbcampus.order.OrderClass;
import com.lyzz.bbcampus.service.AysncTaskPost;
import com.lyzz.bbcampus.service.ImageDownload;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class MissionDetail extends AppCompatActivity {
    private OrderClass order;
    private TextView username,location,ordertitle,bonus;
    private TextView firstround,secondround,thirdround;
    private TextView firsttextfield,secondtextfield,thirdtextfield;
    private ImageView avatar,mapbutton;
    private Button apply,phone,mail;
    private ImageButton back;
    private getUserInfo mAuthTask;
    private changeOrderStatus changeorderstatus;
    private String avatarpath,localpath,orderImgpath;
    private DownloadImage downloadimgtask;
    private User poster;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_detail);
        order=(OrderClass)getIntent().getParcelableExtra("order");
        username=(TextView)findViewById(R.id.username);
        location=(TextView)findViewById(R.id.location);
        ordertitle=(TextView)findViewById(R.id.ordertitle);
        bonus=(TextView)findViewById(R.id.bonus) ;
        firstround=(TextView)findViewById(R.id.firstround) ;
        secondround=(TextView)findViewById(R.id.secondround);
        thirdround=(TextView)findViewById(R.id.thirdround);
        firsttextfield=(TextView)findViewById(R.id.firstfield);
        secondtextfield=(TextView)findViewById(R.id.secondfield);
        thirdtextfield=(TextView)findViewById(R.id.thirdfield);
        avatar=(ImageView)findViewById(R.id.avatar);
        apply=(Button)findViewById(R.id.apply);
        mapbutton=(ImageView)findViewById(R.id.toLBS);
        phone=(Button)findViewById(R.id.tel);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactposter();
            }
        });
        mail=(Button)findViewById(R.id.sms);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mailposter();
            }
        });
        mapbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MissionDetail.this,MissionLocationAndRoute.class);
                intent.putExtra("latitude",order.orderlocationlatitude);
                intent.putExtra("longitude",order.orderlocationlongitude);
                startActivity(intent);
            }
        });
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences user=getSharedPreferences("userstate",0);
                String orderpost="orderId="+order.orderId+"&"+"receiverId="+user.getString("userId","");
                changeorderstatus=new changeOrderStatus(orderpost);
                changeorderstatus.execute((Void) null);
            }
        });
        back=(ImageButton)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MissionDetail.this,MainLBS.class);
                startActivity(intent);
            }
        });
        orderinit();
        postuserinit();
    }
    private void contactposter() {
      /*
      这里使用了 android.support.v7.app.AlertDialog.Builder
      可以直接在头部写 import android.support.v7.app.AlertDialog
      那么下面就可以写成 AlertDialog.Builder
      */
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("联系他");
        builder.setMessage("即将发起人电话");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //跳转拨打
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + poster.userphone);
                intent.setData(data);
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent);
            }
        });
        builder.show();
    }
    private void mailposter() {
      /*
      这里使用了 android.support.v7.app.AlertDialog.Builder
      可以直接在头部写 import android.support.v7.app.AlertDialog
      那么下面就可以写成 AlertDialog.Builder
      */
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("联系他");
        builder.setMessage("即将前往短信界面");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //跳转拨打
                Uri uri = Uri.parse("smsto:" + poster.userphone);
                Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(sendIntent);
            }
        });
        builder.show();
    }
    public void postuserinit()
    {
        String str="userId="+order.posterId;
        mAuthTask = new getUserInfo(str);
        mAuthTask.execute((Void) null);
    }
    public void orderinit()
    {
        String type=order.ordertype;
        switch (type)
        {
            case "解题":
                firstround.setText("解");
                secondround.setText("题");
                thirdround.setText("位");break;
            case "带饭":
                firstround.setText("去");
                secondround.setText("买");
                thirdround.setText("送");break;
            case "维修":
                firstround.setText("物");
                secondround.setText("修");
                thirdround.setText("位");break;
            case "打水":
                firstround.setText("去");
                secondround.setText("事");
                thirdround.setText("到");break;
            case "洗涤":
                firstround.setText("衣");
                secondround.setText("事");
                thirdround.setText("位");break;
            case "留座":
                firstround.setText("座");
                secondround.setText("事");
                thirdround.setText("到");break;
            case "媒体制作":
                firstround.setText("制");
                secondround.setText("项");
                thirdround.setText("位");break;
            case "资料":
                firstround.setText("资");
                secondround.setText("项");
                thirdround.setText("位");break;
            case "快递":
                firstround.setText("点");
                secondround.setText("E");
                thirdround.setText("到");break;
            case "借":
                firstround.setText("物");
                secondround.setText("项");
                thirdround.setText("到");break;
            default:
                firstround.setText("做");
                secondround.setText("事");
                thirdround.setText("到");break;
        }
        location.setText(order.orderloactiondetail);
        ordertitle.setText(order.ordertitle);
        if(order.orderbuylocationdetail!=null)
        {
            firsttextfield.setText(order.orderbuylocationdetail);
        }else
        {
//            if(order.orderimg!=null)
//            {
//                downloadimgtask=new DownloadImage(avatarpath,order.orderimg);
//                downloadimgtask.execute((Void) null);
//            }
            firsttextfield.setText("点击显示图片");
            firsttextfield.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*
                    调用相册显示图片
                     */
//                    Intent intent = new Intent();
//                    intent.setAction(android.content.Intent.ACTION_VIEW);
//                    intent.setDataAndType(Uri.fromFile(file), "image/*");
//                    startActivity(intent);
                }
            });
        }
        secondtextfield.setText(order.ordercontext);
        thirdtextfield.setText(order.orderloactiondetail);
        thirdtextfield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MissionDetail.this,MissionLocationAndRoute.class);
                intent.putExtra("latitude",order.orderlocationlatitude);
                intent.putExtra("longitude",order.orderlocationlongitude);
                startActivity(intent);
            }
        });
        if(order.orderbangbivalue!=null)
        {
            bonus.setText("帮币:"+order.orderbangbivalue);
        }else if(order.orderbonusvalue!=null)
        {
            bonus.setText("赏"+order.orderbonusvalue);
        }
        else
        {
            bonus.setText("积分奖励");
        }
    }
    /*
   异步队列，请求服务器posetuser数据;
    */
    public class getUserInfo extends AsyncTask<Void, Void, String> {
        private String context;
        private String url="http://112.74.41.59:3000/v1/user/getUserInfosByUserId";
        getUserInfo(String out)
        {
            context=out;
        }
        @Override
        protected String doInBackground(Void... params) {
            String result;
            try {
                // Simulate network access.
                //服务器112.74.41.59:3000/v1/user/login
                result = AysncTaskPost.postUserLogin(url,context);
                Thread.sleep(200);
            } catch (InterruptedException e) {
                return "error";
            }
            // TODO: register the new account here.
            return result;
        }

        @Override
        protected void onPostExecute(String success) {
            mAuthTask = null;
            if (success.equals("error")) {
                Toast.makeText(MissionDetail.this,"网络未连接", Toast.LENGTH_SHORT).show();
            }
            else if(success.equals("false"))
            {
                Toast.makeText(MissionDetail.this,"遇到异常，请尝试重启或联系我们", Toast.LENGTH_SHORT).show();
            }else
            {
                JSONObject object=null;
                JSONObject detail=null;
                String code;
                try {
                    object=new JSONObject(success);
                    code=object.getString("code");
                    if(code.equals("201"))
                    {
                        JSONArray array;
                        array=new JSONArray(object.getString("message"));
                        detail=array.getJSONObject(0);
                        avatarpath=detail.getString("userAvatar");
                        username.setText(detail.getString("userNickName"));
                        poster=new User(detail);
                        downloadimgtask=new DownloadImage(avatarpath,detail.getString("userNickName"));
                        downloadimgtask.execute((Void) null);
                        //添加处理函数

                    }else
                    {
                        Toast.makeText(MissionDetail.this,object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("JSON success",success);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
    /*
    下载头像
    */
    private class DownloadImage extends AsyncTask<Void,Void,String>
    {
        String imgurl;
        String filename;
        DownloadImage(String url,String name)
        {
            imgurl=url;
            filename=name;
        }
        @Override
        protected String doInBackground(Void... params)
        {
            String imgpath= ImageDownload.getNetWorkBitmap(imgurl,filename).toString();
            return imgpath;
        }
        @Override
        protected void onPostExecute(final String imgpath)
        {
            /*
            存储头像路径，写入SP
             */
            downloadimgtask=null;
            localpath=imgpath;
            setAvatar();
            Log.d("checkUserState", "下载并保存头像成功");
        }
        @Override
        protected void onCancelled() {

        }
    }
    /*
    加载头像
    */
    private void setAvatar()
    {
        Bitmap bitmap = null;
        try
        {
            File file = new File(localpath);
            if(file.exists())
            {
                bitmap = BitmapFactory.decodeFile(localpath);
                avatar.setImageBitmap(bitmap);
                Log.d("checkUserState", "加载头像完成");
            }
        } catch (Exception e)
        {
            // TODO: handle exception
        }
    }
    /*
    异步队列，更改order状态为进行中;
    */
    public class changeOrderStatus extends AsyncTask<Void, Void, String> {
        private String context;
        private String url="http://112.74.41.59:3000/v1/order/changeToOngoingStatus";
        changeOrderStatus(String out)
        {
            context=out;
        }
        @Override
        protected String doInBackground(Void... params) {
            String result;
            try {
                // Simulate network access.
                //服务器112.74.41.59:3000/v1/user/login
                result = AysncTaskPost.postUserLogin(url,context);
                Thread.sleep(200);
            } catch (InterruptedException e) {
                return "error";
            }
            // TODO: register the new account here.
            return result;
        }

        @Override
        protected void onPostExecute(String success) {
            changeorderstatus = null;
            if (success.equals("error")) {
                Toast.makeText(MissionDetail.this,"网络未连接", Toast.LENGTH_SHORT).show();
            }
            else if(success.equals("false"))
            {
                Toast.makeText(MissionDetail.this,"遇到异常，请尝试重启或联系我们", Toast.LENGTH_SHORT).show();
            }else
            {
                JSONObject object=null;
                JSONObject detail=null;
                String code;
                try {
                    object=new JSONObject(success);
                    code=object.getString("code");
                    if(code.equals("201"))
                    {
                        Toast.makeText(MissionDetail.this,"接受成功，跳转任务追踪页面",Toast.LENGTH_SHORT);
                        Intent intent=new Intent(MissionDetail.this,MissionOnGoingDetail.class);
                        intent.putExtra("order",order);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                        finish();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //添加处理函数

                    }else
                    {
                        Toast.makeText(MissionDetail.this,object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("JSON success",success);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
