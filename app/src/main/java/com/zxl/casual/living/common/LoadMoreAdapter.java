package com.zxl.casual.living.common;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.zxl.casual.living.fragment.TaoBaoAnchorFragment;
import com.zxl.common.DebugUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxl on 2018/11/14.
 */

public abstract class LoadMoreAdapter<T> extends RecyclerView.Adapter{

    private static final String TAG = "LoadMoreAdapter";

    public static final int LOADING_DATA_STATE = 1;
    public static final int LOAD_DATA_SUCCESS_STATE =2 ;
    public static final int LOAD_DATA_ERROR_STATE = 3;

    public static final int CONTENT_TYPE = 1;
    public static final int FOOT_TYPE = 2;

    private int mCurrentState = LOAD_DATA_SUCCESS_STATE;

    private int mCurrentPage = 0;
    private int mTotalPage = 0;

    private List<T> mDatas = new ArrayList<T>();

    public void setCurrentPage(int currentPage){
        mCurrentPage = currentPage;
    }

    public void setTotalPage(int totalPage){
        mTotalPage = totalPage;
    }

    public void setData(List elements,int currentPage,int totalPage){
        mDatas.clear();
        mDatas.addAll(elements);
        mCurrentState = LOAD_DATA_SUCCESS_STATE;

        setCurrentPage(currentPage);
        setTotalPage(totalPage);

        notifyDataSetChanged();
    }

    public List<T> getData(){
        return mDatas;
    }

    public void addData(List elements,int currentPage,int totalPage){
        mDatas.addAll(elements);
        mCurrentState = LOAD_DATA_SUCCESS_STATE;

        setCurrentPage(currentPage);
        setTotalPage(totalPage);

        notifyItemRangeInserted(getItemCount() - 1, elements.size());
    }

    public void setLoadDataState(int state){
        mCurrentState = state;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDatas.size() + (mCurrentPage < mTotalPage - 1 ? 1 : 0);
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType){
            case CONTENT_TYPE:
                return getContentViewHolder(viewGroup);
            case FOOT_TYPE:
                return getFootViewHolder(viewGroup);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if(position == getItemCount() - 1 && mCurrentPage < mTotalPage - 1){
            DebugUtil.d(TAG,"onBindFootViewHolder::mCurrentPage = " + mCurrentPage);
            switch (mCurrentState) {
                case LOAD_DATA_SUCCESS_STATE:
                    onBindFootViewHolderLoadDataSuccess(viewHolder);
                    break;
                case LOADING_DATA_STATE:
                    onBindFootViewHolderLoadingData(viewHolder);
                    break;
                case LOAD_DATA_ERROR_STATE:
                    onBindFootViewHolderLoadDataError(viewHolder);
                    break;
            }
        }else{
            onBindContentViewHolder(viewHolder,position);
        }
    }

    public abstract RecyclerView.ViewHolder getContentViewHolder(@NonNull ViewGroup parent);
    public abstract RecyclerView.ViewHolder getFootViewHolder(@NonNull ViewGroup parent);

    public abstract void onBindContentViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position);
    public abstract void onBindFootViewHolderLoadDataSuccess(@NonNull RecyclerView.ViewHolder viewHolder);
    public abstract void onBindFootViewHolderLoadingData(@NonNull RecyclerView.ViewHolder viewHolder);
    public abstract void onBindFootViewHolderLoadDataError(@NonNull RecyclerView.ViewHolder viewHolder);

}
