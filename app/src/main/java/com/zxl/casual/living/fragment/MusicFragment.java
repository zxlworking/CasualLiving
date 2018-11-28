package com.zxl.casual.living.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zxl.casual.living.R;
import com.zxl.casual.living.custom.view.LrcView;
import com.zxl.casual.living.custom.view.MusicTypeView;
import com.zxl.casual.living.event.GetMusicDetailInfoEvent;
import com.zxl.casual.living.http.HttpUtils;
import com.zxl.casual.living.http.data.LrcInfo;
import com.zxl.casual.living.http.data.LrcListInfo;
import com.zxl.casual.living.http.data.MusicDetailInfo;
import com.zxl.casual.living.http.data.MusicInfoResponseBean;
import com.zxl.casual.living.http.data.MusicLrcDownloadResponseBean;
import com.zxl.casual.living.http.data.MusicSearchResult;
import com.zxl.casual.living.http.data.ResponseBaseBean;
import com.zxl.casual.living.http.listener.NetRequestListener;
import com.zxl.casual.living.utils.CommonUtils;
import com.zxl.casual.living.utils.Constants;
import com.zxl.casual.living.utils.EventBusUtils;
import com.zxl.common.DebugUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zxl on 2018/11/21.
 */

public class MusicFragment extends BaseFragment {

    private static final String TAG = "MusicFragment";

    private static final int MSG_GET_LRC_SUCCESS = 1;

    private static final int OPERATOR_SEARCH_MUSIC_LIST = 1;
    private static final int OPERATOR_SEARCH_MUSIC_ITEM = 2;

    private View mContentView;

    private View mLoadingView;
    private View mLoadErrorView;
    private Button mBtnErrorRefresh;

    private EditText mSearchEt;

    private View mMusicSearchResultView;
    private RecyclerView mMusicSearchResultListView;
    private MusicSearchListAdapter mMusicSearchListAdapter;

    private View mMusicTypeView;
    private TabLayout mMusicTypeTableLayout;
    private ViewPager mMusicTypeViewPager;
    private MusicTypeAdapter mMusicTypeAdapter;

    private View mMusicDetailView;
    private ImageView mMusicDetailImg;
    private LrcView mLrcView;
    private ImageView mMusicPlayPauseImg;

    private boolean isLogining = false;

    private String mKeyWord = "";

    private List<MusicTypeView> mMusicTypeViews = new ArrayList<>();

