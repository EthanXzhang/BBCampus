package com.lyzz.bbcampus.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.lyzz.bbcampus.R;
import com.lyzz.bbcampus.missionclass.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Subscribe extends AppCompatActivity {
    private GridView gview;
    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;
    // 图片封装为一个数组
    private int[] icon = { R.drawable.p1, R.drawable.p2,
            R.drawable.p3, R.drawable.p4, R.drawable.p5,
            R.drawable.p6, R.drawable.p7, R.drawable.p8,
            R.drawable.p9, R.drawable.p0};
    private String[] iconName = { "解题", "带饭", "维修", "打水","洗涤","留座", "媒体制作",
            "资料", "快递", "借"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        ImageButton back=(ImageButton) findViewById(R.id.button);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent(Subscribe.this,MainLBS.class);
                startActivity(intent);
            }
        });
        initGrid();
        gview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Class[] itemSelect={OrderQuestion.class,OrderFood.class, OrderRepair.class, OrderWater.class, OrderWash.class,OrderSeat.class, OrderMedia.class, OrderInform.class, OrderExpress.class, OrderBorrow.class};
                Intent intent=new Intent(Subscribe.this,itemSelect[i]);
                startActivity(intent);
            }
        });
        checkUserLogin();
    }
    private void initGrid()
    {
        gview = (GridView) findViewById(R.id.gview);
        //新建List
        data_list = new ArrayList<Map<String, Object>>();
        //获取数据
        getData();
        //新建适配器
        String [] from ={"image","text"};
        int [] to = {R.id.image,R.id.text};
        sim_adapter = new SimpleAdapter(this, data_list, R.layout.subscribe_class, from, to);
        //配置适配器
        gview.setAdapter(sim_adapter);
    }



    public List<Map<String, Object>> getData(){
        //cion和iconName的长度是相同的，这里任选其一都可以
        for(int i=0;i<icon.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }
        return data_list;
    }
    private void checkUserLogin()
    {
        SharedPreferences userstate=getApplicationContext().getSharedPreferences("userstate",0);
        int loginInt=userstate.getInt("login",0);
        //未登录
        if(loginInt==0)
        {
            Toast.makeText(Subscribe.this,"您还未登陆，请返回主菜单登陆", Toast.LENGTH_SHORT).show();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent=new Intent(Subscribe.this,MainLBS.class);
            startActivity(intent);
        }
    }
}
