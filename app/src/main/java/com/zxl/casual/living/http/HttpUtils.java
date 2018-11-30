package com.zxl.casual.living.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.zxl.casual.living.common.FileUtils;
import com.zxl.casual.living.http.data.CityInfoListResponseBean;
import com.zxl.casual.living.http.data.DailySentenceResponseBean;
import com.zxl.casual.living.http.data.MusicInfoResponseBean;
import com.zxl.casual.living.http.data.MusicSearchResponseBean;
import com.zxl.casual.living.http.data.QSBKElementList;
import com.zxl.casual.living.http.data.ResponseBaseBean;
import com.zxl.casual.living.http.data.TaoBaoAnchorListResponseBean;
import com.zxl.casual.living.http.data.TodayWeatherResponseBean;
import com.zxl.casual.living.http.data.UpdateInfoResponseBean;
import com.zxl.casual.living.http.data.UserInfoResponseBean;
import com.zxl.casual.living.http.listener.NetRequestListener;
import com.zxl.casual.living.http.retrofit.FileRequestBody;
import com.zxl.casual.living.utils.Constants;
import com.zxl.common.DebugUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zxl on 2018/9/5.
 */

public class HttpUtils {
    private static final String TAG = "HttpUtils";

    private static HttpUtils mHttpUtils;

    private static Object mLock = new Object();

    private static HttpAPI mHttpAPI;
    private static DailySentenceHttpAPI mDailySentenceHttpAPI;

