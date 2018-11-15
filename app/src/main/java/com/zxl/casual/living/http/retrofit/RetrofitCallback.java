package com.zxl.casual.living.http.retrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zxl on 2018/11/15.
 */

public abstract class RetrofitCallback<T> implements Callback<T> {
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if(response.isSuccessful()) {
            onSuccess(call, response);
        } else {
            onFailure(call, new Throwable(response.message()));
        }
    }
    public abstract void onSuccess(Call<T> call, Response<T> response);
    //用于进度的回调
    public abstract void onLoading(long total, long progress) ;
}
