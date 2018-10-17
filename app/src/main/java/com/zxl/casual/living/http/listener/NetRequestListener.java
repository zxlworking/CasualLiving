package com.zxl.casual.living.http.listener;

import com.zxl.casual.living.http.data.ResponseBaseBean;

/**
 * Created by zxl on 2018/9/5.
 */

public interface NetRequestListener {
    public void onSuccess(ResponseBaseBean responseBaseBean);

    public void onNetError();
    public void onNetError(Throwable e);

    public void onServerError(ResponseBaseBean responseBaseBean);
}
