package com.tongchuang.phonelive.presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.tongchuang.phonelive.Constants;
import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.bean.LiveChatBean;
import com.tongchuang.phonelive.bean.LiveEnterRoomBean;
import com.tongchuang.phonelive.bean.UserBean;
import com.tongchuang.phonelive.glide.ImgLoader;
import com.tongchuang.phonelive.http.HttpConsts;
import com.tongchuang.phonelive.http.HttpUtil;
import com.tongchuang.phonelive.interfaces.CommonCallback;
import com.tongchuang.phonelive.utils.DpUtil;
import com.tongchuang.phonelive.utils.GifCacheUtil;
import com.tongchuang.phonelive.utils.ScreenDimenUtil;
import com.tongchuang.phonelive.utils.TextRender;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by cxf on 2018/10/11.
 * 观众进场动画效果
 */

public class LiveEnterRoomAnimPresenter {

    private Context mContext;
    private View mBg;
    private View mUserGroup;
    private ImageView mAvatar;
    private TextView mName;
    private View mStar;
    private GifImageView mGifImageView;
    private GifDrawable mGifDrawable;
    private TextView mWordText;
    private MediaController mMediaController;//koral--/android-gif-drawable 这个库用来播放gif动画的
    private ObjectAnimator mBgAnimator1;
    private ObjectAnimator mBgAnimator2;
    private ObjectAnimator mBgAnimator3;
    private ObjectAnimator mUserAnimator1;
    private ObjectAnimator mUserAnimator2;
    private ObjectAnimator mUserAnimator3;
    private Animation mStarAnim;
    private int mDp500;
    private boolean mIsAnimating;//是否在执行动画
    private ConcurrentLinkedQueue<LiveEnterRoomBean> mQueue;
    private Handler mHandler;
    private int mScreenWidth;
    private CommonCallback<File> mDownloadGifCallback;
    private boolean mShowGif;
    private boolean mEnd;


