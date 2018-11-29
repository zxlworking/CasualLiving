package com.zxl.casual.living.fragment;

import android.graphics.Color;
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
import com.zxl.casual.living.custom.view.MusicSearchView;
import com.zxl.casual.living.custom.view.MusicTypeView;
import com.zxl.casual.living.event.GetMusicDetailInfoEvent;
import com.zxl.casual.living.http.HttpUtils;
import com.zxl.casual.living.http.data.LrcInfo;
import com.zxl.casual.living.http.data.LrcListInfo;
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zxl on 2018/11/21.
 */

public class MusicFragment extends BaseFragment {

    private static final String TAG = "MusicFragment";

    private View mContentView;

    private TabLayout mMusicTypeTableLayout;
    private ViewPager mMusicTypeViewPager;
    private MusicTypeAdapter mMusicTypeAdapter;

    private List<View> mMusicViews = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DebugUtil.d(TAG, "onCreateView");

        EventBusUtils.register(this);

        mContentView = inflater.inflate(R.layout.fragment_music, null);

        mMusicTypeTableLayout = mContentView.findViewById(R.id.music_type_table_layout);
        mMusicTypeViewPager = mContentView.findViewById(R.id.music_type_view_pager);

        for(String typeName : Constants.MUSIC_TYPE_NAMES){
            mMusicTypeTableLayout.addTab(mMusicTypeTableLayout.newTab().setText(typeName));
        }

        mMusicTypeAdapter = new MusicTypeAdapter();
        mMusicTypeViewPager.setAdapter(mMusicTypeAdapter);
        mMusicTypeTableLayout.setupWithViewPager(mMusicTypeViewPager);

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
//        getMusicDetailInfo(event.mSongId);
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

        @NonNull
        @Override
        public LrcViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_lrc_view,viewGroup,false);
            return new LrcViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LrcViewHolder lrcViewHolder, int position) {
//            lrcViewHolder.mItemLrcTv.setText(mLrcView.getLrcListInfo().mLrcs.get(position).mContent);
        }

        @Override
        public int getItemCount() {
//            return (mLrcView.getLrcListInfo() == null ? 0 : mLrcView.getLrcListInfo().mLrcs.size());
            return 0;
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
