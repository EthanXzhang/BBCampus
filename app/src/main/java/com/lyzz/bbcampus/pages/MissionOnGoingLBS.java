package com.lyzz.bbcampus.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.LocationSource;
import com.lyzz.bbcampus.R;
import com.lyzz.bbcampus.missionclass.*;

import java.io.File;

public class MissionOnGoingLBS extends AppCompatActivity implements LocationSource,
        AMapLocationListener{
    //控件定义
    private MapView mMapView = null;
    private ImageView owerButton=null;
    private ImageView listButton=null;
    private ImageView publish=null;
    private EditText searchtext=null;
    private AMap aMap;
    //初始化定位服务
    private OnLocationChangedListener mListener;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private ImageButton phone,mail;



    //初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_lbs);
        /**
         * 初始化地图控件
         */
        mMapView = (MapView) findViewById(R.id.mapview);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        /**
         * 界面一般控件
         */
        searchtext=(EditText)findViewById(R.id.search);
        owerButton=(ImageView)findViewById(R.id.owerButton);
        listButton=(ImageView)findViewById(R.id.listButton);
        publish=(ImageView)findViewById(R.id.publish);
        phone=(ImageButton)findViewById(R.id.callphone);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mail=(ImageButton)findViewById(R.id.SMS);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        /*
        按钮头像加载
         */
        setAvatar();
        /**
         控件的点击监听
         */
        searchtext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {

                }
                else
                {
                    owerButton.setFocusable(true);
                }
            }
        });
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MissionOnGoingLBS.this,Subscribe.class);
                startActivity(intent);
            }
        });
        owerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MissionOnGoingLBS.this,OwnerPage.class);
                startActivity(intent);
                onPause();
            }
        });
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MissionOnGoingLBS.this,MainList.class);
                startActivity(intent);
                onPause();
            }
        });
        /**
         * 初始化Amap
         */
        init();

    }
    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mMapView.getMap();
            setUpMap();
        }
    }
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
//                .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                LatLng point=new LatLng(amapLocation.getLatitude(),amapLocation.getLongitude());
                aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(point,18, 30, 30)));
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Toast.makeText(this,errText, Toast.LENGTH_SHORT).show();
                Log.e("AmapErr",errText);
            }
        }
    }
    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式Device_Sensors
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setMockEnable(true);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }
    /*
    *设置用户头像
     */
    private void setAvatar()
    {
        SharedPreferences userstate=getApplicationContext().getSharedPreferences("userstate",0);
        int loginInt=userstate.getInt("login",0);
        if(loginInt==1)
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
                        owerButton.setImageBitmap(bitmap);
                        Log.d("checkUserState", "加载按钮");
                    }
                } catch (Exception e)
                {
                    // TODO: handle exception
                }
            }
        }
    }
}
