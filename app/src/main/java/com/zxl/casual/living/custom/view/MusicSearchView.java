package com.zxl.casual.living.custom.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zxl.casual.living.R;
import com.zxl.casual.living.common.LoadMoreAdapter;
import com.zxl.casual.living.http.HttpUtils;
import com.zxl.casual.living.http.data.MusicSearchResponseBean;
import com.zxl.casual.living.http.data.MusicSearchResult;
import com.zxl.casual.living.http.data.ResponseBaseBean;
import com.zxl.casual.living.http.listener.NetRequestListener;
import com.zxl.casual.living.utils.CommonUtils;
import com.zxl.casual.living.utils.Constants;
import com.zxl.common.DebugUtil;

/**
 * Created by zxl on 2018/11/29.
 */

public class MusicSearchView extends LinearLayout {

    private static final String TAG = "MusicSearchView";

    private static final int MSG_FIRST_LOAD_START = 1;
    private static final int MSG_FIRST_LOAD_SUCCESS = 2;
    private static final int MSG_FIRST_LOAD_ERROR = 3;
    private static final int MSG_LOAD_START = 4;
    private static final int MSG_LOAD_SUCCESS = 5;
    private static final int MSG_LOAD_ERROR = 6;

    private Context mContext;

    private EditText mSearchEt;

    private View mMusicSearchLoadingView;
    private View mMusicSearchLoadErrorView;
    private Button mBtnErrorRefresh;

    private SwipeRefreshLayout mMusicSearchSwipeRefreshLayout;
    private RecyclerView mMusicSearchListView;
    private MusicSearchAdapter mMusicSearchAdapter;

    private int mPageCount = 20;
    private int mCurrentPage = 0;
    private int mTotalPage = 0;

    private boolean isLoading = false;

    private String mKeyWord = "";

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_FIRST_LOAD_START:

                    mMusicSearchListView.setVisibility(View.GONE);
                    mMusicSearchLoadingView.setVisibility(View.VISIBLE);
                    mMusicSearchLoadErrorView.setVisibility(View.GONE);
                    break;
                case MSG_FIRST_LOAD_SUCCESS:
                    mMusicSearchListView.setVisibility(View.VISIBLE);
                    mMusicSearchLoadingView.setVisibility(View.GONE);
                    mMusicSearchLoadErrorView.setVisibility(View.GONE);

                    MusicSearchResponseBean mFirstTemp = (MusicSearchResponseBean) msg.obj;
                    int songnum = Integer.valueOf(mFirstTemp.result.result.songCount);
                    mTotalPage = songnum / mPageCount + (songnum % mPageCount == 0 ? 0 : 1) + 1;
                    mCurrentPage++;
                    mMusicSearchAdapter.setData(mFirstTemp.result.result.songs,mCurrentPage,mTotalPage);

                    isLoading = false;
                    mMusicSearchSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_FIRST_LOAD_ERROR:
                    mMusicSearchListView.setVisibility(View.GONE);
                    mMusicSearchLoadingView.setVisibility(View.GONE);
                    mMusicSearchLoadErrorView.setVisibility(View.VISIBLE);

                    isLoading = false;
                    mMusicSearchSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_LOAD_START:
                    mMusicSearchListView.setVisibility(View.VISIBLE);
                    mMusicSearchLoadingView.setVisibility(View.GONE);
                    mMusicSearchLoadErrorView.setVisibility(View.GONE);
                    break;
                case MSG_LOAD_SUCCESS:
                    mMusicSearchListView.setVisibility(View.VISIBLE);
                    mMusicSearchLoadingView.setVisibility(View.GONE);
                    mMusicSearchLoadErrorView.setVisibility(View.GONE);

                    MusicSearchResponseBean mTemp = (MusicSearchResponseBean) msg.obj;
                    mCurrentPage++;
                    mMusicSearchAdapter.addData(mTemp.result.result.songs,mCurrentPage,mTotalPage);

