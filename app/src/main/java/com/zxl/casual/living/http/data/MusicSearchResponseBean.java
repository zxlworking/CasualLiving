package com.zxl.casual.living.http.data;

/**
 * Created by zxl on 2018/11/29.
 */

public class MusicSearchResponseBean extends ResponseBaseBean {

    public String music_operator = "";

    public MusicSearchResult result = null;

    @Override
    public String toString() {
        return "MusicSearchResponseBean{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                ", music_operator='" + music_operator + '\'' +
                ", result=" + result +
                '}';
    }
}
