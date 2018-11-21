package com.zxl.casual.living.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.zxl.casual.living.GlideApp;
import com.zxl.casual.living.R;
import com.zxl.casual.living.http.HttpUtils;
import com.zxl.casual.living.http.data.DailySentenceResponseBean;
import com.zxl.casual.living.http.data.ResponseBaseBean;
import com.zxl.casual.living.http.listener.NetRequestListener;
import com.zxl.casual.living.utils.CommonUtils;
import com.zxl.casual.living.utils.SharePreUtils;
import com.zxl.common.DebugUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zxl on 2018/11/21.
 */

public class DailySentenceFragment extends BaseFragment {

    private static final String TAG = "DailySentenceFragment";

    private View mContentView;

    private View mLoadingView;
    private View mLoadErrorView;
    private Button mBtnErrorRefresh;

    public ImageView mShareWechatFriendImg;
    public ImageView mShareWechatFriendsImg;

    private View mDailySentenceContentView;
    private ImageView mDailySentenceImg;

    private boolean isLogining = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DebugUtil.d(TAG, "onCreateView");
        mContentView = inflater.inflate(R.layout.fragment_daily_sentence, null);

        mLoadingView = mContentView.findViewById(R.id.loading_view);
        mLoadErrorView = mContentView.findViewById(R.id.load_error_view);
        mBtnErrorRefresh = mLoadErrorView.findViewById(R.id.load_error_btn);

        mShareWechatFriendImg = mContentView.findViewById(R.id.share_wechat_friend_img);
        mShareWechatFriendsImg = mContentView.findViewById(R.id.share_wechat_friends_img);

        mDailySentenceContentView = mContentView.findViewById(R.id.daily_sentence_content_view);
        mDailySentenceImg = mContentView.findViewById(R.id.daily_sentence_img);

        mShareWechatFriendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.shareWXBitmap(mActivity, SharePreUtils.getInstance(mActivity).getDailySentence().fenxiang_img, SendMessageToWX.Req.WXSceneTimeline);
            }
        });

        mShareWechatFriendsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.shareWXBitmap(mActivity, SharePreUtils.getInstance(mActivity).getDailySentence().fenxiang_img, SendMessageToWX.Req.WXSceneSession);
            }
        });

        mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDailySentence();
            }
        });

        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDailySentence();
    }

    private void getDailySentence() {

        if(isLogining){
            return;
        }
        isLogining = true;

        mDailySentenceContentView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.VISIBLE);
        mLoadErrorView.setVisibility(View.GONE);


        DailySentenceResponseBean dailySentenceResponseBean = SharePreUtils.getInstance(mActivity).getDailySentence();
        if(dailySentenceResponseBean != null){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sf.format(new Date());
            DebugUtil.d(TAG,"getDailySentence::dateStr = " + dateStr);
            if(TextUtils.equals(dailySentenceResponseBean.dateline,dateStr)){
                DebugUtil.d(TAG,"getDailySentence::same day::dailySentenceResponseBean = " + dailySentenceResponseBean);

                Glide.with(mActivity).load(dailySentenceResponseBean.fenxiang_img).into(mDailySentenceImg);

                mDailySentenceContentView.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.GONE);

                isLogining = false;
                return;
            }
        }

        HttpUtils.getInstance().getDailySentence(mActivity, new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {
                DailySentenceResponseBean dailySentenceResponseBean = (DailySentenceResponseBean) responseBaseBean;
                DebugUtil.d(TAG,"getDailySentence::onSuccess::dailySentenceResponseBean = " + dailySentenceResponseBean);

                Glide.with(mActivity).load(dailySentenceResponseBean.fenxiang_img).into(mDailySentenceImg);

                SharePreUtils.getInstance(mActivity).saveDailySentence(dailySentenceResponseBean);

                mDailySentenceContentView.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.GONE);

                isLogining = false;
            }

            @Override
            public void onNetError() {
                Toast.makeText(mActivity,R.string.no_network_tip,Toast.LENGTH_SHORT).show();

                mDailySentenceContentView.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.VISIBLE);
                isLogining = false;
            }

            @Override
            public void onNetError(Throwable e) {
                Toast.makeText(mActivity,R.string.network_error_tip,Toast.LENGTH_SHORT).show();

                mDailySentenceContentView.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.VISIBLE);
                isLogining = false;
            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {
                Toast.makeText(mActivity,R.string.network_error_tip,Toast.LENGTH_SHORT).show();

                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.VISIBLE);
                isLogining = false;
            }
        });
    }
}
