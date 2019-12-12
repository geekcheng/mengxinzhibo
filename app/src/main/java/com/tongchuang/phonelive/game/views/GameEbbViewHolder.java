package com.tongchuang.phonelive.game.views;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tongchuang.game.custom.GameEbbView;
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
import com.tongchuang.phonelive.utils.ToastUtil;
import com.tongchuang.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2018/10/31.
 * 二八贝 游戏
 */

public class GameEbbViewHolder extends AbsGameViewHolder {

    private static final int WHAT_READY_END = 101;//准备倒计时结束
    private static final int WHAT_CARD_ANIM_START = 102;//角色缩小，播放发牌动画
    private static final int WHAT_BET_ANIM_DISMISS = 103;//开始下注横条消失
    private static final int WHAT_BET_COUNT_DOWN = 104;//下注倒计时
    private static final int WHAT_GAME_RESULT = 105;//揭晓游戏结果
    private static final int WHAT_GAME_NEXT = 106;//开始下次游戏
    private static final int WHAT_GAME_RESULT_ANIM = 107;
    private static final int MAX_REPEAT_COUNT = 6;
    private TextView mTip;//提示的横条
    private TextView mReadyCountDown;//准备开始倒计时的TextView
    private int mRepeatCount;
    private GameEbbView[] mRoles;
    private TextView mBetCountDown;//下注倒计时的TextView
    private TextView mCoinTextView;//显示用户余额的TextView
    private View mCoverImg;//结束时的遮罩
    private Animation mReadyAnim;//准备开始倒计时的动画
    private Animation mTipHideAnim;//提示横条隐藏的动画
    private Animation mTipShowAnim;//提示横条显示的动画
    private Handler mHandler;
    private int mBetCount;
    private int mWinIndex;//哪个角色获胜了
    private String mWinString;

    public GameEbbViewHolder(GameParam param, GameSoundPool gameSoundPool) {
        super(param, gameSoundPool);
        boolean anchor = param.isAnchor();
        mGameViewHeight = anchor ? DpUtil.dp2px(150) : DpUtil.dp2px(190);
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
        return R.layout.game_view_ebb;
    }


