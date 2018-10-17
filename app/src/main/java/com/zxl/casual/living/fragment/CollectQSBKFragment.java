package com.zxl.casual.living.fragment;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.zxl.casual.living.QSBKActivity;
import com.zxl.casual.living.QSBKDetailActivity;
import com.zxl.casual.living.R;
import com.zxl.casual.living.custom.view.CustomScaleView;
import com.zxl.casual.living.http.HttpUtils;
import com.zxl.casual.living.http.data.QSBKElement;
import com.zxl.casual.living.http.data.QSBKElementList;
import com.zxl.casual.living.http.data.ResponseBaseBean;
import com.zxl.casual.living.http.data.UserInfoResponseBean;
import com.zxl.casual.living.http.listener.NetRequestListener;
import com.zxl.casual.living.utils.CommonUtils;
import com.zxl.casual.living.utils.SharePreUtils;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.zxl.common.DebugUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by zxl on 2018/9/20.
 */

public class CollectQSBKFragment extends BaseFragment {
    private static final String TAG = "CollectQSBKFragment";

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

    private CustomScaleView mCustomScaleView;

    private RecyclerView mRecyclerView;
    private QSBKAdapter mQSBKAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

//    private Retrofit mRetrofit;
//    private IQueryQSBK mIQueryQSBK;

    private int mCurrentPage = 0;
    private int mTotalPage = 0;
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

                    List<QSBKElement> mFirstTemp = (List<QSBKElement>) msg.obj;
                    mQSBKAdapter.setData(mFirstTemp);

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

                    List<QSBKElement> mTemp = (List<QSBKElement>) msg.obj;
                    mQSBKAdapter.addData(mTemp);

                    isLoading = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_LOAD_ERROR:
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLoadingView.setVisibility(View.GONE);
                    mLoadErrorView.setVisibility(View.GONE);

                    mQSBKAdapter.setLoadDataState(QSBKActivity.CalculateAdapter.LOAD_DATA_ERROR_STATE);

                    isLoading = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.fragment_qsbk,null);

        mLoadingView = mContentView.findViewById(R.id.qsbk_loading_view);
        mLoadErrorView = mContentView.findViewById(R.id.qsbk_load_error_view);
        mBtnErrorRefresh = mLoadErrorView.findViewById(R.id.load_error_btn);

        mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPage = 0;
                loadData(true,0);
            }
        });

        mCustomScaleView = mContentView.findViewById(R.id.custom_scale_img);

        mCustomScaleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCustomScaleView.setVisibility(View.GONE);
            }
        });

        mRecyclerView = mContentView.findViewById(R.id.recycler_view);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mQSBKAdapter = new QSBKAdapter();
        mRecyclerView.setAdapter(mQSBKAdapter);

        mSwipeRefreshLayout = mContentView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#3F51B5"),Color.parseColor("#303F9F"),Color.parseColor("#FF4081"));
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                mCurrentPage = 0;
                loadData(true,0);
            }
        });

