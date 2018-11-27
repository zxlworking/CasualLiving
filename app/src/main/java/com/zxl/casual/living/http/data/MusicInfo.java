package com.zxl.casual.living.http.data;

public class MusicInfo {
    /**
     {
     "songinfo":{
     "special_type":0,
     "pic_huge":"http://qukufile2.qianqian.com/data2/music/3088D4F4A92EE9DD113B380A7D5B9B83/256608249/256608249.jpg",
     "ting_uid":"239547387",
     "pic_premium":"http://qukufile2.qianqian.com/data2/music/3088D4F4A92EE9DD113B380A7D5B9B83/256608249/256608249.jpg@s_0,w_500",
     "havehigh":2,
     "si_proxycompany":"嘉兴市映帆文化传播有限公司",
     "author":"凌川夏",
     "toneid":"0",
     "has_mv":0,
     "song_id":"256002518",
     "title":"一次就好",
     "artist_id":"255945067",
     "lrclink":"http://qukufile2.qianqian.com/data2/lrc/256217992/256217992.lrc",
     "relate_status":"0",
     "learn":0,
     "pic_big":"http://qukufile2.qianqian.com/data2/music/3088D4F4A92EE9DD113B380A7D5B9B83/256608249/256608249.jpg@s_0,w_150",
     "play_type":0,
     "album_id":"256002517",
     "pic_radio":"http://qukufile2.qianqian.com/data2/music/3088D4F4A92EE9DD113B380A7D5B9B83/256608249/256608249.jpg@s_0,w_300",
     "bitrate_fee":"{"0":"0|0","1":"0|0"}",
     "song_source":"web",
     "all_artist_id":"255945067",
     "all_artist_ting_uid":"239547387",
     "piao_id":"0",
     "charge":0,
     "copy_type":"0",
     "all_rate":"96,128,224,320,flac",
     "korean_bb_song":"0",
     "is_first_publish":0,
     "has_mv_mobile":0,
     "album_title":"一次就好",
     "pic_small":"http://qukufile2.qianqian.com/data2/music/3088D4F4A92EE9DD113B380A7D5B9B83/256608249/256608249.jpg@s_0,w_90",
     "album_no":"1",
     "resource_type_ext":"0",
     "resource_type":"0"
     },
     "error_code":22000,
     "bitrate":{
     "show_link":"http://zhangmenshiting.qianqian.com/data2/music/9d74504e55681129b22e348bf2f7d990/596746785/596746785.mp3?xcode=bf69240e9ff0df551e0a87f01dbb9fb5",
     "free":1,
     "song_file_id":596746785,
     "file_size":5380258,
     "file_extension":"mp3",
     "file_duration":336,
     "file_bitrate":128,
     "file_link":"http://zhangmenshiting.qianqian.com/data2/music/9d74504e55681129b22e348bf2f7d990/596746785/596746785.mp3?xcode=bf69240e9ff0df551e0a87f01dbb9fb5",
     "hash":"7adaf644e44216004cda592ccf0267e5a026a54e"
     }
     }
     */

    public SongInfo songinfo;
    public int error_code = 0;
    public Bitrate bitrate;

    public class SongInfo{
        public int special_type = 0;
        public String pic_huge = "";
        public String ting_uid = "";
        public String pic_premium = "";
        public int havehigh = 0;
        public String si_proxycompany = "";
        public String author = "";
        public String toneid = "";
        public int has_mv = 0;
        public String song_id = "";
        public String title = "";
        public String artist_id = "";
        public String lrclink = "";
        public String relate_status = "";
        public int learn = 0;
        public String pic_big = "";
        public int play_type = 0;
        public String album_id = "";
        public String pic_radio = "";
        public String bitrate_fee = "";
        public String song_source = "";
        public String all_artist_id = "";
        public String all_artist_ting_uid = "";
        public String piao_id = "";
        public int charge = 0;
        public String copy_type = "";
        public String all_rate = "";
        public String korean_bb_song = "";
        public int is_first_publish = 0;
        public int has_mv_mobile = 0;
        public String album_title = "";
        public String pic_small = "";
        public String album_no = "";
        public String resource_type_ext = "";
        public String resource_type = "";

        @Override
        public String toString() {
            return "SongInfo{" +
                    "special_type=" + special_type +
                    ", pic_huge='" + pic_huge + '\'' +
                    ", ting_uid='" + ting_uid + '\'' +
                    ", pic_premium='" + pic_premium + '\'' +
                    ", havehigh=" + havehigh +
                    ", si_proxycompany='" + si_proxycompany + '\'' +
                    ", author='" + author + '\'' +
                    ", toneid='" + toneid + '\'' +
                    ", has_mv=" + has_mv +
                    ", song_id='" + song_id + '\'' +
                    ", title='" + title + '\'' +
                    ", artist_id='" + artist_id + '\'' +
                    ", lrclink='" + lrclink + '\'' +
                    ", relate_status='" + relate_status + '\'' +
                    ", learn=" + learn +
                    ", pic_big='" + pic_big + '\'' +
                    ", play_type=" + play_type +
                    ", album_id='" + album_id + '\'' +
                    ", pic_radio='" + pic_radio + '\'' +
                    ", bitrate_fee='" + bitrate_fee + '\'' +
                    ", song_source='" + song_source + '\'' +
                    ", all_artist_id='" + all_artist_id + '\'' +
                    ", all_artist_ting_uid='" + all_artist_ting_uid + '\'' +
                    ", piao_id='" + piao_id + '\'' +
                    ", charge=" + charge +
                    ", copy_type='" + copy_type + '\'' +
                    ", all_rate='" + all_rate + '\'' +
                    ", korean_bb_song='" + korean_bb_song + '\'' +
                    ", is_first_publish=" + is_first_publish +
                    ", has_mv_mobile=" + has_mv_mobile +
                    ", album_title='" + album_title + '\'' +
                    ", pic_small='" + pic_small + '\'' +
                    ", album_no='" + album_no + '\'' +
                    ", resource_type_ext='" + resource_type_ext + '\'' +
                    ", resource_type='" + resource_type + '\'' +
                    '}';
        }
    }

    public class Bitrate{
        public String show_link = "";
        public int free = 0;
        public int song_file_id = 0;
        public int file_size = 0;
        public String file_extension = "";
        public int file_duration = 0;
        public int file_bitrate = 0;
        public String file_link = "";
        public String hash = "";

        @Override
        public String toString() {
            return "Bitrate{" +
                    "show_link='" + show_link + '\'' +
                    ", free=" + free +
                    ", song_file_id=" + song_file_id +
                    ", file_size=" + file_size +
                    ", file_extension='" + file_extension + '\'' +
                    ", file_duration=" + file_duration +
                    ", file_bitrate=" + file_bitrate +
                    ", file_link='" + file_link + '\'' +
                    ", hash='" + hash + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MusicInfo{" +
                "songinfo=" + songinfo +
                ", error_code=" + error_code +
                ", bitrate=" + bitrate +
                '}';
    }
}
