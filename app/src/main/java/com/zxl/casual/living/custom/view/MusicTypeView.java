package com.zxl.casual.living.custom.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zxl.casual.living.R;
import com.zxl.casual.living.common.LoadMoreAdapter;
import com.zxl.casual.living.event.GetMusicDetailInfoEvent;
import com.zxl.casual.living.http.HttpUtils;
import com.zxl.casual.living.http.data.MusicInfoResponseBean;
import com.zxl.casual.living.http.data.MusicTypeInfo;
import com.zxl.casual.living.http.data.ResponseBaseBean;
import com.zxl.casual.living.http.listener.NetRequestListener;
import com.zxl.casual.living.utils.CommonUtils;
import com.zxl.casual.living.utils.Constants;
import com.zxl.casual.living.utils.EventBusUtils;
import com.zxl.common.DebugUtil;

/**
 * Created by zxl on 2018/11/27.
 */

public class MusicTypeView extends FrameLayout {

    private static final String TAG = "MusicTypeView";

    private static final int MSG_FIRST_LOAD_START = 1;
    private static final int MSG_FIRST_LOAD_SUCCESS = 2;
    private static final int MSG_FIRST_LOAD_ERROR = 3;
    private static final int MSG_LOAD_START = 4;
    private static final int MSG_LOAD_SUCCESS = 5;
    private static final int MSG_LOAD_ERROR = 6;

    private Context mContext;

    private int mPageCount = 20;
    private int mCurrentPage = 0;
    private int mTotalPage = 0;

    private boolean isLoading = false;

    private int mMusicType = 0;

    private View mMusicTypeLoadingView;
    private View mMusicTypeLoadErrorView;
    private Button mBtnErrorRefresh;

    private SwipeRefreshLayout mMusicTypeSwipeRefreshLayout;
    private RecyclerView mMusicTypeListView;
    private MusicTypeListAdapter mMusicTypeListAdapter;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_FIRST_LOAD_START:

                    mMusicTypeListView.setVisibility(View.GONE);
                    mMusicTypeLoadingView.setVisibility(View.VISIBLE);
                    mMusicTypeLoadErrorView.setVisibility(View.GONE);
                    break;
                case MSG_FIRST_LOAD_SUCCESS:
                    mMusicTypeListView.setVisibility(View.VISIBLE);
                    mMusicTypeLoadingView.setVisibility(View.GONE);
                    mMusicTypeLoadErrorView.setVisibility(View.GONE);

                    MusicInfoResponseBean<MusicTypeInfo> mFirstTemp = (MusicInfoResponseBean<MusicTypeInfo>) msg.obj;
                    int songnum = Integer.valueOf(mFirstTemp.result.billboard.billboard_songnum);
                    mTotalPage = songnum / mPageCount + (songnum % mPageCount == 0 ? 0 : 1) + 1;
                    mCurrentPage++;
//                    mMusicTypeListAdapter.setBillboard(mFirstTemp.result.billboard);
                    mMusicTypeListAdapter.setData(mFirstTemp.result.song_list,mCurrentPage,mTotalPage);

                    isLoading = false;
                    mMusicTypeSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_FIRST_LOAD_ERROR:
                    mMusicTypeListView.setVisibility(View.GONE);
                    mMusicTypeLoadingView.setVisibility(View.GONE);
                    mMusicTypeLoadErrorView.setVisibility(View.VISIBLE);

                    isLoading = false;
                    mMusicTypeSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_LOAD_START:
                    mMusicTypeListView.setVisibility(View.VISIBLE);
                    mMusicTypeLoadingView.setVisibility(View.GONE);
                    mMusicTypeLoadErrorView.setVisibility(View.GONE);
                    break;
                case MSG_LOAD_SUCCESS:
                    mMusicTypeListView.setVisibility(View.VISIBLE);
                    mMusicTypeLoadingView.setVisibility(View.GONE);
                    mMusicTypeLoadErrorView.setVisibility(View.GONE);

                    MusicInfoResponseBean<MusicTypeInfo> mTemp = (MusicInfoResponseBean<MusicTypeInfo>) msg.obj;
                    mCurrentPage++;
                    mMusicTypeListAdapter.addData(mTemp.result.song_list,mCurrentPage,mTotalPage);

                    isLoading = false;
                    mMusicTypeSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_LOAD_ERROR:
                    mMusicTypeListView.setVisibility(View.VISIBLE);
                    mMusicTypeLoadingView.setVisibility(View.GONE);
                    mMusicTypeLoadErrorView.setVisibility(View.GONE);

                    mMusicTypeListAdapter.setLoadDataState(MusicTypeListAdapter.LOAD_DATA_ERROR_STATE);

