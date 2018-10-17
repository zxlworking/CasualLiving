package com.zxl.casual.living.http.data;

import java.util.List;

/**
 * Created by zxl on 2018/9/5.
 */

public class TodayWeatherResponseBean extends ResponseBaseBean {
    public String address_info = "";
    public String city_name = "";
    public TodayWeather today_weather;
    public List<TodayWeatherDetail> today_weather_detail;

    @Override
    public String toString() {
        return "TodayWeatherResponseBean{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                ", address_info='" + address_info + '\'' +
                ", city_name='" + city_name + '\'' +
                ", today_weather=" + today_weather +
                ", today_weather_detail=" + today_weather_detail +
                '}';
    }
}
