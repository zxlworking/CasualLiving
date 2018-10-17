package com.zxl.casual.living.http.data;

/**
 * Created by zxl on 2018/9/5.
 */

public class TodayWeatherIconCss {
    /**
     {
     "img":"http://i.tq121.com.cn/i/weather2015/png/blue80.png",
     "width":"80",
     "height":"80",
     "background_position_x":"-80",
     "background_position_y":"0"
     }
     */
    public String img = "";
    public String width = "";
    public String height = "";
    public String background_position_x = "";
    public String background_position_y = "";

    @Override
    public String toString() {
        return "TodayWeatherIconCss{" +
                "img='" + img + '\'' +
                ", width='" + width + '\'' +
                ", height='" + height + '\'' +
                ", background_position_x='" + background_position_x + '\'' +
                ", background_position_y='" + background_position_y + '\'' +
                '}';
    }
}
