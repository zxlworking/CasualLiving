package com.zxl.casual.living.http.data;

/**
 * Created by zxl on 2018/9/14.
 */

public class CityInfo {
    /**
     {
     "province": "土耳其",
     "city_code": "215114100",
     "city_name": "卡拉曼",
     "_id": 4952,
     "city_py": "KARAMAN",
     "city_head": "K"
     }
     */

    public int _id;
    public String province = "";
    public String city_code = "";
    public String city_name = "";
    public String city_py = "";
    public String city_head = "";

    @Override
    public String toString() {
        return "CityInfo{" +
                "_id=" + _id +
                ", province='" + province + '\'' +
                ", city_code='" + city_code + '\'' +
                ", city_name='" + city_name + '\'' +
                ", city_py='" + city_py + '\'' +
                ", city_head='" + city_head + '\'' +
                '}';
    }
}
