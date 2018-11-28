package com.zxl.casual.living.http.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxl on 2018/11/28.
 */

public class LrcInfo {

    public int mCurrentTime = 0;
    public String mContent = "";

    @Override
    public String toString() {
        return "LrcInfo{" +
                "mCurrentTime='" + mCurrentTime + '\'' +
                ", mContent='" + mContent + '\'' +
                '}';
    }
}
