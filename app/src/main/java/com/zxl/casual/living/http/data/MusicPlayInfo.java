package com.zxl.casual.living.http.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxl on 2018/11/30.
 */

public class MusicPlayInfo {
    /**
     "code":200,
     "data":[
     {
     "code":200,
     "url":"http://m10.music.126.net/20181130180313/c068bfe6ac1159cdde855c964d3e0ba5/ymusic/f20a/ec5f/f6d2/b20e2e89eb5fcb5f5e0de381e398b1a2.mp3",
     "uf":null,
     "canExtend":false,
     "flag":128,
     "payed":0,
     "expi":1200,
     "fee":0,
     "freeTrialInfo":null,
     "gain":-0.44,
     "br":128000,
     "size":3595328,
     "type":"mp3",
     "id":440207429,
     "md5":"b20e2e89eb5fcb5f5e0de381e398b1a2"
     }
     ]
     */

    public int code = 0;
    public List<Data> data = new ArrayList<>();

    public class Data{
        public int code = 0;
        public String url = "";
        public boolean canExtend = false;
        public int flag = 0;
        public int payed = 0;
        public int expi = 0;
        public int fee = 0;
        public double gain = 0;
        public int br = 0;
        public int size = 0;
        public String type = "";
        public long id = 0;
        public String md5 = "";

        @Override
        public String toString() {
            return "Data{" +
                    "code=" + code +
                    ", url='" + url + '\'' +
                    ", canExtend=" + canExtend +
                    ", flag=" + flag +
                    ", payed=" + payed +
                    ", expi=" + expi +
                    ", fee=" + fee +
                    ", gain=" + gain +
                    ", br=" + br +
                    ", size=" + size +
                    ", type='" + type + '\'' +
                    ", id=" + id +
                    ", md5='" + md5 + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MusicPlayInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}
