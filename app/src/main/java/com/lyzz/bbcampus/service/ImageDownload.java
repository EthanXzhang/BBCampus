package com.lyzz.bbcampus.service;

/**
 * Created by Administrator on 2017/3/29.
 */
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * http工具类 http可以使用HttpURLConnection或HttpClient
 *
 * @author Administrator
 * @date 2014.05.10
 * @version V1.0
 */
public class ImageDownload {
    private final static String IMG_PATH =Environment.getExternalStorageDirectory().getAbsolutePath()+"/userdata/";
    private static URL imgUrl = null;
    private static Bitmap bitmap = null;
    private static String filename;
    /**
     * 获取网络图片
     *
     * @param urlString

     */
    public static File getNetWorkBitmap(String urlString,String name) {
        filename=name;
        try {
            imgUrl = new URL(urlString);
            // 使用HttpURLConnection打开连接
            HttpURLConnection urlConn = (HttpURLConnection) imgUrl
                    .openConnection();
            urlConn.setDoInput(true);
            urlConn.connect();
            // 将得到的数据转化成InputStream
            InputStream is = urlConn.getInputStream();
            // 将InputStream转换成Bitmap
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            System.out.println("[getNetWorkBitmap->]MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("[getNetWorkBitmap->]IOException");
            e.printStackTrace();
        }
            return saveFile(bitmap);

    }
    private static File saveFile(Bitmap bm)
    {
        File imgFile=new File(IMG_PATH );
        File myCaptureFile=null;
        if (!imgFile.exists()) {
            imgFile.mkdirs();
        }
        try {
            myCaptureFile = new File(imgFile.getAbsolutePath(),filename);
            if(!myCaptureFile.exists())
            {
                myCaptureFile.createNewFile();
            }
            FileOutputStream bos = new FileOutputStream(myCaptureFile.getAbsolutePath());
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        }catch(IOException e)
        {
            e.printStackTrace();
        }
        return myCaptureFile;
    }
}
