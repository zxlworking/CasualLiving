package com.zxl.casual.living.common;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.zxl.casual.living.event.UploadLogFileEvent;
import com.zxl.casual.living.http.HttpUtils;
import com.zxl.casual.living.http.listener.NetRequestListener;
import com.zxl.casual.living.http.retrofit.FileRequestBody;
import com.zxl.casual.living.http.retrofit.RetrofitCallback;
import com.zxl.casual.living.utils.Constants;
import com.zxl.casual.living.utils.EventBusUtils;
import com.zxl.common.DebugUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by zxl on 2018/11/15.
 */

public class UploadLogFileTask {
    private static final String TAG = "UploadLogFileTask";

    private static boolean isUploading = false;

    private static NetRequestListener mNetRequestListener = null;

    private static HandlerThread mHandlerThread = null;
    private static Handler mHandler = null;

    private static RetrofitCallback mRetrofitCallback = new RetrofitCallback() {
        @Override
        public void onSuccess(Call call, Response response) {
            DebugUtil.d(TAG,"RetrofitCallback::onSuccess");
        }

        @Override
        public void onLoading(long total, long progress) {
            DebugUtil.d(TAG,"RetrofitCallback::onLoading = " + (progress * 1.0 / total));

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(total == progress){
                File logDir = new File(Constants.APP_CRASH_PATH);
                File[] logFiles = logDir.listFiles();
                if(logFiles != null && logFiles.length > 0){
                    File file = logFiles[0];
                    file.delete();
                }

                mHandler.removeCallbacks(mTask);
                mHandler.postDelayed(mTask,500);
            }

            EventBusUtils.post(new UploadLogFileEvent(total,progress));
        }

        @Override
        public void onFailure(Call call, Throwable t) {
            DebugUtil.d(TAG,"RetrofitCallback::onLoading");
        }
    };

    private static Runnable mTask = new Runnable() {
        @Override
        public void run() {
            File logDir = new File(Constants.APP_CRASH_PATH);
            File[] logFiles = logDir.listFiles();
            if(logFiles != null && logFiles.length > 0){
                File file = logFiles[0];
                DebugUtil.d(TAG,"uploadFile::file = " + file.getName());

                MultipartBody.Builder builder = new MultipartBody.Builder();
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
                builder.addFormDataPart("file", file.getName(), requestBody);
                builder.setType(MultipartBody.FORM);
                MultipartBody multipartBody = builder.build();
                FileRequestBody fileRequestBody = new FileRequestBody(multipartBody,mRetrofitCallback);

                HttpUtils.getInstance().uploadFile(fileRequestBody,mNetRequestListener);
            }else{
                isUploading = false;
            }
            DebugUtil.d(TAG,"mTask::isUploading = " + isUploading);
        }
    };


    public static void start(){
        DebugUtil.d(TAG,"start::isUploading = " + isUploading);

        if(isUploading){
            return;
        }
        isUploading = true;

        if(mHandlerThread  == null){
            mHandlerThread = new HandlerThread(TAG);
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper());
        }

        mHandler.postDelayed(mTask,500);
    }

    public static void setNetRequestListener(NetRequestListener listener){
        mNetRequestListener = listener;
    }

    public static boolean isStart(){
        return isUploading;
    }
}
