package com.zxl.casual.living.http.data;

/**
 * Created by zxl on 2018/9/5.
 */

public class TodayWeatherTemperatureIconCss {
    /**
     {
         "img":"http://i.tq121.com.cn/i/weather2015/city/iconall.png",
         "width1":"15",
         "height1":"8",
         "background_position_x1":"-35",
         "background_position_y1":"-137",
         "width2":"15",
         "height2":"64"
         "background_position_x2":"-35",
         "background_position_y2":"-142",
     }
     */
    public String img = "";

    public String width1 = "";
    public String height1 = "";

    public String background_position_x1 = "";
    public String background_position_y1 = "";

    public String width2 = "";
    public String height2 = "";

    public String background_position_x2 = "";
    public String background_position_y2 = "";

    @Override
    public String toString() {
        return "TodayWeatherTemperatureIconCss{" +
                "img='" + img + '\'' +
                ", width1='" + width1 + '\'' +
                ", height1='" + height1 + '\'' +
                ", background_position_x1='" + background_position_x1 + '\'' +
                ", background_position_y1='" + background_position_y1 + '\'' +
                ", width2='" + width2 + '\'' +
                ", height2='" + height2 + '\'' +
                ", background_position_x2='" + background_position_x2 + '\'' +
                ", background_position_y2='" + background_position_y2 + '\'' +
                '}';
    }
}
