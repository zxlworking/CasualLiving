package com.zxl.casual.living.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.support.v4.content.FileProvider;

import com.zxl.common.DebugUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by zxl on 2018/10/9.
 */

public class DownloadUtils {

    private static final String TAG = "DownloadUtils";

    public static final boolean checkDownloadEnable(Context context){
       int state =  context.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
       if(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
               state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER ||
               state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED){
           return false;
       }
       return true;
    }

    public static final void openDownloadEnableSettings(Context context){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("com.android.providers.downloads"));
        context.startActivity(intent);
    }

    public static final void removeDownloadApk(Context context){

    }

    public static final void download(Context context,String url){
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,Constants.UPDATE_APP_NAME);

        request.setTitle("更新");
        request.setDescription("下载更新包");
        request.setMimeType("application/vnd.android.package-archive");
        request.allowScanningByMediaScanner();
        request.setVisibleInDownloadsUi(true);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.remove(SharePreUtils.getInstance(context).getDownloadId());


        long id = downloadManager.enqueue(request);
        SharePreUtils.getInstance(context).saveDownloadId(id);
    }

    public static final void installApk(Context context, long downloadApkId) {
        DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Intent install = new Intent(Intent.ACTION_VIEW);
        Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId);

        DebugUtil.d(TAG, "installApk::downloadFileUri = " + downloadFileUri);


        if (downloadFileUri != null) {
            DebugUtil.d(TAG, "installApk::uri = " + downloadFileUri.toString());
            install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
            if ((Build.VERSION.SDK_INT >= 24)) {//判读版本是否在7.0以上
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                install.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (install.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(install);
            } else {
                DebugUtil.d(TAG, "自动安装失败，请手动安装");
            }
        } else {
            DebugUtil.d(TAG, "download error");
        }
    }

    //检查下载状态
    public static final int checkStatus(Context context, long id) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        //通过下载的id查找
        query.setFilterById(id);
        Cursor c = downloadManager.query(query);
        int status = DownloadManager.STATUS_SUCCESSFUL;
        if (c.moveToFirst()) {
            status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                //下载暂停
                case DownloadManager.STATUS_PAUSED:
                    break;
                //下载延迟
                case DownloadManager.STATUS_PENDING:
                    break;
                //正在下载
                case DownloadManager.STATUS_RUNNING:
                    break;
                //下载完成
                case DownloadManager.STATUS_SUCCESSFUL:
                    //下载完成安装APK
                    break;
                //下载失败
                case DownloadManager.STATUS_FAILED:
                    break;
            }
        }
        c.close();
        return status;
    }
}
