package com.lyzz.bbcampus.service;

import android.content.SharedPreferences;
import android.util.Log;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import org.json.JSONObject;

import java.io.File;

import static android.R.attr.data;

/**
 * Created by Administrator on 2017/5/17.
 */

public class ImagePost {
    private String path;
    private String filename;
    private File imagefile;
    //设置好账号的ACCESS_KEY和SECRET_KEY
    String ACCESS_KEY = "Access_Key";
    String SECRET_KEY = "Secret_Key";
    //要上传的空间
    String bucketname = "Bucket_Name";
    //上传到七牛后保存的文件名
    String key = "my-java.png";
    //上传文件的路径
    String filePath = "/.../...";

    //创建上传对象
    UploadManager uploadManager = new UploadManager();
    ImagePost(String filepath,String name)
    {
        path=filepath;
        filename=name;
        imagefile=new File(path);
    }

}
