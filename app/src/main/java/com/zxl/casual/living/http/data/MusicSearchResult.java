package com.zxl.casual.living.http.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxl on 2018/11/23.
 */

public class MusicSearchResult {
    /**
     {
     "song":[
     {
     "bitrate_fee":"{"0":"0|0","1":"0|0"}",
     "weight":"158099",
     "songname":"一次就好",
     "resource_type":"0",
     "songid":"256002518",
     "has_mv":"0",
     "yyr_artist":"0",
     "resource_type_ext":"0",
     "artistname":"凌川夏",
     "info":"",
     "resource_provider":"1",
     "control":"0000000000",
     "encrypted_songid":"0207f4249d609565d9ac4L"
     },
     Object{...},
     Object{...},
     Object{...},
     Object{...}
     ],
     "order":"song,album",
     "error_code":22000,
     "album":[
     {
     "albumname":"一次就好",
     "weight":"1580",
     "artistname":"凌川夏",
     "resource_type_ext":"0",
     "artistpic":"http://qukufile2.qianqian.com/data2/music/3088D4F4A92EE9DD113B380A7D5B9B83/256608249/256608249.jpg@s_0,w_40",
     "albumid":"256002517"
     }
     ]
     }
     */

    public List<Song> song = new ArrayList<>();
    public String order = "";
    public int error_code = 0;
    public List<Album> album = new ArrayList<>();

    public class Song{
        public String bitrate_fee = "";
        public String weight = "";
        public String songname = "";
        public String resource_type = "";
        public String songid = "";
        public String has_mv = "";
        public String yyr_artist = "";
        public String resource_type_ext = "";
        public String artistname = "";
        public String info = "";
        public String resource_provider = "";
        public String control = "";
        public String encrypted_songid = "";

        @Override
        public String toString() {
            return "Song{" +
                    "bitrate_fee='" + bitrate_fee + '\'' +
                    ", weight='" + weight + '\'' +
                    ", songname='" + songname + '\'' +
                    ", resource_type='" + resource_type + '\'' +
                    ", songid='" + songid + '\'' +
                    ", has_mv='" + has_mv + '\'' +
                    ", yyr_artist='" + yyr_artist + '\'' +
                    ", resource_type_ext='" + resource_type_ext + '\'' +
                    ", artistname='" + artistname + '\'' +
                    ", info='" + info + '\'' +
                    ", resource_provider='" + resource_provider + '\'' +
                    ", control='" + control + '\'' +
                    ", encrypted_songid='" + encrypted_songid + '\'' +
                    '}';
        }
    }

    public class Album{
        public String albumname = "";
        public String weight = "";
        public String artistname = "";
        public String resource_type_ext = "";
        public String artistpic = "";
        public String albumid = "";

        @Override
        public String toString() {
            return "Album{" +
                    "albumname='" + albumname + '\'' +
                    ", weight='" + weight + '\'' +
                    ", artistname='" + artistname + '\'' +
                    ", resource_type_ext='" + resource_type_ext + '\'' +
                    ", artistpic='" + artistpic + '\'' +
                    ", albumid='" + albumid + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MusicSearchResult{" +
                "song=" + song +
                ", order='" + order + '\'' +
                ", error_code='" + error_code + '\'' +
                ", album=" + album +
                '}';
    }
}
