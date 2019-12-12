package com.tongchuang.phonelive.game.views;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSONObject;
import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.activity.MyCoinActivity;
import com.tongchuang.phonelive.custom.LiveLightView;
import com.tongchuang.phonelive.event.GameWindowEvent;
import com.tongchuang.phonelive.game.GameSoundPool;
import com.tongchuang.phonelive.game.bean.GameParam;
import com.tongchuang.phonelive.socket.SocketClient;
import com.tongchuang.phonelive.utils.WordUtil;
import com.tongchuang.phonelive.views.AbsViewHolder;
import com.tongchuang.phonelive.views.LiveRoomViewHolder;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2018/10/31.
 */

public abstract class AbsGameViewHolder extends AbsViewHolder implements View.OnClickListener {

    protected Context mContext;
    protected String mTag;
    protected String mCoinName;
    protected String mChargeString;
    protected int mBetMoney;//每次下注的量
    protected String mLiveUid;
    protected String mStream;
    protected View mTopView;
    protected int mGameViewHeight;//游戏部分的高度
    protected boolean mAnchor;
    protected boolean mShowed;
    protected SocketClient mSocketClient;
    protected GameSoundPool mGameSoundPool;
    protected boolean mEnd;
    protected String mGameID;
    protected String mGameToken;
    protected int mBetTime;
    protected int[] mTotalBet;
    protected int[] mMyBet;
    protected boolean mBetStarted;


    public AbsGameViewHolder(GameParam gameParam, GameSoundPool gameSoundPool) {
        super(gameParam.getContext(), gameParam.getParentView());
        mContext = gameParam.getContext();
        mTopView = gameParam.getTopView();
        mSocketClient = gameParam.getSocketClient();
        mLiveUid = gameParam.getLiveUid();
        mStream = gameParam.getStream();
        mAnchor = gameParam.isAnchor();
        mCoinName = gameParam.getCoinName();
        mGameSoundPool = gameSoundPool;
        mTag = getClass().getSimpleName();
        mChargeString = WordUtil.getString(R.string.game_charge);
    }

    /**
     * 显示游戏窗口
     */
    public void showGameWindow() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTopView.getLayoutParams();
        params.setMargins(0, 0, 0, mGameViewHeight);
        mTopView.setLayoutParams(params);
        LiveLightView.sOffsetY = mGameViewHeight;
        LiveRoomViewHolder.sOffsetY = mGameViewHeight;
        addToParent();
        if (mAnchor) {
            EventBus.getDefault().post(new GameWindowEvent(true));
        }
    }

    /**
     * 隐藏游戏窗口
     */
    public void hideGameWindow() {
        if (mShowed) {
            removeFromParent();
            LiveLightView.sOffsetY = 0;
            LiveRoomViewHolder.sOffsetY = 0;
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTopView.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            mTopView.setLayoutParams(params);
        }
    }

    /**
     * 播放游戏音效
     */
    protected void playGameSound(int key) {
        if (mGameSoundPool != null) {
            mGameSoundPool.play(key);
        }
    }

    public abstract void handleSocket(int action, JSONObject obj);

    /**
     * 主播创建游戏
     */
    protected abstract void anchorCreateGame();

    /**
     * 观众进入直播间，如果游戏正在进行，则打开游戏窗口
     */
    public abstract void enterRoomOpenGameWindow();

    /**
     * 主播关闭游戏
     */
    public abstract void anchorCloseGame();


    /**
     * 观众获取游戏的结果  输赢等
     */
    protected abstract void getGameResult();

    /**
     * 开始下次游戏
     */
    protected abstract void nextGame();

    /**
     * 设置剩余的钻石
     */
    public abstract void setLastCoin(String coin);


    public void release() {
        mGameSoundPool = null;
        mContext = null;
        mParentView = null;
        mTopView = null;
        mSocketClient = null;
        mLiveUid = null;
        mStream = null;
    }


    @Override
    public void addToParent() {
        super.addToParent();
        mShowed = true;
    }

    @Override
    public void removeFromParent() {
        super.removeFromParent();
        mShowed = false;
    }

    /**
     * 前往充值
     */
    protected void forwardCharge() {
        MyCoinActivity.forward(mContext);
    }

    public void setGameID(String gameID) {
        mGameID = gameID;
    }

    public void setGameToken(String gameToken) {
        mGameToken = gameToken;
    }

    public void setBetTime(int betTime) {
        mBetTime = betTime;
    }

    public void setTotalBet(int[] totalBet) {
        mTotalBet = totalBet;
    }

    public void setMyBet(int[] myBet) {
        mMyBet = myBet;
    }

    public boolean isBetStarted() {
        return mBetStarted;
    }
}