//        OkHttpClient mOkHttpClient = new OkHttpClient.Builder().build();
//        mRetrofit = new Retrofit.Builder()
//                //.baseUrl("http://www.zxltest.cn/")
//                .baseUrl("http://118.25.178.69/")
//                .client(mOkHttpClient)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        mIQueryQSBK = mRetrofit.create(IQueryQSBK.class);

        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCurrentPage = 0;
        loadData(true, 0);
    }

    public boolean onBackPressed(){
        if(mCustomScaleView.getVisibility() == View.VISIBLE){
            mCustomScaleView.setVisibility(View.GONE);
            return true;
        }
        return false;
    }


    public void loadData(final boolean isFirstLoad, final int page){
        DebugUtil.d(TAG,"loadData::page = " + page);
        if(isLoading){
            return;
        }
        isLoading = true;

        if(isFirstLoad){
            mHandler.sendEmptyMessage(MSG_FIRST_LOAD_START);
        }else{
            mHandler.sendEmptyMessage(MSG_LOAD_START);
        }

        UserInfoResponseBean userInfoResponseBean = SharePreUtils.getInstance(mActivity).getUserInfo();

        HttpUtils.getInstance().getQSBKFromCollect(mActivity, page,mPageCount,QSBKElement.QSBK_COLLECT_OPERATOR_QUERY_ALL, (userInfoResponseBean != null ? userInfoResponseBean.user_id : ""),new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {
                QSBKElementList mQSBKElementList = (QSBKElementList) responseBaseBean;

                mCurrentPage = mQSBKElementList.current_page;
                mTotalPage = mQSBKElementList.total_page;

                if(isFirstLoad){
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_FIRST_LOAD_SUCCESS;
                    message.obj = mQSBKElementList.result;
                    message.sendToTarget();
                }else{
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_LOAD_SUCCESS;
                    message.obj = mQSBKElementList.result;
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

    public void doForCollect(final int operator, final int position, final QSBKElement qsbkElement){
        UserInfoResponseBean userInfoResponseBean = SharePreUtils.getInstance(mActivity).getUserInfo();
        if(userInfoResponseBean != null){
            HttpUtils.getInstance().collectQSBK(mActivity, operator, userInfoResponseBean.user_id, CommonUtils.mGson.toJson(qsbkElement), new NetRequestListener() {
                @Override
                public void onSuccess(ResponseBaseBean responseBaseBean) {

                    if(QSBKElement.QSBK_COLLECT_OPERATOR_COLLECT == operator){
                        if(position < mQSBKAdapter.getData().size() && TextUtils.equals(mQSBKAdapter.getData().get(position).author_id, qsbkElement.author_id)){
                            mQSBKAdapter.getData().get(position).is_collect = true;
                            mQSBKAdapter.notifyItemChanged(position);
                        }
                        Toast.makeText(mActivity,"已收藏",Toast.LENGTH_SHORT).show();
                    }
                    if(QSBKElement.QSBK_COLLECT_OPERATOR_CANCEL == operator){
                        if(position < mQSBKAdapter.getData().size() && TextUtils.equals(mQSBKAdapter.getData().get(position).author_id, qsbkElement.author_id)){
                            mQSBKAdapter.getData().remove(position);
                            mQSBKAdapter.notifyItemRemoved(position);
                        }
                        Toast.makeText(mActivity,"已取消收藏",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onNetError() {
                    Toast.makeText(mActivity,mActivity.getResources().getString(R.string.no_network_tip),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNetError(Throwable e) {
                    Toast.makeText(mActivity,mActivity.getResources().getString(R.string.network_error_tip),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onServerError(ResponseBaseBean responseBaseBean) {
                    Toast.makeText(mActivity,responseBaseBean.desc,Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(mActivity,"请先登录",Toast.LENGTH_SHORT).show();
        }
    }

    public class QSBKAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        public static final int LOADING_DATA_STATE = 1;
        public static final int LOAD_DATA_SUCCESS_STATE =2 ;
        public static final int LOAD_DATA_ERROR_STATE = 3;

        private static final int CONTENT_TYPE = 1;
        private static final int FOOT_TYPE = 2;

        private int mCurrentState = LOAD_DATA_SUCCESS_STATE;
        private List<QSBKElement> mQSBKElements = new ArrayList<>();

        public void setData(List<QSBKElement> elements){
            mQSBKElements.clear();
            mQSBKElements.addAll(elements);
            mCurrentState = LOAD_DATA_SUCCESS_STATE;
            notifyDataSetChanged();
        }

        public void addData(List<QSBKElement> elements){
            mQSBKElements.addAll(elements);
            mCurrentState = LOAD_DATA_SUCCESS_STATE;
            notifyDataSetChanged();
        }

        public List<QSBKElement> getData(){
            return mQSBKElements;
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
                    View mItemView = LayoutInflater.from(mActivity).inflate(R.layout.item_qsbk_view, parent, false);
                    return new QSBKViewHolder(mItemView);
                case FOOT_TYPE:
                    View mItemFootView = LayoutInflater.from(mActivity).inflate(R.layout.item_qsbk_foot_view, parent, false);
                    return new QSBKFootViewHolder(mItemFootView);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            QSBKViewHolder mQSBKViewHolder = null;
            if(holder instanceof QSBKViewHolder){
                mQSBKViewHolder = (QSBKViewHolder) holder;
            }
            QSBKFootViewHolder mQSBKFootViewHolder = null;
            if(holder instanceof QSBKFootViewHolder){
                mQSBKFootViewHolder = (QSBKFootViewHolder) holder;
            }
            if(position == getItemCount() - 1 && mCurrentPage < mTotalPage - 1 && mQSBKFootViewHolder != null){
                switch (mCurrentState){
                    case LOAD_DATA_SUCCESS_STATE:
                        mQSBKFootViewHolder.mLoadErrorView.setVisibility(View.GONE);
                        mQSBKFootViewHolder.mLoadingView.setVisibility(View.VISIBLE);

                        loadData(false,mCurrentPage + 1);
                        break;
                    case LOADING_DATA_STATE:
                        mQSBKFootViewHolder.mLoadErrorView.setVisibility(View.GONE);
                        mQSBKFootViewHolder.mLoadingView.setVisibility(View.VISIBLE);
                        break;
                    case LOAD_DATA_ERROR_STATE:
                        mQSBKFootViewHolder.mLoadErrorView.setVisibility(View.VISIBLE);
                        mQSBKFootViewHolder.mLoadingView.setVisibility(View.GONE);

                        View mBtnErrorRefresh = mQSBKFootViewHolder.mLoadErrorView.findViewById(R.id.load_error_btn);

                        mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mQSBKAdapter.setLoadDataState(QSBKAdapter.LOADING_DATA_STATE);
                                loadData(false, mCurrentPage + 1);
                            }
                        });
                        break;
                }
            }else{
                if(mQSBKViewHolder != null){
                    final QSBKElement mQSBKElement = mQSBKElements.get(position);

                    Glide.with(mActivity).load(mQSBKElement.author_head_img).into(mQSBKViewHolder.mAuthorHeadImg);

                    mQSBKViewHolder.mAuthorNameTv.setText(mQSBKElement.author_name);

                    if(mQSBKElement.is_collect){
                        mQSBKViewHolder.mCollectImg.setImageResource(R.mipmap.collect_select_icon);
                    }else{
                        mQSBKViewHolder.mCollectImg.setImageResource(R.mipmap.collect_cancel_icon);
                    }

                    if(mQSBKElement.isAnonymity()){
                        mQSBKViewHolder.mAuthorSexAgeLl.setVisibility(View.GONE);
                    }else{
                        mQSBKViewHolder.mAuthorSexAgeLl.setVisibility(View.VISIBLE);
                        if(mQSBKElement.author_sex == QSBKElement.SEX_MAN){
                            mQSBKViewHolder.mAuthorSexTv.setText("男");
                            mQSBKViewHolder.mAuthorSexTv.setTextColor(Color.parseColor("#0000ff"));
                            mQSBKViewHolder.mAuthorAgeTv.setTextColor(Color.parseColor("#0000ff"));
                        }else if(mQSBKElement.author_sex == QSBKElement.SEX_FEMALE){
                            mQSBKViewHolder.mAuthorSexTv.setText("女");
                            mQSBKViewHolder.mAuthorSexTv.setTextColor(Color.parseColor("#aa00aa"));
                            mQSBKViewHolder.mAuthorAgeTv.setTextColor(Color.parseColor("#aa00aa"));
                        }
                        mQSBKViewHolder.mAuthorAgeTv.setText(mQSBKElement.author_age+"岁");
                    }
                    mQSBKViewHolder.mContentTv.setText(mQSBKElement.content);
                    if(mQSBKElement.hasThumb()){
                        mQSBKViewHolder.mThumbImg.setVisibility(View.VISIBLE);
                        Glide.with(mActivity).load(mQSBKElement.thumb).into(mQSBKViewHolder.mThumbImg);

                        mQSBKViewHolder.mThumbImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mCustomScaleView.setUrl(mQSBKElement.thumb);
                            }
                        });
                    }else{
                        mQSBKViewHolder.mThumbImg.setVisibility(View.GONE);
                    }
                    mQSBKViewHolder.mVoteNumberTv.setText(String.valueOf(mQSBKElement.vote_number));
                    mQSBKViewHolder.mCommentNumberTv.setText(String.valueOf(mQSBKElement.comment_number));

                    mQSBKViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String mQSBKElementStr = CommonUtils.mGson.toJson(mQSBKElement);
                            Intent mIntent = new Intent(mActivity, QSBKDetailActivity.class);
                            mIntent.putExtra(QSBKDetailActivity.EXTRA_QSBK_ELEMENT, mQSBKElementStr);
                            startActivity(mIntent);
                        }
                    });

                    mQSBKViewHolder.mContentTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String mQSBKElementStr = CommonUtils.mGson.toJson(mQSBKElement);
                            Intent mIntent = new Intent(mActivity, QSBKDetailActivity.class);
                            mIntent.putExtra(QSBKDetailActivity.EXTRA_QSBK_ELEMENT, mQSBKElementStr);
                            startActivity(mIntent);
                        }
                    });

                    mQSBKViewHolder.mContentTv.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            ClipboardManager cmb = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                            cmb.setText(mQSBKElement.content); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
                            cmb.getText();//获取粘贴信息
                            Toast.makeText(mActivity,"复制成功",Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    });

                    mQSBKViewHolder.mShareWechatFriendImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(mQSBKElement.hasThumb()){
                                CommonUtils.shareWXBitmap(mActivity,mQSBKElement.thumb,SendMessageToWX.Req.WXSceneTimeline);
                            }else{
                                CommonUtils.shareWXText(mQSBKElement.content,mActivity.getPackageName(),SendMessageToWX.Req.WXSceneTimeline);
                            }
                        }
                    });

                    mQSBKViewHolder.mShareWechatFriendsImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(mQSBKElement.hasThumb()){
                                CommonUtils.shareWXBitmap(mActivity,mQSBKElement.thumb,SendMessageToWX.Req.WXSceneSession);
                            }else{
                                CommonUtils.shareWXText(mQSBKElement.content,mActivity.getPackageName(),SendMessageToWX.Req.WXSceneSession);
                            }
                        }
                    });

                    mQSBKViewHolder.mCollectImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!mQSBKElement.is_collect){
                                doForCollect(QSBKElement.QSBK_COLLECT_OPERATOR_COLLECT, position, mQSBKElement);
                            }else{
                                doForCollect(QSBKElement.QSBK_COLLECT_OPERATOR_CANCEL, position, mQSBKElement);
                            }
                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            return mQSBKElements.size() + (mCurrentPage < mTotalPage - 1 ? 1 : 0);
        }
    }

    public class QSBKViewHolder extends RecyclerView.ViewHolder{

        public View mItemView;
        public ImageView mAuthorHeadImg;
        public ImageView mThumbImg;
        public TextView mAuthorNameTv;
        public TextView mAuthorSexTv;
        public TextView mAuthorAgeTv;
        public TextView mContentTv;
        public TextView mVoteNumberTv;
        public TextView mCommentNumberTv;
        public ImageView mShareWechatFriendImg;
        public ImageView mShareWechatFriendsImg;
        public ImageView mCollectImg;

        public LinearLayout mAuthorSexAgeLl;

        public QSBKViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;

            mAuthorHeadImg = mItemView.findViewById(R.id.author_head_img);
            mThumbImg = mItemView.findViewById(R.id.thumb_img);
            mAuthorNameTv = mItemView.findViewById(R.id.author_name_tv);
            mAuthorSexTv = mItemView.findViewById(R.id.author_sex_tv);
            mAuthorAgeTv = mItemView.findViewById(R.id.author_age_tv);
            mContentTv = mItemView.findViewById(R.id.content_tv);
            mVoteNumberTv = mItemView.findViewById(R.id.vote_number_tv);
            mCommentNumberTv = mItemView.findViewById(R.id.comment_number_tv);
            mAuthorSexAgeLl = mItemView.findViewById(R.id.author_sex_age_ll);

            mShareWechatFriendImg = mItemView.findViewById(R.id.share_wechat_friend_img);
            mShareWechatFriendsImg = mItemView.findViewById(R.id.share_wechat_friends_img);

            mCollectImg = mItemView.findViewById(R.id.collect_img);
        }
    }

    public class QSBKFootViewHolder extends RecyclerView.ViewHolder{

        private View mItemView;

        private View mLoadingView;
        private View mLoadErrorView;

        public QSBKFootViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mLoadingView = mItemView.findViewById(R.id.loading_view);
            mLoadErrorView = mItemView.findViewById(R.id.load_error_view);
        }
    }
}
