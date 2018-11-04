package com.zxl.casual.living.custom.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.zxl.casual.living.CityInfoListActivity;
import com.zxl.casual.living.R;
import com.zxl.casual.living.event.LocatePermissionSuccessEvent;
import com.zxl.casual.living.event.RequestLocatePermissionEvent;
import com.zxl.casual.living.event.SelectCityEvent;
import com.zxl.casual.living.http.HttpUtils;
import com.zxl.casual.living.http.data.ResponseBaseBean;
import com.zxl.casual.living.http.data.TodayWeatherResponseBean;
import com.zxl.casual.living.http.listener.NetRequestListener;
import com.zxl.casual.living.utils.CommonUtils;
import com.zxl.casual.living.utils.EventBusUtils;
import com.zxl.common.DebugUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by zxl on 2018/9/5.
 */

public class TodayWeatherView extends CardView {

    private static final String TAG = "TodayWeatherView";

    private Context mContext;



    private LocationManager mLocationManager;
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            DebugUtil.d(TAG, "onLocationChanged::isNeedLoadData = " + isNeedLoadData + "::location = " + location);
            if (isNeedLoadData) {
                mLocationInfo = location.getLatitude() + "," + location.getLongitude();
//                getDataFromNetByLocation(mLocationInfo);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private LocationClient mLocationClient = null;
    private BDAbstractLocationListener mBdAbstractLocationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            DebugUtil.d(TAG,"mBdAbstractLocationListener::onReceiveLocation::bdLocation = " + bdLocation);
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            double latitude = bdLocation.getLatitude();    //获取纬度信息
            double longitude = bdLocation.getLongitude();    //获取经度信息
            float radius = bdLocation.getRadius();    //获取定位精度，默认值为0.0f
            DebugUtil.d(TAG,"mBdAbstractLocationListener::onReceiveLocation::latitude = " + latitude + "::longitude = " + longitude);

            String coorType = bdLocation.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

            int errorCode = bdLocation.getLocType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明

            String addr = bdLocation.getAddrStr();    //获取详细地址信息
            String country = bdLocation.getCountry();    //获取国家
            String province = bdLocation.getProvince();    //获取省份
            String city = bdLocation.getCity();    //获取城市
            String district = bdLocation.getDistrict();    //获取区县
            String street = bdLocation.getStreet();    //获取街道信息
            DebugUtil.d(TAG,"mBdAbstractLocationListener::onReceiveLocation::addr = " + addr + "::city = " + city);

            mLocationClient.stop();

            HttpUtils.getInstance().getZHTianQiByCity(mContext, addr, city , new NetRequestListener() {
                @Override
                public void onSuccess(ResponseBaseBean responseBaseBean) {
                    DebugUtil.d(TAG,"onSuccess::responseBaseBean = " + responseBaseBean);

                    TodayWeatherResponseBean todayWeatherResponseBean = (TodayWeatherResponseBean) responseBaseBean;

                    mLoadingView.setVisibility(GONE);
                    mLoadErrorView.setVisibility(GONE);
                    mTodayWeatherContentView.setVisibility(VISIBLE);

                    setDataToView(todayWeatherResponseBean);

                    isLoading = false;
                    isNeedLoadData = false;
                }

                @Override
                public void onNetError() {
                    DebugUtil.d(TAG,"onNetError");
                    mLoadingView.setVisibility(GONE);
                    mLoadErrorView.setVisibility(VISIBLE);
                    mTodayWeatherContentView.setVisibility(INVISIBLE);
                    mLoadErrorTv.setText(R.string.no_network_tip);

                    isLoading = false;
                }

                @Override
                public void onNetError(Throwable e) {
                    DebugUtil.d(TAG,"onNetError::e = " + e);
                    mLoadingView.setVisibility(GONE);
                    mLoadErrorView.setVisibility(VISIBLE);
                    mTodayWeatherContentView.setVisibility(INVISIBLE);
                    mLoadErrorTv.setText(mContext.getResources().getString(R.string.network_error_tip, ""));

                    isLoading = false;
                }

                @Override
                public void onServerError(ResponseBaseBean responseBaseBean) {
                    DebugUtil.d(TAG,"onServerError::responseBaseBean = " + responseBaseBean);
                    mLoadingView.setVisibility(GONE);
                    mLoadErrorView.setVisibility(VISIBLE);
                    mTodayWeatherContentView.setVisibility(INVISIBLE);
                    mLoadErrorTv.setText(mContext.getResources().getString(R.string.server_error_tip, responseBaseBean.desc));

                    isLoading = false;
                }
            });
        }
    };
    private LocationClientOption mLocationClientOption = new LocationClientOption();

    private String mLocationInfo = "";

    private View mContentView;

    private View mTodayWeatherContentView;
    private View mLoadingView;
    private View mLoadErrorView;
    private TextView mLoadErrorTv;
    private Button mLoadErrorBtn;

    private LinearLayout mAddressInfoLl;
    private TextView mAddressInfo;
    private TextView mNowTimeTv;
    private TodayWeatherTemperatureView mTodayWeatherTemperatureView;
    private TextView mTemperatureTv;
    private TodayWeatherHumidityIconView mTodayWeatherHumidityIconView;
    private TextView mHumidityTv;
    private TodayWeatherWindIconView mTodayWeatherWindIconView;
    private TextView mWindTv;
    private TodayWeatherAirQualityIconView mTodayWeatherAirQualityIconView;
    private TextView mAirQualityTv;
    private LinearLayout mLimitContentLl;
    private TodayWeatherLimitIconView mTodayWeatherLimitIconView;
    private TextView mLimitTv;

    private TextView mTodayWeatherDetail1TitleTv;
    private TodayWeatherDetailIconView mTodayWeatherDetail1IconView;
    private TextView mTodayWeatherDetail1TemperatureTv;
    private TextView mTodayWeatherDetail1WeatherTv;
    private TextView mTodayWeatherDetail1WeatherDescTv;
    private TodayWeatherDetailWindIconView mTodayWeatherDetail1WindIconView;
    private TextView mTodayWeatherDetail1WindTv;
    private TodayWeatherDetailSunIconView mTodayWeatherDetail1SunIconView;
    private TextView mTodayWeatherDetail1SunTimeTv;

    private TextView mTodayWeatherDetail2TitleTv;
    private TodayWeatherDetailIconView mTodayWeatherDetail2IconView;
    private TextView mTodayWeatherDetail2TemperatureTv;
    private TextView mTodayWeatherDetail2WeatherTv;
    private TextView mTodayWeatherDetail2WeatherDescTv;
    private TodayWeatherDetailWindIconView mTodayWeatherDetail2WindIconView;
    private TextView mTodayWeatherDetail2WindTv;
    private TodayWeatherDetailSunIconView mTodayWeatherDetail2SunIconView;
    private TextView mTodayWeatherDetail2SunTimeTv;

    public ImageView mShareWechatFriendImg;
    public ImageView mShareWechatFriendsImg;

    private Toolbar mToolbar;

    private boolean isLoading = false;
    private boolean isNeedLoadData = true;


    public TodayWeatherView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public TodayWeatherView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TodayWeatherView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        DebugUtil.d(TAG, "init");
        mContext = context;

        mContentView = LayoutInflater.from(context).inflate(R.layout.today_weather_view, this);

        mTodayWeatherContentView = mContentView.findViewById(R.id.today_weather_content_view);
        mLoadingView = mContentView.findViewById(R.id.today_weather_loading_view);
        mLoadErrorView = mContentView.findViewById(R.id.today_weather_load_error_view);
        mLoadErrorTv = mLoadErrorView.findViewById(R.id.load_error_tv);
        mLoadErrorBtn = mLoadErrorView.findViewById(R.id.load_error_btn);

        mAddressInfoLl = mContentView.findViewById(R.id.address_info_ll);
        mAddressInfo = mContentView.findViewById(R.id.address_info);
        mNowTimeTv = mContentView.findViewById(R.id.now_time_tv);
        mTodayWeatherTemperatureView = mContentView.findViewById(R.id.today_weather_temperature_view);
        mTemperatureTv = mContentView.findViewById(R.id.temperature_tv);
        mTodayWeatherHumidityIconView = mContentView.findViewById(R.id.today_weather_humidity_icon_view);
        mHumidityTv = mContentView.findViewById(R.id.humidity_tv);
        mTodayWeatherWindIconView = mContentView.findViewById(R.id.today_weather_wind_icon_view);
        mWindTv = mContentView.findViewById(R.id.wind_tv);
        mTodayWeatherAirQualityIconView = mContentView.findViewById(R.id.today_weather_air_quality_icon_view);
        mAirQualityTv = mContentView.findViewById(R.id.air_quality_tv);
        mLimitContentLl = mContentView.findViewById(R.id.limit_content_ll);
        mTodayWeatherLimitIconView = mContentView.findViewById(R.id.today_weather_limit_icon_view);
        mLimitTv = mContentView.findViewById(R.id.limit_tv);

        mTodayWeatherDetail1TitleTv = mContentView.findViewById(R.id.today_weather_detail_1_title_tv);
        mTodayWeatherDetail1IconView = mContentView.findViewById(R.id.today_weather_detail_1_icon_view);
        mTodayWeatherDetail1TemperatureTv = mContentView.findViewById(R.id.today_weather_detail_1_temperature_tv);
        mTodayWeatherDetail1WeatherTv = mContentView.findViewById(R.id.today_weather_detail_1_weather_tv);
        mTodayWeatherDetail1WeatherDescTv = mContentView.findViewById(R.id.today_weather_detail_1_weather_desc_tv);
        mTodayWeatherDetail1WindIconView = mContentView.findViewById(R.id.today_weather_detail_1_wind_icon_view);
        mTodayWeatherDetail1WindTv = mContentView.findViewById(R.id.today_weather_detail_1_wind_tv);
        mTodayWeatherDetail1SunIconView = mContentView.findViewById(R.id.today_weather_detail_1_sun_icon_view);
        mTodayWeatherDetail1SunTimeTv = mContentView.findViewById(R.id.today_weather_detail_1_sun_time_tv);

        mTodayWeatherDetail2TitleTv = mContentView.findViewById(R.id.today_weather_detail_2_title_tv);
        mTodayWeatherDetail2IconView = mContentView.findViewById(R.id.today_weather_detail_2_icon_view);
        mTodayWeatherDetail2TemperatureTv = mContentView.findViewById(R.id.today_weather_detail_2_temperature_tv);
        mTodayWeatherDetail2WeatherTv = mContentView.findViewById(R.id.today_weather_detail_2_weather_tv);
        mTodayWeatherDetail2WeatherDescTv = mContentView.findViewById(R.id.today_weather_detail_2_weather_desc_tv);
        mTodayWeatherDetail2WindIconView = mContentView.findViewById(R.id.today_weather_detail_2_wind_icon_view);
        mTodayWeatherDetail2WindTv = mContentView.findViewById(R.id.today_weather_detail_2_wind_tv);
        mTodayWeatherDetail2SunIconView = mContentView.findViewById(R.id.today_weather_detail_2_sun_icon_view);
        mTodayWeatherDetail2SunTimeTv = mContentView.findViewById(R.id.today_weather_detail_2_sun_time_tv);

        mShareWechatFriendImg = mContentView.findViewById(R.id.share_wechat_friend_img);
        mShareWechatFriendsImg = mContentView.findViewById(R.id.share_wechat_friends_img);

        mLoadErrorBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //getDataFromNetByLocation(mLocationInfo);
                doLocate();
            }
        });

        mAddressInfoLl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CityInfoListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        mShareWechatFriendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTodayWeatherContentView.setDrawingCacheEnabled(true);
