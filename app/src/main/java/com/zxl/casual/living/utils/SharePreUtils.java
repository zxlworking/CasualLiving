package com.zxl.casual.living.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.zxl.casual.living.http.data.CityInfoListResponseBean;
import com.zxl.casual.living.http.data.UserInfoResponseBean;

/**
 * Created by zxl on 2018/9/14.
 */

public class SharePreUtils {

    private static final String S_F_NAME = "test_weather";

    private static SharePreUtils mSharePreUtils;

    private static SharedPreferences mSharedPreferences;

    private static Object mLock = new Object();

    private SharePreUtils(Context context){
        mSharedPreferences = context.getSharedPreferences(S_F_NAME,Context.MODE_PRIVATE);
    }

    public static SharePreUtils getInstance(Context context){
        if(null == mSharePreUtils){
            synchronized (mLock){
                if(null == mSharePreUtils){
                    mSharePreUtils = new SharePreUtils(context);
                }
            }
        }
        return mSharePreUtils;
    }

    public void saveCityInfoList(CityInfoListResponseBean cityInfoListResponseBean){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("CityInfoList",CommonUtils.mGson.toJson(cityInfoListResponseBean));
        editor.commit();
    }

    public CityInfoListResponseBean getCityInfoList(){
        String str = mSharedPreferences.getString("CityInfoList","");
        if(TextUtils.isEmpty(str)){
            return null;
        }else{
            return CommonUtils.mGson.fromJson(str,CityInfoListResponseBean.class);
        }
    }

    public void saveUserInfo(UserInfoResponseBean userInfoResponseBean){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if(userInfoResponseBean != null){
            editor.putString("UserInfo",CommonUtils.mGson.toJson(userInfoResponseBean));
        }else{
            editor.putString("UserInfo","");
        }
        editor.commit();
    }

    public UserInfoResponseBean getUserInfo(){
        String str = mSharedPreferences.getString("UserInfo","");
        if(TextUtils.isEmpty(str)){
            return null;
        }else{
            return CommonUtils.mGson.fromJson(str,UserInfoResponseBean.class);
        }
    }

    public void saveDownloadId(long id){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong("DownloadId",id);
        editor.commit();
    }

    public long getDownloadId(){
        return mSharedPreferences.getLong("DownloadId",0);
    }

}
