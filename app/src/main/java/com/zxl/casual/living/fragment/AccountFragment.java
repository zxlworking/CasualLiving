package com.zxl.casual.living.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zxl.casual.living.R;
import com.zxl.casual.living.event.LoginSuccessEvent;
import com.zxl.casual.living.event.LogoutSuccessEvent;
import com.zxl.casual.living.http.HttpUtils;
import com.zxl.casual.living.http.data.ResponseBaseBean;
import com.zxl.casual.living.http.data.UserInfoResponseBean;
import com.zxl.casual.living.http.listener.NetRequestListener;
import com.zxl.casual.living.utils.CommonUtils;
import com.zxl.casual.living.utils.EventBusUtils;
import com.zxl.casual.living.utils.SharePreUtils;
import com.zxl.common.DebugUtil;

import java.util.regex.Pattern;

/**
 * Created by zxl on 2018/9/21.
 */

public class AccountFragment extends BaseFragment {
    private static final String TAG = "AccountFragment";

    private static final int CLICK_UNKNOWN_STATE = 0;
    private static final int CLICK_REGISTER_STATE = 1;
    private static final int CLICK_LOGIN_STATE = 2;
    private static final int LOGIN_SUCCESS_STATE = 3;

    private View mContentView;

    private View mLoadingView;
    private View mLoadErrorView;

    private TextInputLayout mUserNameTextInputLayout;
    private TextInputEditText mUserNameTextInputEditText;
    private TextInputLayout mPassWordTextInputLayout;
    private TextInputEditText mPassWordTextInputEditText;
    private TextInputLayout mPhoneNumberTextInputLayout;
    private TextInputEditText mPhoneNumberTextInputEditText;
    private TextInputLayout mNickNameTextInputLayout;
    private TextInputEditText mNickNameTextInputEditText;

    private CardView mRegisterCardView;
    private CardView mLoginCardView;
    private CardView mLogoutCardView;
    private CardView mCancelCardView;

