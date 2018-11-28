package com.zxl.casual.living.http.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxl on 2018/11/28.
 */

public class LrcListInfo {
    public List<LrcInfo> mLrcs = new ArrayList<>();

    @Override
    public String toString() {
        return "LrcListInfo{" +
                "mLrcs=" + mLrcs +
                '}';
    }
}
