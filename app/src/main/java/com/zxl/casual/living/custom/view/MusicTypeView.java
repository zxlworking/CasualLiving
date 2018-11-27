package com.zxl.casual.living.custom.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zxl.casual.living.R;
import com.zxl.casual.living.http.HttpUtils;
import com.zxl.casual.living.http.data.ResponseBaseBean;
import com.zxl.casual.living.http.listener.NetRequestListener;
import com.zxl.casual.living.utils.Constants;

/**
 * Created by zxl on 2018/11/27.
 */

public class MusicTypeView extends LinearLayout {

    private static final String TAG = "MusicTypeView";

    private Context mContext;

    private int mPageCount = 20;
    private int mCurrentPage = 0;
    private int mTotalPage = Integer.MAX_VALUE;

    private boolean isLoading = false;

    private int mMusicType = 0;


    public MusicTypeView(Context context) {
        super(context);
        init(context);
    }

    public MusicTypeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MusicTypeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
    }

    public void setType(int type){
        ((TextView)findViewById(R.id.tv)).setText(""+type);
        mMusicType = type;
        getMusicListByType();
    }

    private void getMusicListByType(){
        if(isLoading){
            return;
        }
        isLoading = true;

        int offset = mCurrentPage * mPageCount;
        HttpUtils.getInstance().getMusicListByType(mContext, Constants.MUSIC_GET_BY_TYPE_METHOD +
                Constants.MUSIC_GET_BY_TYPE_KEY_PARAM + mMusicType +
                Constants.MUSIC_GET_BY_TYPE_SIZE_KEY_PARAM + mPageCount +
                Constants.MUSIC_GET_BY_TYPE_OFFSET_KEY_PARAM + offset, new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {

                isLoading = false;
            }

            @Override
            public void onNetError() {
                isLoading = false;
            }

            @Override
            public void onNetError(Throwable e) {
                isLoading = false;
            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {
                isLoading = false;
            }
        });
    }
}
