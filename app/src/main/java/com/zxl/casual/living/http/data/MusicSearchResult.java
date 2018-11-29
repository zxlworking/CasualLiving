package com.zxl.casual.living.http.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxl on 2018/11/29.
 */

public class MusicSearchResult {

    /**
     {
     "result":{
     "songs":[
     {
     "name":"一次就好",
     "id":440207429,
     "pst":0,
     "t":0,
     "ar":[
     {
     "id":2747,
     "name":"费玉清",
     "tns":[

     ],
     "alias":[

     ]
     }
     ],
     "alia":[
     "原唱：杨宗纬"
     ],
     "pop":100,
     "st":0,
     "rt":null,
     "fee":0,
     "v":13,
     "crbt":null,
     "cf":"",
     "al":{
     "id":34985144,
     "name":"天籁之战 第五期",
     "picUrl":"http://p1.music.126.net/UjTXWIpw1wtJwW75d7Vghw==/3311729029882017.jpg",
     "tns":[

     ],
     "pic":3311729029882017
     },
     "dt":224640,
     "h":{
     "br":320000,
     "fid":0,
     "size":8988256,
     "vd":-0.88
     },
     "m":{
     "br":160000,
     "fid":0,
     "size":4494151,
     "vd":-0.43
     },
     "l":{
     "br":96000,
     "fid":0,
     "size":2696508,
     "vd":-0.44
     },
     "a":null,
     "cd":"1",
     "no":3,
     "rtUrl":null,
     "ftype":0,
     "rtUrls":[

     ],
     "djId":0,
     "copyright":2,
     "s_id":0,
     "rtype":0,
     "rurl":null,
     "mst":9,
     "cp":543010,
     "mv":0,
     "publishTime":1478966400007,
     "privilege":{
     "id":440207429,
     "fee":0,
     "payed":0,
     "st":0,
     "pl":320000,
     "dl":320000,
     "sp":7,
     "cp":1,
     "subp":1,
     "cs":false,
     "maxbr":999000,
     "fl":320000,
     "toast":false,
     "flag":128
     }
     },
     Object{...}
     ],
     "songCount":387
     },
     "code":200
     }
     */

    public int code = 0;

    public Result result = null;

    public class Result{
        public int songCount = 0;
        public List<Song> songs = new ArrayList<>();

        @Override
        public String toString() {
            return "Resutl{" +
                    "songCount=" + songCount +
                    ", songs=" + songs +
                    '}';
        }
    }


    public class Song{
        public String name = "";
        public long id = 0;
        public List<Artist> ar = new ArrayList<>();
        public long dt = 0;
        public Album al = null;

        @Override
        public String toString() {
            return "Song{" +
                    "name='" + name + '\'' +
                    ", id=" + id +
                    ", ar=" + ar +
                    ", dt=" + dt +
                    ", al=" + al +
                    '}';
        }
    }

    public class Artist{
        public long id = 0;
        public String name = "";

        @Override
        public String toString() {
            return "Artist{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public class Album{
        public long id = 0;
        public String name = "";
        public String picUrl = "";

        @Override
        public String toString() {
            return "Album{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", picUrl='" + picUrl + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MusicSearchResult{" +
                "code=" + code +
                ", result=" + result +
                '}';
    }
}
