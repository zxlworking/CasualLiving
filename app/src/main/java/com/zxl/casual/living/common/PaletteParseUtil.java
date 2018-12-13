package com.zxl.casual.living.common;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zxl.casual.living.GlideApp;
import com.zxl.casual.living.R;
import com.zxl.casual.living.custom.view.PaletteView;
import com.zxl.common.DebugUtil;

import java.lang.ref.WeakReference;

/**
 * Created by zxl on 2018/12/12.
 */

public class PaletteParseUtil {

    private static final String TAG = "PaletteParseUtil";

    public void parse(String url, ImageView img, OnPaletteCompleteListener listener){
        DebugUtil.d(TAG,"parse::url = " + url);
        CustomSimpleTarget mBitmapTarget = new CustomSimpleTarget();
        mBitmapTarget.setBmpUrl(url);
        mBitmapTarget.setImageView(img);
        mBitmapTarget.setOnPaletteCompleteListener(listener);
        GlideApp
                .with(img)
                .asBitmap()
                .apply(new RequestOptions().placeholder(R.mipmap.pic_loading).error(R.mipmap.pic_load_error))
                .override(img.getWidth(),img.getHeight())
                .transition(new BitmapTransitionOptions().crossFade(500))
                .load(url)
                .transform(new RoundedCorners(20))
                .into(mBitmapTarget);
    }

    class CustomSimpleTarget extends SimpleTarget<Bitmap> {


        private String mBmpUrl = "";

        private ImageView mImageView;

        private OnPaletteCompleteListener mOnPaletteCompleteListener;


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
        }

        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition transition) {
            DebugUtil.d(TAG,"onResourceReady::mBmpUrl = " + mBmpUrl);
            Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    DebugUtil.d(TAG,"onGenerated::mBmpUrl = " + mBmpUrl);
                    /*
                    vibrant      -  有活力的颜色
                    lightVibrant -  有活力的亮色
                    darkVibrant  -  有活力的暗色
                    muted        -  柔和暗淡的颜色
                    lightMuted   -  柔和的亮色
                    darkMuted    -  柔和的暗色
                     */
                    /*
                    //一般会将getRgb设置给控件背景色，getBodyTextColor()设置给文字颜色
                    textView.setBackgroundColor(vibrantSwatch.getRgb());
                    textView.setTextColor(vibrantSwatch.getBodyTextColor());
                     */

                    if(mOnPaletteCompleteListener != null){

                        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                        Palette.Swatch lightVibrantSwatch = palette.getLightVibrantSwatch();
                        Palette.Swatch darkVibrantSwatch = palette.getDarkVibrantSwatch();
                        Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                        Palette.Swatch lightMutedSwatch = palette.getLightMutedSwatch();
                        Palette.Swatch darkMutedSwatch = palette.getDarkMutedSwatch();
                        DebugUtil.d(TAG,"vibrantSwatch = " + palette.getVibrantSwatch());
                        DebugUtil.d(TAG,"lightVibrantSwatch = " + lightVibrantSwatch);
                        DebugUtil.d(TAG,"darkVibrantSwatch = " + darkVibrantSwatch);
                        DebugUtil.d(TAG,"mutedSwatch = " + mutedSwatch);
                        DebugUtil.d(TAG,"lightMutedSwatch = " + lightMutedSwatch);
                        DebugUtil.d(TAG,"darkMutedSwatch = " + darkMutedSwatch);

                        int bgColor = 0;
                        int textColor = 0;
                        if(vibrantSwatch != null){
                            bgColor = vibrantSwatch.getRgb();
                            textColor = vibrantSwatch.getTitleTextColor();
                        }else if(lightVibrantSwatch != null){
                            bgColor = lightVibrantSwatch.getRgb();
                            textColor = lightVibrantSwatch.getTitleTextColor();
                        }else if(darkVibrantSwatch != null){
                            bgColor = darkVibrantSwatch.getRgb();
                            textColor = darkVibrantSwatch.getTitleTextColor();
                        }else if(mutedSwatch != null){
                            bgColor = mutedSwatch.getRgb();
                            textColor = mutedSwatch.getTitleTextColor();
                        }else if(lightMutedSwatch != null){
                            bgColor = lightMutedSwatch.getRgb();
                            textColor = lightMutedSwatch.getTitleTextColor();
                        }else if(darkMutedSwatch != null){
                            bgColor = darkMutedSwatch.getRgb();
                            textColor = darkMutedSwatch.getTitleTextColor();
                        }

                        mOnPaletteCompleteListener.onComplete(mBmpUrl, bgColor, textColor);
                    }
                }
            });
        }
    }

    public interface OnPaletteCompleteListener{
        public void onComplete(String url, int bgColor, int textColor);
    }
}
