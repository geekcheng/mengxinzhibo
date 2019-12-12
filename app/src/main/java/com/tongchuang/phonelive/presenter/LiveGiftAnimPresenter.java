package com.tongchuang.phonelive.presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.MediaController;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tongchuang.phonelive.Constants;
import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.bean.LiveReceiveGiftBean;
import com.tongchuang.phonelive.glide.ImgLoader;
import com.tongchuang.phonelive.http.HttpConsts;
import com.tongchuang.phonelive.http.HttpUtil;
import com.tongchuang.phonelive.interfaces.CommonCallback;
import com.tongchuang.phonelive.utils.DpUtil;
import com.tongchuang.phonelive.utils.GifCacheUtil;
import com.tongchuang.phonelive.utils.L;
import com.tongchuang.phonelive.utils.WordUtil;
import com.tongchuang.phonelive.views.LiveGiftViewHolder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by cxf on 2018/10/13.
 */

public class LiveGiftAnimPresenter {

    private Context mContext;
    private ViewGroup mParent2;
    private GifImageView mGifImageView;
    private GifDrawable mGifDrawable;
    private View mGifGiftTipGroup;
    private TextView mGifGiftTip;
    private ObjectAnimator mGifGiftTipShowAnimator;
    private ObjectAnimator mGifGiftTipHideAnimator;
    private LiveGiftViewHolder[] mLiveGiftViewHolders;
    private ConcurrentLinkedQueue<LiveReceiveGiftBean> mQueue;
    private ConcurrentLinkedQueue<LiveReceiveGiftBean> mGifQueue;
    private Map<String, LiveReceiveGiftBean> mMap;
    private Handler mHandler;
    private MediaController mMediaController;//koral--/android-gif-drawable 这个库用来播放gif动画的
    private static final int WHAT_GIF = -1;
    private static final int WHAT_ANIM = -2;
    private boolean mShowGif;
    private CommonCallback<File> mDownloadGifCallback;
    private int mDp10;
    private int mDp500;
    private LiveReceiveGiftBean mTempGifGiftBean;
    private String mSendString;


