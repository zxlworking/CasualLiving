package com.zxl.casual.living.fragment;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.zxl.casual.living.custom.view.PaletteView;
import com.zxl.casual.living.http.HttpUtils;
import com.zxl.casual.living.http.data.ResponseBaseBean;
import com.zxl.casual.living.http.data.TaoBaoAnchor;
import com.zxl.casual.living.http.data.TaoBaoAnchorListResponseBean;
import com.zxl.casual.living.http.listener.NetRequestListener;
import com.zxl.casual.living.utils.CommonUtils;
import com.zxl.common.DebugUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxl on 2018/9/20.
 */

public class TaoBaoAnchorFragment extends BaseFragment {
    private static final String TAG = "TaoBaoAnchorFragment";

    private static final int MSG_FIRST_LOAD_START = 1;
    private static final int MSG_FIRST_LOAD_SUCCESS = 2;
    private static final int MSG_FIRST_LOAD_ERROR = 3;
    private static final int MSG_LOAD_START = 4;
    private static final int MSG_LOAD_SUCCESS = 5;
    private static final int MSG_LOAD_ERROR = 6;

    private View mContentView;

    private View mLoadingView;
    private View mLoadErrorView;
    private Button mBtnErrorRefresh;

    private RecyclerView mRecyclerView;
    private TaoBaoAnchorAdapter mTaoBaoAnchorAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int mCurrentPage = 1;
    private int mTotalPage = Integer.MAX_VALUE;
    private int mPageCount = 10;

    private boolean isLoading = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_FIRST_LOAD_START:
                    mRecyclerView.setVisibility(View.GONE);
                    mLoadingView.setVisibility(View.VISIBLE);
                    mLoadErrorView.setVisibility(View.GONE);
                    break;
                case MSG_FIRST_LOAD_SUCCESS:
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLoadingView.setVisibility(View.GONE);
                    mLoadErrorView.setVisibility(View.GONE);

                    List<TaoBaoAnchor> mFirstTemp = (List<TaoBaoAnchor>) msg.obj;
                    mTaoBaoAnchorAdapter.setData(mFirstTemp,mCurrentPage,mTotalPage);

                    isLoading = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_FIRST_LOAD_ERROR:
                    mRecyclerView.setVisibility(View.GONE);
                    mLoadingView.setVisibility(View.GONE);
                    mLoadErrorView.setVisibility(View.VISIBLE);

                    isLoading = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_LOAD_START:
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLoadingView.setVisibility(View.GONE);
                    mLoadErrorView.setVisibility(View.GONE);
                    break;
                case MSG_LOAD_SUCCESS:
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLoadingView.setVisibility(View.GONE);
                    mLoadErrorView.setVisibility(View.GONE);

                    List<TaoBaoAnchor> mTemp = (List<TaoBaoAnchor>) msg.obj;
                    mTaoBaoAnchorAdapter.addData(mTemp,mCurrentPage,mTotalPage);

                    isLoading = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_LOAD_ERROR:
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLoadingView.setVisibility(View.GONE);
                    mLoadErrorView.setVisibility(View.GONE);

                    mTaoBaoAnchorAdapter.setLoadDataState(TaoBaoAnchorAdapter.LOAD_DATA_ERROR_STATE);

