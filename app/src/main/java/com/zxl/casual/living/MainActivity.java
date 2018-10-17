package com.zxl.casual.living;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zxl.casual.living.event.BackSelectLeftMenuEvent;
import com.zxl.casual.living.event.LocatePermissionSuccessEvent;
import com.zxl.casual.living.event.RequestLocatePermissionEvent;
import com.zxl.casual.living.event.SelectLeftMenuEvent;
import com.zxl.casual.living.fragment.AccountFragment;
import com.zxl.casual.living.fragment.CheckVersionFragment;
import com.zxl.casual.living.fragment.CollectQSBKFragment;
import com.zxl.casual.living.fragment.LeftMenuFragment;
import com.zxl.casual.living.fragment.QSBKFragment;
import com.zxl.casual.living.fragment.TaoBaoAnchorFragment;
import com.zxl.casual.living.http.data.TodayWeather;
import com.zxl.casual.living.http.data.TodayWeatherResponseBean;
import com.zxl.casual.living.utils.CommonUtils;
import com.zxl.casual.living.utils.Constants;
import com.zxl.casual.living.utils.EventBusUtils;
import com.zxl.casual.living.utils.SharePreUtils;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.zxl.common.DebugUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Context mContext;

    private static final int MSG_CANCEL_CLICK_BACK_TO_FINISH = 1;

    private String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.WRITE_SETTINGS
    };

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mLeftMenuView;

    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private LeftMenuFragment mLeftMenuFragment;
//    private QSBKFragment mQSBKFragment;
//    private TaoBaoAnchorFragment mTaoBaoAnchorFragment;
//    private AccountFragment mAccountFragment;

    private List<Fragment> mContentFragments = new ArrayList<>();

    private Stack<Integer> mLeftMenuPositionStack = new Stack<>();
    private boolean isClickBackToFinish = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_CANCEL_CLICK_BACK_TO_FINISH:
                    isClickBackToFinish = false;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        DebugUtil.d(TAG,"onCreate");

        CommonUtils.regToWX(this);

        mContext = this;
        EventBusUtils.register(this);

        mToolbar = findViewById(R.id.custom_tool_bar);


        mDrawerLayout = findViewById(R.id.drawer_layout);
        mLeftMenuView = findViewById(R.id.left_menu_view);

        mToolbar.setTitle("Toolbar");//设置Toolbar标题
        mToolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);

        showLeftMenuFragment();

        initContentFragments();

        showContentFragment(Constants.LEFT_MENU_POSITION_0);
        mLeftMenuPositionStack.push(Constants.LEFT_MENU_POSITION_0);

        mLeftMenuFragment.setToolbar(mToolbar);

