package com.zxl.casual.living.http.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxl on 2018/11/28.
 */

public class LrcInfo {

    public String mCurrentTime = "";
    public String mContent = "";
    public boolean hasTime = false;

    @Override
    public String toString() {
        return "LrcInfo{" +
                "mCurrentTime='" + mCurrentTime + '\'' +
                ", mContent='" + mContent + '\'' +
                '}';
    }
}