    public LiveEnterRoomAnimPresenter(Context context, View root) {
        mContext = context;
        mBg = root.findViewById(R.id.jg_bg);
        mUserGroup = root.findViewById(R.id.jg_user);
        mAvatar = (ImageView) root.findViewById(R.id.jg_avatar);
        mName = (TextView) root.findViewById(R.id.jg_name);
        mStar = root.findViewById(R.id.star);
        mGifImageView = (GifImageView) root.findViewById(R.id.enter_room_gif);
        mWordText = (TextView) root.findViewById(R.id.enter_room_word);
        mDp500 = DpUtil.dp2px(500);
        mQueue = new ConcurrentLinkedQueue<>();
        Interpolator interpolator1 = new AccelerateDecelerateInterpolator();
        Interpolator interpolator2 = new LinearInterpolator();
        Interpolator interpolator3 = new AccelerateInterpolator();
        mBgAnimator1 = ObjectAnimator.ofFloat(mBg, "translationX", DpUtil.dp2px(70));
        mBgAnimator1.setDuration(1000);
        mBgAnimator1.setInterpolator(interpolator1);

        mBgAnimator2 = ObjectAnimator.ofFloat(mBg, "translationX", 0);
        mBgAnimator2.setDuration(700);
        mBgAnimator2.setInterpolator(interpolator2);

        mBgAnimator3 = ObjectAnimator.ofFloat(mBg, "translationX", -mDp500);
        mBgAnimator3.setDuration(300);
        mBgAnimator3.setInterpolator(interpolator3);

        mUserAnimator1 = ObjectAnimator.ofFloat(mUserGroup, "translationX", DpUtil.dp2px(70));
        mUserAnimator1.setDuration(1000);
        mUserAnimator1.setInterpolator(interpolator1);
        mUserAnimator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBgAnimator2.start();
                mUserAnimator2.start();
            }
        });

        mUserAnimator2 = ObjectAnimator.ofFloat(mUserGroup, "translationX", 0);
        mUserAnimator2.setDuration(700);
        mUserAnimator2.setInterpolator(interpolator2);
        mUserAnimator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mStar.startAnimation(mStarAnim);
            }
        });

        mUserAnimator3 = ObjectAnimator.ofFloat(mUserGroup, "translationX", mDp500);
        mUserAnimator3.setDuration(450);
        mUserAnimator3.setInterpolator(interpolator3);
        mUserAnimator3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBg.setTranslationX(mDp500);
                mUserGroup.setTranslationX(-mDp500);
                if (!mShowGif) {
                    getNextEnterRoom();
                }
            }
        });

        mStarAnim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mStarAnim.setDuration(1500);
        mStarAnim.setInterpolator(interpolator2);
        mStarAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBgAnimator3.start();
                mUserAnimator3.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mScreenWidth = ScreenDimenUtil.getInstance().getScreenWdith();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mShowGif = false;
                if (mWordText != null) {
                    mWordText.setText("");
                }
                if (mMediaController != null) {
                    mMediaController.hide();
                }
                if (mGifImageView != null) {
                    mGifImageView.setImageDrawable(null);
                }
                if (mGifDrawable != null && !mGifDrawable.isRecycled()) {
                    mGifDrawable.stop();
                    mGifDrawable.recycle();
                }
                getNextEnterRoom();
            }
        };
        mDownloadGifCallback = new CommonCallback<File>() {
            @Override
            public void callback(File file) {
                if (file != null) {
                    playGif(file);
                }
            }
        };
    }

    private void getNextEnterRoom() {
        if (mQueue == null) {
            return;
        }
        LiveEnterRoomBean bean = mQueue.poll();
        if (bean == null) {
            mIsAnimating = false;
        } else {
            startAnim(bean);
        }
    }

    public void enterRoom(LiveEnterRoomBean bean) {
        if (mIsAnimating) {
            mQueue.offer(bean);
        } else {
            startAnim(bean);
        }
    }

    private void startAnim(LiveEnterRoomBean bean) {
        UserBean u = bean.getUserBean();
        LiveChatBean liveChatBean = bean.getLiveChatBean();
        if (u != null && liveChatBean != null) {
            mIsAnimating = true;
            boolean needAnim = false;
            if (u.getVipType() != 0 || liveChatBean.getGuardType() != Constants.GUARD_TYPE_NONE) {
                needAnim = true;
                ImgLoader.displayAvatar(bean.getUserBean().getAvatar(), mAvatar);
                TextRender.renderEnterRoom(mName, liveChatBean);
                mBgAnimator1.start();
                mUserAnimator1.start();
            }
            UserBean.Car car = u.getCar();
            if (car != null && car.getId() != 0) {
                String url = car.getSwf();
                if (!TextUtils.isEmpty(url)) {
                    needAnim = true;
                    mShowGif = true;
                    mWordText.setText(u.getUserNiceName() + car.getWords());
                    GifCacheUtil.getFile(Constants.GIF_CAR_PREFIX + car.getId(), url, mDownloadGifCallback);
                }
            }
            if (!needAnim) {
                getNextEnterRoom();
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
        params.height = (int) (mScreenWidth * h / w);
        mGifImageView.setLayoutParams(params);
    }

    /**
     * 播放gif
     */
    private void playGif(File file) {
        if (mEnd) {
            return;
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
                mHandler.sendEmptyMessageDelayed(0, duration);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(0, 4000);
            }
        }
    }

    public void cancelAnim() {
        HttpUtil.cancel(HttpConsts.DOWNLOAD_GIF);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mBgAnimator1 != null) {
            mBgAnimator1.cancel();
        }
        if (mBgAnimator2 != null) {
            mBgAnimator2.cancel();
        }
        if (mBgAnimator3 != null) {
            mBgAnimator3.cancel();
        }
        if (mUserAnimator1 != null) {
            mUserAnimator1.cancel();
        }
        if (mUserAnimator2 != null) {
            mUserAnimator2.cancel();
        }
        if (mUserAnimator3 != null) {
            mUserAnimator3.cancel();
        }
        if (mStar != null) {
            mStar.clearAnimation();
        }
        if (mQueue != null) {
            mQueue.clear();
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
        mIsAnimating = false;
    }


    public void resetAnimView() {
        if (mBg != null) {
            mBg.setTranslationX(mDp500);
        }
        if (mUserGroup != null) {
            mUserGroup.setTranslationX(-mDp500);
        }
        if (mAvatar != null) {
            mAvatar.setImageDrawable(null);
        }
        if (mName != null) {
            mName.setText("");
        }
    }

    public void release() {
        mEnd = true;
        cancelAnim();
        mDownloadGifCallback = null;
        mHandler = null;
    }
}
