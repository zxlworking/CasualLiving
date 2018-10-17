package com.zxl.casual.living.custom.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zxl.casual.living.GlideApp;
import com.zxl.casual.living.http.data.TodayWeatherHumidityIconCss;
import com.zxl.casual.living.http.data.TodayWeatherWindIconCss;
import com.zxl.common.DebugUtil;

/**
 * Created by zxl on 2018/9/5.
 */

public class TodayWeatherWindIconView extends android.support.v7.widget.AppCompatImageView {

    private static final String TAG = "TodayWeatherWindIconView";

    private Context mContext;

    private TodayWeatherWindIconCss mTodayWeatherWindIconCss;

    private Bitmap mBitmap;

    private SimpleTarget<Bitmap> mBitmapTarget = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

            DebugUtil.d(TAG,"TodayWeatherDetailIconView = " + TodayWeatherWindIconView.this);
            DebugUtil.d(TAG,"width = " + (Integer.valueOf(mTodayWeatherWindIconCss.width)));
            DebugUtil.d(TAG,"height = " + (Integer.valueOf(mTodayWeatherWindIconCss.height)));
            DebugUtil.d(TAG,"background_position_x = " + (-Integer.valueOf(mTodayWeatherWindIconCss.background_position_x)));
            DebugUtil.d(TAG,"background_position_y = " + (-Integer.valueOf(mTodayWeatherWindIconCss.background_position_y)));

            mBitmap = Bitmap.createBitmap(Integer.valueOf(mTodayWeatherWindIconCss.width),Integer.valueOf(mTodayWeatherWindIconCss.height), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mBitmap);

            Rect rectRes = new Rect(
                    -Integer.valueOf(mTodayWeatherWindIconCss.background_position_x),
                    -Integer.valueOf(mTodayWeatherWindIconCss.background_position_y),
                    -Integer.valueOf(mTodayWeatherWindIconCss.background_position_x) + Integer.valueOf(mTodayWeatherWindIconCss.width),
                    -Integer.valueOf(mTodayWeatherWindIconCss.background_position_y) + Integer.valueOf(mTodayWeatherWindIconCss.height));
            Rect rectDst = new Rect(0,0,mBitmap.getWidth(),mBitmap.getHeight());

            Paint paint = new Paint();
//            paint.setStyle(Paint.Style.STROKE);
//            paint.setAntiAlias(true);
            canvas.drawBitmap(resource,rectRes,rectDst,paint);

            paint.reset();
            paint = null;

            setImageBitmap(mBitmap);
        }
    };

    public TodayWeatherWindIconView(Context context) {
        super(context);
        init(context);
    }

    public TodayWeatherWindIconView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TodayWeatherWindIconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        DebugUtil.d(TAG,"init");
        mContext = context;
    }

    public void setTodayWeatherHumidityIconCss(TodayWeatherWindIconCss iconCss){
        mTodayWeatherWindIconCss = iconCss;

        GlideApp.with(mContext).asBitmap().load(mTodayWeatherWindIconCss.img).into(mBitmapTarget);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        DebugUtil.d(TAG,"onAttachedToWindow::width = " + getWidth());
        DebugUtil.d(TAG,"onAttachedToWindow::height = " + getHeight());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DebugUtil.d(TAG,"onDetachedFromWindow");
        if(mBitmap != null){
            if(!mBitmap.isRecycled()){
                mBitmap.recycle();
            }
            mBitmap = null;
        }
    }
}
