package com.zxl.casual.living.custom.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zxl.casual.living.QSBKActivity;
import com.zxl.casual.living.QSBKDetailActivity;
import com.zxl.casual.living.R;
import com.zxl.casual.living.http.HttpUtils;
import com.zxl.casual.living.http.data.QSBKElement;
import com.zxl.casual.living.http.data.QSBKElementList;
import com.zxl.casual.living.http.data.ResponseBaseBean;
import com.zxl.casual.living.http.listener.NetRequestListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zxl.common.DebugUtil;

/**
 * Created by zxl on 2018/9/13.
 */

public class QSBKView extends CardView {

    private static final String TAG = "QSBKView";

    private Context mContext;

    private View mContentView;

    private View mQSBKContentView;
    private View mLoadingView;
    private View mLoadErrorView;
    private TextView mLoadErrorTv;
    private Button mLoadErrorBtn;

    private LinearLayout mAuthorSexAgeLl;
    private ImageView mAuthorHeadImg;
    private ImageView mThumbImg;
    private TextView mAuthorNameTv;
    private TextView mAuthorSexTv;
    private TextView mAuthorAgeTv;
    private TextView mContentTv;
    private TextView mVoteNumberTv;
    private TextView mCommentNumberTv;

    private boolean isLoading = false;

    public QSBKView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public QSBKView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public QSBKView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        DebugUtil.d(TAG, "init");
        mContext = context;

        mContentView = LayoutInflater.from(context).inflate(R.layout.qsbk_view, this);

        mQSBKContentView = mContentView.findViewById(R.id.qsbk_content_view);
        mLoadingView = mContentView.findViewById(R.id.loading_view);
        mLoadErrorView = mContentView.findViewById(R.id.load_error_view);
        mLoadErrorTv = mContentView.findViewById(R.id.load_error_tv);
        mLoadErrorBtn = mContentView.findViewById(R.id.load_error_btn);

        mAuthorSexAgeLl = mContentView.findViewById(R.id.author_sex_age_ll);
        mAuthorHeadImg = mContentView.findViewById(R.id.author_head_img);
        mThumbImg = mContentView.findViewById(R.id.thumb_img);
        mAuthorNameTv = mContentView.findViewById(R.id.author_name_tv);
        mAuthorSexTv = mContentView.findViewById(R.id.author_sex_tv);
        mAuthorAgeTv = mContentView.findViewById(R.id.author_age_tv);
        mContentTv = mContentView.findViewById(R.id.content_tv);
        mVoteNumberTv = mContentView.findViewById(R.id.vote_number_tv);
        mCommentNumberTv = mContentView.findViewById(R.id.comment_number_tv);

        mLoadErrorBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromNet(1);
            }
        });

        mQSBKContentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, QSBKActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        getDataFromNet(1);
    }

    private void getDataFromNet(int page) {

        if (isLoading) {
            return;
        }
        isLoading = true;

        mLoadingView.setVisibility(VISIBLE);
        mLoadErrorView.setVisibility(GONE);
        mQSBKContentView.setVisibility(INVISIBLE);

        HttpUtils.getInstance().getQSBK(mContext, page, "", new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {

                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(GONE);
                mQSBKContentView.setVisibility(VISIBLE);

                QSBKElementList qsbkElementList = (QSBKElementList) responseBaseBean;
                final QSBKElement mQSBKElement = qsbkElementList.result.get(0);
                Glide.with(mContext).load(mQSBKElement.author_head_img).into(mAuthorHeadImg);
                mAuthorNameTv.setText(mQSBKElement.author_name);
                if(mQSBKElement.isAnonymity()){
                    mAuthorSexAgeLl.setVisibility(View.GONE);
                }else{
                    mAuthorSexAgeLl.setVisibility(View.VISIBLE);
                    if(mQSBKElement.author_sex == QSBKElement.SEX_MAN){
                        mAuthorSexTv.setText("男");
                        mAuthorSexTv.setTextColor(Color.parseColor("#0000ff"));
                        mAuthorAgeTv.setTextColor(Color.parseColor("#0000ff"));
                    }else if(mQSBKElement.author_sex == QSBKElement.SEX_FEMALE){
                        mAuthorSexTv.setText("女");
                        mAuthorSexTv.setTextColor(Color.parseColor("#aa00aa"));
                        mAuthorAgeTv.setTextColor(Color.parseColor("#aa00aa"));
                    }
                    mAuthorAgeTv.setText(mQSBKElement.author_age+"岁");
                }
                mContentTv.setText(mQSBKElement.content);
                if(mQSBKElement.hasThumb()){
                    mThumbImg.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(mQSBKElement.thumb).into(mThumbImg);

                    WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                    DisplayMetrics outMetrics = new DisplayMetrics();
                    wm.getDefaultDisplay().getMetrics(outMetrics);
                }else{
                    mThumbImg.setVisibility(View.GONE);
                }
                mVoteNumberTv.setText(String.valueOf(mQSBKElement.vote_number));
                mCommentNumberTv.setText(String.valueOf(mQSBKElement.comment_number));

                mContentTv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        String mQSBKElementStr = mGson.toJson(mQSBKElement);
                        Intent mIntent = new Intent(mContext, QSBKDetailActivity.class);
                        mIntent.putExtra(QSBKDetailActivity.EXTRA_QSBK_ELEMENT, mQSBKElementStr);
                        mContext.startActivity(mIntent);
                    }
                });

                isLoading = false;
            }

            @Override
            public void onNetError() {
                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(VISIBLE);
                mQSBKContentView.setVisibility(INVISIBLE);
                mLoadErrorTv.setText(R.string.no_network_tip);

                isLoading = false;
            }

            @Override
            public void onNetError(Throwable e) {
                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(VISIBLE);
                mQSBKContentView.setVisibility(INVISIBLE);
                mLoadErrorTv.setText(mContext.getResources().getString(R.string.network_error_tip, ""));

                isLoading = false;
            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {
                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(VISIBLE);
                mQSBKContentView.setVisibility(INVISIBLE);
                mLoadErrorTv.setText(mContext.getResources().getString(R.string.server_error_tip, responseBaseBean.desc));

                isLoading = false;
            }
        });
    }
}
