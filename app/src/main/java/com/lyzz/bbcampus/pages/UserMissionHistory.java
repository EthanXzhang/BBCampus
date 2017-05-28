package com.lyzz.bbcampus.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lyzz.bbcampus.R;
import com.lyzz.bbcampus.order.OrderClass;
import com.lyzz.bbcampus.order.OrderRepachage;
import com.lyzz.bbcampus.service.AysncTaskPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lyzz.bbcampus.R.id.backButton;

public class UserMissionHistory extends AppCompatActivity {
    private ListView listview;
    private List<Map<String,Object>> list;
    private SimpleAdapter simpleadapter;
    private TextView uservalue;
    private getUserOrder getuserorder;
    private OrderClass[] orderpackage;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_wallet);
        listview=(ListView)findViewById(R.id.historylistview);
        uservalue=(TextView)findViewById(R.id.value);
        backButton=(ImageButton)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserMissionHistory.this,OwnerPage.class);
                startActivity(intent);
                finish();
            }
        });
        SharedPreferences userstate=getApplicationContext().getSharedPreferences("userstate",0);
        int loginInt=userstate.getInt("login",0);
        if (loginInt!=0)
        {
            SharedPreferences user=getSharedPreferences("userstate",0);
            getuserorder=new getUserOrder("receiverId="+user.getString("userId",""));
            getuserorder.execute((Void)null);
        }
        uservalue.setText(userstate.getString("userBonus",""));
    }
    private void init()
    {
        list=new ArrayList<>();
        for(int i=0;i<10&&orderpackage[i]!=null;i++)
        {
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("ordertitle",orderpackage[i].ordertitle);
            map.put("orderdonetime",orderpackage[i].orderdonetime);
            if(orderpackage[i].orderbonusvalue!=null)
            {
                map.put("value","赏金"+orderpackage[i].orderbonusvalue);
            }else if(orderpackage[i].orderbangbivalue!=null)
            {
                map.put("value","帮币"+orderpackage[i].orderbangbivalue);
            }else
            {
                map.put("value","获得积分");
            }
            list.add(map);
        }
        simpleadapter=new SimpleAdapter(this,list,R.layout.history_item,new String[]{"ordertitle","orderdonetime","value"},new int[]{R.id.ordertitle,R.id.ordertime,R.id.value});
        listview.setAdapter(simpleadapter);
    }
    /*
异步队列，获得用户订单信息;
*/
    public class getUserOrder extends AsyncTask<Void, Void, String> {
        private String context;
        private String url="http://112.74.41.59:3000/v1/order/getOrderInfosByPosterId";
        getUserOrder(String out)
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
            getuserorder = null;
            if (success.equals("error")) {
                Toast.makeText(UserMissionHistory.this,"网络未连接", Toast.LENGTH_SHORT).show();
            }
            else if(success.equals("false"))
            {
                Toast.makeText(UserMissionHistory.this,"遇到异常，请尝试重启或联系我们", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(UserMissionHistory.this,"接受成功，跳转任务追踪页面",Toast.LENGTH_SHORT);
                        JSONArray array;
                        array=new JSONArray(object.getString("message"));
                        //添加处理函数
                        orderpackage= OrderRepachage.Repachage(array);
                        //添加处理函数
                        init();
                    }else
                    {
                        Toast.makeText(UserMissionHistory.this,object.getString("message"), Toast.LENGTH_SHORT).show();
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
            getuserorder = null;
        }
    }
}
