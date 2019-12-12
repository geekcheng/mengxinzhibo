package com.tongchuang.phonelive.game.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.tongchuang.game.util.GameIconUtil;
import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.utils.DpUtil;
import com.tongchuang.phonelive.views.AbsViewHolder;

/**
 * Created by cxf on 2018/11/4.
 * 转盘游戏 显示游戏结果的弹窗
 */

public class GameZpResultViewHolder extends AbsViewHolder {

    private ImageView mImg;
    private View mFlash;
    private ObjectAnimator mShowAnim;
    private ObjectAnimator mHideAnim;
    private RotateAnimation mRotateAnimation;

    public GameZpResultViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.game_view_zp_result;
    }

    @Override
    public void init() {
        mImg = (ImageView) findViewById(R.id.img);
        mFlash = findViewById(R.id.flash);
        mRotateAnimation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setDuration(5000);
        mRotateAnimation.setRepeatCount(-1);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        int y = mParentView.getHeight() / 2 + DpUtil.dp2px(100);
        mContentView.setTranslationY(y);
        Interpolator interpolator = new AccelerateDecelerateInterpolator();
        mShowAnim = ObjectAnimator.ofFloat(mContentView, "translationY", y, 0);
        mShowAnim.setDuration(500);
        mShowAnim.setInterpolator(interpolator);
        mShowAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mFlash != null && mRotateAnimation != null) {
                    mFlash.startAnimation(mRotateAnimation);
                }
            }
        });
        mHideAnim = ObjectAnimator.ofFloat(mContentView, "translationY", 0, y);
        mHideAnim.setDuration(500);
        mHideAnim.setInterpolator(interpolator);
    }

    public void show() {
        if (mShowAnim != null) {
            mShowAnim.start();
        }
    }

    public void hide() {
        if (mFlash != null) {
            mFlash.clearAnimation();
        }
        if (mHideAnim != null) {
            mHideAnim.start();
        }
    }

    public void setData(int index) {
        mImg.setImageResource(GameIconUtil.getLuckPanResult(index));
    }

    public void release() {
        if (mFlash != null) {
            mFlash.clearAnimation();
        }
        if (mShowAnim != null) {
            mShowAnim.cancel();
        }
        if (mHideAnim != null) {
            mHideAnim.cancel();
        }
    }
}
