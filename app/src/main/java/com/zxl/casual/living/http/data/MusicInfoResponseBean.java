package com.zxl.casual.living.http.data;

/**
 * Created by zxl on 2018/11/23.
 */

public class MusicInfoResponseBean<T> extends ResponseBaseBean{
    public String param = "";
    public T result;

    @Override
    public String toString() {
        return "MusicInfoResponseBean{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                ", code=" + code +
                ", desc='" + desc + '\'' +
                ", music_method='" + param + '\'' +
                ", result=" + result +
                '}';
    }
}
