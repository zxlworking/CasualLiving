package com.zxl.casual.living.utils;

import android.os.Environment;

/**
 * Created by zxl on 2018/9/6.
 */

public class Constants {

    public static final int LEFT_MENU_POSITION_0 = 0;
    public static final int LEFT_MENU_POSITION_1 = 1;
    public static final int LEFT_MENU_POSITION_2 = 2;
    public static final int LEFT_MENU_POSITION_3 = 3;
    public static final int LEFT_MENU_POSITION_4 = 4;
    public static final int LEFT_MENU_POSITION_5 = 5;
    public static final int LEFT_MENU_POSITION_6 = 6;
    public static final int LEFT_MENU_COLLECT_POSITION = LEFT_MENU_POSITION_4;//收藏

    public static final String WEATHER_BASE_URL = "https://www.zxltest.cn/";
    public static final String QUERY_CITY_BASE_URL = "http://gc.ditu.aliyun.com/regeocoding/";
    //daily sentence
    public static final String DAILY_SENTENCE_BASE_URL = "http://open.iciba.com/";
    public static final String MUSIC_BASE_URL = "http://tingapi.ting.baidu.com/";
    public static final String MUSIC_SEARCH_METHOD = "baidu.ting.search.catalogSug";
    public static final String MUSIC_SEARCH_KEY_PARAM = "query";

    public static final String UPDATE_APP_NAME = "com.zxl.casual.living_update";

    public static final String WX_APP_ID = "wx3588f7555119b55e";

    public static final int THUMB_SIZE = 90;

    public static final String ROOT_DIR_PATH = Environment.getExternalStorageDirectory().getPath();
    public static final String APP_DIR_PATH = ROOT_DIR_PATH + "/" + "com.zxl.casual.living";
    public static final String APP_PICTURE_PATH = APP_DIR_PATH + "/" + "picture";
    public static final String APP_CRASH_PATH = APP_DIR_PATH + "/" + "crash";
}
