package com.zxl.casual.living.http;

import com.zxl.casual.living.http.data.DailySentenceResponseBean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by zxl on 2018/11/21.
 */

public interface DailySentenceHttpAPI {

    @GET("dsapi")
    public Observable<DailySentenceResponseBean> getDailySentence();
//    public Call<ResponseBody> getDailySentence();
}
