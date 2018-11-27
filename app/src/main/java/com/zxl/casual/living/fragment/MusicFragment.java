package com.zxl.casual.living.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.zxl.casual.living.R;
import com.zxl.casual.living.http.HttpUtils;
import com.zxl.casual.living.http.data.DailySentenceResponseBean;
import com.zxl.casual.living.http.data.MusicInfo;
import com.zxl.casual.living.http.data.MusicInfoResponseBean;
import com.zxl.casual.living.http.data.ResponseBaseBean;
import com.zxl.casual.living.http.data.SearchMusicListInfo;
import com.zxl.casual.living.http.listener.NetRequestListener;
import com.zxl.casual.living.utils.CommonUtils;
import com.zxl.casual.living.utils.Constants;
import com.zxl.casual.living.utils.SharePreUtils;
import com.zxl.common.DebugUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zxl on 2018/11/21.
 */

public class MusicFragment extends BaseFragment {

    private static final String TAG = "MusicFragment";

    private static final int OPERATOR_SEARCH_MUSIC_LIST = 1;
    private static final int OPERATOR_SEARCH_MUSIC_ITEM = 2;

    private View mContentView;

    private View mLoadingView;
    private View mLoadErrorView;
    private Button mBtnErrorRefresh;

    private EditText mSearchEt;

    private View mMusicContentView;
    private RecyclerView mMusicListView;
    private MusicInfoListAdapter mMusicInfoListAdapter;

    private boolean isLogining = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DebugUtil.d(TAG, "onCreateView");
        mContentView = inflater.inflate(R.layout.fragment_music, null);

        mLoadingView = mContentView.findViewById(R.id.loading_view);
        mLoadErrorView = mContentView.findViewById(R.id.load_error_view);
        mBtnErrorRefresh = mLoadErrorView.findViewById(R.id.load_error_btn);

        mSearchEt = mContentView.findViewById(R.id.search_et);

        mMusicContentView = mContentView.findViewById(R.id.music_content_view);

        mMusicListView = mContentView.findViewById(R.id.music_list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        mMusicListView.setLayoutManager(linearLayoutManager);
        mMusicInfoListAdapter = new MusicInfoListAdapter();
        mMusicListView.setAdapter(mMusicInfoListAdapter);

        mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
                if (actionId== EditorInfo.IME_ACTION_SEARCH ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)){
                    searchMusicList(mSearchEt.getText().toString());
                    return true;
                }
                return false;
            }
        });

        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

        mMusicContentView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.VISIBLE);
        mLoadErrorView.setVisibility(View.GONE);

        HttpUtils.getInstance().searchMusicList(mActivity, Constants.MUSIC_SEARCH_METHOD, Constants.MUSIC_SEARCH_KEY_PARAM, keyWord, new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {

                MusicInfoResponseBean<SearchMusicListInfo> musicInfoResponseBean = (MusicInfoResponseBean<SearchMusicListInfo>) responseBaseBean;
                mMusicInfoListAdapter.setSearchMusicListInfo(musicInfoResponseBean.result);

                mMusicContentView.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.GONE);

                isLogining = false;
            }

            @Override
            public void onNetError() {
                Toast.makeText(mActivity,R.string.no_network_tip,Toast.LENGTH_SHORT).show();

                mMusicContentView.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.VISIBLE);
                isLogining = false;
            }

            @Override
            public void onNetError(Throwable e) {
                Toast.makeText(mActivity,getResources().getString(R.string.network_error_tip,e.toString()),Toast.LENGTH_SHORT).show();

                mMusicContentView.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.VISIBLE);
                isLogining = false;
            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {
                Toast.makeText(mActivity,getResources().getString(R.string.network_error_tip,responseBaseBean.desc),Toast.LENGTH_SHORT).show();

                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.VISIBLE);
                isLogining = false;
            }
        });
    }

    private void getMusicInfo(String songId) {

        if(isLogining){
            return;
        }
        isLogining = true;

        mMusicContentView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.VISIBLE);
        mLoadErrorView.setVisibility(View.GONE);

        HttpUtils.getInstance().getMusicInfo(mActivity, Constants.MUSIC_GET_INFO_METHOD, Constants.MUSIC_GET_INFO_KEY_PARAM, songId, new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {

                MusicInfoResponseBean<MusicInfo> musicInfoResponseBean = (MusicInfoResponseBean<MusicInfo>) responseBaseBean;
//                mMusicInfoListAdapter.setSearchMusicListInfo(musicInfoResponseBean.result);

                mMusicContentView.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.GONE);

                isLogining = false;
            }

            @Override
            public void onNetError() {
                Toast.makeText(mActivity,R.string.no_network_tip,Toast.LENGTH_SHORT).show();

                mMusicContentView.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.VISIBLE);
                isLogining = false;
            }

            @Override
            public void onNetError(Throwable e) {
                Toast.makeText(mActivity,getResources().getString(R.string.network_error_tip,e.toString()),Toast.LENGTH_SHORT).show();

                mMusicContentView.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.VISIBLE);
                isLogining = false;
            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {
                Toast.makeText(mActivity,getResources().getString(R.string.network_error_tip,responseBaseBean.desc),Toast.LENGTH_SHORT).show();

                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.VISIBLE);
                isLogining = false;
            }
        });
    }

    class MusicInfoListAdapter extends RecyclerView.Adapter<MusicInfoListViewHolder>{

        private SearchMusicListInfo mSearchMusicListInfo;

        public void setSearchMusicListInfo(SearchMusicListInfo searchMusicListInfo){
            mSearchMusicListInfo = searchMusicListInfo;
            notifyDataSetChanged();
        }


        @NonNull
        @Override
        public MusicInfoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(mActivity).inflate(R.layout.item_music_list_info_view,parent,false);
            return new MusicInfoListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MusicInfoListViewHolder musicInfoListViewHolder, int poistion) {
            final SearchMusicListInfo.Song song = mSearchMusicListInfo.song.get(poistion);

            musicInfoListViewHolder.mItemMusicListInfoSongNameTv.setText(song.songname);
            musicInfoListViewHolder.mItemMusicListInfoArtistNameTv.setText(song.artistname);

            musicInfoListViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getMusicInfo(song.songid);
                }
            });
        }

        @Override
        public int getItemCount() {
            if(mSearchMusicListInfo != null){
                return mSearchMusicListInfo.song.size();
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
}
