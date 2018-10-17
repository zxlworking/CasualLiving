package com.zxl.casual.living.http;


import com.zxl.casual.living.http.data.CityInfoListResponseBean;
import com.zxl.casual.living.http.data.QSBKElementList;
import com.zxl.casual.living.http.data.ResponseBaseBean;
import com.zxl.casual.living.http.data.TaoBaoAnchorListResponseBean;
import com.zxl.casual.living.http.data.TodayWeatherResponseBean;
import com.zxl.casual.living.http.data.UpdateInfoResponseBean;
import com.zxl.casual.living.http.data.UserInfoResponseBean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by zxl on 2018/9/5.
 */

public interface HttpAPI {

    @GET("cgi_server/cgi_weather/test2.py")
    public Observable<TodayWeatherResponseBean> getZHTianQiByLocation(@Query("l")String l);
//    public Call<ResponseBody> getZHTianQiByLocation(@Query("l")String l);

    @GET("cgi_server/cgi_weather/test3.py")
    public Observable<TodayWeatherResponseBean> getZHTianQiByCity(@Query("city")String city);
//    public Call<ResponseBody> getZHTianQiByCity(@Query("city")String city);

    @GET("cgi_server/cgi_weather/test4.py")
    public Observable<TaoBaoAnchorListResponseBean> getTaoBaoAnchor(@Query("page")int page);
//    public Call<ResponseBody> getTaoBaoAnchor(@Query("page")int page);

    @GET("cgi_server/cgi_weather/city_list.py")
    public Observable<CityInfoListResponseBean> getCityInfoList();

    @GET("cgi_server/cgi_weather/cgi_qsbk.py")
    public Observable<QSBKElementList> getQSBK(@Query("page")int page, @Query("user_id")String user_id);

    @GET("cgi_server/cgi_weather/test5.py")
    public Observable<UserInfoResponseBean> register(@Query("user_operator")String user_operator, @Query("user_info")String user_info);
//    public Call<ResponseBody> register(@Query("user_operator")String user_operator, @Query("user_info")String user_info);

    @GET("cgi_server/cgi_weather/test6.py")
    public Observable<UpdateInfoResponseBean> getUpdateInfo();
//    public Call<ResponseBody> getUpdateInfo();

    @GET("cgi_server/cgi_weather/test7.py")
    public Observable<ResponseBaseBean> collect_qsbk(@Query("collect_operator")int collect_operator, @Query("user_id")String user_id, @Query("qsbk_parse_element")String qsbk_parse_element);

    @GET("cgi_server/cgi_weather/test7.py")
    public Observable<QSBKElementList> getQSBKFromCollect(@Query("page")int page, @Query("page_count")int page_count, @Query("collect_operator")int collect_operator, @Query("user_id")String user_id);
}