                    isLoading = false;
                    mMusicSearchSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_LOAD_ERROR:
                    mMusicSearchListView.setVisibility(View.VISIBLE);
                    mMusicSearchLoadingView.setVisibility(View.GONE);
                    mMusicSearchLoadErrorView.setVisibility(View.GONE);

                    mMusicSearchAdapter.setLoadDataState(MusicSearchAdapter.LOAD_DATA_ERROR_STATE);

                    isLoading = false;
                    mMusicSearchSwipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    };

    public MusicSearchView(Context context) {
        super(context);
        init(context);
    }

    public MusicSearchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MusicSearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;

        View mContentView = LayoutInflater.from(mContext).inflate(R.layout.music_search_view,this);

        mSearchEt = mContentView.findViewById(R.id.search_et);

        mMusicSearchLoadingView = mContentView.findViewById(R.id.music_search_loading_view);
        mMusicSearchLoadErrorView = mContentView.findViewById(R.id.music_search_load_error_view);
        mBtnErrorRefresh = mMusicSearchLoadErrorView.findViewById(R.id.load_error_btn);

        mMusicSearchSwipeRefreshLayout = mContentView.findViewById(R.id.music_search_swipe_refresh_layout);

        mMusicSearchSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#3F51B5"),Color.parseColor("#303F9F"),Color.parseColor("#FF4081"));
        mMusicSearchSwipeRefreshLayout.setRefreshing(false);
        mMusicSearchSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mMusicSearchSwipeRefreshLayout.setRefreshing(true);
                searchMusic(true, mKeyWord);
            }
        });

        mMusicSearchListView = mContentView.findViewById(R.id.music_search_list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mMusicSearchListView.setLayoutManager(linearLayoutManager);
        mMusicSearchAdapter = new MusicSearchAdapter();
        mMusicSearchListView.setAdapter(mMusicSearchAdapter);

        mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
                if (actionId== EditorInfo.IME_ACTION_SEARCH ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)){
                    mKeyWord = mSearchEt.getText().toString();
                    searchMusic(true, mKeyWord);
                    CommonUtils.hideIputKeyboard((Activity) mContext);
                    return true;
                }
                return false;
            }
        });


        mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMusic(true,mKeyWord);
            }
        });
    }

    private void searchMusic(final boolean isFirstLoad, String keyWord){
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
        HttpUtils.getInstance().searchMusic(mContext,Constants.MUSIC_OPERATOR_SEARCH_MUSIC, keyWord, String.valueOf(offset), String.valueOf(mPageCount), new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {
                MusicSearchResponseBean musicSearchResponseBean = (MusicSearchResponseBean) responseBaseBean;

                if(musicSearchResponseBean.result == null){
                    if(isFirstLoad){
                        mHandler.sendEmptyMessage(MSG_FIRST_LOAD_ERROR);
                    }else{
                        mHandler.sendEmptyMessage(MSG_LOAD_ERROR);
                    }
                }else{
                    if(isFirstLoad){
                        Message message = mHandler.obtainMessage();
                        message.what = MSG_FIRST_LOAD_SUCCESS;
                        message.obj = musicSearchResponseBean;
                        message.sendToTarget();
                    }else{
                        Message message = mHandler.obtainMessage();
                        message.what = MSG_LOAD_SUCCESS;
                        message.obj = musicSearchResponseBean;
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

    public class MusicSearchAdapter extends LoadMoreAdapter<MusicSearchResult.Song> {

        @Override
        public int getItemCount() {
            return getData().size() + (mCurrentPage < mTotalPage - 1 ? 1 : 0);
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
            return null;
        }

        @Override
        public RecyclerView.ViewHolder getContentViewHolder(@NonNull ViewGroup parent, int viewType) {
            View mItemView = LayoutInflater.from(mContext).inflate(R.layout.item_music_search_content_view, parent, false);
            return new MusicSearchContentViewHolder(mItemView);
        }

        @Override
        public RecyclerView.ViewHolder getFootViewHolder(@NonNull ViewGroup parent) {
            View mItemFootView = LayoutInflater.from(mContext).inflate(R.layout.item_music_search_foot_view, parent, false);
            return new MusicSearchFootViewHolder(mItemFootView);
        }

        @Override
        public void onBindContentViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            MusicSearchContentViewHolder musicSearchContentViewHolder = (MusicSearchContentViewHolder) viewHolder;

            Glide.with(mContext).load(getData().get(position).al.picUrl).into(musicSearchContentViewHolder.mItemMusicSearchContentImg);
            musicSearchContentViewHolder.mItemMusicSearchContentTitleTv.setText(getData().get(position).name);
            String artistName = "";
            for(int i = 0; i < getData().get(position).ar.size(); i++){
                MusicSearchResult.Artist artist = getData().get(position).ar.get(i);
                if(i == getData().get(position).ar.size() - 1){
                    artistName = artistName + artist.name;
                }else{
                    artistName = artistName + artist.name + "、";
                }
            }
            musicSearchContentViewHolder.mItemMusicSearchContentAuthorTv.setText(getData().get(position).ar.get(0).name);

            musicSearchContentViewHolder.mItemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    EventBusUtils.post(new GetMusicDetailInfoEvent(musicTypeSong.song_id));
                }
            });
        }

        @Override
        public void onBindFootViewHolderLoadDataSuccess(@NonNull RecyclerView.ViewHolder viewHolder) {
            DebugUtil.d(TAG,"onBindFootViewHolderLoadDataSuccess");

            searchMusic(false, mKeyWord);
        }

        @Override
        public void onBindFootViewHolderLoadingData(@NonNull RecyclerView.ViewHolder viewHolder) {
            DebugUtil.d(TAG,"onBindFootViewHolderLoadingData");

            MusicSearchFootViewHolder musicSearchFootViewHolder = (MusicSearchFootViewHolder) viewHolder;

            musicSearchFootViewHolder.mLoadErrorView.setVisibility(View.GONE);
            musicSearchFootViewHolder.mLoadingView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onBindFootViewHolderLoadDataError(@NonNull RecyclerView.ViewHolder viewHolder) {
            DebugUtil.d(TAG,"onBindFootViewHolderLoadDataError");

            MusicSearchFootViewHolder musicSearchFootViewHolder = (MusicSearchFootViewHolder) viewHolder;

            musicSearchFootViewHolder.mLoadErrorView.setVisibility(View.VISIBLE);
            musicSearchFootViewHolder.mLoadingView.setVisibility(View.GONE);

            View mBtnErrorRefresh = musicSearchFootViewHolder.mLoadErrorView.findViewById(R.id.load_error_btn);

            mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setLoadDataState(LOADING_DATA_STATE);
                    searchMusic(false, mKeyWord);
                }
            });
        }
    }

    public class MusicSearchContentViewHolder extends RecyclerView.ViewHolder{

        private View mItemView;

        private ImageView mItemMusicSearchContentImg;
        private TextView mItemMusicSearchContentTitleTv;
        private TextView mItemMusicSearchContentAuthorTv;

        public MusicSearchContentViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mItemMusicSearchContentImg = mItemView.findViewById(R.id.item_music_search_content_img);
            mItemMusicSearchContentTitleTv = mItemView.findViewById(R.id.item_music_search_content_title_tv);
            mItemMusicSearchContentAuthorTv = mItemView.findViewById(R.id.item_music_search_content_author_tv);
        }
    }

    public class MusicSearchFootViewHolder extends RecyclerView.ViewHolder{

        private View mItemView;

        private View mLoadingView;
        private View mLoadErrorView;

        public MusicSearchFootViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mLoadingView = mItemView.findViewById(R.id.loading_view);
            mLoadErrorView = mItemView.findViewById(R.id.load_error_view);
        }
    }
}
