package com.zxl.casual.living.event;

/**
 * Created by zxl on 2018/11/28.
 */

public class GetMusicDetailInfoEvent {

    public String mSongId = "";

    public GetMusicDetailInfoEvent(String songId){
        mSongId = songId;
    }

}