//                mTodayWeatherContentView.measure(
//                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
//                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//                mTodayWeatherContentView.layout(0, 0, mTodayWeatherContentView.getMeasuredWidth(),
//                        mTodayWeatherContentView.getMeasuredHeight());
                mTodayWeatherContentView.buildDrawingCache();
                Bitmap bitmap = Bitmap.createBitmap(mTodayWeatherContentView.getDrawingCache());

                // 首先保存图片
                File pictureFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile();

                File appDir = new File(pictureFolder ,"test_weather");
                if (!appDir.exists()) {
                    appDir.mkdirs();
                }
                String fileName = "today_weather.png";
                try {
                    File destFile = new File(appDir, fileName);
                    if(destFile.exists()){
                        destFile.delete();
                            destFile.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(destFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                    CommonUtils.shareWXBitmap(mContext, destFile.getAbsolutePath(), SendMessageToWX.Req.WXSceneTimeline);
    //                mTodayWeatherContentView.setDrawingCacheEnabled(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        mShareWechatFriendsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTodayWeatherContentView.setDrawingCacheEnabled(true);
//                mTodayWeatherContentView.measure(
//                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
//                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//                mTodayWeatherContentView.layout(0, 0, mTodayWeatherContentView.getMeasuredWidth(),
//                        mTodayWeatherContentView.getMeasuredHeight());
                mTodayWeatherContentView.buildDrawingCache();
                Bitmap bitmap= Bitmap.createBitmap(mTodayWeatherContentView.getDrawingCache());

                // 首先保存图片
                File pictureFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile();

                File appDir = new File(pictureFolder ,"test_weather");
                if (!appDir.exists()) {
                    appDir.mkdirs();
                }
                String fileName = "today_weather.png";
                try {
                    File destFile = new File(appDir, fileName);
                    if(destFile.exists()){
                        destFile.delete();
                        destFile.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(destFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                    CommonUtils.shareWXBitmap(mContext, destFile.getAbsolutePath(), SendMessageToWX.Req.WXSceneSession);
                    //                mTodayWeatherContentView.setDrawingCacheEnabled(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        boolean isNeedStartRequestPermissionActivity = false;

        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
            isNeedStartRequestPermissionActivity = true;
        }

        DebugUtil.d(TAG,"init::isNeedStartRequestPermissionActivity = " + isNeedStartRequestPermissionActivity);


        if (isNeedStartRequestPermissionActivity) {
            EventBusUtils.post(new RequestLocatePermissionEvent());
        } else {
            doLocate();
//            mLocationInfo = "31.950454,118.809312";
//            getDataFromNetByLocation(mLocationInfo);
        }
    }

//    private void getDataFromNetByLocation(String l) {
//        DebugUtil.d(TAG,"getDataFromNetByLocation::isLoading = " + isLoading);
//        if (isLoading) {
//            return;
//        }
//        isLoading = true;
//
//        mLoadingView.setVisibility(VISIBLE);
//        mLoadErrorView.setVisibility(GONE);
//        mTodayWeatherContentView.setVisibility(INVISIBLE);
//
//        HttpUtils.getInstance().getZHTianQiByLocation(mContext, l, new NetRequestListener() {
//            @Override
//            public void onSuccess(ResponseBaseBean responseBaseBean) {
//                DebugUtil.d(TAG,"onSuccess::responseBaseBean = " + responseBaseBean);
//
//                TodayWeatherResponseBean todayWeatherResponseBean = (TodayWeatherResponseBean) responseBaseBean;
//
//                mLoadingView.setVisibility(GONE);
//                mLoadErrorView.setVisibility(GONE);
//                mTodayWeatherContentView.setVisibility(VISIBLE);
//
//                setDataToView(todayWeatherResponseBean);
//
//                isLoading = false;
//                isNeedLoadData = false;
//            }
//
//            @Override
//            public void onNetError() {
//                DebugUtil.d(TAG,"onNetError");
//                mLoadingView.setVisibility(GONE);
//                mLoadErrorView.setVisibility(VISIBLE);
//                mTodayWeatherContentView.setVisibility(INVISIBLE);
//                mLoadErrorTv.setText(R.string.no_network_tip);
//
//                isLoading = false;
//            }
//
//            @Override
//            public void onNetError(Throwable e) {
//                DebugUtil.d(TAG,"onNetError::e = " + e);
//                mLoadingView.setVisibility(GONE);
//                mLoadErrorView.setVisibility(VISIBLE);
//                mTodayWeatherContentView.setVisibility(INVISIBLE);
//                mLoadErrorTv.setText(mContext.getResources().getString(R.string.network_error_tip, ""));
//
//                isLoading = false;
//            }
//
//            @Override
//            public void onServerError(ResponseBaseBean responseBaseBean) {
//                DebugUtil.d(TAG,"onServerError::responseBaseBean = " + responseBaseBean);
//                mLoadingView.setVisibility(GONE);
//                mLoadErrorView.setVisibility(VISIBLE);
//                mTodayWeatherContentView.setVisibility(INVISIBLE);
//                mLoadErrorTv.setText(mContext.getResources().getString(R.string.server_error_tip, responseBaseBean.desc));
//
//                isLoading = false;
//            }
//        });
//    }

    private void getDataFromNetByCity(final String city) {
        DebugUtil.d(TAG,"getDataFromNetByCity::isLoading = " + isLoading);
        if (isLoading) {
            return;
        }
        isLoading = true;

        mLoadingView.setVisibility(VISIBLE);
        mLoadErrorView.setVisibility(GONE);
        mTodayWeatherContentView.setVisibility(INVISIBLE);

        HttpUtils.getInstance().getZHTianQiByCity(mContext, "", city, new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {
                DebugUtil.d(TAG,"onSuccess::responseBaseBean = " + responseBaseBean);

                TodayWeatherResponseBean todayWeatherResponseBean = (TodayWeatherResponseBean) responseBaseBean;

                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(GONE);
                mTodayWeatherContentView.setVisibility(VISIBLE);

                setDataToView(todayWeatherResponseBean);

                isLoading = false;
                isNeedLoadData = false;
            }

            @Override
            public void onNetError() {
                DebugUtil.d(TAG,"onNetError");
                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(VISIBLE);
                mTodayWeatherContentView.setVisibility(INVISIBLE);
                mLoadErrorTv.setText(R.string.no_network_tip);

                setToolbarTitle("获取"+city+"天气失败");

                isLoading = false;
            }

            @Override
            public void onNetError(Throwable e) {
                DebugUtil.d(TAG,"onNetError::e = " + e);
                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(VISIBLE);
                mTodayWeatherContentView.setVisibility(INVISIBLE);
                mLoadErrorTv.setText(mContext.getResources().getString(R.string.network_error_tip, ""));

                setToolbarTitle("获取"+city+"天气失败");

                isLoading = false;
            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {
                DebugUtil.d(TAG,"onServerError::responseBaseBean = " + responseBaseBean);
                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(VISIBLE);
                mTodayWeatherContentView.setVisibility(INVISIBLE);
                mLoadErrorTv.setText(mContext.getResources().getString(R.string.server_error_tip, responseBaseBean.desc));

                setToolbarTitle("获取"+city+"天气失败");

                isLoading = false;
            }
        });
    }


    private void setDataToView(TodayWeatherResponseBean todayWeatherResponseBean) {
        setToolbarTitle(todayWeatherResponseBean.today_weather.simple_content);

        String addr = todayWeatherResponseBean.address_info;
        DebugUtil.d(TAG,"setDataToView::addr = " + addr);
        if(!TextUtils.isEmpty(addr) && addr.contains(todayWeatherResponseBean.city_name)){
            addr = addr.substring(addr.indexOf(todayWeatherResponseBean.city_name));
        }else{
            addr = todayWeatherResponseBean.city_name;
        }
        mAddressInfo.setText(addr);
        mNowTimeTv.setText(todayWeatherResponseBean.today_weather.now_time);
        mTodayWeatherTemperatureView.setTodayWeatherTemperatureIconCss(todayWeatherResponseBean.today_weather.temperature_icon_css);
        mTemperatureTv.setText(todayWeatherResponseBean.today_weather.temperature + "°C");
        if(todayWeatherResponseBean.today_weather.is_h == 1){
            mHumidityTv.setVisibility(VISIBLE);
            mTodayWeatherHumidityIconView.setVisibility(VISIBLE);

            mHumidityTv.setText("相对湿度 " + todayWeatherResponseBean.today_weather.humidity);
            mTodayWeatherHumidityIconView.setTodayWeatherHumidityIconCss(todayWeatherResponseBean.today_weather.humidity_icon_css);
        }else {
            mHumidityTv.setVisibility(INVISIBLE);
            mTodayWeatherHumidityIconView.setVisibility(INVISIBLE);
        }
        if(todayWeatherResponseBean.today_weather.is_w == 1) {
            mTodayWeatherWindIconView.setVisibility(VISIBLE);
            mWindTv.setVisibility(VISIBLE);

            mTodayWeatherWindIconView.setTodayWeatherHumidityIconCss(todayWeatherResponseBean.today_weather.wind_icon_css);
            mWindTv.setText(todayWeatherResponseBean.today_weather.wind_direction + " " + todayWeatherResponseBean.today_weather.wind_value);
        }else{
            mTodayWeatherWindIconView.setVisibility(INVISIBLE);
            mWindTv.setVisibility(INVISIBLE);
        }
        if(todayWeatherResponseBean.today_weather.is_pol == 1) {
            mTodayWeatherAirQualityIconView.setVisibility(VISIBLE);
            mAirQualityTv.setVisibility(VISIBLE);

            mTodayWeatherAirQualityIconView.setTodayWeatherHumidityIconCss(todayWeatherResponseBean.today_weather.air_quality_icon_css);
            mAirQualityTv.setText(todayWeatherResponseBean.today_weather.air_quality);
        }else{
            mTodayWeatherAirQualityIconView.setVisibility(INVISIBLE);
            mAirQualityTv.setVisibility(INVISIBLE);
        }
        if(todayWeatherResponseBean.today_weather.is_limit == 1){
            mLimitContentLl.setVisibility(VISIBLE);

            mTodayWeatherLimitIconView.setTodayWeatherHumidityIconCss(todayWeatherResponseBean.today_weather.limit_icon_css);
            mLimitTv.setText(todayWeatherResponseBean.today_weather.limit_content);
        }else{
            mLimitContentLl.setVisibility(INVISIBLE);
        }

        mTodayWeatherDetail1TitleTv.setText(todayWeatherResponseBean.today_weather_detail.get(0).title);
        mTodayWeatherDetail1IconView.setTodayWeatherDetailIconCss(todayWeatherResponseBean.today_weather_detail.get(0).weather_icon_css);
        mTodayWeatherDetail1TemperatureTv.setText(todayWeatherResponseBean.today_weather_detail.get(0).temperature + "°C");
        mTodayWeatherDetail1WeatherTv.setText(todayWeatherResponseBean.today_weather_detail.get(0).weather);
        String weatherDesc1 = todayWeatherResponseBean.today_weather_detail.get(0).weather_desc;
        if (!TextUtils.isEmpty(weatherDesc1)) {
            mTodayWeatherDetail1WeatherDescTv.setVisibility(VISIBLE);
            mTodayWeatherDetail1WeatherDescTv.setText(weatherDesc1);
        } else {
            mTodayWeatherDetail1WeatherDescTv.setVisibility(GONE);
        }
        mTodayWeatherDetail1WindIconView.setTodayWeatherHumidityIconCss(todayWeatherResponseBean.today_weather_detail.get(0).wind_icon_css);
        mTodayWeatherDetail1WindTv.setText(todayWeatherResponseBean.today_weather_detail.get(0).wind_direction + " " + todayWeatherResponseBean.today_weather_detail.get(0).wind_value);
        mTodayWeatherDetail1SunIconView.setTodayWeatherHumidityIconCss(todayWeatherResponseBean.today_weather_detail.get(0).sun_icon_css);
        mTodayWeatherDetail1SunTimeTv.setText(todayWeatherResponseBean.today_weather_detail.get(0).sun_time);

        mTodayWeatherDetail2TitleTv.setText(todayWeatherResponseBean.today_weather_detail.get(1).title);
        mTodayWeatherDetail2IconView.setTodayWeatherDetailIconCss(todayWeatherResponseBean.today_weather_detail.get(1).weather_icon_css);
        mTodayWeatherDetail2TemperatureTv.setText(todayWeatherResponseBean.today_weather_detail.get(1).temperature + "°C");
        mTodayWeatherDetail2WeatherTv.setText(todayWeatherResponseBean.today_weather_detail.get(1).weather);
        String weatherDesc2 = todayWeatherResponseBean.today_weather_detail.get(1).weather_desc;
        if (!TextUtils.isEmpty(weatherDesc2)) {
            mTodayWeatherDetail2WeatherDescTv.setVisibility(VISIBLE);
            mTodayWeatherDetail2WeatherDescTv.setText(weatherDesc2);
        } else {
            mTodayWeatherDetail2WeatherDescTv.setVisibility(GONE);
        }
        mTodayWeatherDetail2WindIconView.setTodayWeatherHumidityIconCss(todayWeatherResponseBean.today_weather_detail.get(1).wind_icon_css);
        mTodayWeatherDetail2WindTv.setText(todayWeatherResponseBean.today_weather_detail.get(1).wind_direction + " " + todayWeatherResponseBean.today_weather_detail.get(1).wind_value);
        mTodayWeatherDetail2SunIconView.setTodayWeatherHumidityIconCss(todayWeatherResponseBean.today_weather_detail.get(1).sun_icon_css);
        mTodayWeatherDetail2SunTimeTv.setText(todayWeatherResponseBean.today_weather_detail.get(1).sun_time);
    }


    public void doLocate() {
        DebugUtil.d(TAG,"doLocate");

        mLoadingView.setVisibility(VISIBLE);
        mLoadErrorView.setVisibility(GONE);
        mTodayWeatherContentView.setVisibility(INVISIBLE);

//        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//
//        DebugUtil.d(TAG,"can doLocate");
//
//        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
//        if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//            DebugUtil.d(TAG, "NETWORK_PROVIDER");
//            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 6 * 60 * 1000, 10, mLocationListener);
//        }else if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//            DebugUtil.d(TAG,"GPS_PROVIDER");
//            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6 * 60 * 1000, 10, mLocationListener);
//        }else{
//            //将手机位置服务中--基于网络的位置服务关闭后，则获取不到数据
//            DebugUtil.d(TAG,"NETWORK_PROVIDER不可用，无法获取GPS信息!");
//        }

        //声明LocationClient类
        mLocationClient = new LocationClient(mContext.getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(mBdAbstractLocationListener);

        mLocationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        mLocationClientOption.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；
        //BD09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

        mLocationClientOption.setScanSpan(60000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        mLocationClientOption.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        mLocationClientOption.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        mLocationClientOption.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        mLocationClientOption.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        mLocationClientOption.setWifiCacheTimeOut(5*60*1000);
        //可选，V7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位

        mLocationClientOption.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        mLocationClientOption.setIsNeedAddress(true);

        mLocationClient.setLocOption(mLocationClientOption);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明

        mLocationClient.start();
        //mLocationClient为第二步初始化过的LocationClient对象
        //调用LocationClient的start()方法，便可发起定位请求
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        DebugUtil.d(TAG,"onAttachedToWindow");
        EventBusUtils.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DebugUtil.d(TAG,"onDetachedFromWindow");
        EventBusUtils.unregister(this);

        mLocationClient.unRegisterLocationListener(mBdAbstractLocationListener);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocatePermissionSuccessEvent(LocatePermissionSuccessEvent event){
        doLocate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectCityEvent(SelectCityEvent event){
        setToolbarTitle("获取"+event.mCityInfo.city_name+"天气中...");
        getDataFromNetByCity(event.mCityInfo.city_name);
    }

    public void setToolbar(Toolbar toolbar){
        mToolbar = toolbar;
    }

    public void setToolbarTitle(String s){
        if(mToolbar != null){
            //mToolbar.setTitle(s);
            TextView tv = mToolbar.findViewById(R.id.toolbar_title);
            tv.setText(s);
        }
    }

}
