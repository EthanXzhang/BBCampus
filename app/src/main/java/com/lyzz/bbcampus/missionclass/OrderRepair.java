package com.lyzz.bbcampus.missionclass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.lyzz.bbcampus.R;
import com.lyzz.bbcampus.pages.MainLBS;
import com.lyzz.bbcampus.pages.Subscribe;
import com.lyzz.bbcampus.service.AysncTaskPost;
import com.lyzz.bbcampus.service.MissionPackage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrderRepair extends AppCompatActivity {
    private EditText title, thing, buylocation, mylocation;
    private Spinner moneySpinner, limittimeSpinner;
    private List<Map<String, Object>> money_list;
    private ArrayList<String> datatime;
    private SimpleAdapter moneyadapter = null;
    private ArrayAdapter timeadapter=null;
    private ImageButton back;
    private Button subscribe;
    private ImageButton mylocationButton;
    private ImageView photoButton;
    private HashMap<String,String> buylocation_map,mylocation_map;
    private static String ordertype="维修";
    private  OrderSubscribeTask mAuthTask = null;
    private String photopath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_repair);
        title = (EditText) findViewById(R.id.title);
        thing = (EditText) findViewById(R.id.thingsname);
        buylocation = (EditText) findViewById(R.id.buylocationedittext);
        mylocation = (EditText) findViewById(R.id.mylocationedittext);
        moneySpinner = (Spinner) findViewById(R.id.moneyspinner);
        limittimeSpinner = (Spinner) findViewById(R.id.limittimeSpinner);
        back=(ImageButton)findViewById(R.id.back);
        initSpinner();
        subscribe=(Button)findViewById(R.id.subscribe);
        mylocationButton=(ImageButton)findViewById(R.id.mylocationbutton);
        photoButton=(ImageView)findViewById(R.id.buylocationbutton);
        mylocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OrderRepair.this, OrderDetailLocation.class);
                int requestCode=0;
                startActivityForResult(intent,requestCode);
            }
        });
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkdetail())
                {
                    Toast.makeText(OrderRepair.this,"发送中", Toast.LENGTH_SHORT).show();
                    mAuthTask = new OrderSubscribeTask(detailPackage());
                    mAuthTask.execute((Void) null);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OrderRepair.this,Subscribe.class);
                startActivity(intent);
            }
        });
    }
    private boolean checkdetail()
    {
        boolean flag=false;
        if(!TextUtils.isEmpty(title.getText().toString())&&!TextUtils.isEmpty(thing.getText().toString())&&!(mylocation_map==null))
        {
            flag=true;
        }
        else
        {
            Toast.makeText(OrderRepair.this, "请填写完整求助信息", Toast.LENGTH_SHORT).show();
        }
        return flag;
    }
    private void initSpinner() {
        initMoneySpinner();
        initLimitTimeSpinner();
    }

    private void initMoneySpinner()
    {
        money_list = new ArrayList<Map<String, Object>>();
        String[] moneytext = {"赞5帮币", "赞10帮币", "赏5元", "赏10元", "赏15元"};
        int[] icon = {R.drawable.point, R.drawable.point, R.drawable.wallet, R.drawable.wallet, R.drawable.wallet};
        for (int i = 0; i < 5; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("text", moneytext[i]);
            map.put("image", icon[i]);
            money_list.add(map);
        }
        moneyadapter = new SimpleAdapter(this, money_list, R.layout.money_item, new String[]{"text", "image"}, new int[]{R.id.text, R.id.image});
        moneySpinner.setAdapter(moneyadapter);
    }
    private void initLimitTimeSpinner()
    {
        String[] time={"30分钟","45分钟","60分钟","90分钟","120分钟"};
        datatime=new ArrayList<String>();
        for(int i=0;i<5;i++)
        {
            datatime.add(time[i]);
        }
        timeadapter=new ArrayAdapter(this,R.layout.spinner_item,datatime);
        limittimeSpinner.setAdapter(timeadapter);
    }
    private String getDoneTime()
    {
        int i=limittimeSpinner.getSelectedItemPosition();
        long[] selecttime={30*60*1000,45*60*1000,60*60*1000,90*60*1000,120*60*1000};
        return String.valueOf(selecttime[i]);
    }
    private String detailPackage()
    {
        String titletext,contexttext,bonusvaluetext="",bangbivaluetext="",limittimetext;
        titletext=title.getText().toString();
        contexttext=thing.getText().toString();
        limittimetext=getDoneTime();
        int i=moneySpinner.getSelectedItemPosition();
        switch (i)
        {
            case 1:bangbivaluetext="5";break;
            case 2:bangbivaluetext="10";break;
            case 3:bonusvaluetext="5";break;
            case 4:bonusvaluetext="10";break;
            case 5:bonusvaluetext="15";break;
        }
        SharedPreferences userstate=getApplicationContext().getSharedPreferences("userstate",0);
        String userId=userstate.getString("userId",null);
        Map<String,String> map=new HashMap<String,String>();
        map.put("orderdonetime",limittimetext);
        map.put("orderpostid",userId);
        map.put("ordertype",ordertype);
        map.put("ordertitle",titletext);
        map.put("ordercontext",contexttext);
        map.put("orderlocation",mylocation_map.get("longitude")+","+mylocation_map.get("latitude"));
        map.put("orderlocationdetail",mylocation.getText().toString());
//        map.put("orderbuylocation",buylocation_map.get("longitude")+","+buylocation_map.get("latitude"));
        map.put("orderbuylocationdetail",buylocation.getText().toString());
        map.put("orderthings",contexttext);
        map.put("orderbangbivalue",bangbivaluetext);
        map.put("orderbonusvalue",bonusvaluetext);
        String context=MissionPackage.getPackage(map);
        return context;
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        if(requestCode==0&&resultCode==0)
        {
            mylocation_map=new HashMap<String,String>();
            mylocation_map.put("latitude",data.getStringExtra("latitude"));
            mylocation_map.put("longitude",data.getStringExtra("longitude"));
            if((data.getStringExtra("address")!=null)&&(data.getStringExtra("detail")!=null))
            {
                mylocation.setText(data.getStringExtra("address")+data.getStringExtra("detail"));
                mylocation.setTextSize(12);
            }else
            {
                mylocation.setText("未检索到位置信息");
                Toast.makeText(OrderRepair.this,"可手动输入地点信息", Toast.LENGTH_SHORT).show();
            }

        }
        else if(requestCode==2)
        {
            if(data==null){
                return;//当data为空的时候，不做任何处理
            }
        }
        else if(requestCode==1)
        {
            Bitmap bitmap = null;
            //获取从相册界面返回的缩略图
            bitmap = data.getParcelableExtra("data");
            if(bitmap==null){//如果返回的图片不够大，就不会执行缩略图的代码，因此需要判断是否为null,如果是小图，直接显示原图即可
                try {
                    //通过URI得到输入流
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    //通过输入流得到bitmap对象
                    bitmap = BitmapFactory.decodeStream(inputStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            photopath=data.getData().toString();
            File file=new File(photopath);
            buylocation.setText(file.getName());
            //将选择的图片设置到控件上
            photoButton.setImageBitmap(bitmap);
        }
        else if(requestCode==0)
        {
            mylocation.setText("未选取位置");
        }
    }
    public class OrderSubscribeTask extends AsyncTask<Void, Void, String> {
        private String context;
        private String url="http://112.74.41.59:3000/v1/order/add";
        OrderSubscribeTask(String out)
        {
            context=out;
        }

        @Override
        protected String doInBackground(Void... voids) {
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
                Toast.makeText(OrderRepair.this,"网络未连接", Toast.LENGTH_SHORT).show();
            }
            else if(success.equals("false"))
            {
                Toast.makeText(OrderRepair.this,"遇到异常，请尝试重启或联系我们", Toast.LENGTH_SHORT).show();
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
                        detail=object.getJSONObject("message");
                        //添加处理函数
                        Intent intent=new Intent(OrderRepair.this,MainLBS.class);
                        startActivity(intent);
                    }else
                    {
                        Toast.makeText(OrderRepair.this,object.getString("message"), Toast.LENGTH_SHORT).show();
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