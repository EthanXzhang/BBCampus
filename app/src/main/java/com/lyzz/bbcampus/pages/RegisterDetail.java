package com.lyzz.bbcampus.pages;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.lyzz.bbcampus.R;
import com.lyzz.bbcampus.service.TimeStampTrans;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterDetail extends AppCompatActivity {
    private Button cancel,selectavatar;
    private ImageButton finish;
    private EditText phone,username;
    private ArrayList<String> datayear,datamonth;
    private ArrayList<String> dataday=new ArrayList<String>();;
    private Spinner year,month,day;
    private ArrayAdapter<String> adapteryear,adaptermonth,adapterday;
    private String avatarpath;
    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_detail);
        cancel=(Button)findViewById(R.id.cancel);
        selectavatar=(Button)findViewById(R.id.selectavatar);
        finish=(ImageButton)findViewById(R.id.finish);
        phone=(EditText)findViewById(R.id.phone);
        username=(EditText)findViewById(R.id.username);
        year=(Spinner)findViewById(R.id.year);
        month=(Spinner)findViewById(R.id.month);
        day=(Spinner)findViewById(R.id.day);
        avatar=(ImageView) findViewById(R.id.avatar);
        initSpinnnerdata();
        month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dataday.clear();
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, Integer.valueOf(year.getSelectedItem().toString()));
                cal.set(Calendar.MONTH, i);
                int dayofm = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int j = 1; j <= dayofm; j++) {
                    dataday.add("" + (j < 10 ? "0" + j : j));
                }
                adapterday.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String time=year.getSelectedItem().toString()+"-"+month.getSelectedItem().toString()+"-"+day.getSelectedItem().toString()+"-00-00-00";
                Log.d("time",time);
                /*
                转换时间戳
                */
                String birth= TimeStampTrans.dataTime(time);
                Log.d("bitrh",birth);
                Intent intent=new Intent(RegisterDetail.this,RegisterActivity.class);
                String un,up;
                un=username.getText().toString();
                up=phone.getText().toString();
                intent.putExtra("username",un);
                intent.putExtra("phone",up);
                intent.putExtra("avatar",avatarpath);
                intent.putExtra("birth",birth);
                startActivity(intent);
                onPause();
            }
        });
        selectavatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegisterDetail.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 2)
        {
            if(data==null){
                return;//当data为空的时候，不做任何处理
            }
        }
            Bitmap bitmap = null;
            if(requestCode==0){
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
            }else if(requestCode==1){
                bitmap = (Bitmap) data.getExtras().get("data");
            }
            avatarpath=data.getData().toString();
            //将选择的图片设置到控件上
            avatar.setImageBitmap(bitmap);
    }

/*
*初始化Spinner
 */
    private void initSpinnnerdata()
    {
        datayear=new ArrayList<String>();
        datamonth=new ArrayList<String>();
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 40; i++) {
            datayear.add("" + (cal.get(Calendar.YEAR) - 40 + i));
        }
        for (int i = 1; i <= 12; i++) {
            datamonth.add("" + (i < 10 ? "0" + i : i));
        }
        adapteryear=new ArrayAdapter<String>(this,R.layout.timespinner_item,datayear);
        adapteryear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year.setAdapter(adapteryear);
        adaptermonth=new ArrayAdapter<String>(this,R.layout.timespinner_item,datamonth);
        adaptermonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        month.setAdapter(adaptermonth);
        adapterday = new ArrayAdapter<String>(this, R.layout.timespinner_item, dataday);
        adapterday.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day.setAdapter(adapterday);
    }


}