    private int mClickState = CLICK_UNKNOWN_STATE;
    private boolean isRegistering = false;
    private boolean isLogining = false;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.register_card_view:
                    if(mClickState == CLICK_REGISTER_STATE){
                        register();
                    }else{
                        mClickState = CLICK_REGISTER_STATE;
                    }
                    break;
                case R.id.login_card_view:
                    if(mClickState == CLICK_LOGIN_STATE){
                        login();
                    }else{
                        mClickState = CLICK_LOGIN_STATE;
                    }
                    break;
                case R.id.logout_card_view:
                    mClickState = CLICK_UNKNOWN_STATE;
                    SharePreUtils.getInstance(mActivity).saveUserInfo(null);
                    EventBusUtils.post(new LogoutSuccessEvent());
                    break;
                case R.id.cancel_card_view:
                    mClickState = CLICK_UNKNOWN_STATE;
                    break;
            }
            doForkState(mClickState);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DebugUtil.d(TAG,"onCreateView");
        mContentView = inflater.inflate(R.layout.fragment_account,null);

        mLoadingView = mContentView.findViewById(R.id.loading_view);
        mLoadErrorView = mContentView.findViewById(R.id.load_error_view);


        mUserNameTextInputLayout = mContentView.findViewById(R.id.user_name_input_l);
        mUserNameTextInputEditText = mContentView.findViewById(R.id.user_name_input_et);

        mPassWordTextInputLayout = mContentView.findViewById(R.id.pass_word_input_l);
        mPassWordTextInputEditText = mContentView.findViewById(R.id.pass_word_input_et);
        mPassWordTextInputLayout.setPasswordVisibilityToggleEnabled(true);

        mPhoneNumberTextInputLayout = mContentView.findViewById(R.id.phone_number_input_l);
        mPhoneNumberTextInputEditText = mContentView.findViewById(R.id.phone_number_input_et);

        mNickNameTextInputLayout = mContentView.findViewById(R.id.nick_name_input_l);
        mNickNameTextInputEditText = mContentView.findViewById(R.id.nick_name_input_et);

        mRegisterCardView = mContentView.findViewById(R.id.register_card_view);
        mLoginCardView = mContentView.findViewById(R.id.login_card_view);
        mLogoutCardView = mContentView.findViewById(R.id.logout_card_view);
        mCancelCardView = mContentView.findViewById(R.id.cancel_card_view);

        mRegisterCardView.setOnClickListener(mOnClickListener);
        mLoginCardView.setOnClickListener(mOnClickListener);
        mLogoutCardView.setOnClickListener(mOnClickListener);
        mCancelCardView.setOnClickListener(mOnClickListener);

        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        UserInfoResponseBean userInfoResponseBean = SharePreUtils.getInstance(mActivity).getUserInfo();
        if(userInfoResponseBean != null){
            doForkState(LOGIN_SUCCESS_STATE);
        }else{
            mRegisterCardView.setVisibility(View.VISIBLE);
            mLoginCardView.setVisibility(View.VISIBLE);
            mLogoutCardView.setVisibility(View.GONE);
        }
    }

    public void doForkState(int state){
        switch (state){
            case CLICK_UNKNOWN_STATE:
                mUserNameTextInputLayout.setVisibility(View.GONE);
                mPassWordTextInputLayout.setVisibility(View.GONE);
                mPhoneNumberTextInputLayout.setVisibility(View.GONE);
                mNickNameTextInputLayout.setVisibility(View.GONE);

                initInputContent(state);

                mRegisterCardView.setVisibility(View.VISIBLE);
                mLoginCardView.setVisibility(View.VISIBLE);
                mLogoutCardView.setVisibility(View.GONE);
                mCancelCardView.setVisibility(View.GONE);
                break;
            case CLICK_REGISTER_STATE:
                mUserNameTextInputLayout.setVisibility(View.VISIBLE);
                mPassWordTextInputLayout.setVisibility(View.VISIBLE);
                mPhoneNumberTextInputLayout.setVisibility(View.VISIBLE);
                mNickNameTextInputLayout.setVisibility(View.VISIBLE);

                initInputContent(state);

                mRegisterCardView.setVisibility(View.VISIBLE);
                mLoginCardView.setVisibility(View.GONE);
                mLogoutCardView.setVisibility(View.GONE);
                mCancelCardView.setVisibility(View.VISIBLE);
                break;
            case CLICK_LOGIN_STATE:
                mUserNameTextInputLayout.setVisibility(View.VISIBLE);
                mPassWordTextInputLayout.setVisibility(View.VISIBLE);
                mPhoneNumberTextInputLayout.setVisibility(View.GONE);
                mNickNameTextInputLayout.setVisibility(View.GONE);

                mRegisterCardView.setVisibility(View.GONE);
                mLoginCardView.setVisibility(View.VISIBLE);
                mLogoutCardView.setVisibility(View.GONE);
                mCancelCardView.setVisibility(View.VISIBLE);
                break;
            case LOGIN_SUCCESS_STATE:
                mUserNameTextInputLayout.setVisibility(View.VISIBLE);
                mPassWordTextInputLayout.setVisibility(View.GONE);
                mPhoneNumberTextInputLayout.setVisibility(View.VISIBLE);
                mNickNameTextInputLayout.setVisibility(View.VISIBLE);

                initInputContent(state);

                mRegisterCardView.setVisibility(View.GONE);
                mLoginCardView.setVisibility(View.GONE);
                mLogoutCardView.setVisibility(View.VISIBLE);
                mCancelCardView.setVisibility(View.GONE);
                break;
        }
    }

    private void initInputContent(int state) {
        switch (state){
            case CLICK_UNKNOWN_STATE:
            case CLICK_REGISTER_STATE:
            case CLICK_LOGIN_STATE:
                mUserNameTextInputEditText.setEnabled(true);
                mPassWordTextInputEditText.setEnabled(true);
                mPhoneNumberTextInputEditText.setEnabled(true);
                mNickNameTextInputEditText.setEnabled(true);

                mUserNameTextInputEditText.setText("");
                mPassWordTextInputEditText.setText("");
                mPhoneNumberTextInputEditText.setText("");
                mNickNameTextInputEditText.setText("");
                mUserNameTextInputEditText.requestFocus();
                break;
            case LOGIN_SUCCESS_STATE:

                mUserNameTextInputEditText.setEnabled(false);
                mPassWordTextInputEditText.setEnabled(false);
                mPhoneNumberTextInputEditText.setEnabled(false);
                mNickNameTextInputEditText.setEnabled(false);

                UserInfoResponseBean userInfoResponseBean = SharePreUtils.getInstance(mActivity).getUserInfo();

                mUserNameTextInputEditText.setText(userInfoResponseBean.user_name);
                mPassWordTextInputEditText.setText("");
                mPhoneNumberTextInputEditText.setText(userInfoResponseBean.phone_number);
                mNickNameTextInputEditText.setText(userInfoResponseBean.nick_name);
                break;
        }
    }

    public UserInfoResponseBean createUserInfo(){
        String userName = mUserNameTextInputEditText.getText().toString();
        String passWord = mPassWordTextInputEditText.getText().toString();
        String phoneNumber = mPhoneNumberTextInputEditText.getText().toString();
        String nickName = mNickNameTextInputEditText.getText().toString();

        if(TextUtils.isEmpty(userName)){
            Toast.makeText(mActivity,"用户名不能为空",Toast.LENGTH_SHORT).show();
            return null;
        }
        if(TextUtils.isEmpty(passWord)){
            Toast.makeText(mActivity,"密码不能为空",Toast.LENGTH_SHORT).show();
            return null;
        }

        if(mClickState == CLICK_REGISTER_STATE){
            if(TextUtils.isEmpty(phoneNumber)){
                Toast.makeText(mActivity,"手机号不能为空",Toast.LENGTH_SHORT).show();
                return null;
            }else{
                Pattern pattern = Pattern.compile("\\d+");
                if(!pattern.matcher(phoneNumber).matches()){
                    Toast.makeText(mActivity,"手机号格式错误",Toast.LENGTH_SHORT).show();
                    return null;
                }
            }
        }

        if(mClickState == CLICK_REGISTER_STATE){
            if(TextUtils.isEmpty(nickName)){
                Toast.makeText(mActivity,"昵称不能为空",Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        UserInfoResponseBean userInfoResponseBean = new UserInfoResponseBean();
        userInfoResponseBean.user_name = userName;
        userInfoResponseBean.pass_word = passWord;
        userInfoResponseBean.phone_number = phoneNumber;
        userInfoResponseBean.nick_name = nickName;
        return userInfoResponseBean;
    }

    public void register(){
        UserInfoResponseBean userInfoResponseBean = createUserInfo();
        if(null == userInfoResponseBean){
            return;
        }

        if(isRegistering){
            return;
        }
        isRegistering = true;

        mLoadingView.setVisibility(View.VISIBLE);
        mLoadErrorView.setVisibility(View.GONE);

        HttpUtils.getInstance().register(mActivity, UserInfoResponseBean.USER_OPERATOR_CREATE, CommonUtils.mGson.toJson(userInfoResponseBean), new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {
                UserInfoResponseBean userInfoResponseBean1 = (UserInfoResponseBean) responseBaseBean;

                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.GONE);

                mClickState = CLICK_UNKNOWN_STATE;
                doForkState(mClickState);

                isRegistering = false;
                Toast.makeText(mActivity,"注册成功!",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNetError() {
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.GONE);
                isRegistering = false;
            }

            @Override
            public void onNetError(Throwable e) {
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.GONE);
                isRegistering = false;
            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {

                Toast.makeText(mActivity,responseBaseBean.desc,Toast.LENGTH_SHORT).show();

                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.GONE);
                isRegistering = false;
            }
        });
    }

    public void login(){
        UserInfoResponseBean userInfoResponseBean = createUserInfo();
        if(null == userInfoResponseBean){
            return;
        }

        if(isLogining){
            return;
        }
        isLogining = true;

        mLoadingView.setVisibility(View.VISIBLE);
        mLoadErrorView.setVisibility(View.GONE);

        HttpUtils.getInstance().register(mActivity, UserInfoResponseBean.USER_OPERATOR_LOGIN, CommonUtils.mGson.toJson(userInfoResponseBean), new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {
                UserInfoResponseBean userInfoResponseBean = (UserInfoResponseBean) responseBaseBean;

                SharePreUtils.getInstance(mActivity).saveUserInfo(userInfoResponseBean);

                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.GONE);

                mClickState = LOGIN_SUCCESS_STATE;
                doForkState(mClickState);

                EventBusUtils.post(new LoginSuccessEvent());

                isLogining = false;
                Toast.makeText(mActivity,"登录成功!",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNetError() {
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.GONE);
                isLogining = false;
            }

            @Override
            public void onNetError(Throwable e) {
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.GONE);
                isLogining = false;
            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {

                Toast.makeText(mActivity,responseBaseBean.desc,Toast.LENGTH_SHORT).show();

                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.GONE);
                isLogining = false;
            }
        });
    }

}
