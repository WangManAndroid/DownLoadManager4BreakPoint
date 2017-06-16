package com.youliao.downloadmanager4breakpoint.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.content.ContentValues.TAG;

/**
 * Created by admin on 2017/6/16.
 */

public class DownLoadManagerImpl implements DownLoadManager {
    private static Executor  excuter= Executors.newCachedThreadPool();
    private  volatile static DownLoadManagerImpl instance;
    private static Object lock=new Object();
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            onProgress(msg.arg1, msg.arg2);
        }
    };
    public static DownLoadManagerImpl getInstance(){

        if (instance==null){
            synchronized (lock){
                instance=new DownLoadManagerImpl();
            }
        }
        return instance;
    }
    @Override
    public void startDownload(final File file, final  String path) {
        excuter.execute(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                startHttp(file,path);
                Looper.loop();
            }
        });

    }

    @Override
    public void onProgress(int total, int now) {
        Log.d(TAG, Thread.currentThread().getName()+"  "+(float)now*100/total+"%");
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete: ");
    }


    private void startHttp( File file,String urlPath){
        URL url = null;
        HttpURLConnection httpURLConnection = null;
        try {
            url = new URL(urlPath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //获取HttpURLConnection对象
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //设置请求方式
        try {
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Range", "bytes=" + file.length() + "-");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
        try {
            httpURLConnection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (httpURLConnection.getResponseCode() == 206) {
                writeFile((int) (httpURLConnection.getContentLength()+file.length()),file.getAbsolutePath(),httpURLConnection.getInputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 写入指定字节的数据
     *
     * @param filePath 要读取的文件的路径
     * @return 返回读取指定字节的字节数组
     */
    private void  writeFile(int totallength,String filePath,InputStream inputStream) throws FileNotFoundException,EOFException,IOException {
        RandomAccessFile randomAccessFile = null;
        randomAccessFile = new RandomAccessFile(filePath, "rw");
        BufferedInputStream bufferedInputStream=new BufferedInputStream(inputStream);
        byte[] buffer=new byte[2048];
        long offset=randomAccessFile.length();
        randomAccessFile.seek(offset);
        int readLength;
        Log.d(TAG, "writeFile: "+totallength);  //31143200
        while ((readLength=bufferedInputStream.read(buffer))!=-1){
            randomAccessFile.write(buffer, 0, readLength);
            offset+=readLength;
            Message msg=mHandler.obtainMessage();
            msg.arg1= totallength;
            msg.arg2= (int) randomAccessFile.length();
            msg.sendToTarget();


        }
        bufferedInputStream.close();
        new File(filePath).delete();
        onComplete();
    }
}
