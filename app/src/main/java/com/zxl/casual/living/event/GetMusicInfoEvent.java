package com.zxl.casual.living.event;

/**
 * Created by zxl on 2018/11/28.
 */

public class GetMusicInfoEvent {

    public long mSongId = 0;

    public String mSongUrl = "";

    public GetMusicInfoEvent(long songId, String songUrl){
        mSongId = songId;
        mSongUrl = songUrl;
    }

}