    @Override
    public void init() {
        mTip = (TextView) findViewById(R.id.tip);
        mReadyCountDown = (TextView) findViewById(R.id.count_down_1);
        mRoles = new GameEbbView[3];
        mRoles[0] = (GameEbbView) findViewById(R.id.role_1);
        mRoles[1] = (GameEbbView) findViewById(R.id.role_2);
        mRoles[2] = (GameEbbView) findViewById(R.id.role_3);
        mBetCountDown = (TextView) findViewById(R.id.count_down_2);
        mCoverImg = findViewById(R.id.cover);
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
                    case WHAT_CARD_ANIM_START://角色缩小，播放发牌动画
                        playCardAnim();
                        break;
                    case WHAT_BET_ANIM_DISMISS:
                        if (mTip != null) {
                            mTip.startAnimation(mTipHideAnim);
                        }
                        break;
                    case WHAT_BET_COUNT_DOWN://下注倒计时
                        betCountDown();
                        break;
                    case WHAT_GAME_RESULT://揭晓游戏结果
                        showGameResult(msg.arg1, (String[]) msg.obj);
                        break;
                    case WHAT_GAME_NEXT:
                        nextGame();
                        break;
                    case WHAT_GAME_RESULT_ANIM:
                        showResultAnim(msg.arg1);
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
            case SocketGameUtil.GAME_ACTION_CREATE://游戏被创建
                onGameCreate();
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
        HttpUtil.gameEbbCreate(mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mGameID = obj.getString("gameid");
                    mGameToken = obj.getString("token");
                    mBetTime = obj.getIntValue("time");
                    SocketGameUtil.ebbAnchorCreateGame(mSocketClient, mGameID);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    /**
     * 所有人收到游戏被创建的socket后，开始执行发牌动画
     */
    private void onGameCreate() {
        if (!mShowed) {
            showGameWindow();
            if (mTip != null && mTip.getVisibility() == View.VISIBLE) {
                mTip.setVisibility(View.INVISIBLE);
            }
        }
        if (mTip != null && mTipHideAnim != null && mTip.getVisibility() == View.VISIBLE) {
            mTip.startAnimation(mTipHideAnim);//横条消失
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(WHAT_CARD_ANIM_START, 1000);
        }
    }


    /**
     * 主播通知所有人下注
     */
    private void playCardAnim() {
        //主播通知所有人下注
        if (mAnchor) {
            SocketGameUtil.ebbAnchorNotifyGameBet(mSocketClient, mLiveUid, mGameID, mGameToken, mBetTime);
        }
    }

    /**
     * 收到主播通知下注的socket,播放动画，开始下注倒计时
     */
    private void onGameBetStart(JSONObject obj) {
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
        HttpUtil.gameEbbBet(mGameID, mBetMoney, index, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    setLastCoin(JSON.parseObject(info[0]).getString("coin"));
                    SocketGameUtil.ebbAudienceBetGame(mSocketClient, mBetMoney, index);
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
        mRoles[index].updateBetVal(money, isSelf);
        if (isSelf) {//自己下的注
            playGameSound(GameSoundPool.GAME_SOUND_BET_SUCCESS);
        }

    }

    /**
     * 收到游戏结果揭晓的的消息
     */
    private void onGameResult(JSONObject obj) {
        mWinIndex = -1;
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
        String[][] result = JSON.parseObject(obj.getString("ct"), String[][].class);
        for (int i = 0, length = result.length; i < length; i++) {
            Message msg = Message.obtain();
            msg.what = WHAT_GAME_RESULT;
            msg.arg1 = i;
            msg.obj = result[i];
            if (mHandler != null) {
                mHandler.sendMessageDelayed(msg, i * 2000);
            }
        }
    }

    /**
     * 揭晓游戏结果
     */
    private void showGameResult(int i, String[] result) {
        mRoles[i].showResult(result[0], result[1], result[2]);
        if (mHandler != null) {
            Message msg = Message.obtain();
            msg.what = WHAT_GAME_RESULT_ANIM;
            msg.arg1 = i;
            mHandler.sendMessageDelayed(msg, 1000);
        }
        if (mWinIndex == -1) {
            if ("1".equals(result[4])) {
                mWinIndex = i;
            }
        }
        if (i == 2) {
            if (mCoverImg != null && mCoverImg.getVisibility() != View.VISIBLE) {
                mCoverImg.setVisibility(View.VISIBLE);
            }
            if (mWinIndex >= 0) {
                for (int j = 0; j < mRoles.length; j++) {
                    if (j != mWinIndex) {
                        mRoles[j].showCover();
                    }
                }
            }
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(WHAT_GAME_NEXT, 10000);//10秒后重新开始游戏
            }
            if (!mAnchor) {
                getGameResult();
            }
        }
        playGameSound(GameSoundPool.GAME_SOUND_RESULT);
    }

    private void showResultAnim(int index) {
        if (mRoles != null && mRoles[index] != null) {
            mRoles[index].showResultAnim();
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
            if (mTip != null) {
                if (mTip.getVisibility() != View.VISIBLE) {
                    mTip.setVisibility(View.VISIBLE);
                }
                mTip.setText(R.string.game_start_support);
                mTip.startAnimation(mTipShowAnim);
            }
            if (mRoles != null) {
                for (int i = 0, length = mRoles.length; i < length; i++) {
                    GameEbbView view = mRoles[i];
                    if (view != null) {
                        view.setBetVal(mTotalBet[i], mMyBet[i]);
                    }
                }
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
        if (mCoverImg != null && mCoverImg.getVisibility() == View.VISIBLE) {
            mCoverImg.setVisibility(View.INVISIBLE);
        }
        if (mRoles != null) {
            for (GameEbbView view : mRoles) {
                if (view != null) {
                    view.reset();
                }
            }
        }
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
        SocketGameUtil.ebbAnchorCloseGame(mSocketClient);
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
    public void release() {
        mEnd = true;
        HttpUtil.cancel(HttpConsts.GET_COIN);
        HttpUtil.cancel(HttpConsts.GAME_EBB_CREATE);
        HttpUtil.cancel(HttpConsts.GAME_EBB_BET);
        HttpUtil.cancel(HttpConsts.GAME_SETTLE);
        super.release();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        if (mReadyCountDown != null) {
            mReadyCountDown.clearAnimation();
        }
        if (mTip != null) {
            mTip.clearAnimation();
        }
        if (mRoles != null) {
            for (GameEbbView v : mRoles) {
                if (v != null) {
                    v.clearAnim();
                }
            }
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
