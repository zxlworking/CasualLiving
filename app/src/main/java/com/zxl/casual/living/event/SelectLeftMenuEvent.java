package com.zxl.casual.living.event;

import com.zxl.casual.living.http.data.CityInfo;

/**
 * Created by zxl on 2018/9/6.
 */

public class SelectLeftMenuEvent {
    public int mPosition;

    public SelectLeftMenuEvent(int position){
        mPosition = position;
    }
}