//        String s = "{\"code\": 0, \"address_info\": \"南京市\", \"today_weather\": {\"now_time\": \"17:50 实况\", \"temperature\": \"22\", \"is_w\": 1, \"simple_content\": \"周一 阴转小雨 18/24°C\", \"wind_direction\": \"东风\", \"air_quality\": \"67良\", \"humidity\": \"64%\", \"humidity_icon_css\": {\"width\": \"24\", \"height\": \"24\", \"background_position_x\": \"-2\", \"img\": \"http://i.tq121.com.cn/i/weather2015/city/iconall.png\", \"background_position_y\": \"-289\"}, \"is_limit\": 0, \"wind_icon_css\": {\"width\": \"24\", \"height\": \"24\", \"background_position_x\": \"-36\", \"img\": \"http://i.tq121.com.cn/i/weather2015/city/iconall.png\", \"background_position_y\": \"-291\"}, \"air_quality_icon_css\": {\"width\": \"24\", \"height\": \"24\", \"background_position_x\": \"-2\", \"img\": \"http://i.tq121.com.cn/i/weather2015/city/iconall.png\", \"background_position_y\": \"-314\"}, \"is_h\": 1, \"wind_value\": \"2级\", \"is_pol\": 1, \"temperature_icon_css\": {\"background_position_y1\": \"-137\", \"background_position_y2\": \"-142\", \"img\": \"http://i.tq121.com.cn/i/weather2015/city/iconall.png\", \"width2\": \"15\", \"width1\": \"15\", \"background_position_x2\": \"-35\", \"background_position_x1\": \"-35\", \"height1\": \"8\", \"height2\": \"57.5938\"}}, \"city_name\": \"南京市\", \"today_weather_detail\": [{\"sun_icon_css\": {\"width\": \"22\", \"height\": \"22\", \"background_position_x\": \"-2\", \"img\": \"http://i.tq121.com.cn/i/weather2015/city/iconall.png\", \"background_position_y\": \"-269\"}, \"is_sun_up\": 0, \"temperature\": \"18\", \"title\": \"8日夜间\", \"wind_direction\": \"东风\", \"wind_icon_css\": {\"width\": \"24\", \"height\": \"25\", \"background_position_x\": \"-82\", \"img\": \"http://i.tq121.com.cn/i/weather2015/city/iconall.png\", \"background_position_y\": \"-69\"}, \"sun_time\": \"日落 17:43\", \"weather\": \"阴\", \"wind_value\": \"3-4级\", \"weather_icon_css\": {\"width\": \"80\", \"height\": \"80\", \"background_position_x\": \"-160\", \"img\": \"https://i.tq121.com.cn/i/weather2015/png/blue80.png\", \"background_position_y\": \"-320\"}}, {\"sun_icon_css\": {\"width\": \"22\", \"height\": \"22\", \"background_position_x\": \"-33\", \"img\": \"http://i.tq121.com.cn/i/weather2015/city/iconall.png\", \"background_position_y\": \"-269\"}, \"is_sun_up\": 1, \"temperature\": \"24\", \"title\": \"9日白天\", \"wind_direction\": \"西北风\", \"wind_icon_css\": {\"width\": \"24\", \"height\": \"25\", \"background_position_x\": \"-82\", \"img\": \"http://i.tq121.com.cn/i/weather2015/city/iconall.png\", \"background_position_y\": \"-188\"}, \"weather_desc\": \"天空阴沉\", \"weather\": \"小雨\", \"sun_time\": \"日出 06:03\", \"wind_value\": \"4-5级\", \"weather_icon_css\": {\"width\": \"80\", \"height\": \"80\", \"background_position_x\": \"-560\", \"img\": \"https://i.tq121.com.cn/i/weather2015/png/blue80.png\", \"background_position_y\": \"0\"}}], \"desc\": \"success\"}";
//        try {
//            TodayWeatherResponseBean todayWeatherResponseBean = CommonUtils.mGson.fromJson(s, TodayWeatherResponseBean.class);
//            DebugUtil.d(TAG, "onCreate::todayWeatherResponseBean = " + todayWeatherResponseBean);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    private void showLeftMenuFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(null == mLeftMenuFragment){
            mLeftMenuFragment = (LeftMenuFragment) Fragment.instantiate(mContext,"com.zxl.casual.living.fragment.LeftMenuFragment");
            fragmentTransaction.add(R.id.left_menu_view,mLeftMenuFragment);
        }else{
            fragmentTransaction.show(mLeftMenuFragment);
        }
        fragmentTransaction.commit();
    }

    private void initContentFragments(){
        mContentFragments.clear();
        QSBKFragment mQSBKFragment = (QSBKFragment) Fragment.instantiate(mContext,"com.zxl.casual.living.fragment.QSBKFragment");
        mContentFragments.add(mQSBKFragment);
        TaoBaoAnchorFragment mTaoBaoAnchorFragment = (TaoBaoAnchorFragment) Fragment.instantiate(mContext,"com.zxl.casual.living.fragment.TaoBaoAnchorFragment");
        mContentFragments.add(mTaoBaoAnchorFragment);
        CollectQSBKFragment mCollectQSBKFragment = (CollectQSBKFragment) Fragment.instantiate(mContext,"com.zxl.casual.living.fragment.CollectQSBKFragment");
        mContentFragments.add(mCollectQSBKFragment);
        AccountFragment mAccountFragment = (AccountFragment) Fragment.instantiate(mContext,"com.zxl.casual.living.fragment.AccountFragment");
        mContentFragments.add(mAccountFragment);
        CheckVersionFragment mCheckVersionFragment = (CheckVersionFragment) Fragment.instantiate(mContext,"com.zxl.casual.living.fragment.CheckVersionFragment");
        mContentFragments.add(mCheckVersionFragment);
    }

    private void showContentFragment(int index){
        for(int i = 0; i < mContentFragments.size(); i++){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = mContentFragments.get(i);
            DebugUtil.d(TAG,"showContentFragment::i = " + i);
            if(i == index){
                DebugUtil.d(TAG,"showContentFragment::index = " + index + "::isAdded = " + fragment.isAdded());
                DebugUtil.d(TAG,"showContentFragment::fragment = " + fragment);
                if(fragment.isAdded()){
                    fragmentTransaction.show(fragment);
                }else{
                    fragmentTransaction.add(R.id.container_view,fragment);
                    fragmentTransaction.show(fragment);
                }
                fragmentTransaction.commit();
            }
        }

        for(int i = 0; i < mContentFragments.size(); i++){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = mContentFragments.get(i);
            if(i != index){
                fragmentTransaction.hide(fragment);
            }
            fragmentTransaction.commit();
        }
    }

    private void requestLocatePermission() {
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermissionOk = true;
        if (requestCode == 1) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isPermissionOk = false;
                    break;
                }
            }
        }
        if(isPermissionOk){
            EventBusUtils.post(new LocatePermissionSuccessEvent());
        }
        DebugUtil.d(TAG,"onRequestPermissionsResult::isPermissionOk = " + isPermissionOk);
    }

    @Override
    public void onBackPressed() {
        DebugUtil.d(TAG,"onBackPressed::mLeftMenuPositionStack = " + mLeftMenuPositionStack);

        if(mDrawerLayout.isDrawerOpen(mLeftMenuView)){
            mDrawerLayout.closeDrawer(mLeftMenuView);
        }else{
            int position = Constants.LEFT_MENU_POSITION_0;

            if(mLeftMenuPositionStack.size() > 0){
                position = mLeftMenuPositionStack.get(mLeftMenuPositionStack.size() - 1);
                if(mContentFragments.get(position) instanceof QSBKFragment){
                    QSBKFragment qsbkFragment = (QSBKFragment) mContentFragments.get(position);
                    boolean isNeedHand = qsbkFragment.onBackPressed();
                    if(isNeedHand){
                        return;
                    }
                }
            }

            if(mLeftMenuPositionStack.size() > 1){
                mLeftMenuPositionStack.pop();
                position = mLeftMenuPositionStack.get(mLeftMenuPositionStack.size() - 1);

                if(position == Constants.LEFT_MENU_POSITION_2 && SharePreUtils.getInstance(mContext).getUserInfo() == null){
                    mLeftMenuPositionStack.pop();
                    position = mLeftMenuPositionStack.get(mLeftMenuPositionStack.size() - 1);
                }

                showContentFragment(position);

                EventBusUtils.post(new BackSelectLeftMenuEvent(position));
            }else{
                if(mLeftMenuPositionStack.size() == 1 && mLeftMenuPositionStack.get(0) == Constants.LEFT_MENU_POSITION_0){
                    if(!isClickBackToFinish){
                        isClickBackToFinish = true;
                        Toast.makeText(mContext,"再按一次退出",Toast.LENGTH_SHORT).show();
                        mHandler.sendEmptyMessageDelayed(MSG_CANCEL_CLICK_BACK_TO_FINISH,1500);
                    }else {
                        super.onBackPressed();
                    }
                }else{
                    mLeftMenuPositionStack.clear();
                    mLeftMenuPositionStack.push(Constants.LEFT_MENU_POSITION_0);
                    showContentFragment(Constants.LEFT_MENU_POSITION_0);
                    EventBusUtils.post(new BackSelectLeftMenuEvent(Constants.LEFT_MENU_POSITION_0));
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtils.unregister(this);

        mDrawerLayout.removeDrawerListener(mActionBarDrawerToggle);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRequestDoLocateEvent(RequestLocatePermissionEvent event){
        requestLocatePermission();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectLeftMenuEvent(SelectLeftMenuEvent event){
        if(event.mPosition == Constants.LEFT_MENU_POSITION_0){
            mLeftMenuPositionStack.clear();
        }else{
            int index = -1;
            for(int i = 0; i < mLeftMenuPositionStack.size(); i++){
                if(mLeftMenuPositionStack.get(i).intValue() == event.mPosition){
                    index = i;
                    break;
                }
            }
            if(index > -1){
                mLeftMenuPositionStack.remove(index);
            }
        }
        mLeftMenuPositionStack.push(event.mPosition);

        showContentFragment(event.mPosition);
        mDrawerLayout.closeDrawer(mLeftMenuView,true);
    }

}
