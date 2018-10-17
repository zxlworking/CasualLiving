package com.zxl.casual.living.custom.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zxl.casual.living.GlideApp;
import com.zxl.casual.living.http.data.TodayWeatherDetailIconCss;
import com.zxl.casual.living.http.data.TodayWeatherTemperatureIconCss;
import com.zxl.common.DebugUtil;

/**
 * Created by zxl on 2018/9/5.
 */

public class TodayWeatherDetailIconView extends android.support.v7.widget.AppCompatImageView {

    private static final String TAG = "TodayWeatherDetailIconView";

    private Context mContext;

    private TodayWeatherDetailIconCss mTodayWeatherDetailIconCss;

    private Bitmap mBitmap;

    private SimpleTarget<Bitmap> mBitmapTarget = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

            DebugUtil.d(TAG,"TodayWeatherDetailIconView = " + TodayWeatherDetailIconView.this);
            DebugUtil.d(TAG,"width = " + (Integer.valueOf(mTodayWeatherDetailIconCss.width)));
            DebugUtil.d(TAG,"height = " + (Integer.valueOf(mTodayWeatherDetailIconCss.height)));
            DebugUtil.d(TAG,"background_position_x = " + (-Integer.valueOf(mTodayWeatherDetailIconCss.background_position_x)));
            DebugUtil.d(TAG,"background_position_y = " + (-Integer.valueOf(mTodayWeatherDetailIconCss.background_position_y)));

            mBitmap = Bitmap.createBitmap(Integer.valueOf(mTodayWeatherDetailIconCss.width),Integer.valueOf(mTodayWeatherDetailIconCss.height), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mBitmap);

            Rect rectRes = new Rect(
                    -Integer.valueOf(mTodayWeatherDetailIconCss.background_position_x),
                    -Integer.valueOf(mTodayWeatherDetailIconCss.background_position_y),
                    -Integer.valueOf(mTodayWeatherDetailIconCss.background_position_x) + Integer.valueOf(mTodayWeatherDetailIconCss.width),
                    -Integer.valueOf(mTodayWeatherDetailIconCss.background_position_y) + Integer.valueOf(mTodayWeatherDetailIconCss.height));
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

    public TodayWeatherDetailIconView(Context context) {
        super(context);
        init(context);
    }

    public TodayWeatherDetailIconView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TodayWeatherDetailIconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        DebugUtil.d(TAG,"init");
        mContext = context;
    }

    public void setTodayWeatherDetailIconCss(TodayWeatherDetailIconCss iconCss){
        mTodayWeatherDetailIconCss = iconCss;

        GlideApp.with(mContext).asBitmap().load(mTodayWeatherDetailIconCss.img).into(mBitmapTarget);
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
