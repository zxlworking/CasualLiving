package com.zxl.casual.living.http.data;

/**
 * Created by zxl on 2018/9/5.
 */

public class TodayWeather {
    /*
        "now_time":"17:40 实况",
        "temperature":"24",
        "is_w":1,
        "simple_content":"周一 多云转阴 21/26°C",
        "wind_direction":"东北风",
        "air_quality":"暂无数据",
        "humidity":"83%",
        "humidity_icon_css":Object{...},
        "is_limit":0,
        "wind_icon_css":Object{...},
        "is_h":1,
        "wind_value":"2级",
        "is_pol":0,
        "temperature_icon_css":Object{...}
    },
    */

    public String now_time = "";
    public String temperature = "";
    public String wind_direction = "";
    public String air_quality = "";
    public String humidity = "";
    public String wind_value = "";
    public String limit_content = "";
    public String simple_content = "";
    public int is_w;
    public int is_h;
    public int is_pol;
    public int is_limit;

    public TodayWeatherTemperatureIconCss temperature_icon_css;

    public TodayWeatherHumidityIconCss humidity_icon_css;
    public TodayWeatherWindIconCss wind_icon_css;
    public TodayWeatherAirQualityIconCss air_quality_icon_css;
    public TodayWeatherLimitIconCss limit_icon_css;

    @Override
    public String toString() {
        return "TodayWeather{" +
                "now_time='" + now_time + '\'' +
                ", temperature='" + temperature + '\'' +
                ", wind_direction='" + wind_direction + '\'' +
                ", air_quality='" + air_quality + '\'' +
                ", humidity='" + humidity + '\'' +
                ", wind_value='" + wind_value + '\'' +
                ", limit_content='" + limit_content + '\'' +
                ", simple_content='" + simple_content + '\'' +
                ", is_w=" + is_w +
                ", is_h=" + is_h +
                ", is_pol=" + is_pol +
                ", is_limit=" + is_limit +
                ", temperature_icon_css=" + temperature_icon_css +
                ", humidity_icon_css=" + humidity_icon_css +
                ", wind_icon_css=" + wind_icon_css +
                ", air_quality_icon_css=" + air_quality_icon_css +
                ", limit_icon_css=" + limit_icon_css +
                '}';
    }
}
