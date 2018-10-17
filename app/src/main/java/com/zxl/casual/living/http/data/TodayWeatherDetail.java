package com.zxl.casual.living.http.data;

/**
 * Created by zxl on 2018/9/5.
 */

public class TodayWeatherDetail {
    /**
     "is_sun_up":1,
     "temperature":"29",
     "title":"6日白天",
     "wind_direction":"西风",
     "weather_desc":"天空阴沉",
     "weather":"阴",
     "sun_time":"日出 05:44",
     "wind_value":"3-4级",
     "weather_icon_css":Object{...}
     "wind_icon_css":Object{...},
     "sun_icon_css":Object{...},
     */
    public String temperature = "";
    public String title = "";
    public String wind_direction = "";
    public String weather_desc = "";
    public String weather = "";
    public String sun_time = "";
    public String wind_value = "";
    public TodayWeatherDetailIconCss weather_icon_css;
    public TodayWeatherDetailWindIconCss wind_icon_css;
    public TodayWeatherDetailSunIconCss sun_icon_css;

    @Override
    public String toString() {
        return "TodayWeatherDetail{" +
                "temperature='" + temperature + '\'' +
                ", title='" + title + '\'' +
                ", wind_direction='" + wind_direction + '\'' +
                ", weather_desc='" + weather_desc + '\'' +
                ", weather='" + weather + '\'' +
                ", sun_time='" + sun_time + '\'' +
                ", wind_value='" + wind_value + '\'' +
                ", weather_icon_css=" + weather_icon_css +
                ", wind_icon_css=" + wind_icon_css +
                ", sun_icon_css=" + sun_icon_css +
                '}';
    }
}
