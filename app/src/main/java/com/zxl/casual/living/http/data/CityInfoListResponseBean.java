package com.zxl.casual.living.http.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxl on 2018/9/14.
 */

public class CityInfoListResponseBean extends ResponseBaseBean {
    public List<CityInfo> city_list = new ArrayList<>();

    @Override
    public String toString() {
        return "CityInfoListResponseBean{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                ", city_list=" + city_list +
                '}';
    }
}
