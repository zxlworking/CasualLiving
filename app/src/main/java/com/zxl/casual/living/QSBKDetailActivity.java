package com.zxl.casual.living;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zxl.casual.living.custom.view.CustomScaleView;
import com.zxl.casual.living.http.data.QSBKComment;
import com.zxl.casual.living.http.data.QSBKDetail;
import com.zxl.casual.living.http.data.QSBKElement;
import com.zxl.casual.living.utils.CommonUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.zxl.common.DebugUtil;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by uidq0955 on 2018/6/15.
 */

public class QSBKDetailActivity extends Activity {

    private static final String TAG = "QSBKDetailActivity";

    private static final int MSG_LOAD_START = 1;
    private static final int MSG_LOAD_SUCCESS = 2;
    private static final int MSG_LOAD_ERROR = 3;

    public static final String EXTRA_QSBK_ELEMENT = "EXTRA_QSBK_ELEMENT";

    private Context mContext;

    private IQueryQSBKDetail mIQueryQSBKDetail;
    private Retrofit mRetrofit;

    private View mLoadingView;
    private View mLoadErrorView;
    private Button mBtnErrorRefresh;

    private CustomScaleView mCustomScaleView;

    private RecyclerView mRecyclerView;
    private QSBKDetailAdapter mQSBKDetailAdapter;

    private QSBKElement mQSBKElement;

    private boolean isLoading = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_LOAD_START:
                    mRecyclerView.setVisibility(View.GONE);
                    mLoadingView.setVisibility(View.VISIBLE);
                    mLoadErrorView.setVisibility(View.GONE);
                    break;
                case MSG_LOAD_SUCCESS:
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLoadingView.setVisibility(View.GONE);
                    mLoadErrorView.setVisibility(View.GONE);

                    QSBKDetail mTemp = (QSBKDetail) msg.obj;
                    mRecyclerView.setAdapter(mQSBKDetailAdapter);
                    mQSBKDetailAdapter.setData(mQSBKElement,mTemp);

                    isLoading = false;
                    break;
                case MSG_LOAD_ERROR:
                    mRecyclerView.setVisibility(View.GONE);
                    mLoadingView.setVisibility(View.GONE);
                    mLoadErrorView.setVisibility(View.VISIBLE);


