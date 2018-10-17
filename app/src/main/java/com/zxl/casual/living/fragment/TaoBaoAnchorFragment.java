package com.zxl.casual.living.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zxl.casual.living.R;
import com.zxl.casual.living.http.HttpUtils;
import com.zxl.casual.living.http.data.ResponseBaseBean;
import com.zxl.casual.living.http.data.TaoBaoAnchor;
import com.zxl.casual.living.http.data.TaoBaoAnchorListResponseBean;
import com.zxl.casual.living.http.listener.NetRequestListener;

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
                    mTaoBaoAnchorAdapter.setData(mFirstTemp);

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
                    mTaoBaoAnchorAdapter.addData(mTemp);

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
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mTaoBaoAnchorAdapter = new TaoBaoAnchorAdapter();
        mRecyclerView.setAdapter(mTaoBaoAnchorAdapter);

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

    public class TaoBaoAnchorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        public static final int LOADING_DATA_STATE = 1;
        public static final int LOAD_DATA_SUCCESS_STATE =2 ;
        public static final int LOAD_DATA_ERROR_STATE = 3;

        private static final int CONTENT_TYPE = 1;
        private static final int FOOT_TYPE = 2;

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
            notifyDataSetChanged();
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

                Glide.with(mActivity).load(mTaoBaoAnchors.get(position).anchor_img).into(mTaoBaoAnchorViewHolder.mItemTaobaoAnchorImg);
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

    public class TaoBaoAnchorViewHolder extends RecyclerView.ViewHolder{

        public View mItemView;

        public ImageView mItemTaobaoAnchorImg;
        public ImageView mItemTaobaoAnchorVflag;
        public TextView mItemTaobaoAnchorName;
        public TextView mItemTaobaoAnchorFansCount;

        public TaoBaoAnchorViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;

            mItemTaobaoAnchorImg = mItemView.findViewById(R.id.item_taobao_anchor_img);
            mItemTaobaoAnchorVflag = mItemView.findViewById(R.id.item_taobao_anchor_vflag);
            mItemTaobaoAnchorName = mItemView.findViewById(R.id.item_taobao_anchor_name);
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
