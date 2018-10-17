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
import com.zxl.casual.living.http.data.TodayWeatherTemperatureIconCss;
import com.zxl.common.DebugUtil;

/**
 * Created by zxl on 2018/9/5.
 */

public class TodayWeatherTemperatureView extends android.support.v7.widget.AppCompatImageView {

    private static final String TAG = "TodayWeatherTemperatureView";

    private Context mContext;

    private TodayWeatherTemperatureIconCss mTodayWeatherTemperatureIconCss;

    private Bitmap mBottomBitmap;
    private Bitmap mTopBitmap;

    private SimpleTarget<Bitmap> mBottomBitmapTarget = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {


            mBottomBitmap = Bitmap.createBitmap(80,-Integer.valueOf(mTodayWeatherTemperatureIconCss.background_position_y2) - 4, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mBottomBitmap);

            Rect rectRes = new Rect(0,-Integer.valueOf(mTodayWeatherTemperatureIconCss.background_position_y1) - 4,80,-Integer.valueOf(mTodayWeatherTemperatureIconCss.background_position_y1) - Integer.valueOf(mTodayWeatherTemperatureIconCss.background_position_y2) - 8);
            Rect rectDst = new Rect(0,0,mBottomBitmap.getWidth(),mBottomBitmap.getHeight());

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            canvas.drawBitmap(resource,rectRes,rectDst,paint);

            paint.reset();
            paint = null;

            GlideApp.with(mContext).asBitmap().load(mTodayWeatherTemperatureIconCss.img).into(mTopBitmapTarget);
        }
    };

    private SimpleTarget<Bitmap> mTopBitmapTarget = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            double temperatureHeight = Double.valueOf(mTodayWeatherTemperatureIconCss.height2);
            mTopBitmap = Bitmap.createBitmap(14, (int) (-Integer.valueOf(mTodayWeatherTemperatureIconCss.background_position_y1) - 38 - temperatureHeight), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mTopBitmap);

            Rect rectRes = new Rect(36, (int) temperatureHeight,50, -Integer.valueOf(mTodayWeatherTemperatureIconCss.background_position_y1) - 38);
            Rect rectDst = new Rect(0,0,mTopBitmap.getWidth(), mTopBitmap.getHeight());
            canvas.drawBitmap(resource,rectRes,rectDst,new Paint());

            canvas = new Canvas(mBottomBitmap);
            rectRes = new Rect(0,0,mTopBitmap.getWidth(), mTopBitmap.getHeight());
            rectDst = new Rect(mBottomBitmap.getWidth()/2 - 4,0,mBottomBitmap.getWidth()/2 + mTopBitmap.getWidth() - 4, mTopBitmap.getHeight() - 2);
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            canvas.drawBitmap(mTopBitmap,rectRes,rectDst,paint);

            DebugUtil.d(TAG,"mTopBitmapTarget::width = " + getWidth());
            DebugUtil.d(TAG,"mTopBitmapTarget::height = " + getHeight());

            setImageBitmap(mBottomBitmap);
        }
    };

    public TodayWeatherTemperatureView(Context context) {
        super(context);
        init(context);
    }

    public TodayWeatherTemperatureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TodayWeatherTemperatureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        DebugUtil.d(TAG,"init");
        mContext = context;
    }

    public void setTodayWeatherTemperatureIconCss(TodayWeatherTemperatureIconCss iconCss){
        mTodayWeatherTemperatureIconCss = iconCss;

        GlideApp.with(mContext).asBitmap().load(mTodayWeatherTemperatureIconCss.img).into(mBottomBitmapTarget);
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
        if(mTopBitmap != null){
            if(!mTopBitmap.isRecycled()){
                mTopBitmap.recycle();
            }
            mTopBitmap = null;
        }

        if(mBottomBitmap != null){
            if(!mBottomBitmap.isRecycled()){
                mBottomBitmap.recycle();
            }
            mBottomBitmap = null;
        }
    }
}
