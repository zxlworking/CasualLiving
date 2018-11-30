package com.zxl.casual.living.event;

/**
 * Created by zxl on 2018/11/28.
 */

public class GetMusicInfoEvent {

    public long mSongId = 0;

    public GetMusicInfoEvent(long songId){
        mSongId = songId;
    }

}
