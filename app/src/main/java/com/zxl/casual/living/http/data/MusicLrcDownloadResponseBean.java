package com.zxl.casual.living.http.data;

/**
 * Created by zxl on 2018/11/28.
 */

public class MusicLrcDownloadResponseBean extends ResponseBaseBean {

    public String mLrcUrl = "";
    public String mPath = "";

    @Override
    public String toString() {
        return "MusicLrcDownloadResponseBean{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                ", mLrcUrl='" + mLrcUrl + '\'' +
                ", mPath='" + mPath + '\'' +
                '}';
    }
}
