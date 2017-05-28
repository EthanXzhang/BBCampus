package com.lyzz.bbcampus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.lyzz.bbcampus.pages.LoginActivity;
import com.lyzz.bbcampus.pages.MainLBS;
import com.lyzz.bbcampus.pages.OwnerPage;
import com.lyzz.bbcampus.pages.OwnerPage;
import com.lyzz.bbcampus.pages.RegisterActivity;
import com.lyzz.bbcampus.service.ImageDownload;

import java.io.File;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends AppCompatActivity {
    private ImageView welcome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        welcome=(ImageView)findViewById(R.id.welcomeimage);
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        Button welButton=(Button)findViewById(R.id.welcome);
        welButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, MainLBS.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.activity_in,R.anim.activity_fade);
            }
        });
    }

}
