package com.youliao.downloadmanager4breakpoint.download;

import java.io.File;

/**
 * Created by admin on 2017/6/16.
 */

public interface DownLoadManager {
    public void  startDownload(File file,String path);
    public  void  onProgress(int total,int now);
    public  void  onError(Exception e);
    public  void  onComplete();
}
