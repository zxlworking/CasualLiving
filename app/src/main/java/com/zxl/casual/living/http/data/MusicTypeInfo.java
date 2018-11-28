package com.zxl.casual.living.http.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxl on 2018/11/28.
 */

public class MusicTypeInfo {
    /**
     {
     "song_list":[
     {
     "artist_id":"1483",
     "language":"国语",
     "pic_big":"http://qukufile2.qianqian.com/data2/pic/b447319eb0064f88f6d15f278cdc4df1/608316740/608316740.jpg@s_1,w_150,h_150",
     "pic_small":"http://qukufile2.qianqian.com/data2/pic/b447319eb0064f88f6d15f278cdc4df1/608316740/608316740.jpg@s_1,w_90,h_90",
     "country":"内地",
     "area":"0",
     "publishtime":"2018-11-22",
     "album_no":"1",
     "lrclink":"http://qukufile2.qianqian.com/data2/lrc/0c15511d8c220b64e15d4108e4c86603/608320250/608320250.lrc",
     "copy_type":"1",
     "hot":"52637",
     "all_artist_ting_uid":"1557",
     "resource_type":"0",
     "is_new":"1",
     "rank_change":"2",
     "rank":"1",
     "all_artist_id":"1483",
     "style":"",
     "del_status":"0",
     "relate_status":"0",
     "toneid":"0",
     "all_rate":"96,224,128,320,flac",
     "file_duration":255,
     "has_mv_mobile":0,
     "versions":"",
     "bitrate_fee":"{"0":"0|0","1":"0|0"}",
     "biaoshi":"first,lossless,perm-1",
     "info":"",
     "has_filmtv":"0",
     "si_proxycompany":"华宇世博音乐文化（北京）有限公司-海蝶音乐",
     "res_encryption_flag":"0",
     "song_id":"608296304",
     "title":"《绝代风华》（天下3十周年主题曲）",
     "ting_uid":"1557",
     "author":"许嵩",
     "album_id":"608296302",
     "album_title":"绝代风华（天下3十周年主题曲）",
     "is_first_publish":0,
     "havehigh":2,
     "charge":0,
     "has_mv":0,
     "learn":0,
     "song_source":"web",
     "piao_id":"0",
     "korean_bb_song":"0",
     "resource_type_ext":"0",
     "mv_provider":"0000000000",
     "artist_name":"许嵩",
     "pic_radio":"http://qukufile2.qianqian.com/data2/pic/b447319eb0064f88f6d15f278cdc4df1/608316740/608316740.jpg@s_1,w_300,h_300",
     "pic_s500":"http://qukufile2.qianqian.com/data2/pic/b447319eb0064f88f6d15f278cdc4df1/608316740/608316740.jpg@s_1,w_500,h_500",
     "pic_premium":"http://qukufile2.qianqian.com/data2/pic/b447319eb0064f88f6d15f278cdc4df1/608316740/608316740.jpg@s_1,w_500,h_500",
     "pic_huge":"http://qukufile2.qianqian.com/data2/pic/b447319eb0064f88f6d15f278cdc4df1/608316740/608316740.jpg@s_1,w_1000,h_1000",
     "album_500_500":"http://qukufile2.qianqian.com/data2/pic/b447319eb0064f88f6d15f278cdc4df1/608316740/608316740.jpg@s_1,w_500,h_500",
     "album_800_800":"",
     "album_1000_1000":"http://qukufile2.qianqian.com/data2/pic/b447319eb0064f88f6d15f278cdc4df1/608316740/608316740.jpg@s_1,w_1000,h_1000"
     }
     ],
     "billboard":{
     "billboard_type":"1",
     "billboard_no":"2757",
     "update_date":"2018-11-28",
     "billboard_songnum":"93",
     "havemore":1,
     "name":"新歌榜",
     "comment":"该榜单是根据千千音乐平台歌曲每日播放量自动生成的数据榜单，统计范围为近期发行的歌曲，每日更新一次",
     "pic_s192":"http://business.cdn.qianqian.com/qianqian/pic/bos_client_9a4fbbbfa50203aaa9e69bf189c6a45b.jpg",
     "pic_s640":"http://business.cdn.qianqian.com/qianqian/pic/bos_client_a4aa99cf8bf218304de9786b6ba38982.jpg",
     "pic_s444":"http://hiphotos.qianqian.com/ting/pic/item/78310a55b319ebc4845c84eb8026cffc1e17169f.jpg",
     "pic_s260":"http://hiphotos.qianqian.com/ting/pic/item/e850352ac65c1038cb0f3cb0b0119313b07e894b.jpg",
     "pic_s210":"http://business.cdn.qianqian.com/qianqian/pic/bos_client_dea655f4be544132fb0b5899f063d82e.jpg",
     "web_url":"http://music.baidu.com/top/new"
     },
     "error_code":22000
     }
     */

    public int error_code = 0;
    public List<MusicTypeSong> song_list = new ArrayList<>();
    public Billboard billboard;

