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
    /*
    参数：	type = 1-新歌榜,2-热歌榜,11-摇滚榜,12-爵士,16-流行,21-欧美金曲榜,22-经典老歌榜,23-情歌对唱榜,24-影视金曲榜,25-网络歌曲榜
    http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.billboard.billList&type=1&size=10&offset=0
    http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.search.catalogSug&query=一次就好
    http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.song.play&songid=256002518
    http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.song.playAAC&songid=256002518
    http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.song.lry&songid=256002518
    http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.song.play&songid=256002518
     */
//    public static final String MUSIC_BASE_URL = "http://tingapi.ting.baidu.com/";
    public static final int MUSIC_TYPES[] = new int[]{1,2,11,12,16,21,22,23,24,25};
    public static final String MUSIC_TYPE_NAMES[] = new String[]{"新歌榜","热歌榜","摇滚榜","爵士","流行","欧美金曲榜","经典老歌榜","情歌对唱榜","影视金曲榜","网络歌曲榜"};

    public static final String MUSIC_SEARCH_METHOD = "method=baidu.ting.search.catalogSug";
    public static final String MUSIC_SEARCH_KEY_PARAM = "&query=";

    public static final String MUSIC_GET_INFO_METHOD = "method=baidu.ting.song.play";
    public static final String MUSIC_GET_INFO_KEY_PARAM = "&songid=";

    public static final String MUSIC_GET_BY_TYPE_METHOD = "method=baidu.ting.billboard.billList";
    public static final String MUSIC_GET_BY_TYPE_KEY_PARAM = "&type=";
    public static final String MUSIC_GET_BY_TYPE_SIZE_KEY_PARAM = "&size=";
    public static final String MUSIC_GET_BY_TYPE_OFFSET_KEY_PARAM = "&offset=";

    public static final String UPDATE_APP_NAME = "com.zxl.casual.living_update";

    public static final String WX_APP_ID = "wx3588f7555119b55e";

    public static final int THUMB_SIZE = 90;

    public static final String ROOT_DIR_PATH = Environment.getExternalStorageDirectory().getPath();
    public static final String APP_DIR_PATH = ROOT_DIR_PATH + "/" + "com.zxl.casual.living";
    public static final String APP_PICTURE_PATH = APP_DIR_PATH + "/" + "picture";
    public static final String APP_CRASH_PATH = APP_DIR_PATH + "/" + "crash";
}
