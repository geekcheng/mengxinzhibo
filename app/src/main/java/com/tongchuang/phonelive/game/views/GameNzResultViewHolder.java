package com.tongchuang.phonelive.game.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.utils.DpUtil;
import com.tongchuang.phonelive.utils.WordUtil;
import com.tongchuang.phonelive.views.AbsViewHolder;

/**
 * Created by cxf on 2018/11/4.
 * 开心牛仔 显示游戏结果的弹窗
 */

public class GameNzResultViewHolder extends AbsViewHolder {

    private TextView mBen;
    private TextView mZhuang;
    private String mBenString;
    private String mZhuangString;
    private ObjectAnimator mShowAnim;
    private ObjectAnimator mHideAnim;

    public GameNzResultViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.game_view_nz_result;
    }

    @Override
    public void init() {
        mBen = (TextView) findViewById(R.id.ben);
        mZhuang = (TextView) findViewById(R.id.zhuang);
        mBenString = WordUtil.getString(R.string.game_nz_ben);
        mZhuangString = WordUtil.getString(R.string.game_nz_zhuang);
        Interpolator interpolator = new AccelerateDecelerateInterpolator();
        mShowAnim = ObjectAnimator.ofFloat(mContentView, "translationY", 0);
        mShowAnim.setDuration(300);
        mShowAnim.setInterpolator(interpolator);
        mHideAnim = ObjectAnimator.ofFloat(mContentView, "translationY", DpUtil.dp2px(180));
        mHideAnim.setDuration(300);
        mHideAnim.setInterpolator(interpolator);
    }

    public void show() {
        if (mShowAnim != null) {
            mShowAnim.start();
        }
    }

    public void hide() {
        if (mHideAnim != null) {
            mHideAnim.start();
        }
    }

    public void setData(String benCoin, String bankerCoin, String coinName) {
        if (mBen != null) {
            mBen.setText(mBenString + benCoin + coinName);
        }
        if (mZhuang != null) {
            mZhuang.setText(mZhuangString + bankerCoin + coinName);
        }
    }

    public void release(){
        if (mShowAnim != null) {
            mShowAnim.cancel();
        }
        if (mHideAnim != null) {
            mHideAnim.cancel();
        }
    }
}