    public LiveGiftAnimPresenter(Context context, View v, GifImageView gifImageView) {
        mContext = context;
        mParent2 = (ViewGroup) v.findViewById(R.id.gift_group_1);
        mGifImageView = gifImageView;
        mGifGiftTipGroup = v.findViewById(R.id.gif_gift_tip_group);
        mGifGiftTip = (TextView) v.findViewById(R.id.gif_gift_tip);
        mDp500 = DpUtil.dp2px(500);
        mGifGiftTipShowAnimator = ObjectAnimator.ofFloat(mGifGiftTipGroup, "translationX", mDp500, 0);
        mGifGiftTipShowAnimator.setDuration(1000);
        mGifGiftTipShowAnimator.setInterpolator(new LinearInterpolator());
        mGifGiftTipShowAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(WHAT_ANIM, 2000);
                }
            }
        });
        mDp10 = DpUtil.dp2px(10);
        mGifGiftTipHideAnimator = ObjectAnimator.ofFloat(mGifGiftTipGroup, "translationX", 0);
        mGifGiftTipHideAnimator.setDuration(800);
        mGifGiftTipHideAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mGifGiftTipHideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mGifGiftTipGroup.setAlpha(1 - animation.getAnimatedFraction());
            }
        });
        mSendString = WordUtil.getString(R.string.live_send_gift_3);
        mLiveGiftViewHolders = new LiveGiftViewHolder[2];
        mLiveGiftViewHolders[0] = new LiveGiftViewHolder(context, (ViewGroup) v.findViewById(R.id.gift_group_2));
        mLiveGiftViewHolders[0].addToParent();
        mQueue = new ConcurrentLinkedQueue<>();
        mGifQueue = new ConcurrentLinkedQueue<>();
        mMap = new HashMap<>();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == WHAT_GIF) {
                    mShowGif = false;
                    if (mGifImageView != null) {
                        mGifImageView.setImageDrawable(null);
                    }
                    if (mGifDrawable != null && !mGifDrawable.isRecycled()) {
                        mGifDrawable.stop();
                        mGifDrawable.recycle();
                    }
                    LiveReceiveGiftBean bean = mGifQueue.poll();
                    if (bean != null) {
                        showGifGift(bean);
                    }
                } else if (msg.what == WHAT_ANIM) {
                    mGifGiftTipHideAnimator.setFloatValues(0, -mDp10 - mGifGiftTipGroup.getWidth());
                    mGifGiftTipHideAnimator.start();
                } else {
                    LiveGiftViewHolder vh = mLiveGiftViewHolders[msg.what];
                    if (vh != null) {
                        LiveReceiveGiftBean bean = mQueue.poll();
                        if (bean != null) {
                            mMap.remove(bean.getKey());
                            vh.show(bean, false);
                            resetTimeCountDown(msg.what);
                        } else {
                            vh.hide();
                        }
                    }
                }
            }
        };
        mDownloadGifCallback = new CommonCallback<File>() {
            @Override
            public void callback(File file) {
                if (file != null) {
                    playGif(file);
                } else {
                    mShowGif = false;
                }
            }
        };
    }

    public void showGiftAnim(LiveReceiveGiftBean bean) {
        if (bean.getGif() == 1) {
            showGifGift(bean);
        } else {
            showNormalGift(bean);
        }
    }

    /**
     * 显示gif礼物
     */
    private void showGifGift(LiveReceiveGiftBean bean) {
        String url = bean.getGifUrl();
        L.e("gif礼物----->" + bean.getGiftName() + "----->" + url);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (mShowGif) {
            if (mGifQueue != null) {
                mGifQueue.offer(bean);
            }
        } else {
            mShowGif = true;
            mTempGifGiftBean = bean;
            if (!url.endsWith(".gif")) {
                ImgLoader.display(url, null, 0, 0, false, null, null, new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
                        resizeGifImageView(drawable);
                        mGifImageView.setImageDrawable(drawable);
                        mGifGiftTip.setText(mTempGifGiftBean.getUserNiceName() + "  " + mSendString + mTempGifGiftBean.getGiftName());
                        mGifGiftTipGroup.setAlpha(1f);
                        mGifGiftTipShowAnimator.start();
                        if (mHandler != null) {
                            mHandler.sendEmptyMessageDelayed(WHAT_GIF, 4000);
                        }
                    }
                });
            } else {
                GifCacheUtil.getFile(Constants.GIF_GIFT_PREFIX + bean.getGiftId(), url, mDownloadGifCallback);
            }
        }
    }

    /**
     * 调整mGifImageView的大小
     */
    private void resizeGifImageView(Drawable drawable) {
        float w = drawable.getIntrinsicWidth();
        float h = drawable.getIntrinsicHeight();
        ViewGroup.LayoutParams params = mGifImageView.getLayoutParams();
        params.height = (int) (mGifImageView.getWidth() * h / w);
        mGifImageView.setLayoutParams(params);
    }

    /**
     * 播放gif
     */
    private void playGif(File file) {
        if (mTempGifGiftBean != null) {
            mGifGiftTip.setText(mTempGifGiftBean.getUserNiceName() + "  " + mSendString + mTempGifGiftBean.getGiftName());
            mGifGiftTipGroup.setAlpha(1f);
            mGifGiftTipShowAnimator.start();
        }
        try {
            mGifDrawable = new GifDrawable(file);
            mGifDrawable.setLoopCount(1);
            resizeGifImageView(mGifDrawable);
            mGifImageView.setImageDrawable(mGifDrawable);
            if (mMediaController == null) {
                mMediaController = new MediaController(mContext);
                mMediaController.setVisibility(View.GONE);
            }
            mMediaController.setMediaPlayer((GifDrawable) mGifImageView.getDrawable());
            mMediaController.setAnchorView(mGifImageView);
            int duration = mGifDrawable.getDuration();
            mMediaController.show(duration);
            if (duration < 4000) {
                duration = 4000;
            }
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(WHAT_GIF, duration);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mShowGif = false;
        }
    }

    /**
     * 显示普通礼物
     */
    private void showNormalGift(LiveReceiveGiftBean bean) {
        if (mLiveGiftViewHolders[0].isIdle()) {
            if (mLiveGiftViewHolders[1] != null && mLiveGiftViewHolders[1].isSameUser(bean)) {
                mLiveGiftViewHolders[1].show(bean, true);
                resetTimeCountDown(1);
                return;
            }
            mLiveGiftViewHolders[0].show(bean, false);
            resetTimeCountDown(0);
            return;
        }
        if (mLiveGiftViewHolders[0].isSameUser(bean)) {
            mLiveGiftViewHolders[0].show(bean, true);
            resetTimeCountDown(0);
            return;
        }
        if (mLiveGiftViewHolders[1] == null) {
            mLiveGiftViewHolders[1] = new LiveGiftViewHolder(mContext, mParent2);
            mLiveGiftViewHolders[1].addToParent();
        }
        if (mLiveGiftViewHolders[1].isIdle()) {
            mLiveGiftViewHolders[1].show(bean, false);
            resetTimeCountDown(1);
            return;
        }
        if (mLiveGiftViewHolders[1].isSameUser(bean)) {
            mLiveGiftViewHolders[1].show(bean, true);
            resetTimeCountDown(1);
            return;
        }
        String key = bean.getKey();
        if (!mMap.containsKey(key)) {
            mMap.put(key, bean);
            mQueue.offer(bean);
        } else {
            LiveReceiveGiftBean bean1 = mMap.get(key);
            bean1.setLianCount(bean1.getLianCount() + 1);
        }
    }

    private void resetTimeCountDown(int index) {
        if (mHandler != null) {
            mHandler.removeMessages(index);
            mHandler.sendEmptyMessageDelayed(index, 5000);
        }
    }


    public void cancelAllAnim() {
        clearAnim();
        mShowGif = false;
        cancelNormalGiftAnim();
        if (mGifGiftTipGroup != null && mGifGiftTipGroup.getTranslationX() != mDp500) {
            mGifGiftTipGroup.setTranslationX(mDp500);
        }
    }

    private void cancelNormalGiftAnim() {
        if (mLiveGiftViewHolders[0] != null) {
            mLiveGiftViewHolders[0].cancelAnimAndHide();
        }
        if (mLiveGiftViewHolders[1] != null) {
            mLiveGiftViewHolders[1].cancelAnimAndHide();
        }
    }


    private void clearAnim() {
        HttpUtil.cancel(HttpConsts.DOWNLOAD_GIF);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mGifGiftTipShowAnimator != null) {
            mGifGiftTipShowAnimator.cancel();
        }
        if (mGifGiftTipHideAnimator != null) {
            mGifGiftTipHideAnimator.cancel();
        }
        if (mQueue != null) {
            mQueue.clear();
        }
        if (mGifQueue != null) {
            mGifQueue.clear();
        }
        if (mMap != null) {
            mMap.clear();
        }
        if (mMediaController != null) {
            mMediaController.hide();
            mMediaController.setAnchorView(null);
        }
        if (mGifImageView != null) {
            mGifImageView.setImageDrawable(null);
        }
        if (mGifDrawable != null && !mGifDrawable.isRecycled()) {
            mGifDrawable.stop();
            mGifDrawable.recycle();
            mGifDrawable = null;
        }
    }

    public void release() {
        clearAnim();
        if (mLiveGiftViewHolders[0] != null) {
            mLiveGiftViewHolders[0].release();
        }
        if (mLiveGiftViewHolders[1] != null) {
            mLiveGiftViewHolders[1].release();
        }
        mDownloadGifCallback = null;
        mHandler = null;
    }

}
