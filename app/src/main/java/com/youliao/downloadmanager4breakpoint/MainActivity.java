package com.youliao.downloadmanager4breakpoint;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.youliao.downloadmanager4breakpoint.download.DownLoadManagerImpl;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
              File  mfile=new File(getExternalCacheDir().getAbsolutePath()+"/downapk.apk");
              if (!mfile.exists()) try {
                  mfile.createNewFile();
              } catch (IOException e) {
                  e.printStackTrace();
              }
              String path="https://wap3.ucweb.com/files/UCBrowser/zh-cn/999/UCBrowser_V11.5.6.946_android_pf145_(Build170614134804).apk?auth_key=1498118457-0-0-934d75a0828de311c1f173fb2ec69753&SESSID=97b4b4668e368483e869ae3ba196a32e";
              DownLoadManagerImpl.getInstance().startDownload(mfile,path);


    }
}
