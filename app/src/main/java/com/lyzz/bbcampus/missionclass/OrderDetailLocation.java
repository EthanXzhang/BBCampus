package com.lyzz.bbcampus.missionclass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.lyzz.bbcampus.R;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.AMap.OnMapLongClickListener;
import com.amap.api.maps2d.model.LatLng;

import java.io.File;

public class OrderDetailLocation extends AppCompatActivity implements LocationSource,
        AMapLocationListener , OnMapClickListener, OnMapLongClickListener,GeocodeSearch.OnGeocodeSearchListener{
    //控件定义
    private MapView mMapView = null;
    private ImageView owerButton=null;
    private ImageView listButton=null;
    private Button cancel;
    private ImageButton finish;
    private EditText searchtext=null;
    private AMap aMap;
    //初始化定位服务
    private OnLocationChangedListener mListener;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private GeocodeSearch geocodeSearch;
    private String addressName,addressDetail;
    private LatLonPoint latlonpoint;
    private LatLng latlngpoint;
    private Marker regeoMarker;
    private boolean firstflag;

    //初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_lbs);
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
        cancel=(Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                setResult(1,intent);
                //resultCode 1,cancel返回
                finish();
            }
        });
        finish=(ImageButton)findViewById(R.id.complete);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.putExtra("latitude",""+latlngpoint.latitude);
                intent.putExtra("longitude",""+latlngpoint.longitude);
                intent.putExtra("address",addressName);
                intent.putExtra("detail",addressDetail);
                setResult(0,intent);
                //resultCode 0,finish返回，intent中包含地理信息
                finish();
            }
        });
        /**
         * 初始化Amap
         */
        init();
        geocodeSearch=new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
        Toast.makeText(OrderDetailLocation.this,"长按选定位置，轻点撤销", Toast.LENGTH_SHORT).show();
    }
    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mMapView.getMap();
            setUpMap();
            aMap.setOnMapClickListener(this);
            aMap.setOnMapLongClickListener(this);
        }
    }
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.gps_point));// 设置小蓝点的图标
        myLocationStyle.radiusFillColor(Color.argb(80, 200,250, 250));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(0.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
        firstflag=true;
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
                if(firstflag)
                {
                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(point,18, 30, 30)));
                    firstflag=false;
                }
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
            //设置为高精度定位模式
            //Device_Sensors设备模式
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
    @Override
    public void onMapLongClick(LatLng point)
    {
        LatLonPoint auto;
        auto=convertToLatLonPoint(point);
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                convertToLatLng(auto), 15));
        infoSearch(auto);
        latlngpoint=point;
        setMarker(latlngpoint);
    }
    @Override
    public void onMapClick(LatLng point)
    {
        aMap.clear();
        setUpMap();
    }
    private void setMarker(LatLng point)
    {
        aMap.clear();
        setUpMap();
        MarkerOptions markeroption=new MarkerOptions();
        markeroption.position(point);
        markeroption.anchor(0.5f, 0.5f);
        regeoMarker=aMap.addMarker(markeroption);
        markeroption.icon(BitmapDescriptorFactory
                .fromResource(R.drawable.poi_marker_pressed));
    }
    private void infoSearch(LatLonPoint latLonPoint)
    {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query);
    }
    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
    }
    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        Log.d("myinfo","myinfo"+result.getRegeocodeAddress().getCity().toString());
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                addressName = result.getRegeocodeAddress().getFormatAddress()
                        + "附近";
                addressDetail=result.getRegeocodeAddress().getCity()+result.getRegeocodeAddress().getBuilding();
                Log.d("ASYN",addressName);
                regeoMarker.setPosition(latlngpoint);
                regeoMarker.setTitle(addressDetail);
                regeoMarker.setSnippet(addressName);
                regeoMarker.showInfoWindow();
            }
        }
    }
    public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }
    public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
        return new LatLonPoint(latlon.latitude, latlon.longitude);
    }
}