    public class MusicTypeSong{
        public String artist_id = "";
        public String language = "";
        public String pic_big = "";
        public String pic_small = "";
        public String country = "";
        public String area = "";
        public String publishtime = "";
        public String album_no = "";
        public String lrclink = "";
        public String copy_type = "";
        public String hot = "";
        public String all_artist_ting_uid = "";
        public String resource_type = "";
        public String is_new = "";
        public String rank_change = "";
        public String rank = "";
        public String all_artist_id = "";
        public String style = "";
        public String del_status = "";
        public String relate_status = "";
        public String toneid = "";
        public String all_rate = "";
        public int file_duration = 0;
        public int has_mv_mobile = 0;
        public String versions = "";
        public String bitrate_fee = "";
        public String biaoshi = "";
        public String info = "";
        public String has_filmtv = "";
        public String si_proxycompany = "";
        public String res_encryption_flag = "";
        public String song_id = "";
        public String title = "";
        public String ting_uid = "";
        public String author = "";
        public String album_id = "";
        public String album_title = "";
        public int is_first_publish = 0;
        public int havehigh = 0;
        public int charge = 0;
        public int has_mv = 0;
        public int learn = 0;
        public String song_source = "";
        public String piao_id = "";
        public String korean_bb_song = "";
        public String resource_type_ext = "";
        public String mv_provider = "";
        public String artist_name = "";
        public String pic_radio = "";
        public String pic_s500 = "";
        public String pic_premium = "";
        public String pic_huge = "";
        public String album_500_500 = "";
        public String album_800_800 = "";
        public String album_1000_1000 = "";

        @Override
        public String toString() {
            return "MusicTypeSong{" +
                    "artist_id='" + artist_id + '\'' +
                    ", language='" + language + '\'' +
                    ", pic_big='" + pic_big + '\'' +
                    ", pic_small='" + pic_small + '\'' +
                    ", country='" + country + '\'' +
                    ", area='" + area + '\'' +
                    ", publishtime='" + publishtime + '\'' +
                    ", album_no='" + album_no + '\'' +
                    ", lrclink='" + lrclink + '\'' +
                    ", copy_type='" + copy_type + '\'' +
                    ", hot='" + hot + '\'' +
                    ", all_artist_ting_uid='" + all_artist_ting_uid + '\'' +
                    ", resource_type='" + resource_type + '\'' +
                    ", is_new='" + is_new + '\'' +
                    ", rank_change='" + rank_change + '\'' +
                    ", rank='" + rank + '\'' +
                    ", all_artist_id='" + all_artist_id + '\'' +
                    ", style='" + style + '\'' +
                    ", del_status='" + del_status + '\'' +
                    ", relate_status='" + relate_status + '\'' +
                    ", toneid='" + toneid + '\'' +
                    ", all_rate='" + all_rate + '\'' +
                    ", file_duration=" + file_duration +
                    ", has_mv_mobile=" + has_mv_mobile +
                    ", versions='" + versions + '\'' +
                    ", bitrate_fee='" + bitrate_fee + '\'' +
                    ", biaoshi='" + biaoshi + '\'' +
                    ", info='" + info + '\'' +
                    ", has_filmtv='" + has_filmtv + '\'' +
                    ", si_proxycompany='" + si_proxycompany + '\'' +
                    ", res_encryption_flag='" + res_encryption_flag + '\'' +
                    ", song_id='" + song_id + '\'' +
                    ", title='" + title + '\'' +
                    ", ting_uid='" + ting_uid + '\'' +
                    ", author='" + author + '\'' +
                    ", album_id='" + album_id + '\'' +
                    ", album_title='" + album_title + '\'' +
                    ", is_first_publish=" + is_first_publish +
                    ", havehigh=" + havehigh +
                    ", charge=" + charge +
                    ", has_mv=" + has_mv +
                    ", learn=" + learn +
                    ", song_source='" + song_source + '\'' +
                    ", piao_id='" + piao_id + '\'' +
                    ", korean_bb_song='" + korean_bb_song + '\'' +
                    ", resource_type_ext='" + resource_type_ext + '\'' +
                    ", mv_provider='" + mv_provider + '\'' +
                    ", artist_name='" + artist_name + '\'' +
                    ", pic_radio='" + pic_radio + '\'' +
                    ", pic_s500='" + pic_s500 + '\'' +
                    ", pic_premium='" + pic_premium + '\'' +
                    ", pic_huge='" + pic_huge + '\'' +
                    ", album_500_500='" + album_500_500 + '\'' +
                    ", album_800_800='" + album_800_800 + '\'' +
                    ", album_1000_1000='" + album_1000_1000 + '\'' +
                    '}';
        }
    }

    public class Billboard{
        public String billboard_type = "";
        public String billboard_no = "";
        public String update_date = "";
        public String billboard_songnum = "";
        public int havemore = 0;
        public String name = "";
        public String comment = "";
        public String pic_s192 = "";
        public String pic_s640 = "";
        public String pic_s444 = "";
        public String pic_s260 = "";
        public String pic_s210 = "";
        public String web_url = "";

        @Override
        public String toString() {
            return "Billboard{" +
                    "billboard_type='" + billboard_type + '\'' +
                    ", billboard_no='" + billboard_no + '\'' +
                    ", update_date='" + update_date + '\'' +
                    ", billboard_songnum='" + billboard_songnum + '\'' +
                    ", havemore=" + havemore +
                    ", name='" + name + '\'' +
                    ", comment='" + comment + '\'' +
                    ", pic_s192='" + pic_s192 + '\'' +
                    ", pic_s640='" + pic_s640 + '\'' +
                    ", pic_s444='" + pic_s444 + '\'' +
                    ", pic_s260='" + pic_s260 + '\'' +
                    ", pic_s210='" + pic_s210 + '\'' +
                    ", web_url='" + web_url + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MusicTypeInfo{" +
                "error_code=" + error_code +
                ", song_list=" + song_list +
                ", billboard=" + billboard +
                '}';
    }
}
