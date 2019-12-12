package com.tongchuang.phonelive.game.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tongchuang.game.custom.ZpBetView;
import com.tongchuang.phonelive.AppConfig;
import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.event.GameWindowEvent;
import com.tongchuang.phonelive.game.GameSoundPool;
import com.tongchuang.phonelive.game.bean.GameParam;
import com.tongchuang.phonelive.game.socket.SocketGameUtil;
import com.tongchuang.phonelive.http.HttpCallback;
import com.tongchuang.phonelive.http.HttpConsts;
import com.tongchuang.phonelive.http.HttpUtil;
import com.tongchuang.phonelive.utils.DialogUitl;
import com.tongchuang.phonelive.utils.DpUtil;
import com.tongchuang.phonelive.utils.L;
import com.tongchuang.phonelive.utils.ScreenDimenUtil;
import com.tongchuang.phonelive.utils.ToastUtil;
import com.tongchuang.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2018/10/31.
 * 幸运转盘 游戏
 */

public class GameZpViewHolder extends AbsGameViewHolder {

    private static final int WHAT_READY_END = 101;//准备倒计时结束
    private static final int WHAT_BET_ANIM_DISMISS = 103;//开始下注横条消失
    private static final int WHAT_BET_COUNT_DOWN = 104;//下注倒计时
    private static final int WHAT_GAME_NEXT = 105;//开始下次游戏
    private static final int WHAT_GET_RESULT = 106;//观众获取游戏输赢结果
    private static final int WHAT_HIDE_DIALOG = 107;//隐藏弹窗
    private static final int WHAT_LIGHT = 108;//灯闪烁
    private static final int MAX_REPEAT_COUNT = 6;
    private TextView mTip;//提示的横条
    private TextView mReadyCountDown;//准备开始倒计时的TextView
    private int mRepeatCount;
    private ZpBetView[] mRoles;
    private TextView mBetCountDown;//下注倒计时的TextView
    private TextView mCoinTextView;//显示用户余额的TextView
    private Animation mReadyAnim;//准备开始倒计时的动画
    private Animation mTipHideAnim;//提示横条隐藏的动画
    private Animation mTipShowAnim;//提示横条显示的动画
    private Handler mHandler;
    private int mBetCount;
    private String mWinString;
    private View mPan;
    private ImageView mLight;
    private boolean mLightFlag;
    private ObjectAnimator mPanIdleAnim;//转盘待机动画
    private ObjectAnimator mPanResultAnim;//转盘展示结果的动画
    private int mResultIndex;
    private GameZpResultViewHolder mGameZpResultViewHolder;

