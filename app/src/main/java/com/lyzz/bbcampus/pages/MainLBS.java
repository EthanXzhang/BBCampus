package com.lyzz.bbcampus.pages;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.LocationSource;
import com.lyzz.bbcampus.R;
import com.lyzz.bbcampus.missionclass.OrderQuestion;
import com.lyzz.bbcampus.order.OrderClass;
import com.lyzz.bbcampus.order.OrderRepachage;
import com.lyzz.bbcampus.service.AysncTaskPost;
import com.lyzz.bbcampus.service.LBSService;
import com.lyzz.bbcampus.service.MissionPackage;
import com.lyzz.bbcampus.viewsetting.RoundBitmap;
import com.lyzz.bbcampus.viewsetting.RoundDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainLBS extends AppCompatActivity implements LocationSource,
        AMapLocationListener, AMap.OnMarkerClickListener {
    //控件定义
    private MapView mMapView = null;
    private ImageView owerButton = null;
    private ImageView listButton = null;
    private ImageView publish = null, refresh = null, contact = null;
    private EditText searchtext = null;
    private AMap aMap;
    //初始化定位服务
    private OnLocationChangedListener mListener;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private RefreshOrder mAuthTask;
    private String latitude, longitude;
    private OrderClass[] orderpackage;
    private Marker[] markermap = new Marker[10];
    private boolean firstflag = true;

    //初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_lbs);
        /**
         * 初始化地图控件
         */
        mMapView = (MapView) findViewById(R.id.mapview);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        /**
         * 界面一般控件
         */
        searchtext = (EditText) findViewById(R.id.search);
        owerButton = (ImageView) findViewById(R.id.owerButton);
        listButton = (ImageView) findViewById(R.id.listButton);
        publish = (ImageView) findViewById(R.id.publish);
        refresh = (ImageView) findViewById(R.id.refresh);
        contact = (ImageView) findViewById(R.id.contact);
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
                if (b) {

                } else {
                    owerButton.setFocusable(true);
                }
            }
        });
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainLBS.this, Subscribe.class);
                startActivity(intent);
            }
        });
        owerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainLBS.this, OwnerPage.class);
                startActivity(intent);
                onPause();
            }
        });
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderpackage == null) {
                    Toast.makeText(MainLBS.this, "等待连接服务器", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainLBS.this, MainList.class);
                    ArrayList<OrderClass> list = new ArrayList<OrderClass>();
                    for (int i = 0; i < orderpackage.length; i++) {
                        if (orderpackage[i] != null) {
                            list.add(orderpackage[i]);
                        }
                    }
                    intent.putParcelableArrayListExtra("order", list);
                    startActivity(intent);
                    onPause();
                }
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userInfoPackage() != null) {
                    mAuthTask = new RefreshOrder(userInfoPackage());
                    mAuthTask.execute((Void) null);
                } else {
                    Toast.makeText(MainLBS.this, "请等待获取定位信息", Toast.LENGTH_LONG).show();
                }
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactus();
            }
        });
        /**
         * 初始化Amap
         */
        init();
    }

    private void contactus() {
      /*
      这里使用了 android.support.v7.app.AlertDialog.Builder
      可以直接在头部写 import android.support.v7.app.AlertDialog
      那么下面就可以写成 AlertDialog.Builder
      */
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("联系我们");
        builder.setMessage("即将拨打客服电话");
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
                Uri data = Uri.parse("tel:" + "13500000000");
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
    private String userInfoPackage()
    {
        Map device=new HashMap<String,String>();
        if(longitude!=null&&latitude!=null)
        {
            device.put("requestlocation",longitude+","+latitude);
            return MissionPackage.getPackage(device);
        }else
        {
            return null;
        }
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
        //重置定位状态
        firstflag=true;
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.gps_point));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(80, 200,250, 250));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(0.1f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
        aMap.setOnMarkerClickListener(this);
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
                latitude=String.valueOf(amapLocation.getLatitude());
                longitude=String.valueOf(amapLocation.getLongitude());
                LatLng point=new LatLng(amapLocation.getLatitude(),amapLocation.getLongitude());
                if(firstflag)
                {
                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(point,18, 30, 30)));
                    mAuthTask = new RefreshOrder(userInfoPackage());
                    mAuthTask.execute((Void) null);
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
            //设置为高精度定位模式Hight_Accuracy
            //模拟器中使用仅设备定位Device_Sensors，该模式仅使用GPS进行定位
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
    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (aMap != null) {
            OrderClass ord=(OrderClass)marker.getObject();
            Intent intent=new Intent(MainLBS.this,MissionDetail.class);
            intent.putExtra("order",ord);
            startActivity(intent);
        }
        return true;
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
    /*
    *Marker
     */
    private void setMarker()
    {
        OrderClass order;
        LatLng point;
        for(int i=0;i<orderpackage.length;i++)
        {
            if(orderpackage[i]!=null)
            {
                Resources res=getResources();
                order=orderpackage[i];
                Map<String,String> map=order.getLocation();
                point=new LatLng(Double.valueOf(map.get("latitude")).doubleValue(),Double.valueOf(map.get("longitude")));
                MarkerOptions markeroption=new MarkerOptions();
                markeroption.position(point);
                markeroption.anchor(0.5f, 0.5f);
                switch (order.ordertype)
                {
                    case "解题":markeroption.icon(BitmapDescriptorFactory.fromBitmap(RoundBitmap.getOvalBitmap(BitmapFactory.decodeResource(res,R.drawable.s1))));break;
                    case "带饭":markeroption.icon(BitmapDescriptorFactory.fromBitmap(RoundBitmap.getOvalBitmap(BitmapFactory.decodeResource(res,R.drawable.s2))));break;
                    case "维修":markeroption.icon(BitmapDescriptorFactory.fromBitmap(RoundBitmap.getOvalBitmap(BitmapFactory.decodeResource(res,R.drawable.s3))));break;
                    case "打水":markeroption.icon(BitmapDescriptorFactory.fromBitmap(RoundBitmap.getOvalBitmap(BitmapFactory.decodeResource(res,R.drawable.s4))));break;
                    case "洗涤":markeroption.icon(BitmapDescriptorFactory.fromBitmap(RoundBitmap.getOvalBitmap(BitmapFactory.decodeResource(res,R.drawable.s5))));break;
                    case "留座":markeroption.icon(BitmapDescriptorFactory.fromBitmap(RoundBitmap.getOvalBitmap(BitmapFactory.decodeResource(res,R.drawable.s6))));break;
                    case "媒体制作":markeroption.icon(BitmapDescriptorFactory.fromBitmap(RoundBitmap.getOvalBitmap(BitmapFactory.decodeResource(res,R.drawable.s7))));break;
                    case "资料":markeroption.icon(BitmapDescriptorFactory.fromBitmap(RoundBitmap.getOvalBitmap(BitmapFactory.decodeResource(res,R.drawable.s8))));break;
                    case "快递":markeroption.icon(BitmapDescriptorFactory.fromBitmap(RoundBitmap.getOvalBitmap(BitmapFactory.decodeResource(res,R.drawable.s9))));break;
                    case "借":markeroption.icon(BitmapDescriptorFactory.fromBitmap(RoundBitmap.getOvalBitmap(BitmapFactory.decodeResource(res,R.drawable.s0))));break;
                    default:markeroption.icon(BitmapDescriptorFactory.fromResource(R.drawable.send));
                }
                markermap[i]=aMap.addMarker(markeroption);
                markermap[i].setObject(order);
            }
        }
    }
    /*
    异步队列，请求服务器order数据;
    操作会获取10个order订单信息，并调用setMarker函数添加地图坐标
     */
    public class RefreshOrder extends AsyncTask<Void, Void, String> {
        private String context;
        private String url="http://112.74.41.59:3000/v1/order/getOrderInfosByLocation";
        RefreshOrder(String out)
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
                Toast.makeText(MainLBS.this,"网络未连接", Toast.LENGTH_SHORT).show();
            }
            else if(success.equals("false"))
            {
                Toast.makeText(MainLBS.this,"遇到异常，请尝试重启或联系我们", Toast.LENGTH_SHORT).show();
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
                        //添加处理函数
                        orderpackage= OrderRepachage.Repachage(array);
                        setMarker();
                    }else
                    {
                        Toast.makeText(MainLBS.this,object.getString("message"), Toast.LENGTH_SHORT).show();
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