                    isLoading = false;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qsbk_detail);

        mContext = this;

        mLoadingView = findViewById(R.id.loading_view);
        mLoadErrorView = findViewById(R.id.load_error_view);
        mBtnErrorRefresh = mLoadErrorView.findViewById(R.id.load_error_btn);

        mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(mQSBKElement);
            }
        });

        mCustomScaleView = findViewById(R.id.custom_scale_img);

        mCustomScaleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCustomScaleView.setVisibility(View.GONE);
            }
        });


        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mQSBKDetailAdapter = new QSBKDetailAdapter();

        OkHttpClient mOkHttpClient = new OkHttpClient.Builder().build();
        mRetrofit = new Retrofit.Builder()
                //.baseUrl("http://www.zxltest.cn/")
                .baseUrl("http://118.25.178.69/")
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mIQueryQSBKDetail = mRetrofit.create(IQueryQSBKDetail.class);

        Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        mQSBKElement = mGson.fromJson(getIntent().getStringExtra(EXTRA_QSBK_ELEMENT),QSBKElement.class);
        loadData(mQSBKElement);
    }

    @Override
    public void onBackPressed() {
        if(mCustomScaleView.getVisibility() == View.VISIBLE){
            mCustomScaleView.setVisibility(View.GONE);
        }else{
            super.onBackPressed();
        }
    }

    private void loadData(final QSBKElement mQSBKElement) {
        if(isLoading){
            return;
        }
        isLoading = true;

        mHandler.sendEmptyMessage(MSG_LOAD_START);

        DebugUtil.d(TAG,"loadData::mQSBKElement = "+ mQSBKElement);

        new Thread(new Runnable() {
            @Override
            public void run() {
//                Call<ResponseBody> mCall = mIQueryQSBKDetail.queryQSBKDetail(mQSBKElement.author_id);
//                mCall.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        try {
//                            String result = new String(response.body().bytes());
//                            System.out.println("zxl--->onResponse--->"+ result);
//                            Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
//                            QSBKElementList mQSBKElementList = mGson.fromJson(result,QSBKElementList.class);
//                            System.out.println("zxl--->onResponse--->mQSBKElementList--->"+ mQSBKElementList);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        System.out.println("zxl--->onFailure--->"+t.toString());
//                    }
//                });
                Call<QSBKDetail> mCall = mIQueryQSBKDetail.queryQSBKDetail(mQSBKElement.author_id);
//                Call<QSBKDetail> mCall = mIQueryQSBKDetail.queryQSBKDetail("120535270");
                mCall.enqueue(new Callback<QSBKDetail>() {
                    @Override
                    public void onResponse(Call<QSBKDetail> call, Response<QSBKDetail> response) {
                        QSBKDetail mQSBKDetail = response.body();
                        System.out.println("zxl--->mQSBKDetail--->"+ mQSBKDetail);

                        Message message = mHandler.obtainMessage();
                        message.what = MSG_LOAD_SUCCESS;
                        message.obj = mQSBKDetail;
                        message.sendToTarget();
                    }

                    @Override
                    public void onFailure(Call<QSBKDetail> call, Throwable t) {
                        System.out.println("zxl--->onFailure--->"+t.toString());
                        mHandler.sendEmptyMessage(MSG_LOAD_ERROR);
                    }
                });
            }
        }).start();
    }

    public class QSBKDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private static final int HEAD_TYPE = 1;
        private static final int CONTENT_TYPE = 2;

        private QSBKElement mQSBKElement = null;
        private QSBKDetail mQsbkDetail = null;

        public void setData(QSBKElement qSBKElement, QSBKDetail qsbkDetail){
            mQSBKElement = qSBKElement;
            mQsbkDetail = qsbkDetail;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0){
                return HEAD_TYPE;
            }
            return CONTENT_TYPE;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType){
                case HEAD_TYPE:
                    View mItemHeadView = LayoutInflater.from(mContext).inflate(R.layout.item_qsbk_detail_head_view, parent, false);
                    return new QSBKDetailHeadViewHolder(mItemHeadView);
                case CONTENT_TYPE:
                    View mItemCommentView = LayoutInflater.from(mContext).inflate(R.layout.item_qsbk_detail_comment_view, parent, false);
                    return new QSBKDetailCommentViewHolder(mItemCommentView);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            QSBKDetailHeadViewHolder mQSBKDetailHeadViewHolder = null;
            if(holder instanceof QSBKDetailHeadViewHolder){
                mQSBKDetailHeadViewHolder = (QSBKDetailHeadViewHolder) holder;
            }
            QSBKDetailCommentViewHolder mQSBKDetailCommentViewHolder = null;
            if(holder instanceof QSBKDetailCommentViewHolder){
                mQSBKDetailCommentViewHolder = (QSBKDetailCommentViewHolder) holder;
            }
            if(position == 0){
                if(mQSBKDetailHeadViewHolder != null){
                    Glide.with(mContext).load(mQSBKElement.author_head_img).into(mQSBKDetailHeadViewHolder.mAuthorHeadImg);
                    mQSBKDetailHeadViewHolder.mAuthorNameTv.setText(mQSBKElement.author_name);
                    if(mQSBKElement.isAnonymity()){
                        mQSBKDetailHeadViewHolder.mAuthorSexAgeLl.setVisibility(View.GONE);
                    }else{
                        mQSBKDetailHeadViewHolder.mAuthorSexAgeLl.setVisibility(View.VISIBLE);
                        if(mQSBKElement.author_sex == QSBKElement.SEX_MAN){
                            mQSBKDetailHeadViewHolder.mAuthorSexTv.setText("男");
                            mQSBKDetailHeadViewHolder.mAuthorSexTv.setTextColor(Color.parseColor("#0000ff"));
                            mQSBKDetailHeadViewHolder.mAuthorAgeTv.setTextColor(Color.parseColor("#0000ff"));
                        }else if(mQSBKElement.author_sex == QSBKElement.SEX_FEMALE){
                            mQSBKDetailHeadViewHolder.mAuthorSexTv.setText("女");
                            mQSBKDetailHeadViewHolder.mAuthorSexTv.setTextColor(Color.parseColor("#aa00aa"));
                            mQSBKDetailHeadViewHolder.mAuthorAgeTv.setTextColor(Color.parseColor("#aa00aa"));
                        }
                        mQSBKDetailHeadViewHolder.mAuthorAgeTv.setText(mQSBKElement.author_age+"岁");
                    }
                    mQSBKDetailHeadViewHolder.mContentTv.setText(mQsbkDetail.qsbk_detail_content);
                    if(mQSBKElement.hasThumb()){
                        mQSBKDetailHeadViewHolder.mThumbImg.setVisibility(View.VISIBLE);
                        Glide.with(mContext).load(mQSBKElement.thumb).into(mQSBKDetailHeadViewHolder.mThumbImg);

                        mQSBKDetailHeadViewHolder.mThumbImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mCustomScaleView.setUrl(mQSBKElement.thumb);
                            }
                        });
                    }else{
                        mQSBKDetailHeadViewHolder.mThumbImg.setVisibility(View.GONE);
                    }
                    mQSBKDetailHeadViewHolder.mCommentCount.setText("评论("+mQsbkDetail.user_comment_list.size()+")：");

                    mQSBKDetailHeadViewHolder.mContentTv.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            cmb.setText(mQSBKElement.content); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
                            cmb.getText();//获取粘贴信息
                            Toast.makeText(mContext,"复制成功",Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    });

                    mQSBKDetailHeadViewHolder.mShareWechatFriendImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(mQSBKElement.hasThumb()){
                                CommonUtils.shareWXBitmap(mContext,mQSBKElement.thumb, SendMessageToWX.Req.WXSceneTimeline);
                            }else{
                                CommonUtils.shareWXText(mQSBKElement.content,mContext.getPackageName(),SendMessageToWX.Req.WXSceneTimeline);
                            }
                        }
                    });

                    mQSBKDetailHeadViewHolder.mShareWechatFriendsImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(mQSBKElement.hasThumb()){
                                CommonUtils.shareWXBitmap(mContext,mQSBKElement.thumb,SendMessageToWX.Req.WXSceneSession);
                            }else{
                                CommonUtils.shareWXText(mQSBKElement.content,mContext.getPackageName(),SendMessageToWX.Req.WXSceneSession);
                            }
                        }
                    });
                }
            }else{
                QSBKComment mQsbkComment = mQsbkDetail.user_comment_list.get(position - 1);

                Glide.with(mContext).load(mQsbkComment.user_head_img).into(mQSBKDetailCommentViewHolder.mUserHeadImg);
                mQSBKDetailCommentViewHolder.mUserNameTv.setText(mQsbkComment.user_name);

                if(mQsbkComment.user_sex == QSBKElement.SEX_MAN){
                    mQSBKDetailCommentViewHolder.mUserSexTv.setText("男");
                    mQSBKDetailCommentViewHolder.mUserSexTv.setTextColor(Color.parseColor("#0000ff"));
                    mQSBKDetailCommentViewHolder.mUserAgeTv.setTextColor(Color.parseColor("#0000ff"));
                }else if(mQsbkComment.user_sex == QSBKElement.SEX_FEMALE){
                    mQSBKDetailCommentViewHolder.mUserSexTv.setText("女");
                    mQSBKDetailCommentViewHolder.mUserSexTv.setTextColor(Color.parseColor("#aa00aa"));
                    mQSBKDetailCommentViewHolder.mUserAgeTv.setTextColor(Color.parseColor("#aa00aa"));
                }
                mQSBKDetailCommentViewHolder.mUserAgeTv.setText(mQsbkComment.user_age+"岁");
                mQSBKDetailCommentViewHolder.mCommentTv.setText(mQsbkComment.comment_content);
                mQSBKDetailCommentViewHolder.mCommentReport.setText(""+mQsbkComment.comment_report);
            }
        }

        @Override
        public int getItemCount() {
            return mQsbkDetail.user_comment_list.size() + 1;
        }
    }

    public class QSBKDetailHeadViewHolder extends RecyclerView.ViewHolder{

        public View mItemView;
        public ImageView mAuthorHeadImg;
        public ImageView mThumbImg;
        public TextView mAuthorNameTv;
        public TextView mAuthorSexTv;
        public TextView mAuthorAgeTv;
        public TextView mContentTv;
        public TextView mCommentCount;
        public ImageView mShareWechatFriendImg;
        public ImageView mShareWechatFriendsImg;

        public LinearLayout mAuthorSexAgeLl;

        public QSBKDetailHeadViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;

            mAuthorHeadImg = mItemView.findViewById(R.id.author_head_img);
            mThumbImg = mItemView.findViewById(R.id.thumb_img);
            mAuthorNameTv = mItemView.findViewById(R.id.author_name_tv);
            mAuthorSexTv = mItemView.findViewById(R.id.author_sex_tv);
            mAuthorAgeTv = mItemView.findViewById(R.id.author_age_tv);
            mContentTv = mItemView.findViewById(R.id.content_tv);
            mCommentCount = mItemView.findViewById(R.id.comment_count);
            mAuthorSexAgeLl = mItemView.findViewById(R.id.author_sex_age_ll);

            mShareWechatFriendImg = mItemView.findViewById(R.id.share_wechat_friend_img);
            mShareWechatFriendsImg = mItemView.findViewById(R.id.share_wechat_friends_img);
        }
    }

    public class QSBKDetailCommentViewHolder extends RecyclerView.ViewHolder{

        private View mItemView;

        public ImageView mUserHeadImg;
        public TextView mUserNameTv;
        public TextView mUserSexTv;
        public TextView mUserAgeTv;
        public TextView mCommentTv;
        public TextView mCommentReport;

        public LinearLayout mUserSexAgeLl;

        public QSBKDetailCommentViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mUserHeadImg = mItemView.findViewById(R.id.user_head_img);
            mUserNameTv = mItemView.findViewById(R.id.user_name_tv);
            mUserSexTv = mItemView.findViewById(R.id.user_sex_tv);
            mUserAgeTv = mItemView.findViewById(R.id.user_age_tv);
            mCommentTv = mItemView.findViewById(R.id.comment_content_tv);
            mCommentReport = mItemView.findViewById(R.id.comment_report);
            mUserSexAgeLl = mItemView.findViewById(R.id.user_sex_age_ll);
        }
    }

    public interface IQueryQSBKDetail{
        @GET("/cgi_server/cgi_qsbk/cgi_qsbk_detail.py")
        public Call<QSBKDetail> queryQSBKDetail(@Query("author_id") String author_id);
    }
}
