package com.zxl.casual.living.custom.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zxl.casual.living.GlideApp;
import com.zxl.casual.living.R;
import com.zxl.casual.living.utils.CommonUtils;
import com.zxl.casual.living.utils.Constants;
import com.zxl.casual.living.utils.WXUtil;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.zxl.common.DebugUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by zxl on 2018/10/12.
 */

public class CustomScaleView extends FrameLayout {

    private static final String TAG = "CustomScaleImageView";

    private Context mContext;

    private View mLoadingView;
    private TextView mLoadingTv;
    private View mLoadErrorView;
    private TextView mLoadErrorTv;
    private Button mLoadErrorBtn;

    private ImageView mScaleImg;


    private int mDownX;
    private int mDownY;

    private int mLastDownX;
    private int mLastDownY;

    private int mCurrentDownX;
    private int mCurrentDownY;

    private double mLastDoublePointerDistance = 0;
    private double mCurrentDoublePointerDistance = 0;

    private boolean isCustomeMove = false;
    private boolean isDoublePointerToOne = false;

    private String mImgUrl = null;

    private boolean isLoading = false;
    private boolean isLoadSuccess = false;

    public CustomScaleView(Context context) {
        super(context);
        init(context);
    }

    public CustomScaleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
    }

    public void setUrl(final String url){

        setVisibility(VISIBLE);

        mImgUrl = url;

        loadUrl(url,true);
    }

 //    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        DebugUtil.d(TAG,"onMeasure::widthMode = " + widthMode);
