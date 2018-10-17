package com.zxl.casual.living.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.zxl.casual.living.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zxl.common.DebugUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class CommonUtils {
    public static final String TAG = "CommonUtils";

    /** 判断是否是快速点击 */
    private static long lastClickTime;

    public static Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public static IWXAPI mIwxapi;

    public static String getVersionName(Context context){
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getVersionCode(Context context){
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int px2dip(int pxValue){
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static float dip2px(float dipValue){
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return  (dipValue * scale + 0.5f);
    }

    public static int screenWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int screenHeight(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

//    public static void loadImage(ImageView img, int defaultResId, String url){
//        img.setImageResource(R.drawable.icon_album);
//        Glide.with(SmartRadioApp.get())
//                .load(url)
//                .transition(new DrawableTransitionOptions().crossFade(500))
//                .apply(new RequestOptions().placeholder(R.drawable.icon_album)
////                        .dontAnimate()
////                        .signature(EmptySignature.obtain()))
////                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE
//                )
//                //.thumbnail(0.1f)
//                .into(img);
//    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        DebugUtil.d(TAG,"isFastDoubleClick::timeD = " + timeD);
        if (0 < timeD && timeD < 500) {

            return true;
        }
        lastClickTime = time;
        return false;

    }


    public static final void regToWX(Context context){
        mIwxapi = WXAPIFactory.createWXAPI(context,Constants.WX_APP_ID,true);
        mIwxapi.registerApp(Constants.WX_APP_ID);
    }

    public static final void sendWXMessage(BaseReq baseReq){
        mIwxapi.sendReq(baseReq);
    }

    public static final void shareWXText(String content,String desc,int scene){
        //SendMessageToWX.Req.WXSceneTimeline 设置发送到朋友圈
        //SendMessageToWX.Req.WXSceneSession 设置发送给朋友
        WXTextObject wxTextObject = new WXTextObject();
        wxTextObject.text = content;
        WXMediaMessage wxMediaMessage = new WXMediaMessage();
        wxMediaMessage.mediaObject = wxTextObject;
        wxMediaMessage.description = content;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = wxMediaMessage;
        req.scene = scene;
        CommonUtils.sendWXMessage(req);
    }

    public static final void shareWXBitmap(final Context context, final String bitmapUrl, final int scene){
//        Glide.with(context)
//                .asBitmap()
//                .load(bitmapUrl)
//                .into(new SimpleTarget<Bitmap>(CommonUtils.screenWidth(),CommonUtils.screenHeight()) {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//
//
////                      Bitmap thumbBmp = WXUtil.createBitmapThumbnail(resource, finalMQSBKViewHolder.mThumbImg.getWidth(),finalMQSBKViewHolder.mThumbImg.getHeight());
//                        Bitmap thumbBmp = WXUtil.createBitmapThumbnail(resource, 90, 90);
//
//                        WXImageObject wxImageObject = new WXImageObject(resource);
//                        resource.recycle();
//
//                        WXMediaMessage wxMediaMessage = new WXMediaMessage();
//                        wxMediaMessage.mediaObject = wxImageObject;
//                        wxMediaMessage.thumbData = WXUtil.bmpToByteArray(thumbBmp,true);
//
//                        SendMessageToWX.Req req = new SendMessageToWX.Req();
//                        req.transaction = String.valueOf(System.currentTimeMillis())+"img";
//                        req.message = wxMediaMessage;
//                        req.scene = scene;
//                        CommonUtils.sendWXMessage(req);
//
//                        loadingView.setVisibility(View.GONE);
//                    }
//                });

        new AsyncTask<Void, Integer, String>() {

            @Override
            protected String doInBackground(Void... params) {
                File file = null;
                try {
                    FutureTarget<File> future = Glide
                            .with(context)
                            .load(bitmapUrl)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

                    file = future.get();

                    // 首先保存图片
                    File pictureFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile();

                    File appDir = new File(pictureFolder ,"test_weather");
                    if (!appDir.exists()) {
                        appDir.mkdirs();
                    }
                    String fileName = bitmapUrl.substring(bitmapUrl.lastIndexOf("/"));
                    File destFile = new File(appDir, fileName);

                    FileInputStream fis = new FileInputStream(file);
                    FileOutputStream fos = new FileOutputStream(destFile);
                    byte buffer[] = new byte[1024];
                    int count = 0;
                    while((count = fis.read(buffer)) != -1){
                        fos.write(buffer,0,count);
                    }
                    fos.close();
                    fis.close();

                    return destFile.getPath();
                } catch (Exception e) {
                    DebugUtil.d(TAG, e.getMessage());
                }
                return "";
            }

            @Override
            protected void onPostExecute(String path) {
                DebugUtil.d(TAG,"onPostExecute::path = " + path);
                Bitmap thumbBmp = WXUtil.createBitmapThumbnail(path, Constants.THUMB_SIZE, Constants.THUMB_SIZE);

                WXImageObject wxImageObject = new WXImageObject();
                wxImageObject.imagePath = path;

                WXMediaMessage wxMediaMessage = new WXMediaMessage();
                wxMediaMessage.mediaObject = wxImageObject;
                wxMediaMessage.thumbData = WXUtil.bmpToByteArray(thumbBmp,true);
                wxMediaMessage.title = "title";
                wxMediaMessage.description = "description";

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = String.valueOf(System.currentTimeMillis())+"img";
                req.message = wxMediaMessage;
                req.scene = scene;
                CommonUtils.sendWXMessage(req);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }
        }.execute();
    }
}
