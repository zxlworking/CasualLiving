package com.zxl.casual.living.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.zxl.casual.living.R;
import com.zxl.casual.living.common.FastBlurUtil;
import com.zxl.casual.living.common.PaletteParseUtil;
import com.zxl.casual.living.custom.view.MusicSearchView;
import com.zxl.casual.living.custom.view.MusicTypeView;
import com.zxl.casual.living.event.GetMusicInfoEvent;
import com.zxl.casual.living.http.HttpUtils;
import com.zxl.casual.living.http.data.LrcInfo;
import com.zxl.casual.living.http.data.MusicInfoResponseBean;
import com.zxl.casual.living.http.data.MusicLrc;
import com.zxl.casual.living.http.data.ResponseBaseBean;
import com.zxl.casual.living.http.listener.NetRequestListener;
import com.zxl.casual.living.utils.CommonUtils;
import com.zxl.casual.living.utils.Constants;
import com.zxl.casual.living.utils.EventBusUtils;
import com.zxl.common.DebugUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxl on 2018/11/21.
 */

public class MusicFragment extends BaseFragment {

    private static final String TAG = "MusicFragment";

    private View mContentView;

    private View mMusicContentView;

    private View mMusicInfoLoadingView;
    private View mMusicInfoLoadErrorView;
    private Button mBtnErrorRefresh;

    private TabLayout mMusicTypeTableLayout;
    private ViewPager mMusicTypeViewPager;
    private MusicTypeAdapter mMusicTypeAdapter;

    private RecyclerView mLrcRecyclerView;
    private LrcAdapter mLrcAdapter;
    private LinearLayoutManager mLrcLinearLayoutManager;

    private View mMusicInfoContentView;
    private ImageView mMusicInfoFastBlurView;
    private ImageView mMusicInfoImg;
    private ImageView mMusicInfoBackImg;
    private ImageView mMusicPlayAndPauseImg;
    private View mMusicInfoControllerView;

    private List<View> mMusicViews = new ArrayList<>();

    private boolean isLoading = false;

    private GetMusicInfoEvent mCurrentGetMusicInfoEvent;

    private Bitmap mLastFastBlurBmp = null;

    private Handler mHandler = new Handler();

    private MediaPlayer mMediaPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DebugUtil.d(TAG, "onCreateView");

        EventBusUtils.register(this);

        mContentView = inflater.inflate(R.layout.fragment_music, null);

        mMusicContentView = mContentView.findViewById(R.id.music_content_view);

        mMusicInfoLoadingView = mContentView.findViewById(R.id.music_info_loading_view);
        mMusicInfoLoadErrorView = mContentView.findViewById(R.id.music_info_load_error_view);
        mBtnErrorRefresh = mMusicInfoLoadErrorView.findViewById(R.id.load_error_btn);

        mMusicTypeTableLayout = mContentView.findViewById(R.id.music_type_table_layout);
        mMusicTypeViewPager = mContentView.findViewById(R.id.music_type_view_pager);

        for(String typeName : Constants.MUSIC_TYPE_NAMES){
            mMusicTypeTableLayout.addTab(mMusicTypeTableLayout.newTab().setText(typeName));
        }

        mMusicTypeAdapter = new MusicTypeAdapter();
        mMusicTypeViewPager.setAdapter(mMusicTypeAdapter);
        mMusicTypeTableLayout.setupWithViewPager(mMusicTypeViewPager);

        mMusicInfoContentView = mContentView.findViewById(R.id.music_info_content_view);
        mMusicInfoFastBlurView = mContentView.findViewById(R.id.music_info_fast_blur_img);
        mMusicInfoImg = mContentView.findViewById(R.id.music_info_img);
        mMusicInfoBackImg = mContentView.findViewById(R.id.music_info_back_img);
        mMusicPlayAndPauseImg = mContentView.findViewById(R.id.music_play_and_pause_img);
        mMusicInfoControllerView = mContentView.findViewById(R.id.music_info_controller_view);