//    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        DebugUtil.d(TAG,"onVisibilityChanged::visibility = " + visibility);
        DebugUtil.d(TAG,"onVisibilityChanged::width = " + getWidth() + "::height = " + getHeight());

        if(visibility == View.VISIBLE && getWidth() > 0){
//            int width = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
//            int height = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
//            measure(width,height);
//            width = getMeasuredWidth();
//            height = getMeasuredHeight();
//            DebugUtil.d(TAG,"onVisibilityChanged::getMeasuredWidth = " + width + "::getMeasuredHeight = " + height);

            scrollTo(getPaddingStart(),getPaddingTop());
            mScaleImg.setLayoutParams(new FrameLayout.LayoutParams(getWidth(),getHeight()));

        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);

        mScaleImg = findViewById(R.id.scale_img);

        mLoadingView = findViewById(R.id.custom_scale_loading_view);
        mLoadingTv = mLoadingView.findViewById(R.id.loading_tv);
        mLoadErrorView = findViewById(R.id.custom_scale_load_error_view);
        mLoadErrorTv = mLoadErrorView.findViewById(R.id.load_error_tv);
        mLoadErrorBtn = mLoadErrorView.findViewById(R.id.load_error_btn);

        mLoadErrorBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                loadUrl(mImgUrl,true);
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        DebugUtil.d(TAG,"onTouchEvent::isLoadSuccess = " + isLoadSuccess);
        DebugUtil.d(TAG,"onTouchEvent::getPointerCount = " + event.getPointerCount());
        if(!isLoadSuccess){
            return super.onTouchEvent(event);
        }
        if(event.getPointerCount() > 1){
            isDoublePointerToOne = true;
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    mLastDoublePointerDistance = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int distanceX = (int) (event.getX(0) - event.getX(1));
                    int distanceY = (int) (event.getY(0) - event.getY(1));
                    double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
                    if(mLastDoublePointerDistance > 0){
                        mLastDoublePointerDistance = mCurrentDoublePointerDistance;
                        mCurrentDoublePointerDistance = distance;


                        double deltaDistance = mCurrentDoublePointerDistance - mLastDoublePointerDistance;
                        DebugUtil.d(TAG,"DoublePointer::deltaDistance = " + deltaDistance);

                        double width = mScaleImg.getWidth() + deltaDistance * 2;
                        double height = mScaleImg.getHeight() + deltaDistance * 2;

                        mScaleImg.setLayoutParams(new FrameLayout.LayoutParams((int)width,(int)height));

                        scrollBy((int) deltaDistance,(int) deltaDistance);

                    }else{
                        mLastDoublePointerDistance = distance;
                        mCurrentDoublePointerDistance = distance;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    DebugUtil.d(TAG,"DOUBLE ACTION_UP::isDoublePointerToOne = " + isDoublePointerToOne);
                    break;
            }
        }else{
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    isCustomeMove = false;

                    mDownX = (int) event.getX();
                    mDownY = (int) event.getY();

                    mLastDownX = (int) event.getX();
                    mLastDownY = (int) event.getY();

                    mCurrentDownX = mLastDownX;
                    mCurrentDownY = mLastDownY;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int deltaX = (int) Math.abs(event.getX() - mDownX);
                    int deltaY = (int) Math.abs(event.getY() - mDownY);
//                    DebugUtil.d(TAG,"ACTION_MOVE::deltaX = " + deltaX + "::deltaY = " + deltaY);
                    if(deltaX > 5 || deltaY > 5){
                        isCustomeMove = true;
                    }

                    mLastDownX = mCurrentDownX;
                    mLastDownY = mCurrentDownY;
                    mCurrentDownX = (int) event.getX();
                    mCurrentDownY = (int) event.getY();
                    if(!isDoublePointerToOne && isCustomeMove){
                        scrollBy(mLastDownX - mCurrentDownX,mLastDownY - mCurrentDownY);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    DebugUtil.d(TAG,"ONE ACTION_UP::isDoublePointerToOne = " + isDoublePointerToOne + "::isCustomeMove = " + isCustomeMove);
                    if(!isDoublePointerToOne && !isCustomeMove){
                        performClick();
                    }
                    if(isDoublePointerToOne){
                        loadUrl(mImgUrl,false);
                    }
                    isDoublePointerToOne = false;

                    mLastDoublePointerDistance = 0;
                    mCurrentDoublePointerDistance = 0;

                    mDownX = 0;
                    mDownY = 0;

                    mLastDownX = 0;
                    mLastDownY = 0;

                    mCurrentDownX = 0;
                    mCurrentDownY = 0;

                    break;
            }
        }
        return true;
    }

    private void loadUrl(final String url, boolean isFirst) {
        DebugUtil.d(TAG,"loadUrl::isLoading = " + isLoading);
        DebugUtil.d(TAG,"loadUrl::url = " + url);

        if(isLoading){
            return;
        }
        isLoading = true;
        isLoadSuccess = false;

        if(isFirst){
            mLoadingView.setVisibility(VISIBLE);
            mLoadingTv.setText("获取图片中...");
            mLoadErrorView.setVisibility(GONE);
            mScaleImg.setVisibility(GONE);
        }

//        Glide.with(mScaleImg)
//                .load(url)
//                .listener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        mLoadingView.setVisibility(GONE);
//                        mLoadErrorView.setVisibility(VISIBLE);
//                        mLoadErrorTv.setText("获取图片失败");
//                        mScaleImg.setVisibility(GONE);
//
//                        isLoading = false;
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//
//                        mLoadingView.setVisibility(GONE);
//                        mLoadErrorView.setVisibility(GONE);
//                        mScaleImg.setVisibility(VISIBLE);
//
//                        isLoadSuccess = true;
//                        isLoading = false;
//                        return false;
//                    }
//                })
//                .into(mScaleImg);

        RequestManager requestManager = Glide.with(mScaleImg);
        RequestBuilder<File> requestBuilder = requestManager.downloadOnly();
        requestBuilder.load(url);
        requestBuilder.listener(new RequestListener<File>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(VISIBLE);
                mLoadErrorTv.setText("获取图片失败");
                mScaleImg.setVisibility(GONE);

                isLoading = false;
                return false;
            }

            @Override
            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                DebugUtil.d(TAG,"onResourceReady::resource = " + resource.length());

                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(GONE);
                mScaleImg.setVisibility(VISIBLE);

                GlideApp.with(mScaleImg)
                        .asBitmap()
                        .load(resource)
                        .override(mScaleImg.getWidth(),mScaleImg.getHeight())
                        .transition(BitmapTransitionOptions.withCrossFade(800))
                        .into(mScaleImg);

                isLoadSuccess = true;
                isLoading = false;
                return false;
            }
        });
        requestBuilder.preload();

//        new AsyncTask<Void, Integer, File>() {
//
//            @Override
//            protected File doInBackground(Void... params) {
//                File file = null;
//                try {
//                    FutureTarget<File> future = Glide
//                            .with(mScaleImg)
//                            .load(url)
//                            .downloadOnly(mScaleImg.getWidth(),mScaleImg.getHeight());
//
//                    file = future.get();
//
//                } catch (Exception e) {
//                    DebugUtil.d(TAG, e.getMessage());
//                }
//                return file;
//            }
//
//            @Override
//            protected void onPostExecute(File file) {
//                DebugUtil.d(TAG,"onPostExecute::file = " + file);
//
//                if(file == null){
//                    mLoadingView.setVisibility(GONE);
//                    mLoadErrorView.setVisibility(VISIBLE);
//                    mLoadErrorTv.setText("获取图片失败");
//                    mScaleImg.setVisibility(GONE);
//                }else{
//                    Glide.with(mScaleImg).load(file).into(mScaleImg);
//
//                    mLoadingView.setVisibility(GONE);
//                    mLoadErrorView.setVisibility(GONE);
//                    mScaleImg.setVisibility(VISIBLE);
//
//                    isLoadSuccess = true;
//                }
//
//                isLoading = false;
//            }
//
//            @Override
//            protected void onProgressUpdate(Integer... values) {
//                super.onProgressUpdate(values);
//            }
//        }.execute();
    }
}