    public GameZpViewHolder(GameParam param, GameSoundPool gameSoundPool) {
        super(param, gameSoundPool);
        boolean anchor = param.isAnchor();
        int h = ScreenDimenUtil.getInstance().getScreenWdith() / 2 + DpUtil.dp2px(20);
        mGameViewHeight = anchor ? h : h + DpUtil.dp2px(40);
        if (!anchor) {
            ViewStub viewStub = (ViewStub) findViewById(R.id.view_stub);
            View view = viewStub.inflate();
            view.findViewById(R.id.btn_bet_shi).setOnClickListener(this);
            view.findViewById(R.id.btn_bet_bai).setOnClickListener(this);
            view.findViewById(R.id.btn_bet_qian).setOnClickListener(this);
            view.findViewById(R.id.btn_bet_wan).setOnClickListener(this);
            mCoinTextView = (TextView) view.findViewById(R.id.coin);
            mCoinTextView.setOnClickListener(this);
            for (View v : mRoles) {
                v.setOnClickListener(this);
            }
            mBetMoney = 10;
            HttpUtil.getCoin(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        setLastCoin(JSONObject.parseObject(info[0]).getString("coin"));
                    }
                }
            });
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.game_view_zp;
    }


    @Override
    public void init() {
        mTip = (TextView) findViewById(R.id.tip);
        mReadyCountDown = (TextView) findViewById(R.id.count_down_1);
        mPan = findViewById(R.id.pan);
        mLight = (ImageView) findViewById(R.id.light);
        mRoles = new ZpBetView[4];
        mRoles[0] = (ZpBetView) findViewById(R.id.role_1);
        mRoles[1] = (ZpBetView) findViewById(R.id.role_2);
        mRoles[2] = (ZpBetView) findViewById(R.id.role_3);
        mRoles[3] = (ZpBetView) findViewById(R.id.role_4);
        mBetCountDown = (TextView) findViewById(R.id.count_down_2);
        //转盘待机动画
        mPanIdleAnim = ObjectAnimator.ofFloat(mPan, "rotation", 0, 359);
        mPanIdleAnim.setRepeatCount(-1);
        mPanIdleAnim.setDuration(8000);
        mPanIdleAnim.setInterpolator(new LinearInterpolator());
        //转盘展示结果的动画
        mPanResultAnim = ObjectAnimator.ofFloat(mPan, "rotation", 0);
        mPanResultAnim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                playGameSound(GameSoundPool.GAME_SOUND_RESULT);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                showResult();
            }
        });
        mPanResultAnim.setDuration(6000);
        mPanResultAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        mReadyAnim = new ScaleAnimation(4, 1, 4, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mReadyAnim.setDuration(1000);
        mReadyAnim.setRepeatCount(MAX_REPEAT_COUNT);
        mReadyAnim.setRepeatMode(Animation.RESTART);
        mReadyAnim.setInterpolator(new AccelerateInterpolator());
        mReadyAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mReadyCountDown != null && mReadyCountDown.getVisibility() == View.VISIBLE) {
                    mReadyCountDown.setVisibility(View.INVISIBLE);//隐藏准备倒计时
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                mReadyCountDown.setText(String.valueOf(mRepeatCount));
                mRepeatCount--;
            }
        });

        mTipShowAnim = new ScaleAnimation(0.2f, 1, 0.2f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mTipShowAnim.setDuration(500);
        mTipHideAnim = new ScaleAnimation(1, 0.2f, 1, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mTipHideAnim.setDuration(500);
        mTipHideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mTip != null && mTip.getVisibility() == View.VISIBLE) {
                    mTip.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WHAT_READY_END://准备倒计时结束
                        anchorCreateGame();
                        break;
                    case WHAT_BET_ANIM_DISMISS:
                        if (mTip != null) {
                            mTip.startAnimation(mTipHideAnim);
                        }
                        break;
                    case WHAT_BET_COUNT_DOWN://下注倒计时
                        betCountDown();
                        break;
                    case WHAT_GAME_NEXT:
                        nextGame();
                        break;
                    case WHAT_GET_RESULT:
                        getGameResult();
                        break;
                    case WHAT_HIDE_DIALOG:
                        if (mGameZpResultViewHolder != null) {
                            mGameZpResultViewHolder.hide();
                        }
                        break;
                    case WHAT_LIGHT:
                        light();
                        break;
                }
            }
        };
        mWinString = WordUtil.getString(R.string.game_win);
    }

    /**
     * 显示观众的余额
     */
    @Override
    public void setLastCoin(String coin) {
        if (mCoinTextView != null) {
            mCoinTextView.setText(coin + " " + mChargeString);
        }
    }

    /**
     * 处理socket回调的数据
     */
    public void handleSocket(int action, JSONObject obj) {
        if (mEnd) {
            return;
        }
        L.e(mTag, "-----handleSocket--------->" + obj.toJSONString());
        switch (action) {
            case SocketGameUtil.GAME_ACTION_OPEN_WINDOW://打开游戏窗口
                onGameWindowShow();
                break;
            case SocketGameUtil.GAME_ACTION_CLOSE://主播关闭游戏
                onGameClose();
                break;
            case SocketGameUtil.GAME_ACTION_NOTIFY_BET://开始下注
                onGameBetStart(obj);
                break;
            case SocketGameUtil.GAME_ACTION_BROADCAST_BET://收到下注消息
                onGameBetChanged(obj);
                break;
            case SocketGameUtil.GAME_ACTION_RESULT://收到游戏结果揭晓的的消息
                onGameResult(obj);
                break;
        }
    }

    /**
     * 灯闪烁
     */
    private void startLight() {
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(WHAT_LIGHT, 1000);
        }
    }


    /**
     * 灯闪烁
     */
    private void light() {
        if (mLight != null) {
            if (mLightFlag) {
                mLight.setImageResource(R.mipmap.icon_zp_light_1);
            } else {
                mLight.setImageResource(R.mipmap.icon_zp_light_2);
            }
            mLightFlag = !mLightFlag;
            startLight();
        }
    }


    /**
     * 所有人收到 打开游戏窗口的socket后，   打开游戏窗口，启动角色待机动画，进入8秒准备倒计时，
     */
    private void onGameWindowShow() {
        if (!mShowed) {
            showGameWindow();
            mBetStarted = false;
            mRepeatCount = MAX_REPEAT_COUNT;
            mReadyCountDown.setText(String.valueOf(mRepeatCount + 1));
            mReadyCountDown.startAnimation(mReadyAnim);
            if (mAnchor && mHandler != null) {
                mHandler.sendEmptyMessageDelayed(WHAT_READY_END, 7000);
            }
            startLight();
        }
    }


    /**
     * 主播在8秒准备时间结束后，请求接口，创建游戏
     */
    @Override
    public void anchorCreateGame() {
        if (!mAnchor) {
            return;
        }
        HttpUtil.gameLuckPanCreate(mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mGameID = obj.getString("gameid");
                    mGameToken = obj.getString("token");
                    mBetTime = obj.getIntValue("time");
                    SocketGameUtil.zpAnchorNotifyGameBet(mSocketClient, mLiveUid, mGameID, mGameToken, mBetTime);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    /**
     * 收到主播通知下注的socket,播放动画，开始下注倒计时
     */
    private void onGameBetStart(JSONObject obj) {
        if (!mShowed) {
            showGameWindow();
            startLight();
        }
        mBetStarted = true;
        if (!mAnchor) {
            mGameID = obj.getString("gameid");
            mGameToken = obj.getString("token");
            mBetTime = obj.getIntValue("time");
        }
        mBetCount = mBetTime - 1;
        if (mBetCountDown != null) {
            if (mBetCountDown.getVisibility() != View.VISIBLE) {
                mBetCountDown.setVisibility(View.VISIBLE);
            }
            mBetCountDown.setText(String.valueOf(mBetCount));
        }
        if (mTip != null) {
            if (mTip.getVisibility() != View.VISIBLE) {
                mTip.setVisibility(View.VISIBLE);
            }
            mTip.setText(R.string.game_start_support);
            mTip.startAnimation(mTipShowAnim);
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(WHAT_BET_COUNT_DOWN, 1000);
            mHandler.sendEmptyMessageDelayed(WHAT_BET_ANIM_DISMISS, 1500);
        }
        playGameSound(GameSoundPool.GAME_SOUND_BET_START);
        if (mPanIdleAnim != null) {
            mPanIdleAnim.start();
        }
    }

    /**
     * 下注倒计时
     */
    private void betCountDown() {
        mBetCount--;
        if (mBetCount > 0) {
            mBetCountDown.setText(String.valueOf(mBetCount));
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(WHAT_BET_COUNT_DOWN, 1000);
            }
        } else {
            mBetCountDown.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * 观众下注
     */
    private void audienceBetGame(final int index) {
        HttpUtil.gameJinhuaBet(mGameID, mBetMoney, index, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    setLastCoin(JSON.parseObject(info[0]).getString("coin"));
                    SocketGameUtil.zpAudienceBetGame(mSocketClient, mBetMoney, index);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * 所有人收到下注的观众socket，更新下注金额
     */
    private void onGameBetChanged(JSONObject obj) {
        String uid = obj.getString("uid");
        int money = obj.getIntValue("money");
        int index = obj.getIntValue("type") - 1;
        boolean isSelf = uid.equals(AppConfig.getInstance().getUid());
        if (isSelf) {//自己下的注
            playGameSound(GameSoundPool.GAME_SOUND_BET_SUCCESS);
        }
        if (mRoles != null) {
            if (index >= 0 && index < 4) {
                if (mRoles[index] != null) {
                    mRoles[index].updateBetVal(money, isSelf);
                }
            }
        }
    }

    /**
     * 收到游戏结果揭晓的的消息
     */
    private void onGameResult(JSONObject obj) {
        if (mTip != null) {
            if (mTip.getVisibility() != View.VISIBLE) {
                mTip.setVisibility(View.VISIBLE);
            }
            mTip.setText(R.string.game_show_result);//揭晓结果
            mTip.startAnimation(mTipShowAnim);
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(WHAT_BET_ANIM_DISMISS, 1500);
            }
        }
        if (mPanIdleAnim != null) {
            mPanIdleAnim.cancel();
        }
        if (mPan != null) {
            mPan.setRotation(0);
        }
        int[] result = JSON.parseObject(obj.getString("ct"), int[].class);
        if (mPanResultAnim != null) {
            mResultIndex = result[0];
            mPanResultAnim.setFloatValues((1 - result[0]) * 18 + 360 * 6);
            mPanResultAnim.start();
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(WHAT_GAME_NEXT, 16000);
        }
    }

    private void showResult() {
        if (mGameZpResultViewHolder == null) {
            mGameZpResultViewHolder = new GameZpResultViewHolder(mContext, mParentView);
            mGameZpResultViewHolder.addToParent();
        }
        mGameZpResultViewHolder.setData(mResultIndex);
        mGameZpResultViewHolder.show();
        if (mHandler != null) {
            if (!mAnchor) {
                mHandler.sendEmptyMessageDelayed(WHAT_GET_RESULT, 500);
            }
            mHandler.sendEmptyMessageDelayed(WHAT_HIDE_DIALOG, 8000);
        }
    }


    @Override
    protected void getGameResult() {
        HttpUtil.gameSettle(mGameID, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    setLastCoin(obj.getString("coin"));
                    int winCoin = obj.getIntValue("gamecoin");
                    if (winCoin > 0) {
                        DialogUitl.showSimpleTipDialog(mContext, mWinString, winCoin + mCoinName);
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * 游戏中途进入直播间的打开游戏窗口
     */
    @Override
    public void enterRoomOpenGameWindow() {
        if (!mShowed) {
            showGameWindow();
            mBetCount = mBetTime - 1;
            if (mBetCount > 0 && mBetCountDown != null) {
                if (mBetCountDown.getVisibility() != View.VISIBLE) {
                    mBetCountDown.setVisibility(View.VISIBLE);
                }
                mBetCountDown.setText(String.valueOf(mBetCount));
            }
            startLight();
            //启动角色待机动画
            if (mPanIdleAnim != null) {
                mPanIdleAnim.start();
            }
            if (mTip != null) {
                if (mTip.getVisibility() != View.VISIBLE) {
                    mTip.setVisibility(View.VISIBLE);
                }
                mTip.setText(R.string.game_start_support);
                mTip.startAnimation(mTipShowAnim);
            }
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(WHAT_BET_COUNT_DOWN, 1000);
                mHandler.sendEmptyMessageDelayed(WHAT_BET_ANIM_DISMISS, 1500);
            }
            playGameSound(GameSoundPool.GAME_SOUND_BET_START);
        }
    }


    /**
     * 开始下次游戏
     */
    @Override
    protected void nextGame() {
        mBetStarted = false;
        mRepeatCount = MAX_REPEAT_COUNT;
        if (mTip != null) {
            if (mTip.getVisibility() != View.VISIBLE) {
                mTip.setVisibility(View.VISIBLE);
            }
            mTip.setText(R.string.game_wait_start);
        }
        if (mReadyCountDown != null) {
            if (mReadyCountDown.getVisibility() != View.VISIBLE) {
                mReadyCountDown.setVisibility(View.VISIBLE);
            }
            mReadyCountDown.setText(String.valueOf(mRepeatCount + 1));
            mReadyCountDown.startAnimation(mReadyAnim);
        }
        if (mRoles != null) {
            for (ZpBetView view : mRoles) {
                if (view != null) {
                    view.reset();
                }
            }
        }
        if (mAnchor && mHandler != null) {
            mHandler.sendEmptyMessageDelayed(WHAT_READY_END, 7000);
        }
    }

    @Override
    public void anchorCloseGame() {
        if (mBetStarted) {
            ToastUtil.show(R.string.game_wait_end);
            return;
        }
        SocketGameUtil.zpAnchorCloseGame(mSocketClient);
        EventBus.getDefault().post(new GameWindowEvent(false));
    }


    /**
     * 主播关闭游戏的回调
     */
    private void onGameClose() {
        L.e(mTag, "---------onGameClose----------->");
        hideGameWindow();
        release();
    }

    @Override
    public void removeFromParent() {
        super.removeFromParent();
        if (mGameZpResultViewHolder != null) {
            mGameZpResultViewHolder.removeFromParent();
        }
    }

    @Override
    public void release() {
        mEnd = true;
        HttpUtil.cancel(HttpConsts.GET_COIN);
        HttpUtil.cancel(HttpConsts.GAME_LUCK_PAN_CREATE);
        HttpUtil.cancel(HttpConsts.GAME_LUCK_PAN_BET);
        HttpUtil.cancel(HttpConsts.GAME_SETTLE);
        super.release();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        if (mGameZpResultViewHolder != null) {
            mGameZpResultViewHolder.release();
        }
        if (mPanIdleAnim != null) {
            mPanIdleAnim.cancel();
        }
        if (mPanResultAnim != null) {
            mPanResultAnim.cancel();
        }
        if (mReadyCountDown != null) {
            mReadyCountDown.clearAnimation();
        }
        if (mTip != null) {
            mTip.clearAnimation();
        }
        L.e(mTag, "---------release----------->");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.role_1:
                audienceBetGame(1);
                break;
            case R.id.role_2:
                audienceBetGame(2);
                break;
            case R.id.role_3:
                audienceBetGame(3);
                break;
            case R.id.role_4:
                audienceBetGame(4);
                break;
            case R.id.btn_bet_shi:
                mBetMoney = 10;
                playGameSound(GameSoundPool.GAME_SOUND_BET_CHOOSE);
                break;
            case R.id.btn_bet_bai:
                mBetMoney = 100;
                playGameSound(GameSoundPool.GAME_SOUND_BET_CHOOSE);
                break;
            case R.id.btn_bet_qian:
                mBetMoney = 1000;
                playGameSound(GameSoundPool.GAME_SOUND_BET_CHOOSE);
                break;
            case R.id.btn_bet_wan:
                mBetMoney = 10000;
                playGameSound(GameSoundPool.GAME_SOUND_BET_CHOOSE);
                break;
            case R.id.coin://充值
                forwardCharge();
                break;
        }
    }
}
