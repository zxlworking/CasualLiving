package com.zxl.casual.living.fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zxl.casual.living.R;
import com.zxl.casual.living.http.HttpUtils;
import com.zxl.casual.living.http.data.ResponseBaseBean;
import com.zxl.casual.living.http.data.UpdateInfoResponseBean;
import com.zxl.casual.living.http.listener.NetRequestListener;
import com.zxl.casual.living.utils.CommonUtils;
import com.zxl.casual.living.utils.Constants;
import com.zxl.casual.living.utils.DownloadUtils;
import com.zxl.casual.living.utils.SharePreUtils;
import com.zxl.common.DebugUtil;

/**
 * Created by zxl on 2018/9/28.
 */

public class CheckVersionFragment extends BaseFragment {

    private static final String TAG = "CheckVersionFragment";

    private View mContentView;

    private View mLoadingView;
    private TextView mLoadingTv;
    private View mLoadErrorView;
    private TextView mLoadErrorTv;
    private Button mLoadErrorBtn;

    private TextView mCurrentVersionTv;
    private View mCheckVersionContentView;
    private TextView mNewVersionTv;
    private TextView mUpdateTv;

    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_check_version,null);

        mLoadingView = mContentView.findViewById(R.id.loading_view);
        mLoadingTv = mLoadingView.findViewById(R.id.loading_tv);
        mLoadErrorView = mContentView.findViewById(R.id.load_error_view);
        mLoadErrorTv = mLoadErrorView.findViewById(R.id.load_error_tv);
        mLoadErrorBtn = mLoadErrorView.findViewById(R.id.load_error_btn);

        mCurrentVersionTv = mContentView.findViewById(R.id.current_version_tv);

        mCheckVersionContentView = mContentView.findViewById(R.id.check_version_content_view);
        mNewVersionTv = mContentView.findViewById(R.id.new_version_tv);
        mUpdateTv = mContentView.findViewById(R.id.update_tv);


        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCurrentVersionTv.setText("当前版本：" + CommonUtils.getVersionName(mActivity));

        getUpdateInfo();
    }

    private void getUpdateInfo() {
        DebugUtil.d(TAG, "getUpdateInfo::isLoading = " + isLoading);
        if(isLoading){
            return;
        }
        isLoading = true;

        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingTv.setText("检查更新中...");
        mLoadingTv.setTextColor(Color.WHITE);
        mLoadErrorView.setVisibility(View.GONE);
        mCheckVersionContentView.setVisibility(View.GONE);

        HttpUtils.getInstance().getUpdateInfo(mActivity, new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {
                DebugUtil.d(TAG, "onActivityCreated::onSuccess:: = " + responseBaseBean);
                UpdateInfoResponseBean updateInfoResponseBean = (UpdateInfoResponseBean) responseBaseBean;

                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.GONE);

                mCheckVersionContentView.setVisibility(View.VISIBLE);
                mNewVersionTv.setText("最新版本：" + updateInfoResponseBean.versionName);

                int currentVersion = CommonUtils.getVersionCode(mActivity);
                int updateVersion = 0;
                try {
                    updateVersion = Integer.valueOf(updateInfoResponseBean.versionCode);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(updateVersion > currentVersion){
                    mUpdateTv.setVisibility(View.VISIBLE);

                    mCheckVersionContentView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            long id = SharePreUtils.getInstance(mActivity).getDownloadId();
                            if(DownloadUtils.checkStatus(mActivity,id) == DownloadManager.STATUS_PENDING ||
                                    DownloadUtils.checkStatus(mActivity,id) == DownloadManager.STATUS_RUNNING){
                                Toast.makeText(mActivity,"已在下载队列中",Toast.LENGTH_SHORT).show();
                            }else{
                                DownloadUtils.download(mActivity, Constants.WEATHER_BASE_URL + "cgi_server/test.apk");
                            }
                        }
                    });
                }else{
                    mUpdateTv.setVisibility(View.GONE);
                    mCheckVersionContentView.setOnClickListener(null);
                }

                isLoading = false;
            }

            @Override
            public void onNetError() {
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.VISIBLE);
                mCheckVersionContentView.setVisibility(View.GONE);

                mLoadErrorTv.setText(R.string.no_network_tip);

                isLoading = false;
            }

            @Override
            public void onNetError(Throwable e) {
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.VISIBLE);
                mCheckVersionContentView.setVisibility(View.GONE);

                mLoadErrorTv.setText(mActivity.getResources().getString(R.string.network_error_tip, ""));

                isLoading = false;
            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.VISIBLE);
                mCheckVersionContentView.setVisibility(View.GONE);

                mLoadErrorTv.setText(mActivity.getResources().getString(R.string.server_error_tip, responseBaseBean.desc));

                isLoading = false;
            }
        });
    }
}