    private String mCurrentLrcUrl = "";
    private MusicDetailInfo mCurrentMusicDetailInfo = null;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(TextUtils.isEmpty(s)){
                mMusicTypeView.setVisibility(View.VISIBLE);
                mMusicSearchResultView.setVisibility(View.GONE);
                mMusicDetailView.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_GET_LRC_SUCCESS:
                    LrcListInfo lrcListInfo = (LrcListInfo) msg.obj;
                    mLrcView.setLrcListInfo(mCurrentMusicDetailInfo, lrcListInfo);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DebugUtil.d(TAG, "onCreateView");

        EventBusUtils.register(this);

        mContentView = inflater.inflate(R.layout.fragment_music, null);

        mLoadingView = mContentView.findViewById(R.id.fragment_music_loading_view);
        mLoadErrorView = mContentView.findViewById(R.id.fragment_music_load_error_view);
        mBtnErrorRefresh = mLoadErrorView.findViewById(R.id.load_error_btn);

        mSearchEt = mContentView.findViewById(R.id.search_et);

        mMusicSearchResultView = mContentView.findViewById(R.id.music_search_result_view);

        mMusicSearchResultListView = mContentView.findViewById(R.id.music_search_result_list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        mMusicSearchResultListView.setLayoutManager(linearLayoutManager);
        mMusicSearchListAdapter = new MusicSearchListAdapter();
        mMusicSearchResultListView.setAdapter(mMusicSearchListAdapter);

        mMusicTypeView = mContentView.findViewById(R.id.music_type_view);
        mMusicTypeTableLayout = mContentView.findViewById(R.id.music_type_table_layout);
        mMusicTypeViewPager = mContentView.findViewById(R.id.music_type_view_pager);

        mMusicDetailView = mContentView.findViewById(R.id.music_detail_view);
        mMusicDetailImg = mContentView.findViewById(R.id.music_detail_img);
        mLrcView = mContentView.findViewById(R.id.lrc_view);
        mMusicPlayPauseImg = mContentView.findViewById(R.id.music_play_pause_img);
        mLrcView.setMusicPlayPauseImg(mMusicPlayPauseImg);

        for(String typeName : Constants.MUSIC_TYPE_NAMES){
            mMusicTypeTableLayout.addTab(mMusicTypeTableLayout.newTab().setText(typeName));
        }

        mMusicTypeAdapter = new MusicTypeAdapter();
        mMusicTypeViewPager.setAdapter(mMusicTypeAdapter);
        mMusicTypeTableLayout.setupWithViewPager(mMusicTypeViewPager);


        mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMusicList(mKeyWord);
            }
        });

        mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
                if (actionId== EditorInfo.IME_ACTION_SEARCH ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)){
                    mKeyWord = mSearchEt.getText().toString();
                    searchMusicList(mKeyWord);
                    CommonUtils.hideIputKeyboard(mActivity);
                    return true;
                }
                return false;
            }
        });

        mMusicPlayPauseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLrcView.doPausePlayClick();
            }
        });


        mSearchEt.addTextChangedListener(mTextWatcher);

        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMusicTypeViews.clear();
        for(int i = 0; i < Constants.MUSIC_TYPE_NAMES.length; i++){
//            MusicTypeView musicTypeView = (MusicTypeView) LayoutInflater.from(mActivity).inflate(R.layout.music_type_view,null);
            MusicTypeView musicTypeView = new MusicTypeView(mActivity);
            mMusicTypeViews.add(musicTypeView);
        }
        mMusicTypeAdapter.notifyDataSetChanged();

        for(int i = 0; i < Constants.MUSIC_TYPE_NAMES.length; i++){
            mMusicTypeTableLayout.getTabAt(i).setText(Constants.MUSIC_TYPE_NAMES[i]);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMusicDetailInfoEvent(GetMusicDetailInfoEvent event){
        getMusicDetailInfo(event.mSongId);
    }

    private void searchMusicList(String keyWord) {

        if(TextUtils.isEmpty(keyWord)){
            Toast.makeText(mActivity,"请输入歌曲名称",Toast.LENGTH_SHORT).show();
            return;
        }

        if(isLogining){
            return;
        }
        isLogining = true;

        mMusicSearchResultView.setVisibility(View.GONE);
        mMusicTypeView.setVisibility(View.GONE);
        mMusicDetailView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
        mLoadErrorView.setVisibility(View.GONE);

        HttpUtils.getInstance().searchMusicList(mActivity, Constants.MUSIC_SEARCH_METHOD + Constants.MUSIC_SEARCH_KEY_PARAM + keyWord, new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {

                MusicInfoResponseBean<MusicSearchResult> musicInfoResponseBean = (MusicInfoResponseBean<MusicSearchResult>) responseBaseBean;
                mMusicSearchListAdapter.setSearchMusicListInfo(musicInfoResponseBean.result);

                mMusicSearchResultView.setVisibility(View.VISIBLE);
                mMusicTypeView.setVisibility(View.GONE);
                mMusicDetailView.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.GONE);

                isLogining = false;
            }

            @Override
            public void onNetError() {
                Toast.makeText(mActivity,R.string.no_network_tip,Toast.LENGTH_SHORT).show();

                mMusicSearchResultView.setVisibility(View.GONE);
                mMusicTypeView.setVisibility(View.GONE);
                mMusicDetailView.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.VISIBLE);
                isLogining = false;
            }

            @Override
            public void onNetError(Throwable e) {
                Toast.makeText(mActivity,getResources().getString(R.string.network_error_tip,e.toString()),Toast.LENGTH_SHORT).show();

                mMusicSearchResultView.setVisibility(View.GONE);
                mMusicTypeView.setVisibility(View.GONE);
                mMusicDetailView.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.VISIBLE);
                isLogining = false;
            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {
                Toast.makeText(mActivity,getResources().getString(R.string.network_error_tip,responseBaseBean.desc),Toast.LENGTH_SHORT).show();

                mMusicSearchResultView.setVisibility(View.GONE);
                mMusicTypeView.setVisibility(View.GONE);
                mMusicDetailView.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.VISIBLE);
                isLogining = false;
            }
        });
    }
    private void getMusicDetailInfo(String songId) {

        if(isLogining){
            return;
        }
        isLogining = true;

        mMusicSearchResultView.setVisibility(View.GONE);
        mMusicTypeView.setVisibility(View.GONE);
        mMusicDetailView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
        mLoadErrorView.setVisibility(View.GONE);

        HttpUtils.getInstance().getMusicDetailInfo(mActivity, Constants.MUSIC_GET_INFO_METHOD + Constants.MUSIC_GET_INFO_KEY_PARAM + songId, new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {

                MusicInfoResponseBean<MusicDetailInfo> musicInfoResponseBean = (MusicInfoResponseBean<MusicDetailInfo>) responseBaseBean;

                mMusicSearchResultView.setVisibility(View.GONE);
                mMusicTypeView.setVisibility(View.GONE);
                mMusicDetailView.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.GONE);

                mCurrentMusicDetailInfo = musicInfoResponseBean.result;
                Glide.with(mActivity).load(mCurrentMusicDetailInfo.songinfo.pic_huge).into(mMusicDetailImg);

                if(TextUtils.isEmpty(mCurrentMusicDetailInfo.songinfo.lrclink)){
                    mLrcView.setLrcListInfo(mCurrentMusicDetailInfo, new LrcListInfo());
                }else{
                    getLrc(mCurrentMusicDetailInfo.songinfo.lrclink);
                }

                isLogining = false;
            }

            @Override
            public void onNetError() {
                Toast.makeText(mActivity,R.string.no_network_tip,Toast.LENGTH_SHORT).show();

                mMusicSearchResultView.setVisibility(View.GONE);
                mMusicTypeView.setVisibility(View.GONE);
                mMusicDetailView.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.VISIBLE);
                isLogining = false;
            }

            @Override
            public void onNetError(Throwable e) {
                Toast.makeText(mActivity,getResources().getString(R.string.network_error_tip,e.toString()),Toast.LENGTH_SHORT).show();

                mMusicSearchResultView.setVisibility(View.GONE);
                mMusicTypeView.setVisibility(View.GONE);
                mMusicDetailView.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.VISIBLE);
                isLogining = false;
            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {
                Toast.makeText(mActivity,getResources().getString(R.string.network_error_tip,responseBaseBean.desc),Toast.LENGTH_SHORT).show();

                mMusicSearchResultView.setVisibility(View.GONE);
                mMusicTypeView.setVisibility(View.GONE);
                mMusicDetailView.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.VISIBLE);
                isLogining = false;
            }
        });
    }

    private void getLrc(String url){
        mCurrentLrcUrl = url;
        HttpUtils.getInstance().downloadFileWithUrlAsync(mActivity, url, new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {
                DebugUtil.d(TAG,"getLrc::onSuccess::mCurrentLrcUrl = " + mCurrentLrcUrl);
                DebugUtil.d(TAG,"getLrc::onSuccess::responseBaseBean = " + responseBaseBean);
                MusicLrcDownloadResponseBean musicLrcDownloadResponseBean = (MusicLrcDownloadResponseBean) responseBaseBean;
                if(TextUtils.equals(mCurrentLrcUrl,musicLrcDownloadResponseBean.mLrcUrl)){
                    File lrcFile = new File(musicLrcDownloadResponseBean.mPath);
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(lrcFile));
                        String s = null;

                        String regex = "\\[(.*?)\\](.*?)";
                        Pattern pattern = Pattern.compile(regex);

                        String timeRegex = "(.*?):(.*?)\\.(.*?)";
                        Pattern timePattern = Pattern.compile(timeRegex);

                        LrcListInfo lrcListInfo = new LrcListInfo();
                        while((s = br.readLine()) != null){
                            DebugUtil.d(TAG,"getLrc::onSuccess::s = " + s);
                            Matcher matcher = pattern.matcher(s);
                            if(matcher.matches()){
                                LrcInfo lrcInfo = new LrcInfo();

                                String timeStr = matcher.group(1);
                                Matcher timeMatcher = timePattern.matcher(timeStr);
                                if(timeMatcher.matches()){
                                    lrcInfo.mCurrentTime = Integer.valueOf(timeMatcher.group(1)) * 60 + Integer.valueOf(timeMatcher.group(2));
                                    lrcInfo.mCurrentTime = lrcInfo.mCurrentTime * 1000 + Integer.valueOf(timeMatcher.group(3));
                                }

                                lrcInfo.mContent = matcher.group(2);

                                lrcListInfo.mLrcs.add(lrcInfo);
                            }
                        }
                        DebugUtil.d(TAG,"getLrc::onSuccess::lrcListInfo = " + lrcListInfo);

                        Message message = mHandler.obtainMessage();
                        message.what = MSG_GET_LRC_SUCCESS;
                        message.obj = lrcListInfo;
                        message.sendToTarget();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNetError() {

            }

            @Override
            public void onNetError(Throwable e) {

            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {

            }
        });
    }


    class MusicSearchListAdapter extends RecyclerView.Adapter<MusicInfoListViewHolder>{

        private MusicSearchResult mMusicSearchResult;

        public void setSearchMusicListInfo(MusicSearchResult musicSearchResult){
            mMusicSearchResult = musicSearchResult;
            notifyDataSetChanged();
        }


        @NonNull
        @Override
        public MusicInfoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(mActivity).inflate(R.layout.item_music_search_list_info_view,parent,false);
            return new MusicInfoListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MusicInfoListViewHolder musicInfoListViewHolder, int poistion) {
            final MusicSearchResult.Song song = mMusicSearchResult.song.get(poistion);

            musicInfoListViewHolder.mItemMusicListInfoSongNameTv.setText(song.songname);
            musicInfoListViewHolder.mItemMusicListInfoArtistNameTv.setText(song.artistname);

            musicInfoListViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getMusicDetailInfo(song.songid);
                }
            });
        }

        @Override
        public int getItemCount() {
            if(mMusicSearchResult != null){
                return mMusicSearchResult.song.size();
            }
            return 0;
        }
    }

    class MusicInfoListViewHolder extends RecyclerView.ViewHolder{

        public View mItemView;
        public TextView mItemMusicListInfoSongNameTv;
        public TextView mItemMusicListInfoArtistNameTv;

        public MusicInfoListViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;

            mItemMusicListInfoSongNameTv = mItemView.findViewById(R.id.item_music_list_info_song_name_tv);
            mItemMusicListInfoArtistNameTv = mItemView.findViewById(R.id.item_music_list_info_artist_name_tv);
        }
    }

    class MusicTypeAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mMusicTypeViews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            MusicTypeView musicTypeView = mMusicTypeViews.get(position);
            musicTypeView.setType(Constants.MUSIC_TYPES[position]);
            container.addView(musicTypeView);
            return musicTypeView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
