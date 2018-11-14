package com.zxl.casual.living.custom.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zxl.casual.living.GlideApp;
import com.zxl.casual.living.R;
import com.zxl.casual.living.utils.CommonUtils;
import com.zxl.common.DebugUtil;

import java.lang.ref.WeakReference;

/**
 * Created by zxl on 2018/11/13.
 */

public class PaletteView extends LinearLayout {

    private static final String TAG = "PaletteView";

    private Context mContext;

    private int mPaletteBackgroundColor;

    private String mImgUrl = "";

    private CustomSimpleTarget mBitmapTarget;


    public PaletteView(Context context) {
        super(context);
        init(context);
    }

    public PaletteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PaletteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        int width = getWidth();
        int height = getHeight();
        int padding = (int) CommonUtils.dip2px(8);

        DebugUtil.d(TAG,"onDraw::width = " + width + "::height = " + height);


//        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);
        paint.setColor(mPaletteBackgroundColor);
        paint.setShadowLayer(5, padding, padding, mPaletteBackgroundColor);


        RectF mRectFShadow = new RectF(2, 2, width - padding - 2, height - padding -2);
        canvas.drawRoundRect(mRectFShadow, 20, 20, paint);

    }

    public void parse(String url, ImageView img, OnPaletteCompleteListener listener){
        DebugUtil.d(TAG,"parse::url = " + url);
        mImgUrl = url;
        if(mBitmapTarget == null || !TextUtils.equals(url,mBitmapTarget.getBmpUrl())){
            mBitmapTarget = new CustomSimpleTarget();
            mBitmapTarget.setBmpUrl(url);
            mBitmapTarget.setImageView(img);
            mBitmapTarget.setOnPaletteCompleteListener(listener);
            GlideApp
                    .with(mContext)
                    .asBitmap()
                    .placeholder(R.mipmap.pic_loading)
                    .error(R.mipmap.pic_load_error)
                    .override(img.getWidth(),img.getHeight())
                    .transition(new BitmapTransitionOptions().crossFade(500))
                    .load(url)
                    .transform(new RoundedCorners(20))
                    .into(mBitmapTarget);
        }
    }

    public void setPaletteBackgroundColor(int color){
        mPaletteBackgroundColor = color;
        invalidate();
    }

    class CustomSimpleTarget extends SimpleTarget<Bitmap>{
        private String mBmpUrl = "";

        private ImageView mImageView;

        private OnPaletteCompleteListener mOnPaletteCompleteListener;

        private WeakReference<PaletteView> mPaletteImageViewWeakReference = null;

        public String getBmpUrl(){
            return mBmpUrl;
        }

        public void setBmpUrl(String url){
            mBmpUrl = url;
        }

        public void setImageView(ImageView imageView){
            mImageView = imageView;
        }

        public void setOnPaletteCompleteListener(OnPaletteCompleteListener listener){
            mOnPaletteCompleteListener = listener;

            mPaletteImageViewWeakReference = new WeakReference<PaletteView>(PaletteView.this);
        }

        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition transition) {
            DebugUtil.d(TAG,"onResourceReady::mImgUrl = " + mImgUrl + "::mBmpUrl = " + mBmpUrl);
            if(!TextUtils.equals(mImgUrl,mBmpUrl)){
                return;
            }

            mImageView.setImageBitmap(resource);
//            mImageView.setVisibility(INVISIBLE);

            if(mPaletteImageViewWeakReference.get() != null){
                setPaletteBackgroundColor(mPaletteImageViewWeakReference.get().mPaletteBackgroundColor);
            }

            Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    DebugUtil.d(TAG,"onGenerated::mImgUrl = " + mImgUrl + "::mBmpUrl = " + mBmpUrl);
                    if(!TextUtils.equals(mImgUrl,mBmpUrl)){
                        return;
                    }
                    /*
                    vibrant      -  有活力的颜色
                    lightVibrant -  有活力的亮色
                    darkVibrant  -  有活力的暗色
                    muted        -  柔和暗淡的颜色
                    lightMuted   -  柔和的亮色
                    darkMuted    -  柔和的暗色
                     */
                    Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                    /*
                    //一般会将getRgb设置给控件背景色，getBodyTextColor()设置给文字颜色
                    textView.setBackgroundColor(vibrantSwatch.getRgb());
                    textView.setTextColor(vibrantSwatch.getBodyTextColor());
                     */

                    if(mOnPaletteCompleteListener != null){
                        mOnPaletteCompleteListener.onComplete(palette);
                    }
                }
            });
        }
    }

    public interface OnPaletteCompleteListener{
        public void onComplete(Palette palette);
    }
}
