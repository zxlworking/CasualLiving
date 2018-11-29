package com.zxl.casual.living.custom.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.zxl.casual.living.R;
import com.zxl.casual.living.http.data.LrcListInfo;

import java.io.IOException;

/**
 * Created by zxl on 2018/11/28.
 */

public class LrcView extends android.support.v7.widget.AppCompatTextView {

    private static final String TAG = "LrcView";

    private float width;                   //歌词视图宽度
    private float height;                 //歌词视图高度
    private Paint currentPaint;          //当前画笔对象
    private Paint notCurrentPaint;      //非当前画笔对象
    private float textHeight = 65;      //文本高度
    private float textMaxSize = 50;
    private float textSize = 40;        //文本大小
    private int index = 0;              //list集合下标
    private LrcListInfo mLrcListInfo;              //歌词信息

    private String mMusicUrl;

    private MediaPlayer mMediaPlayer;

    private ImageView mMusicPlayPauseImg;

    private Handler mHandler = new Handler();

    Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            setIndex(lrcIndex());
            invalidate();
            mHandler.postDelayed(mRunnable, 100);
        }
    };

    public LrcView(Context context) {
        super(context);
        init();
    }

    public LrcView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LrcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setMusicPlayPauseImg(ImageView img){
        mMusicPlayPauseImg = img;
    }

    public void setLrcListInfo(String musicUrl, LrcListInfo lrcListInfo) {
        mMusicUrl = musicUrl;
        mLrcListInfo = lrcListInfo;
        invalidate();

        initMediaPlayer();

        mMediaPlayer.reset();// 把各项参数恢复到初始状态
        try {
            mMediaPlayer.setDataSource(mMusicUrl);
            mMediaPlayer.prepare(); // 进行缓冲
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    startPlay();
                }
            });// 注册一个监听器
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LrcListInfo getLrcListInfo(){
        return mLrcListInfo;
    }

    public void startPlay(){
        mMusicPlayPauseImg.setImageResource(R.mipmap.pause_icon);
        mMediaPlayer.start();
        mHandler.post(mRunnable);
    }

    public void stopPlay(){
        mMusicPlayPauseImg.setImageResource(R.mipmap.play_icon);
        mMediaPlayer.pause();
        mHandler.removeCallbacks(mRunnable);
    }

    public void doPausePlayClick(){
        if(mMediaPlayer != null){
            if(mMediaPlayer.isPlaying()){
                stopPlay();
            }else{
                startPlay();
            }
        }
    }

    private void init() {

        setFocusable(true);     //设置可对焦
        //显示歌词部分
        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);    //设置抗锯齿，让文字美观饱满
        currentPaint.setTextAlign(Paint.Align.CENTER);//设置文本对齐方式

        //非高亮部分
        notCurrentPaint = new Paint();
        notCurrentPaint.setAntiAlias(true);
        notCurrentPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void initMediaPlayer() {
        if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
        }
    }

    /**
     * 绘画歌词
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) {
            return;
        }

        currentPaint.setColor(Color.argb(210, 251, 248, 29));
        notCurrentPaint.setColor(Color.argb(140, 255, 255, 255));

        currentPaint.setTextSize(textMaxSize);
        currentPaint.setTypeface(Typeface.SERIF);

        notCurrentPaint.setTextSize(textSize);
        notCurrentPaint.setTypeface(Typeface.DEFAULT);

        try {

            setText("");

            canvas.drawText(mLrcListInfo.mLrcs.get(index).mContent, width / 2, height / 2, currentPaint);

            float tempY = height / 2;
            //画出本句之前的句子
            for (int i = index - 1; i >= 0; i--) {
                //向上推移
                tempY = tempY - textHeight;
                canvas.drawText(mLrcListInfo.mLrcs.get(i).mContent, width / 2, tempY, notCurrentPaint);
            }
            tempY = height / 2;
            //画出本句之后的句子
            for (int i = index + 1; i < mLrcListInfo.mLrcs.size(); i++) {
                //往下推移
                tempY = tempY + textHeight;
                canvas.drawText(mLrcListInfo.mLrcs.get(i).mContent, width / 2, tempY, notCurrentPaint);
            }

        } catch (Exception e) {
            setText("...木有歌词文件，赶紧去下载...");
        }

    }

    /**
     * 当view大小改变的时候调用的方法
     */

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initMediaPlayer();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        destoryMediaPlayer();
    }

    private void destoryMediaPlayer() {
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private int lrcIndex() {
        int currentTime = 0;
        int duration = 0;
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            currentTime = mMediaPlayer.getCurrentPosition();
            duration = mMediaPlayer.getDuration();
        }

        //289802
//        DebugUtil.d(TAG,"lrcIndex::currentTime = " + currentTime + "::duration = " + duration);

        if (currentTime < duration) {
            for (int i = 0; i < mLrcListInfo.mLrcs.size(); i++) {
//                DebugUtil.d(TAG,"lrcIndex::mLrcs mCurrentTime = " + mLrcListInfo.mLrcs.get(i).mCurrentTime);
                if (i < mLrcListInfo.mLrcs.size() - 1) {
                    if (currentTime < mLrcListInfo.mLrcs.get(i).mCurrentTime && i == 0) {
                        index = i;
                    }
                    if ((currentTime > mLrcListInfo.mLrcs.get(i).mCurrentTime)&& currentTime < mLrcListInfo.mLrcs.get(i + 1).mCurrentTime) {
                        index = i;
                    }
                }
                if ((i == mLrcListInfo.mLrcs.size() - 1)&& currentTime > mLrcListInfo.mLrcs.get(i).mCurrentTime) {
                    index = i;
                }
            }
        }
//        DebugUtil.d(TAG,"lrcIndex::index = " + index);
//        DebugUtil.d(TAG,"lrcIndex::==============================");
        return index;
    }
}
