package com.zxl.casual.living.http.data;

/**
 * Created by zxl on 2018/11/30.
 */

public class MusicLrc {
    /**
     "lrc":{
     "version":5,
     "lyric":"[00:00.00] 作曲 : 董冬冬
     [00:00.446] 作词 : 晨曦
     [00:01.340]编曲 : 董健剑
     [00:02.340]音乐总监：安栋
     [00:03.340]音响总监：安栋
     [00:04.340]吉他：倪方来/毕赫宸
     [00:05.340]键盘：张继旗
     [00:06.340]贝斯：张梦斌
     [00:07.340]鼓手：Martin Ngim
     [00:08.340]打击乐：王宏涛
     [00:09.340]和音：爱之音和音
     [00:10.340]Program：杨磊
     [00:11.340]弦乐：吾音管弦乐队 陈阳岳彤（首席）
     [00:18.340]想看你笑
     [00:21.040]想和你闹
     [00:23.310]想拥你入我怀抱
     [00:28.760]上一秒红着脸在争吵
     [00:34.350]下一秒转身就能和好
     [00:40.880]不怕你哭
     [00:43.660]不怕你叫
     [00:45.730]因为你是我的骄傲
     [00:51.380]一双眼睛追着你乱跑
     [00:57.040]一颗心早已经准备好
     [01:08.340]一次就好我带你去看天荒地老
     [01:14.190]在阳光灿烂的日子里开怀大笑
     [01:19.910]在自由自在的空气里吵吵闹闹
     [01:25.150]你可知道我唯一的想要
     [01:30.880]世界还小我陪你去到天涯海角
     [01:36.820]在没有烦恼的角落里停止寻找
     [01:42.570]在无忧无虑的时光里慢慢变老
     [01:47.860]你可知道我全部的心跳
     [01:52.770]随你跳
     [02:08.280]不怕你哭
     [02:11.150]不怕你叫
     [02:13.310]因为你是我的骄傲
     [02:19.180]一双眼睛追着你乱跑
     [02:24.630]一颗心早已经准备好
     [02:36.110]一次就好我带你去看天荒地老
     [02:41.830]在阳光灿烂的日子里开怀大笑
     [02:47.440]在自由自在的空气里吵吵闹闹
     [02:52.760]你可知道我唯一的想要
     [02:58.510]世界还小我陪你去到天涯海角
     [03:04.490]在没有烦恼的角落里停止寻找
     [03:10.270]在无忧无虑的时光里慢慢变老
     [03:17.990]你可知道我全部的心跳
     [03:23.510]随你跳
     "
     },
     "code":200,
     "qfy":false,
     "sfy":false,
     "tlyric":{
     "version":0,
     "lyric":null
     },
     "sgc":false
     */

    public int code = 0;
    public boolean qfy = false;
    public boolean sfy = false;
    public boolean sgc = false;
    public Lrc lrc = null;
    public Tlyric tlyric = null;

    public class Lrc{
        public int version = 0;
        public String lyric = "";
    }

    public class Tlyric{
        public int version = 0;

        @Override
        public String toString() {
            return "Tlyric{" +
                    "version=" + version +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MusicLrc{" +
                "code=" + code +
                ", qfy=" + qfy +
                ", sfy=" + sfy +
                ", sgc=" + sgc +
                ", tlyric=" + tlyric +
                '}';
    }
}
