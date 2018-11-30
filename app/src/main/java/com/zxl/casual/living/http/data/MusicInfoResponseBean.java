package com.zxl.casual.living.http.data;

/**
 * Created by zxl on 2018/11/30.
 */

public class MusicInfoResponseBean extends ResponseBaseBean {
    public String music_operator = "";
    public MusicComment music_comment = null;
    public MusicPlayInfo music_play_info = null;
    public MusicLrc music_lrc = null;

    @Override
    public String toString() {
        return "MusicInfoResponseBean{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                ", music_operator='" + music_operator + '\'' +
                ", music_comment=" + music_comment +
                ", music_play_info=" + music_play_info +
                ", music_lrc=" + music_lrc +
                '}';
    }
}
