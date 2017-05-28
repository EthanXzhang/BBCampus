package com.lyzz.bbcampus.pages;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lyzz.bbcampus.R;
import com.lyzz.bbcampus.order.MissionLocationAndRoute;
import com.lyzz.bbcampus.order.OrderClass;
import com.lyzz.bbcampus.service.AysncTaskPost;
import com.lyzz.bbcampus.service.ImageDownload;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class MissionOnGoingDetail extends AppCompatActivity {
    private OrderClass order;
    private TextView username,location,ordertitle,bonus;
    private TextView firstround,secondround,thirdround;
    private TextView firsttextfield,secondtextfield,thirdtextfield;
    private ImageView avatar,mapbutton;
    private Button apply;
    private ImageButton back;
    private getUserInfo mAuthTask;
    private changeOrderStatus changeorderstatus;
    private String avatarpath,localpath,orderImgpath;
    private DownloadImage downloadimgtask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_ongong_detail);
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
        mapbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MissionOnGoingDetail.this,MissionLocationAndRoute.class);
                intent.putExtra("latitude",order.orderlocationlatitude);
                intent.putExtra("longitude",order.orderlocationlongitude);
                startActivity(intent);
            }
        });
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String orderpost="orderId="+order.orderId;
                changeorderstatus=new changeOrderStatus(orderpost,"http://112.74.41.59:3000/v1/order/changeToFinishedStatus",1);
                changeorderstatus.execute((Void) null);
            }
        });
        back=(ImageButton)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        Toast.makeText(MissionOnGoingDetail.this,"请在进行中保持该页面",Toast.LENGTH_SHORT).show();
        orderinit();
        postuserinit();
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
                Intent intent=new Intent(MissionOnGoingDetail.this,MissionLocationAndRoute.class);
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
    private void showDialog() {
  /*
  这里使用了 android.support.v7.app.AlertDialog.Builder
  可以直接在头部写 import android.support.v7.app.AlertDialog
  那么下面就可以写成 AlertDialog.Builder  */
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("退出任务");
        builder.setMessage("这将取消当前任务，可能会影响到您的信用与积分");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String str="orderId="+order.posterId;
                changeorderstatus=new changeOrderStatus(str,"http://112.74.41.59:3000/v1/order/changeToCancelledStatus",0);
                changeorderstatus.execute((Void) null);
            }
        });
        builder.show();
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
                Toast.makeText(MissionOnGoingDetail.this,"网络未连接", Toast.LENGTH_SHORT).show();
            }
            else if(success.equals("false"))
            {
                Toast.makeText(MissionOnGoingDetail.this,"遇到异常，请尝试重启或联系我们", Toast.LENGTH_SHORT).show();
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
                        downloadimgtask=new DownloadImage(avatarpath,detail.getString("userNickName"));
                        downloadimgtask.execute((Void) null);
                        //添加处理函数

                    }else
                    {
                        Toast.makeText(MissionOnGoingDetail.this,object.getString("message"), Toast.LENGTH_SHORT).show();
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
    异步队列，请求服务器posetuser数据;
    */
    public class changeOrderStatus extends AsyncTask<Void, Void, String> {
        private String context;
        private String url;
        private int i;
        //"http://112.74.41.59:3000/v1/order/changeToFinishedStatus"
        //"http://112.74.41.59:3000v1/order/changeToCancelledStatus"
        changeOrderStatus(String out,String url,int i)
        {
            this.url=url;
            context=out;
            this.i=i;
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
                Toast.makeText(MissionOnGoingDetail.this,"网络未连接", Toast.LENGTH_SHORT).show();
            }
            else if(success.equals("false"))
            {
                Toast.makeText(MissionOnGoingDetail.this,"遇到异常，请尝试重启或联系我们", Toast.LENGTH_SHORT).show();
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
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent intent;
                        switch (i)
                        {
                            case 0:
                                //取消任务，返回主页
                                intent=new Intent(MissionOnGoingDetail.this,MainLBS.class);
                                Toast.makeText(MissionOnGoingDetail.this,"已取消任务，返回主页面ing",Toast.LENGTH_SHORT).show();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                startActivity(intent);
                                break;
                            case 1:
                                //完成任务，跳转评价
                                intent=new Intent(MissionOnGoingDetail.this,MainLBS.class);
                                Toast.makeText(MissionOnGoingDetail.this,"已递交完成，发起人确认后可在订单历史中评价",Toast.LENGTH_SHORT).show();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                startActivity(intent);
                                break;
                        }
                        //添加处理函数

                    }else
                    {
                        Toast.makeText(MissionOnGoingDetail.this,object.getString("message"), Toast.LENGTH_SHORT).show();
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