    private HttpUtils(){
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.connectTimeout(1, TimeUnit.MINUTES);
        okBuilder.readTimeout(1,TimeUnit.MINUTES);
        OkHttpClient okHttpClient = okBuilder.build();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        Retrofit retrofit = retrofitBuilder
//                .baseUrl("http://www.zxltest.cn/cgi_server/")
                .baseUrl(Constants.WEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        mHttpAPI = retrofit.create(HttpAPI.class);

        Retrofit.Builder dailySentenceRetrofitBuilder = new Retrofit.Builder();
        Retrofit dailySentenceRetrofit = dailySentenceRetrofitBuilder
                .baseUrl(Constants.DAILY_SENTENCE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        mDailySentenceHttpAPI = dailySentenceRetrofit.create(DailySentenceHttpAPI.class);
    }

    public static HttpUtils getInstance(){
        DebugUtil.d(TAG,"getInstance");

        if(null == mHttpUtils){
            synchronized (mLock){
                if(null == mHttpUtils){
                    mHttpUtils = new HttpUtils();
                }
            }
        }
        return mHttpUtils;
    }


    public void getZHTianQiByLocation(Context context, String l, final NetRequestListener listener){
        DebugUtil.d(TAG,"getZHTianQiByLocation::l = " + l);

//        if(isNetworkAvailable(context)) {
//            Call<ResponseBody> call = mHttpAPI.getZHTianQiByLocation(l);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    try {
//                        String s = new String(response.body().bytes());
//                        DebugUtil.d(TAG, "getZHTianQiByLocation::s = " + s);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                }
//            });
//        }
        if(isNetworkAvailable(context)){
//        if(true){
            Observable<TodayWeatherResponseBean> observable = mHttpAPI.getZHTianQiByLocation(l);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBaseBean>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"getZHTianQiByLocation::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"getZHTianQiByLocation::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(ResponseBaseBean responseBaseBean) {
                            DebugUtil.d(TAG,"getZHTianQiByLocation::onNext::responseBaseBean = " + responseBaseBean);
                            if(responseBaseBean.code == 0){
                                if(listener != null){
                                    listener.onSuccess(responseBaseBean);
                                }
                            }else{
                                if(listener != null){
                                    listener.onServerError(responseBaseBean);
                                }
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"getZHTianQiByLocation::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }

    public void getZHTianQiByCity(Context context, String addr, String city, final NetRequestListener listener){
//        city = "南京";
//        addr = "";
        DebugUtil.d(TAG,"getZHTianQiByCity::addr = " + addr + "::city = " + city);

        if(isNetworkAvailable(context)){
//            Call<ResponseBody> call = mHttpAPI.getZHTianQiByCity(addr,city);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    try {
//                        String s = new String(response.body().bytes());
//                        DebugUtil.d(TAG,"getZHTianQiByCity::s = " + s);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                }
//            });
            Observable<TodayWeatherResponseBean> observable = mHttpAPI.getZHTianQiByCity(addr, city);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBaseBean>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"getZHTianQiByCity::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"getZHTianQiByCity::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(ResponseBaseBean responseBaseBean) {
                            DebugUtil.d(TAG,"getZHTianQiByCity::onNext::todayWeatherResponseBean = " + responseBaseBean);
                            if(responseBaseBean.code == 0){
                                if(listener != null){
                                    listener.onSuccess(responseBaseBean);
                                }
                            }else{
                                if(listener != null){
                                    listener.onServerError(responseBaseBean);
                                }
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"getZHTianQiByCity::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }


    public void getCityInfoList(Context context, final NetRequestListener listener){
        DebugUtil.d(TAG,"getCityInfoList");

        if(isNetworkAvailable(context)){
            Observable<CityInfoListResponseBean> observable = mHttpAPI.getCityInfoList();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBaseBean>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"getCityInfoList::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"getCityInfoList::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(ResponseBaseBean responseBaseBean) {
                            DebugUtil.d(TAG,"getCityInfoList::onNext::cityInfoListResponseBean = " + responseBaseBean);
                            if(responseBaseBean.code == 0){
                                if(listener != null){
                                    listener.onSuccess(responseBaseBean);
                                }
                            }else{
                                if(listener != null){
                                    listener.onServerError(responseBaseBean);
                                }
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"getCityInfoList::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }

    public void getTaoBaoAnchor(Context context,int page, final NetRequestListener listener){
        DebugUtil.d(TAG,"getTaoBaoAnchor::page = " + page);

//        if(isNetworkAvailable(context)){
//            Call<ResponseBody> call = mHttpAPI.getTaoBaoAnchor(page);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    try {
//                        String s = new String(response.body().bytes());
//                        DebugUtil.d(TAG,"getTaoBaoAnchor::s = " + s);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                }
//            });

        if(true){
            Observable<TaoBaoAnchorListResponseBean> observable = mHttpAPI.getTaoBaoAnchor(page);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBaseBean>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"getTaoBaoAnchor::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"getTaoBaoAnchor::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(ResponseBaseBean responseBaseBean) {
                            DebugUtil.d(TAG,"getTaoBaoAnchor::onNext::taoBaoAnchorListResponseBean = " + responseBaseBean);
                            if(responseBaseBean.code == 0){
                                if(listener != null){
                                    listener.onSuccess(responseBaseBean);
                                }
                            }else{
                                if(listener != null){
                                    listener.onServerError(responseBaseBean);
                                }
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"getTaoBaoAnchor::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }

    public void getQSBK(Context context, int page, String user_id, final NetRequestListener listener){
        DebugUtil.d(TAG,"getQSBK::page = " + page);

        if(isNetworkAvailable(context)){
            Observable<QSBKElementList> observable = mHttpAPI.getQSBK(page, user_id);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBaseBean>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"getQSBK::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"getQSBK::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(ResponseBaseBean responseBaseBean) {
                            DebugUtil.d(TAG,"getQSBK::onNext::responseBaseBean = " + responseBaseBean);
                            if(responseBaseBean.code == 0){
                                if(listener != null){
                                    listener.onSuccess(responseBaseBean);
                                }
                            }else{
                                if(listener != null){
                                    listener.onServerError(responseBaseBean);
                                }
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"getQSBK::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }

    public void register(Context context, String user_operator, String user_info, final NetRequestListener listener){
        DebugUtil.d(TAG,"register::user_operator = " + user_operator + "::user_info = " + user_info);

//        if(isNetworkAvailable(context)) {
//            Call<ResponseBody> call = mHttpAPI.register(user_operator,user_info);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    try {
//                        String s = new String(response.body().bytes());
//                        DebugUtil.d(TAG, "register::s = " + s);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                }
//            });
//        }

        if(isNetworkAvailable(context)){
            Observable<UserInfoResponseBean> observable = mHttpAPI.register(user_operator,user_info);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBaseBean>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"register::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"register::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(ResponseBaseBean responseBaseBean) {
                            DebugUtil.d(TAG,"register::onNext::userInfoResponseBean = " + responseBaseBean);
                            if(responseBaseBean.code == 0){
                                if(listener != null){
                                    listener.onSuccess(responseBaseBean);
                                }
                            }else{
                                if(listener != null){
                                    listener.onServerError(responseBaseBean);
                                }
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"register::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }

    public void getUpdateInfo(Context context, final NetRequestListener listener){
        DebugUtil.d(TAG,"getUpdateInfo");

        if(isNetworkAvailable(context)){

//            Call<ResponseBody> call = mHttpAPI.getUpdateInfo();
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    try {
//                        String s = new String(response.body().bytes());
//                        DebugUtil.d(TAG, "getUpdateInfo::s = " + s);
//
//                        UpdateInfoResponseBean updateInfoResponseBean = CommonUtils.mGson.fromJson(s, UpdateInfoResponseBean.class);
//                        DebugUtil.d(TAG, "getUpdateInfo::updateInfoResponseBean = " + updateInfoResponseBean);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                }
//            });



            Observable<UpdateInfoResponseBean> observable = mHttpAPI.getUpdateInfo();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBaseBean>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"getUpdateInfo::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"getUpdateInfo::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(ResponseBaseBean responseBaseBean) {
                            DebugUtil.d(TAG,"getUpdateInfo::onNext::responseBaseBean = " + responseBaseBean);
                            if(listener != null){
                                if(responseBaseBean.code == 0){
                                    listener.onSuccess(responseBaseBean);
                                }else{
                                    listener.onServerError(responseBaseBean);
                                }
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"getUpdateInfo::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }

    public void collectQSBK(Context context, int collect_operator, String user_id, String qsbk_parse_element, final NetRequestListener listener){
        DebugUtil.d(TAG,"collectQSBK::collect_operator = " + collect_operator + "::user_id = " + user_id + "::qsbk_parse_element = " + qsbk_parse_element);

        if(isNetworkAvailable(context)){
            Observable<ResponseBaseBean> observable = mHttpAPI.collect_qsbk(collect_operator, user_id, qsbk_parse_element);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBaseBean>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"collectQSBK::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"collectQSBK::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(ResponseBaseBean responseBaseBean) {
                            DebugUtil.d(TAG,"collectQSBK::onNext::responseBaseBean = " + responseBaseBean);
                            if(listener != null){
                                if(responseBaseBean.code == 0){
                                    listener.onSuccess(responseBaseBean);
                                }else{
                                    listener.onServerError(responseBaseBean);
                                }
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"collectQSBK::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }

    public void getQSBKFromCollect(Context context, int page, int page_count, int collect_operator, String user_id, final NetRequestListener listener){
        DebugUtil.d(TAG,"getQSBKFromCollect::page = " + page + "::page_count = " + page_count + "::collect_operator = " + collect_operator + "::user_id = " + user_id);

        if(isNetworkAvailable(context)){
            Observable<QSBKElementList> observable = mHttpAPI.getQSBKFromCollect(page, page_count, collect_operator, user_id);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBaseBean>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"getQSBKFromCollect::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"getQSBKFromCollect::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(ResponseBaseBean responseBaseBean) {
                            DebugUtil.d(TAG,"getQSBKFromCollect::onNext::responseBaseBean = " + responseBaseBean);
                            if(responseBaseBean.code == 0){
                                if(listener != null){
                                    listener.onSuccess(responseBaseBean);
                                }
                            }else{
                                if(listener != null){
                                    listener.onServerError(responseBaseBean);
                                }
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"getQSBKFromCollect::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }

    public void uploadFile(FileRequestBody fileRequestBody, final NetRequestListener listener){

        //multipart/form-data
        //image/png

//        List<MultipartBody.Part> parts = new ArrayList<>();
//        for (File file : files) {
//            DebugUtil.d(TAG,"uploadFile::file = " + file.getName());
//            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
//            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
//            parts.add(part);
//        }

//        MultipartBody.Builder builder = new MultipartBody.Builder();
//        for (File file : files) {
//            RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
//            builder.addFormDataPart("file", file.getName(), requestBody);
//        }
//        builder.setType(MultipartBody.FORM);
//        MultipartBody multipartBody = builder.build();
//        FileRequestBody fileRequestBody = new FileRequestBody(multipartBody,callback);

//        Map<String, RequestBody> params = new HashMap<>();
//        for (File file : files) {
//            DebugUtil.d(TAG,"uploadFile::file = " + file.getPath());
//            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//            params.put("file\"; filename=\""+ file.getName(), requestBody);
//        }

//        if(isNetworkAvailable(context)){
//            Observable<QSBKElementList> observable = mHttpAPI.fileUpload(builder.build());
//            observable.subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Subscriber<ResponseBaseBean>() {
//                        @Override
//                        public void onCompleted() {
//                            DebugUtil.d(TAG,"uploadFile::onCompleted");
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            DebugUtil.d(TAG,"uploadFile::onError::e = " + e);
//                            if(listener != null){
//                                listener.onNetError(e);
//                            }
//                        }
//
//                        @Override
//                        public void onNext(ResponseBaseBean responseBaseBean) {
//                            DebugUtil.d(TAG,"uploadFile::onNext::responseBaseBean = " + responseBaseBean);
//                            if(responseBaseBean.code == 0){
//                                if(listener != null){
//                                    listener.onSuccess(responseBaseBean);
//                                }
//                            }else{
//                                if(listener != null){
//                                    listener.onServerError(responseBaseBean);
//                                }
//                            }
//                        }
//                    });
//        }else{
//            DebugUtil.d(TAG,"uploadFile::net work error");
//            if(listener != null){
//                listener.onNetError();
//            }
//        }

        Call<ResponseBody> call = mHttpAPI.uploadFile(fileRequestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response != null && response.body() != null){
                    try {
                        String s = new String(response.body().bytes());
                        DebugUtil.d(TAG, "uploadFile::s = " + s);

                        if(listener != null){
                            listener.onSuccess(null);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                        if(listener != null){
                            listener.onNetError();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(listener != null){
                    listener.onNetError();
                }
            }
        });
    }

    public void getDailySentence(Context context, final NetRequestListener listener){
        DebugUtil.d(TAG,"getDailySentence");

        if(isNetworkAvailable(context)){
//            Call<ResponseBody> call = mDailySentenceHttpAPI.getDailySentence();
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    try {
//                        String s = new String(response.body().bytes());
//                        DebugUtil.d(TAG, "getDailySentence::s = " + s);
//
//                        DailySentenceResponseBean dailySentenceResponseBean = CommonUtils.mGson.fromJson(s, DailySentenceResponseBean.class);
//                        DebugUtil.d(TAG, "getDailySentence::dailySentenceResponseBean = " + dailySentenceResponseBean);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                }
//            });


            Observable<DailySentenceResponseBean> observable = mDailySentenceHttpAPI.getDailySentence();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBaseBean>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"getDailySentence::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"getDailySentence::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(ResponseBaseBean responseBaseBean) {
                            DebugUtil.d(TAG,"getDailySentence::onNext::responseBaseBean = " + responseBaseBean);
                            if(responseBaseBean.code == 0){
                                if(listener != null){
                                    listener.onSuccess(responseBaseBean);
                                }
                            }else{
                                if(listener != null){
                                    listener.onServerError(responseBaseBean);
                                }
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"getDailySentence::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }

    public void searchMusic(Context context, String music_operator, String search_music_name, String search_music_offset, String search_music_page_count, final NetRequestListener listener){
        DebugUtil.d(TAG,"searchMusic::music_operator = " + music_operator +
                "::search_music_name = " + search_music_name +
                "::search_music_offset = " + search_music_offset +
                "::search_music_page_count = " + search_music_page_count);

        if(isNetworkAvailable(context)){
//            Call<ResponseBody> call = mHttpAPI.searchMusic(music_operator,search_music_name,search_music_offset,search_music_page_count);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    try {
//                        String s = new String(response.body().bytes());
//                        DebugUtil.d(TAG, "searchMusic::s = " + s);
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                }
//            });

            Observable<MusicSearchResponseBean> observable = mHttpAPI.searchMusic(music_operator,search_music_name,search_music_offset,search_music_page_count);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBaseBean>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"searchMusic::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"searchMusic::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(ResponseBaseBean responseBaseBean) {
                            DebugUtil.d(TAG,"searchMusic::onNext::responseBaseBean = " + responseBaseBean);
                            if(responseBaseBean.code == 0){
                                if(listener != null){
                                    listener.onSuccess(responseBaseBean);
                                }
                            }else{
                                if(listener != null){
                                    listener.onServerError(responseBaseBean);
                                }
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"searchMusic::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }

    public void getMusicInfo(Context context, String music_operator, String id, String comment_offset, String comment_page_count, final NetRequestListener listener){
        DebugUtil.d(TAG,"getMusicInfo::music_operator = " + music_operator +
                "::id = " + id +
                "::comment_offset = " + comment_offset +
                "::comment_page_count = " + comment_page_count);

        if(isNetworkAvailable(context)){
//            Call<ResponseBody> call = mHttpAPI.getMusicInfo(music_operator,id,comment_offset,comment_page_count);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    try {
//                        String s = new String(response.body().bytes());
//                        DebugUtil.d(TAG, "getMusicInfo::s = " + s);
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                }
//            });

            Observable<MusicInfoResponseBean> observable = mHttpAPI.getMusicInfo(music_operator,id,comment_offset,comment_page_count);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBaseBean>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"getMusicInfo::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"getMusicInfo::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(ResponseBaseBean responseBaseBean) {
                            DebugUtil.d(TAG,"getMusicInfo::onNext::responseBaseBean = " + responseBaseBean);
                            if(responseBaseBean.code == 0){
                                if(listener != null){
                                    listener.onSuccess(responseBaseBean);
                                }
                            }else{
                                if(listener != null){
                                    listener.onServerError(responseBaseBean);
                                }
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"getMusicInfo::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }


    //==============NetworkAvailable===============
    /**
     * 没有连接网络
     */
    private static final int NETWORK_NONE = -1;
    /**
     * 移动网络
     */
    private static final int NETWORK_MOBILE = 0;
    /**
     * 无线网络
     */
    private static final int NETWORK_WIFI = 1;

    public static boolean isNetworkAvailable(Context context) {
        return isNetConnect(getNetWorkState(context));
    }

    private static boolean isNetConnect(int state) {
        DebugUtil.d(TAG, "isNetConnect::state = " + state);
        if (state == NETWORK_WIFI) {
            return true;
        } else if (state == NETWORK_MOBILE) {
            return true;
        } else if (state == NETWORK_NONE) {
            return false;
        }
        return false;
    }

    private static int getNetWorkState(Context context) {
        // 得到连接管理器对象
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return NETWORK_WIFI;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return NETWORK_MOBILE;
            }
        } else {
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }

    public boolean isNetworkAvailable2(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        // 获取NetworkInfo对象
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        if (networkInfo != null && networkInfo.length > 0) {
            for (int i = 0; i < networkInfo.length; i++) {
                // 判断当前网络状态是否为连接状态
                if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }

        return false;
    }
}