                    isLoading = false;
                    mMusicTypeSwipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    };

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

        View mContentView = LayoutInflater.from(mContext).inflate(R.layout.music_type_view,this);

        mMusicTypeLoadingView = mContentView.findViewById(R.id.music_type_loading_view);
        mMusicTypeLoadErrorView = mContentView.findViewById(R.id.music_type_load_error_view);
        mBtnErrorRefresh = mMusicTypeLoadErrorView.findViewById(R.id.load_error_btn);

        mMusicTypeSwipeRefreshLayout = mContentView.findViewById(R.id.music_type_swipe_refresh_layout);

        mMusicTypeSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#3F51B5"),Color.parseColor("#303F9F"),Color.parseColor("#FF4081"));
        mMusicTypeSwipeRefreshLayout.setRefreshing(false);
        mMusicTypeSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mMusicTypeSwipeRefreshLayout.setRefreshing(true);
                getMusicListByType(true);
            }
        });

        mMusicTypeListView = mContentView.findViewById(R.id.music_type_list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mMusicTypeListView.setLayoutManager(linearLayoutManager);
        mMusicTypeListAdapter = new MusicTypeListAdapter();
        mMusicTypeListView.setAdapter(mMusicTypeListAdapter);


        mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMusicListByType(true);
            }
        });
    }

    public void setType(int type){
        mMusicType = type;
        getMusicListByType(true);
    }

    private void getMusicListByType(final boolean isFirstLoad){
        if(isLoading){
            return;
        }
        isLoading = true;

        if(isFirstLoad){
            mCurrentPage = 0;
            mTotalPage = 0;

            mHandler.sendEmptyMessage(MSG_FIRST_LOAD_START);
        }else{
            mHandler.sendEmptyMessage(MSG_LOAD_START);
        }

        int offset = mCurrentPage * mPageCount;
        HttpUtils.getInstance().getMusicListByType(mContext, Constants.MUSIC_GET_BY_TYPE_METHOD +
                Constants.MUSIC_GET_BY_TYPE_KEY_PARAM + mMusicType +
                Constants.MUSIC_GET_BY_TYPE_SIZE_KEY_PARAM + mPageCount +
                Constants.MUSIC_GET_BY_TYPE_OFFSET_KEY_PARAM + offset, new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {
                MusicInfoResponseBean<MusicTypeInfo> musicInfoResponseBean = (MusicInfoResponseBean<MusicTypeInfo>) responseBaseBean;

                if(musicInfoResponseBean.result.song_list == null || musicInfoResponseBean.result.billboard == null){
                    if(isFirstLoad){
                        mHandler.sendEmptyMessage(MSG_FIRST_LOAD_ERROR);
                    }else{
                        mHandler.sendEmptyMessage(MSG_LOAD_ERROR);
                    }
                }else{
                    if(isFirstLoad){
                        Message message = mHandler.obtainMessage();
                        message.what = MSG_FIRST_LOAD_SUCCESS;
                        message.obj = musicInfoResponseBean;
                        message.sendToTarget();
                    }else{
                        Message message = mHandler.obtainMessage();
                        message.what = MSG_LOAD_SUCCESS;
                        message.obj = musicInfoResponseBean;
                        message.sendToTarget();
                    }
                }

            }

            @Override
            public void onNetError() {
                if(isFirstLoad){
                    mHandler.sendEmptyMessage(MSG_FIRST_LOAD_ERROR);
                }else{
                    mHandler.sendEmptyMessage(MSG_LOAD_ERROR);
                }
            }

            @Override
            public void onNetError(Throwable e) {
                if(isFirstLoad){
                    mHandler.sendEmptyMessage(MSG_FIRST_LOAD_ERROR);
                }else{
                    mHandler.sendEmptyMessage(MSG_LOAD_ERROR);
                }
            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {
                if(isFirstLoad){
                    mHandler.sendEmptyMessage(MSG_FIRST_LOAD_ERROR);
                }else{
                    mHandler.sendEmptyMessage(MSG_LOAD_ERROR);
                }
            }
        });
    }

    public class MusicTypeListAdapter extends LoadMoreAdapter<MusicTypeInfo.MusicTypeSong> {

        private MusicTypeInfo.Billboard mBillboard;

        public void setBillboard(MusicTypeInfo.Billboard billboard){
            mBillboard = billboard;
        }

        @Override
        public int getItemCount() {
            return (mBillboard != null ? 1 : 0) + getData().size() + (mCurrentPage < mTotalPage - 1 ? 1 : 0);
        }

        @Override
        public int getItemViewType(int position) {
//            if(position == 0){
//                return HEAD_TYPE;
//            }else
            if(position == getItemCount() - 1 && mCurrentPage < mTotalPage - 1){
                return FOOT_TYPE;
            }else{
                return CONTENT_TYPE;
            }
        }

        @Override
        public RecyclerView.ViewHolder getHeadViewHolder(@NonNull ViewGroup parent) {
            View mItemView = LayoutInflater.from(mContext).inflate(R.layout.item_music_type_head_view, parent, false);
            return new MusicTypeHeadViewHolder(mItemView);
        }

        @Override
        public RecyclerView.ViewHolder getContentViewHolder(@NonNull ViewGroup parent, int viewType) {
            View mItemView = LayoutInflater.from(mContext).inflate(R.layout.item_music_type_content_view, parent, false);
            return new MusicTypeContentViewHolder(mItemView);
        }

        @Override
        public RecyclerView.ViewHolder getFootViewHolder(@NonNull ViewGroup parent) {
            View mItemFootView = LayoutInflater.from(mContext).inflate(R.layout.item_music_type_foot_view, parent, false);
            return new MusicTypeFootViewHolder(mItemFootView);
        }

        @Override
        public void onBindContentViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            if(viewHolder instanceof MusicTypeHeadViewHolder){
                MusicTypeHeadViewHolder musicTypeHeadViewHolder = (MusicTypeHeadViewHolder) viewHolder;

                Glide.with(mContext).load(mBillboard.pic_s192).into(musicTypeHeadViewHolder.mItemMusicTypeHeadImg);
                musicTypeHeadViewHolder.mItemMusicTypeHeadDescTv.setText(mBillboard.name);
            }
            if(viewHolder instanceof MusicTypeContentViewHolder){
                MusicTypeContentViewHolder musicTypeContentViewHolder = (MusicTypeContentViewHolder) viewHolder;
                final MusicTypeInfo.MusicTypeSong musicTypeSong = getData().get(position);

                Glide.with(mContext).load(musicTypeSong.pic_big).into(musicTypeContentViewHolder.mItemMusicTypeContentImg);
                musicTypeContentViewHolder.mItemMusicTypeContentTitleTv.setText(musicTypeSong.title);
                musicTypeContentViewHolder.mItemMusicTypeContentAuthorTv.setText(musicTypeSong.author);

                musicTypeContentViewHolder.mItemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBusUtils.post(new GetMusicDetailInfoEvent(musicTypeSong.song_id));
                    }
                });

            }
        }

        @Override
        public void onBindFootViewHolderLoadDataSuccess(@NonNull RecyclerView.ViewHolder viewHolder) {
            DebugUtil.d(TAG,"onBindFootViewHolderLoadDataSuccess");

            getMusicListByType(false);
        }

        @Override
        public void onBindFootViewHolderLoadingData(@NonNull RecyclerView.ViewHolder viewHolder) {
            DebugUtil.d(TAG,"onBindFootViewHolderLoadingData");

            MusicTypeFootViewHolder musicTypeFootViewHolder = (MusicTypeFootViewHolder) viewHolder;

            musicTypeFootViewHolder.mLoadErrorView.setVisibility(View.GONE);
            musicTypeFootViewHolder.mLoadingView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onBindFootViewHolderLoadDataError(@NonNull RecyclerView.ViewHolder viewHolder) {
            DebugUtil.d(TAG,"onBindFootViewHolderLoadDataError");

            MusicTypeFootViewHolder musicTypeFootViewHolder = (MusicTypeFootViewHolder) viewHolder;

            musicTypeFootViewHolder.mLoadErrorView.setVisibility(View.VISIBLE);
            musicTypeFootViewHolder.mLoadingView.setVisibility(View.GONE);

            View mBtnErrorRefresh = musicTypeFootViewHolder.mLoadErrorView.findViewById(R.id.load_error_btn);

            mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setLoadDataState(LOADING_DATA_STATE);
                    getMusicListByType(false);
                }
            });
        }
    }


    public class MusicTypeHeadViewHolder extends RecyclerView.ViewHolder{

        private View mItemView;

        private ImageView mItemMusicTypeHeadImg;
        private TextView mItemMusicTypeHeadDescTv;

        public MusicTypeHeadViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mItemMusicTypeHeadImg = mItemView.findViewById(R.id.item_music_type_head_img);
            mItemMusicTypeHeadDescTv = mItemView.findViewById(R.id.item_music_type_head_desc_tv);
        }
    }

    public class MusicTypeContentViewHolder extends RecyclerView.ViewHolder{

        private View mItemView;

        private ImageView mItemMusicTypeContentImg;
        private TextView mItemMusicTypeContentTitleTv;
        private TextView mItemMusicTypeContentAuthorTv;

        public MusicTypeContentViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mItemMusicTypeContentImg = mItemView.findViewById(R.id.item_music_type_content_img);
            mItemMusicTypeContentTitleTv = mItemView.findViewById(R.id.item_music_type_content_title_tv);
            mItemMusicTypeContentAuthorTv = mItemView.findViewById(R.id.item_music_type_content_author_tv);
        }
    }

    public class MusicTypeFootViewHolder extends RecyclerView.ViewHolder{

        private View mItemView;

        private View mLoadingView;
        private View mLoadErrorView;

        public MusicTypeFootViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mLoadingView = mItemView.findViewById(R.id.loading_view);
            mLoadErrorView = mItemView.findViewById(R.id.load_error_view);
        }
    }
}
