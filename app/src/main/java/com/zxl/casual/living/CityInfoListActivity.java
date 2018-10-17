package com.zxl.casual.living;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zxl.casual.living.event.SelectCityEvent;
import com.zxl.casual.living.http.HttpUtils;
import com.zxl.casual.living.http.data.CityInfo;
import com.zxl.casual.living.http.data.CityInfoListResponseBean;
import com.zxl.casual.living.http.data.ResponseBaseBean;
import com.zxl.casual.living.http.listener.NetRequestListener;
import com.zxl.casual.living.utils.EventBusUtils;
import com.zxl.casual.living.utils.SharePreUtils;
import com.zxl.common.DebugUtil;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by zxl on 2018/9/14.
 */

public class CityInfoListActivity extends AppCompatActivity {

    private static final String TAG = "CityInfoListActivity";

    private Context mContext;

    private LinearLayout mCityInfoListContentLl;
    private EditText mSearchEt;
    private RecyclerView mRecyclerView;
    private View mLoadingView;
    private View mLoadErrorView;
    private TextView mLoadErrorTv;
    private Button mLoadErrorBtn;

    private CityInfoListAdapter mCityInfoListAdapter;

    private boolean isLoading = false;

    private CityInfoListResponseBean mCityInfoListResponseBean;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(TextUtils.isEmpty(s)){
                mCityInfoListAdapter.setData(mCityInfoListResponseBean);
            }else{
                CityInfoListResponseBean cityInfoListResponseBean = new CityInfoListResponseBean();
                for(CityInfo cityInfo : mCityInfoListResponseBean.city_list){
                    if(cityInfo.city_name.contains(s)){
                        cityInfoListResponseBean.city_list.add(cityInfo);
                    }
                }
                mCityInfoListAdapter.setData(cityInfoListResponseBean);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_city_info_list);

        mContext = this;

        mCityInfoListContentLl = findViewById(R.id.city_info_list_content_ll);
        mSearchEt = findViewById(R.id.search_et);
        mRecyclerView = findViewById(R.id.recycler_view);
        mLoadingView = findViewById(R.id.loading_view);
        mLoadErrorView = findViewById(R.id.load_error_view);
        mLoadErrorTv = findViewById(R.id.load_error_tv);
        mLoadErrorBtn = findViewById(R.id.load_error_btn);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mCityInfoListAdapter = new CityInfoListAdapter();


        mLoadErrorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        mSearchEt.addTextChangedListener(mTextWatcher);

        getData();
    }

    private void getData(){

        if(isLoading){
            return;
        }
        isLoading = true;

        mLoadingView.setVisibility(VISIBLE);
        mLoadErrorView.setVisibility(GONE);
        mCityInfoListContentLl.setVisibility(GONE);

        CityInfoListResponseBean localCityInfoListResponseBean = SharePreUtils.getInstance(mContext).getCityInfoList();
        if(localCityInfoListResponseBean != null){
            DebugUtil.d(TAG,"localCityInfoListResponseBean not null");

            mCityInfoListResponseBean = localCityInfoListResponseBean;

            mLoadingView.setVisibility(GONE);
            mLoadErrorView.setVisibility(GONE);
            mCityInfoListContentLl.setVisibility(VISIBLE);

            mRecyclerView.setAdapter(mCityInfoListAdapter);
            mCityInfoListAdapter.setData(localCityInfoListResponseBean);

            isLoading = false;
            return;
        }

        HttpUtils.getInstance().getCityInfoList(this, new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {
                DebugUtil.d(TAG,"onSuccess");

                CityInfoListResponseBean cityInfoListResponseBean = (CityInfoListResponseBean) responseBaseBean;
                mCityInfoListResponseBean = cityInfoListResponseBean;

                SharePreUtils.getInstance(mContext).saveCityInfoList(cityInfoListResponseBean);

                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(GONE);
                mCityInfoListContentLl.setVisibility(VISIBLE);

                mRecyclerView.setAdapter(mCityInfoListAdapter);
                mCityInfoListAdapter.setData(cityInfoListResponseBean);

                isLoading = false;
            }

            @Override
            public void onNetError() {
                DebugUtil.d(TAG,"onNetError");
                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(VISIBLE);
                mCityInfoListContentLl.setVisibility(GONE);
                mLoadErrorTv.setText(R.string.no_network_tip);

                isLoading = false;
            }

            @Override
            public void onNetError(Throwable e) {
                DebugUtil.d(TAG,"onNetError::e = " + e);
                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(VISIBLE);
                mCityInfoListContentLl.setVisibility(GONE);
                mLoadErrorTv.setText(mContext.getResources().getString(R.string.network_error_tip, ""));

                isLoading = false;
            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {
                DebugUtil.d(TAG,"onServerError::responseBaseBean = " + responseBaseBean);
                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(VISIBLE);
                mCityInfoListContentLl.setVisibility(GONE);
                mLoadErrorTv.setText(mContext.getResources().getString(R.string.server_error_tip, responseBaseBean.desc));

                isLoading = false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSearchEt.removeTextChangedListener(mTextWatcher);
    }

    class CityInfoListAdapter extends RecyclerView.Adapter<CityInfoViewHolder>{

        private CityInfoListResponseBean mCityInfoListResponseBean;

        public void setData(CityInfoListResponseBean cityInfoListResponseBean){
            mCityInfoListResponseBean = cityInfoListResponseBean;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public CityInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_city_info_view,parent,false);
            return new CityInfoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CityInfoViewHolder holder, int position) {
            final CityInfo cityInfo = mCityInfoListResponseBean.city_list.get(position);
            holder.mCityNameTv.setText(cityInfo.city_head + " --- " + cityInfo.city_name);

            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBusUtils.post(new SelectCityEvent(cityInfo));
                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCityInfoListResponseBean.city_list.size();
        }
    }

    class CityInfoViewHolder extends RecyclerView.ViewHolder{

        public View mItemView;
        public TextView mCityNameTv;

        public CityInfoViewHolder(View itemView) {
            super(itemView);

            mItemView = itemView;
            mCityNameTv = mItemView.findViewById(R.id.city_name_tv);
        }
    }
}