        mLrcRecyclerView = mContentView.findViewById(R.id.lrc_recycler_view);

        mMusicInfoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMusicInfoImg.setVisibility(View.GONE);
                mLrcRecyclerView.setVisibility(View.VISIBLE);
            }
        });
        mMusicInfoBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMusicInfoContentView.setVisibility(View.GONE);
                mMusicContentView.setVisibility(View.VISIBLE);
            }
        });
        mMusicInfoContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mMusicPlayAndPauseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediaPlayer != null){
                    if(mMediaPlayer.isPlaying()){
                        mMediaPlayer.pause();
                        mMusicPlayAndPauseImg.setImageResource(R.mipmap.play_icon);
                    }else{
                        mMediaPlayer.start();
                        mMusicPlayAndPauseImg.setImageResource(R.mipmap.pause_icon);
                    }
                }
            }
        });

        mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMusicInfo(true, mCurrentGetMusicInfoEvent.mSongId);
            }
        });

        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMusicViews.clear();
        for(int i = 0; i < Constants.MUSIC_TYPE_NAMES.length - 1; i++){
            MusicTypeView musicTypeView = new MusicTypeView(mActivity);
            mMusicViews.add(musicTypeView);
        }
        MusicSearchView musicSearchView = new MusicSearchView(mActivity);
        mMusicViews.add(musicSearchView);
        mMusicTypeAdapter.notifyDataSetChanged();

        for(int i = 0; i < Constants.MUSIC_TYPE_NAMES.length; i++){
            mMusicTypeTableLayout.getTabAt(i).setText(Constants.MUSIC_TYPE_NAMES[i]);
        }

        LinearLayout linearLayout = (LinearLayout) mMusicTypeTableLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(mActivity, R.drawable.layout_divider_vertical));

        mLrcLinearLayoutManager = new LinearLayoutManager(mActivity);
        mLrcRecyclerView.setLayoutManager(mLrcLinearLayoutManager);
        mLrcAdapter = new LrcAdapter();
        mLrcRecyclerView.setAdapter(mLrcAdapter);

    }


    private void getMusicInfo(boolean isFirst, long songId) {
        if(isLoading){
            return;
        }
        isLoading = true;

        mMusicInfoLoadingView.setVisibility(View.VISIBLE);
        mMusicInfoLoadErrorView.setVisibility(View.GONE);
        mMusicContentView.setVisibility(View.GONE);
        mMusicInfoContentView.setVisibility(View.VISIBLE);
        mMusicInfoImg.setVisibility(View.GONE);
        mLrcRecyclerView.setVisibility(View.GONE);

        HttpUtils.getInstance().getMusicInfo(mActivity, Constants.MUSIC_OPERATOR_OTHER, String.valueOf(songId), "0", "20", new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {
                MusicInfoResponseBean musicInfoResponseBean = (MusicInfoResponseBean) responseBaseBean;
                if(musicInfoResponseBean.music_play_info.data != null && musicInfoResponseBean.music_play_info.data.size() > 0){
                    if(mCurrentGetMusicInfoEvent.mSongId == musicInfoResponseBean.music_play_info.data.get(0).id){

                        Glide
                                .with(mActivity)
                                .load(mCurrentGetMusicInfoEvent.mSongUrl)
                                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                .into(mMusicInfoImg);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                FastBlurUtil.getUrlBitmap(mActivity, mCurrentGetMusicInfoEvent.mSongUrl, CommonUtils.screenWidth(), CommonUtils.screenHeight(), new FastBlurUtil.OnCompleteListener() {
                                    @Override
                                    public void onComplete(String url, final Bitmap bitmap) {
                                        if(TextUtils.equals(url,mCurrentGetMusicInfoEvent.mSongUrl)){
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    destroyFastBlurBmp();
                                                    mMusicInfoFastBlurView.setImageBitmap(bitmap);
                                                    mLastFastBlurBmp = bitmap;
                                                }
                                            });
                                        }else{
                                            destroyFastBlurBmp(bitmap);
                                        }
                                    }
                                });
                            }
                        }).start();

                        List<LrcInfo> lrcInfos = new ArrayList<>();
                        MusicLrc musicLrc = musicInfoResponseBean.music_lrc;
                        if(musicLrc != null && musicLrc.lrc != null && !TextUtils.isEmpty(musicLrc.lrc.lyric)){
                            String lrcStr = musicLrc.lrc.lyric;
                            String[] lrcArray = lrcStr.split("\\[");
                            for(String s : lrcArray){
                                DebugUtil.d(TAG,"lrc::s = " + s + "--->" + s.contains("]"));
                                if(s.contains("]")){
                                    String[] sArray = s.split("]");
                                    LrcInfo lrcInfo = new LrcInfo();
                                    lrcInfo.mCurrentTime = sArray[0];
                                    lrcInfo.mContent = sArray[1];
                                    lrcInfo.hasTime = true;
                                    lrcInfos.add(lrcInfo);
                                }
                            }

                            if(lrcInfos.isEmpty()){
                                DebugUtil.d(TAG,"lrcInfos.isEmpty::s = " + lrcStr.contains("\n"));
                                lrcArray = lrcStr.split("\n");
                                for(String s : lrcArray){
                                    DebugUtil.d(TAG,"lrcInfos.isEmpty::s = " + s);
                                    LrcInfo lrcInfo = new LrcInfo();
                                    lrcInfo.mCurrentTime = "00:00";
                                    lrcInfo.mContent = s;
                                    lrcInfos.add(lrcInfo);
                                }
                            }
                        }else{
                            LrcInfo lrcInfo = new LrcInfo();
                            lrcInfo.mCurrentTime = "00:00";
                            lrcInfo.mContent = "暂无歌词";
                            lrcInfos.add(lrcInfo);
                        }
                        mLrcAdapter.setData(lrcInfos);
                        if(lrcInfos.size() > 0){
                            mLrcLinearLayoutManager.scrollToPosition(0);
                        }

                        PaletteParseUtil paletteParseUtil = new PaletteParseUtil();
                        paletteParseUtil.parse(mCurrentGetMusicInfoEvent.mSongUrl, mMusicInfoImg, new PaletteParseUtil.OnPaletteCompleteListener() {
                            @Override
                            public void onComplete(String url, int bgColor, int textColor) {
                                if(TextUtils.equals(url,mCurrentGetMusicInfoEvent.mSongUrl)){
                                    mMusicInfoBackImg.setBackgroundColor(bgColor);
                                    mMusicInfoControllerView.setBackgroundColor(bgColor);
                                    mLrcAdapter.setTextColor(textColor);
                                }
                            }
                        });

                        startPlayMusic(musicInfoResponseBean.music_play_info.data.get(0).url);

                        mMusicInfoLoadingView.setVisibility(View.GONE);
                        mMusicInfoLoadErrorView.setVisibility(View.GONE);
                        mMusicInfoContentView.setVisibility(View.VISIBLE);
                        mMusicContentView.setVisibility(View.GONE);

                        mMusicInfoImg.setVisibility(View.VISIBLE);
                        mLrcRecyclerView.setVisibility(View.GONE);

                        isLoading = false;
                        return;
                    }
                }

                Toast.makeText(mActivity,"获取歌曲信息失败",Toast.LENGTH_SHORT).show();

                isLoading = false;
            }

            @Override
            public void onNetError() {

                mMusicInfoLoadingView.setVisibility(View.GONE);
                mMusicInfoLoadErrorView.setVisibility(View.VISIBLE);
                mMusicInfoContentView.setVisibility(View.VISIBLE);
                mMusicContentView.setVisibility(View.GONE);

                isLoading = false;

            }

            @Override
            public void onNetError(Throwable e) {
                mMusicInfoLoadingView.setVisibility(View.GONE);
                mMusicInfoLoadErrorView.setVisibility(View.VISIBLE);
                mMusicInfoContentView.setVisibility(View.VISIBLE);
                mMusicContentView.setVisibility(View.GONE);

                isLoading = false;

            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {
                mMusicInfoLoadingView.setVisibility(View.GONE);
                mMusicInfoLoadErrorView.setVisibility(View.VISIBLE);
                mMusicInfoContentView.setVisibility(View.VISIBLE);
                mMusicContentView.setVisibility(View.GONE);

                isLoading = false;

            }
        });
    }

    private void startPlayMusic(final String url) {
        DebugUtil.d(TAG,"startPlayMusic::url = " + url);
        if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
        }else{
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMusicPlayAndPauseImg.setImageResource(R.mipmap.play_icon);
        }
        try {
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    DebugUtil.d(TAG,"startPlayMusic::onPrepared");
                    mp.start();
                    mMusicPlayAndPauseImg.setImageResource(R.mipmap.pause_icon);
                }
            });

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    DebugUtil.d(TAG,"startPlayMusic::onCompletion");
                    mMusicPlayAndPauseImg.setImageResource(R.mipmap.play_icon);
                }
            });
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void destroyFastBlurBmp(){
        if(mLastFastBlurBmp != null){
            if(!mLastFastBlurBmp.isRecycled()){
                mLastFastBlurBmp.recycle();
            }
            mLastFastBlurBmp = null;
        }
    }

    private void destroyFastBlurBmp(Bitmap bmp){
        if(bmp != null){
            if(!bmp.isRecycled()){
                bmp.recycle();
            }
            bmp = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        DebugUtil.d(TAG,"onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBusUtils.unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyFastBlurBmp();

        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();

            mMusicPlayAndPauseImg.setImageResource(R.mipmap.play_icon);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMusicInfoEvent(GetMusicInfoEvent event){
        mCurrentGetMusicInfoEvent = event;
        getMusicInfo(true, event.mSongId);
    }

    class MusicTypeAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mMusicViews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View musicView = mMusicViews.get(position);
            if(musicView instanceof MusicTypeView){
                MusicTypeView musicTypeView = (MusicTypeView) musicView;
//                musicTypeView.setType(Constants.MUSIC_TYPES[position]);
            }
            container.addView(musicView);
            return musicView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    class LrcAdapter extends RecyclerView.Adapter<LrcViewHolder>{

        private int mTextColor = 0;

        private List<LrcInfo> mLrcInfos = new ArrayList<>();

        public void setTextColor(int color){
            mTextColor = color;
            notifyDataSetChanged();
        }

        public void setData(List<LrcInfo> list){
            DebugUtil.d(TAG,"LrcAdapter::setData::list = " + list.size() + "--->" + Thread.currentThread());
            mLrcInfos.clear();
            mLrcInfos.addAll(list);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public LrcViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_lrc_view,viewGroup,false);
            return new LrcViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LrcViewHolder lrcViewHolder, int position) {
            lrcViewHolder.mItemLrcTv.setTextColor(mTextColor);
            lrcViewHolder.mItemLrcTv.setText(mLrcInfos.get(position).mContent);
            lrcViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLrcRecyclerView.setVisibility(View.GONE);
                    mMusicInfoImg.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public int getItemCount() {
            int count = mLrcInfos.size();
            return count;
        }
    }

    class LrcViewHolder extends RecyclerView.ViewHolder{

        public View mItemView;

        public TextView mItemLrcTv;

        public LrcViewHolder(@NonNull View itemView) {
            super(itemView);

            mItemView = itemView;
            mItemLrcTv = itemView.findViewById(R.id.item_lrc_tv);
        }
    }
}
