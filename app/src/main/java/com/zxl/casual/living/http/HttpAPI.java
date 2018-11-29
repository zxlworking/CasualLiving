package com.zxl.casual.living.http;


import com.zxl.casual.living.http.data.CityInfoListResponseBean;
import com.zxl.casual.living.http.data.MusicSearchResponseBean;
import com.zxl.casual.living.http.data.QSBKElementList;
import com.zxl.casual.living.http.data.ResponseBaseBean;
import com.zxl.casual.living.http.data.TaoBaoAnchorListResponseBean;
import com.zxl.casual.living.http.data.TodayWeatherResponseBean;
import com.zxl.casual.living.http.data.UpdateInfoResponseBean;
import com.zxl.casual.living.http.data.UserInfoResponseBean;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by zxl on 2018/9/5.
 */

public interface HttpAPI {

    @GET("cgi_server/cgi_weather/test2.py")
    public Observable<TodayWeatherResponseBean> getZHTianQiByLocation(@Query("l")String l);
//    public Call<ResponseBody> getZHTianQiByLocation(@Query("l")String l);

    @GET("cgi_server/cgi_weather/test3.py")
    public Observable<TodayWeatherResponseBean> getZHTianQiByCity(@Query("addr")String addr, @Query("city")String city);
//    public Call<ResponseBody> getZHTianQiByCity(@Query("addr")String addr, @Query("city")String city);

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

//    @Multipart
//    @POST("cgi_server/cgi_weather/test8.py")
//    public Call<ResponseBody> uploadFile(@Part() List<MultipartBody.Part> parts);

    @POST("cgi_server/cgi_weather/test8.py")
    public Call<ResponseBody> uploadFile(@Body RequestBody multipartBody);

//    @Multipart
//    @Headers("Content-Type: multipart/form-data; boundary=------------xxxxx")
//    @POST("cgi_server/cgi_weather/test8.py")
//    Call<ResponseBody> uploadFile(@PartMap Map<String, RequestBody> params);

    //http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.search.catalogSug&query=一次就好
    @GET("cgi_server/cgi_weather/test10.py")
    public Observable<MusicSearchResponseBean> searchMusic(@Query("music_operator")String music_operator,@Query("search_music_name")String search_music_name,@Query("search_music_offset")String search_music_offset,@Query("search_music_page_count")String search_music_page_count);
//    public Call<ResponseBody> searchMusic(@Query("music_operator")String music_operator,@Query("search_music_name")String search_music_name,@Query("search_music_offset")String search_music_offset,@Query("search_music_page_count")String search_music_page_count);

    @Streaming
    @GET
    Call<ResponseBody> downloadFileWithUrlAsync(@Url String fileUrl);

}