                    isLoading = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_taobao_anchor,null);

        mLoadingView = mContentView.findViewById(R.id.taobao_anchor_loading_view);
        mLoadErrorView = mContentView.findViewById(R.id.taobao_anchor_error_view);
        mBtnErrorRefresh = mLoadErrorView.findViewById(R.id.load_error_btn);

        mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(true,1);
            }
        });

        mRecyclerView = mContentView.findViewById(R.id.recycler_view);


        mSwipeRefreshLayout = mContentView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#3F51B5"),Color.parseColor("#303F9F"),Color.parseColor("#FF4081"));
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                loadData(true,1);
            }
        });

        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity,2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(mTaoBaoAnchorAdapter.getItemViewType(position) == TaoBaoAnchorAdapter.FOOT_TYPE){
                    return 2;
                }
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mTaoBaoAnchorAdapter = new TaoBaoAnchorAdapter();
        mRecyclerView.setAdapter(mTaoBaoAnchorAdapter);

        loadData(true, 1);
    }

    public void loadData(final boolean isFirstLoad, final int page){
        if(isLoading){
            return;
        }
        isLoading = true;

        if(isFirstLoad){
            mHandler.sendEmptyMessage(MSG_FIRST_LOAD_START);
        }else{
            mHandler.sendEmptyMessage(MSG_LOAD_START);
        }

        HttpUtils.getInstance().getTaoBaoAnchor(mActivity, page, new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {
                TaoBaoAnchorListResponseBean taoBaoAnchorListResponseBean = (TaoBaoAnchorListResponseBean) responseBaseBean;
                mCurrentPage = taoBaoAnchorListResponseBean.current_page;
                if(isFirstLoad){
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_FIRST_LOAD_SUCCESS;
                    message.obj = taoBaoAnchorListResponseBean.taobao_anchor_list;
                    message.sendToTarget();
                }else{
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_LOAD_SUCCESS;
                    message.obj = taoBaoAnchorListResponseBean.taobao_anchor_list;
                    message.sendToTarget();
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

    public class TaoBaoAnchorAdapter extends LoadMoreAdapter<TaoBaoAnchor>{

        @Override
        public RecyclerView.ViewHolder getContentViewHolder(@NonNull ViewGroup parent) {
            View mItemView = LayoutInflater.from(mActivity).inflate(R.layout.item_taobao_anchor_view, parent, false);
            return new TaoBaoAnchorViewHolder(mItemView);
        }

        @Override
        public RecyclerView.ViewHolder getFootViewHolder(@NonNull ViewGroup parent) {
            View mItemFootView = LayoutInflater.from(mActivity).inflate(R.layout.item_taobao_anchor_foot_view, parent, false);
            return new TaoBaoAnchorFootViewHolder(mItemFootView);
        }

        @Override
        public void onBindContentViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            TaoBaoAnchorViewHolder mTaoBaoAnchorViewHolder = (TaoBaoAnchorViewHolder) viewHolder;

            ViewGroup.LayoutParams lp = mTaoBaoAnchorViewHolder.mItemTaobaoAnchorImg.getLayoutParams();
            lp.width = (int) ((CommonUtils.screenWidth() - CommonUtils.dip2px(12) * 3) / 2 - CommonUtils.dip2px(8)) + 2;
            lp.height = lp.width;
            mTaoBaoAnchorViewHolder.mItemTaobaoAnchorImg.setLayoutParams(lp);


            final TaoBaoAnchorViewHolder finalMTaoBaoAnchorViewHolder = mTaoBaoAnchorViewHolder;
            mTaoBaoAnchorViewHolder.mPaletteView.parse(getData().get(position).anchor_img, mTaoBaoAnchorViewHolder.mItemTaobaoAnchorImg, new PaletteView.OnPaletteCompleteListener() {
                @Override
                public void onComplete(Palette palette) {
                    Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                    Palette.Swatch lightVibrantSwatch = palette.getLightVibrantSwatch();
                    Palette.Swatch darkVibrantSwatch = palette.getDarkVibrantSwatch();
                    Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                    Palette.Swatch lightMutedSwatch = palette.getLightMutedSwatch();
                    Palette.Swatch darkMutedSwatch = palette.getDarkMutedSwatch();
                    DebugUtil.d(TAG,"vibrantSwatch = " + palette.getVibrantSwatch());
                    DebugUtil.d(TAG,"lightVibrantSwatch = " + lightVibrantSwatch);
                    DebugUtil.d(TAG,"darkVibrantSwatch = " + darkVibrantSwatch);
                    DebugUtil.d(TAG,"mutedSwatch = " + mutedSwatch);
                    DebugUtil.d(TAG,"lightMutedSwatch = " + lightMutedSwatch);
                    DebugUtil.d(TAG,"darkMutedSwatch = " + darkMutedSwatch);

                    int bgColor = 0;
                    int textColor = 0;
                    if(vibrantSwatch != null){
                        bgColor = vibrantSwatch.getRgb();
                        textColor = vibrantSwatch.getTitleTextColor();
                    }else if(lightVibrantSwatch != null){
                        bgColor = lightVibrantSwatch.getRgb();
                        textColor = lightVibrantSwatch.getTitleTextColor();
                    }else if(darkVibrantSwatch != null){
                        bgColor = darkVibrantSwatch.getRgb();
                        textColor = darkVibrantSwatch.getTitleTextColor();
                    }else if(mutedSwatch != null){
                        bgColor = mutedSwatch.getRgb();
                        textColor = mutedSwatch.getTitleTextColor();
                    }else if(lightMutedSwatch != null){
                        bgColor = lightMutedSwatch.getRgb();
                        textColor = lightMutedSwatch.getTitleTextColor();
                    }else if(darkMutedSwatch != null){
                        bgColor = darkMutedSwatch.getRgb();
                        textColor = darkMutedSwatch.getTitleTextColor();
                    }

                    finalMTaoBaoAnchorViewHolder.mPaletteView.setPaletteBackgroundColor(bgColor);
                    finalMTaoBaoAnchorViewHolder.mItemTaobaoAnchorName.setTextColor(textColor);
                    finalMTaoBaoAnchorViewHolder.mItemTaobaoAnchorFansName.setTextColor(textColor);
                    finalMTaoBaoAnchorViewHolder.mItemTaobaoAnchorFansCount.setTextColor(textColor);
                }
            });

//                Glide.with(mActivity).load(mTaoBaoAnchors.get(position).anchor_img).into(mTaoBaoAnchorViewHolder.mItemTaobaoAnchorImg);
            Glide.with(mActivity).load(getData().get(position).anchor_vflag).into(mTaoBaoAnchorViewHolder.mItemTaobaoAnchorVflag);
            mTaoBaoAnchorViewHolder.mItemTaobaoAnchorName.setText(getData().get(position).anchor_name);
            mTaoBaoAnchorViewHolder.mItemTaobaoAnchorFansCount.setText(getData().get(position).fans_count);
        }

        @Override
        public void onBindFootViewHolderLoadDataSuccess(@NonNull RecyclerView.ViewHolder viewHolder) {
            DebugUtil.d(TAG,"onBindFootViewHolderLoadDataSuccess");

            TaoBaoAnchorFootViewHolder mTaoBaoAnchorFootViewHolder = (TaoBaoAnchorFootViewHolder) viewHolder;

            mTaoBaoAnchorFootViewHolder.mLoadErrorView.setVisibility(View.GONE);
            mTaoBaoAnchorFootViewHolder.mLoadingView.setVisibility(View.VISIBLE);

            loadData(false,mCurrentPage + 1);
        }

        @Override
        public void onBindFootViewHolderLoadingData(@NonNull RecyclerView.ViewHolder viewHolder) {
            DebugUtil.d(TAG,"onBindFootViewHolderLoadingData");

            TaoBaoAnchorFootViewHolder mTaoBaoAnchorFootViewHolder = (TaoBaoAnchorFootViewHolder) viewHolder;

            mTaoBaoAnchorFootViewHolder.mLoadErrorView.setVisibility(View.GONE);
            mTaoBaoAnchorFootViewHolder.mLoadingView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onBindFootViewHolderLoadDataError(@NonNull RecyclerView.ViewHolder viewHolder) {
            DebugUtil.d(TAG,"onBindFootViewHolderLoadDataError");

            TaoBaoAnchorFootViewHolder mTaoBaoAnchorFootViewHolder = (TaoBaoAnchorFootViewHolder) viewHolder;

            mTaoBaoAnchorFootViewHolder.mLoadErrorView.setVisibility(View.VISIBLE);
            mTaoBaoAnchorFootViewHolder.mLoadingView.setVisibility(View.GONE);

            View mBtnErrorRefresh = mTaoBaoAnchorFootViewHolder.mLoadErrorView.findViewById(R.id.load_error_btn);

            mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTaoBaoAnchorAdapter.setLoadDataState(TaoBaoAnchorAdapter.LOADING_DATA_STATE);
                    loadData(false, mCurrentPage + 1);
                }
            });
        }
    }


    /*
    public class TaoBaoAnchorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        public static final int LOADING_DATA_STATE = 1;
        public static final int LOAD_DATA_SUCCESS_STATE =2 ;
        public static final int LOAD_DATA_ERROR_STATE = 3;

        public static final int CONTENT_TYPE = 1;
        public static final int FOOT_TYPE = 2;

        private int mCurrentState = LOAD_DATA_SUCCESS_STATE;
        private List<TaoBaoAnchor> mTaoBaoAnchors = new ArrayList<>();

        public void setData(List<TaoBaoAnchor> elements){
            mTaoBaoAnchors.clear();
            mTaoBaoAnchors.addAll(elements);
            mCurrentState = LOAD_DATA_SUCCESS_STATE;
            notifyDataSetChanged();
        }

        public void addData(List<TaoBaoAnchor> elements){
            mTaoBaoAnchors.addAll(elements);
            mCurrentState = LOAD_DATA_SUCCESS_STATE;
            notifyItemRangeInserted(getItemCount() - 1, elements.size());
        }

        public void setLoadDataState(int state){
            mCurrentState = state;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            if(position == getItemCount() - 1 && mCurrentPage < mTotalPage - 1){
                return FOOT_TYPE;
            }
            return CONTENT_TYPE;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType){
                case CONTENT_TYPE:
                    View mItemView = LayoutInflater.from(mActivity).inflate(R.layout.item_taobao_anchor_view, parent, false);
                    return new TaoBaoAnchorViewHolder(mItemView);
                case FOOT_TYPE:
                    View mItemFootView = LayoutInflater.from(mActivity).inflate(R.layout.item_taobao_anchor_foot_view, parent, false);
                    return new TaoBaoAnchorFootViewHolder(mItemFootView);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TaoBaoAnchorViewHolder mTaoBaoAnchorViewHolder = null;
            if(holder instanceof TaoBaoAnchorViewHolder){
                mTaoBaoAnchorViewHolder = (TaoBaoAnchorViewHolder) holder;

                ViewGroup.LayoutParams lp = mTaoBaoAnchorViewHolder.mItemTaobaoAnchorImg.getLayoutParams();
                lp.width = (int) ((CommonUtils.screenWidth() - CommonUtils.dip2px(12) * 3) / 2 - CommonUtils.dip2px(8)) + 2;
                lp.height = lp.width;
                mTaoBaoAnchorViewHolder.mItemTaobaoAnchorImg.setLayoutParams(lp);

                final TaoBaoAnchorViewHolder finalMTaoBaoAnchorViewHolder = mTaoBaoAnchorViewHolder;
                mTaoBaoAnchorViewHolder.mPaletteView.parse(mTaoBaoAnchors.get(position).anchor_img, mTaoBaoAnchorViewHolder.mItemTaobaoAnchorImg, new PaletteView.OnPaletteCompleteListener() {
                    @Override
                    public void onComplete(Palette palette) {
                        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                        Palette.Swatch lightVibrantSwatch = palette.getLightVibrantSwatch();
                        Palette.Swatch darkVibrantSwatch = palette.getDarkVibrantSwatch();
                        Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                        Palette.Swatch lightMutedSwatch = palette.getLightMutedSwatch();
                        Palette.Swatch darkMutedSwatch = palette.getDarkMutedSwatch();
                        DebugUtil.d(TAG,"vibrantSwatch = " + palette.getVibrantSwatch());
                        DebugUtil.d(TAG,"lightVibrantSwatch = " + lightVibrantSwatch);
                        DebugUtil.d(TAG,"darkVibrantSwatch = " + darkVibrantSwatch);
                        DebugUtil.d(TAG,"mutedSwatch = " + mutedSwatch);
                        DebugUtil.d(TAG,"lightMutedSwatch = " + lightMutedSwatch);
                        DebugUtil.d(TAG,"darkMutedSwatch = " + darkMutedSwatch);

                        int bgColor = 0;
                        int textColor = 0;
                        if(vibrantSwatch != null){
                            bgColor = vibrantSwatch.getRgb();
                            textColor = vibrantSwatch.getTitleTextColor();
                        }else if(lightVibrantSwatch != null){
                            bgColor = lightVibrantSwatch.getRgb();
                            textColor = lightVibrantSwatch.getTitleTextColor();
                        }else if(darkVibrantSwatch != null){
                            bgColor = darkVibrantSwatch.getRgb();
                            textColor = darkVibrantSwatch.getTitleTextColor();
                        }else if(mutedSwatch != null){
                            bgColor = mutedSwatch.getRgb();
                            textColor = mutedSwatch.getTitleTextColor();
                        }else if(lightMutedSwatch != null){
                            bgColor = lightMutedSwatch.getRgb();
                            textColor = lightMutedSwatch.getTitleTextColor();
                        }else if(darkMutedSwatch != null){
                            bgColor = darkMutedSwatch.getRgb();
                            textColor = darkMutedSwatch.getTitleTextColor();
                        }

                        finalMTaoBaoAnchorViewHolder.mPaletteView.setPaletteBackgroundColor(bgColor);
                        finalMTaoBaoAnchorViewHolder.mItemTaobaoAnchorName.setTextColor(textColor);
                        finalMTaoBaoAnchorViewHolder.mItemTaobaoAnchorFansName.setTextColor(textColor);
                        finalMTaoBaoAnchorViewHolder.mItemTaobaoAnchorFansCount.setTextColor(textColor);
                    }
                });

//                Glide.with(mActivity).load(mTaoBaoAnchors.get(position).anchor_img).into(mTaoBaoAnchorViewHolder.mItemTaobaoAnchorImg);
                Glide.with(mActivity).load(mTaoBaoAnchors.get(position).anchor_vflag).into(mTaoBaoAnchorViewHolder.mItemTaobaoAnchorVflag);
                mTaoBaoAnchorViewHolder.mItemTaobaoAnchorName.setText(mTaoBaoAnchors.get(position).anchor_name);
                mTaoBaoAnchorViewHolder.mItemTaobaoAnchorFansCount.setText(mTaoBaoAnchors.get(position).fans_count);
            }
            TaoBaoAnchorFootViewHolder mTaoBaoAnchorFootViewHolder = null;
            if(holder instanceof TaoBaoAnchorFootViewHolder){
                mTaoBaoAnchorFootViewHolder = (TaoBaoAnchorFootViewHolder) holder;
            }
            if(position == getItemCount() - 1 && mCurrentPage < mTotalPage - 1 && mTaoBaoAnchorFootViewHolder != null){
                switch (mCurrentState){
                    case LOAD_DATA_SUCCESS_STATE:
                        mTaoBaoAnchorFootViewHolder.mLoadErrorView.setVisibility(View.GONE);
                        mTaoBaoAnchorFootViewHolder.mLoadingView.setVisibility(View.VISIBLE);

                        loadData(false,mCurrentPage + 1);
                        break;
                    case LOADING_DATA_STATE:
                        mTaoBaoAnchorFootViewHolder.mLoadErrorView.setVisibility(View.GONE);
                        mTaoBaoAnchorFootViewHolder.mLoadingView.setVisibility(View.VISIBLE);
                        break;
                    case LOAD_DATA_ERROR_STATE:
                        mTaoBaoAnchorFootViewHolder.mLoadErrorView.setVisibility(View.VISIBLE);
                        mTaoBaoAnchorFootViewHolder.mLoadingView.setVisibility(View.GONE);

                        View mBtnErrorRefresh = mTaoBaoAnchorFootViewHolder.mLoadErrorView.findViewById(R.id.load_error_btn);

                        mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTaoBaoAnchorAdapter.setLoadDataState(TaoBaoAnchorAdapter.LOADING_DATA_STATE);
                                loadData(false, mCurrentPage + 1);
                            }
                        });
                        break;
                }
            }else{
                if(mTaoBaoAnchorViewHolder != null){
                    final TaoBaoAnchor mTaoBaoAnchor = mTaoBaoAnchors.get(position);

                }
            }
        }

        @Override
        public int getItemCount() {
            return mTaoBaoAnchors.size() + (mCurrentPage < mTotalPage - 1 ? 1 : 0);
        }
    }
    */

    public class TaoBaoAnchorViewHolder extends RecyclerView.ViewHolder{

        public View mItemView;

        public PaletteView mPaletteView;

        public View mItemTaobaoAnchorBottomView;

        public ImageView mItemTaobaoAnchorImg;
        public ImageView mItemTaobaoAnchorVflag;
        public TextView mItemTaobaoAnchorName;
        public TextView mItemTaobaoAnchorFansName;
        public TextView mItemTaobaoAnchorFansCount;

        public TaoBaoAnchorViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;

            mPaletteView = mItemView.findViewById(R.id.item_taobao_anchor_palette_view);

            mItemTaobaoAnchorBottomView = mItemView.findViewById(R.id.item_taobao_anchor_bottom_view);

            mItemTaobaoAnchorImg = mItemView.findViewById(R.id.item_taobao_anchor_img);
            mItemTaobaoAnchorVflag = mItemView.findViewById(R.id.item_taobao_anchor_vflag);
            mItemTaobaoAnchorName = mItemView.findViewById(R.id.item_taobao_anchor_name);
            mItemTaobaoAnchorFansName = mItemView.findViewById(R.id.item_taobao_anchor_fans_name);
            mItemTaobaoAnchorFansCount = mItemView.findViewById(R.id.item_taobao_anchor_fans_count);
        }
    }

    public class TaoBaoAnchorFootViewHolder extends RecyclerView.ViewHolder{

        private View mItemView;

        private View mLoadingView;
        private View mLoadErrorView;

        public TaoBaoAnchorFootViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mLoadingView = mItemView.findViewById(R.id.loading_view);
            mLoadErrorView = mItemView.findViewById(R.id.load_error_view);
        }
    }
}
