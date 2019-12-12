package com.tongchuang.phonelive.game;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.activity.LiveActivity;
import com.tongchuang.phonelive.game.bean.BankerBean;
import com.tongchuang.phonelive.game.bean.GameParam;
import com.tongchuang.phonelive.game.socket.SocketGameUtil;
import com.tongchuang.phonelive.game.views.AbsGameViewHolder;
import com.tongchuang.phonelive.game.views.GameEbbViewHolder;
import com.tongchuang.phonelive.game.views.GameHdViewHolder;
import com.tongchuang.phonelive.game.views.GameNzViewHolder;
import com.tongchuang.phonelive.game.views.GameZjhViewHolder;
import com.tongchuang.phonelive.game.views.GameZpViewHolder;
import com.tongchuang.phonelive.socket.SocketClient;
import com.tongchuang.phonelive.utils.ToastUtil;
import com.tongchuang.phonelive.utils.WordUtil;

import java.util.List;

/**
 * Created by cxf on 2018/10/31.
 */

public class GamePresenter {

    private static final int GAME_ACTION_ZJH = 1;//智勇三张
    private static final int GAME_ACTION_HD = 2;//海盗船长
    private static final int GAME_ACTION_ZP = 3;//幸运转盘
    private static final int GAME_ACTION_NZ = 4;//开心牛仔
    private static final int GAME_ACTION_EBB = 5;//二八贝

    private GameParam mGameParam;
    private Context mContext;
    private SocketClient mSocketClient;
    private List<Integer> mGameList;
    private AbsGameViewHolder mGameViewHolder;
    private GameSoundPool mGameSoundPool;
    private boolean mEnd;
    private BankerBean mBankerBean;
    private String mBankerLimitString;

    public GamePresenter() {
        mGameSoundPool = new GameSoundPool();
    }

    public GamePresenter(GameParam param) {
        this();
        setGameParam(param);
    }

    public void setGameParam(GameParam param) {
        mGameParam = param;
        mContext = param.getContext();
        mSocketClient = param.getSocketClient();
        boolean anchor = param.isAnchor();
        JSONObject obj = param.getObj();
        mBankerBean = new BankerBean(
                obj.getString("game_bankerid"),
                obj.getString("game_banker_name"),
                obj.getString("game_banker_avatar"),
                obj.getString("game_banker_coin"));
        mBankerLimitString = WordUtil.getString(R.string.game_nz_apply_sz_yajin_zd) + obj.getString("game_banker_limit") + param.getCoinName();
        if (!anchor) {
            int gameAction = obj.getIntValue("gameaction");
            int betTime = obj.getIntValue("gametime");
            int[] totalBet = obj.getObject("game", int[].class);
            int[] myBet = obj.getObject("gamebet", int[].class);
            if (gameAction != 0 && betTime > 0 && totalBet.length > 0 && myBet.length > 0) {
                createGameViewHolder(gameAction);
                if (mGameViewHolder != null) {
                    mGameViewHolder.setGameID(obj.getString("gameid"));
                    mGameViewHolder.setBetTime(betTime);
                    mGameViewHolder.setTotalBet(totalBet);
                    mGameViewHolder.setMyBet(myBet);
                    mGameViewHolder.enterRoomOpenGameWindow();
                }
            }
        }
    }

    public void setGameList(List<Integer> gameList) {
        mGameList = gameList;
    }

    public List<Integer> getGameList() {
        return mGameList;
    }

    /**
     * 开始游戏
     */
    public void startGame(int gameAction) {
        if (((LiveActivity) mContext).isLinkMic() || ((LiveActivity) mContext).isLinkMicAnchor()) {
            ToastUtil.show(R.string.live_link_mic_cannot_game);
            return;
        }
        if (mGameViewHolder != null && mGameViewHolder.isBetStarted()) {
            ToastUtil.show(R.string.game_wait_end);
            return;
        }
        if (mSocketClient == null) {
            return;
        }
        switch (gameAction) {
            case GAME_ACTION_ZJH:
                SocketGameUtil.zjhShowGameWindow(mSocketClient);
                break;
            case GAME_ACTION_HD:
                SocketGameUtil.hdShowGameWindow(mSocketClient);
                break;
            case GAME_ACTION_ZP:
                SocketGameUtil.zpShowGameWindow(mSocketClient);
                break;
            case GAME_ACTION_NZ:
                SocketGameUtil.nzShowGameWindow(mSocketClient);
                break;
            case GAME_ACTION_EBB:
                SocketGameUtil.ebbShowGameWindow(mSocketClient);
                break;
        }
    }

    /**
     * 关闭游戏
     */
    public void closeGame() {
        if (mGameViewHolder != null) {
            mGameViewHolder.anchorCloseGame();
        }
    }

    private void createGameViewHolder(int gameAction) {
        AbsGameViewHolder gameViewHolder = null;
        switch (gameAction) {
            case GAME_ACTION_ZJH:
                gameViewHolder = new GameZjhViewHolder(mGameParam, mGameSoundPool);
                break;
            case GAME_ACTION_HD:
                gameViewHolder = new GameHdViewHolder(mGameParam, mGameSoundPool);
                break;
            case GAME_ACTION_ZP:
                gameViewHolder = new GameZpViewHolder(mGameParam, mGameSoundPool);
                break;
            case GAME_ACTION_NZ:
                if (mBankerBean != null) {
                    gameViewHolder = new GameNzViewHolder(mGameParam, mGameSoundPool, mBankerBean, mBankerLimitString);
                }
                break;
            case GAME_ACTION_EBB:
                gameViewHolder = new GameEbbViewHolder(mGameParam, mGameSoundPool);
                break;
        }
        if (gameViewHolder != null) {
            mGameViewHolder = gameViewHolder;
            ((LiveActivity) mContext).setGamePlaying(true);
        }
    }


    /**
     * 收到 智勇三张 socket回调
     */
    public void onGameZjhSocket(JSONObject obj) {
        if (mEnd) {
            return;
        }
        int action = obj.getIntValue("action");
        if (action == SocketGameUtil.GAME_ACTION_OPEN_WINDOW) {
            if (mGameViewHolder != null) {
                mGameViewHolder.removeFromParent();
                mGameViewHolder.release();
                mGameViewHolder = null;
            }
            createGameViewHolder(GAME_ACTION_ZJH);
        } else if (action == SocketGameUtil.GAME_ACTION_CREATE) {
            if (mGameViewHolder != null) {
                if (!(mGameViewHolder instanceof GameZjhViewHolder)) {
                    mGameViewHolder.removeFromParent();
                    mGameViewHolder.release();
                    mGameViewHolder = null;
                    createGameViewHolder(GAME_ACTION_ZJH);
                }
            } else {
                createGameViewHolder(GAME_ACTION_ZJH);
            }
        }
        if (mGameViewHolder != null && mGameViewHolder instanceof GameZjhViewHolder) {
            mGameViewHolder.handleSocket(action, obj);
        }
        if (action == SocketGameUtil.GAME_ACTION_CLOSE) {
            mGameViewHolder = null;
            ((LiveActivity) mContext).setGamePlaying(false);
        }
    }

    /**
     * 收到 海盗船长 socket回调
     */
    public void onGameHdSocket(JSONObject obj) {
        if (mEnd) {
            return;
        }
        int action = obj.getIntValue("action");
        if (action == SocketGameUtil.GAME_ACTION_OPEN_WINDOW) {
            if (mGameViewHolder != null) {
                mGameViewHolder.removeFromParent();
                mGameViewHolder.release();
                mGameViewHolder = null;
            }
            createGameViewHolder(GAME_ACTION_HD);
        } else if (action == SocketGameUtil.GAME_ACTION_CREATE) {
            if (mGameViewHolder != null) {
                if (!(mGameViewHolder instanceof GameHdViewHolder)) {
                    mGameViewHolder.removeFromParent();
                    mGameViewHolder.release();
                    mGameViewHolder = null;
                    createGameViewHolder(GAME_ACTION_HD);
                }
            } else {
                createGameViewHolder(GAME_ACTION_HD);
            }
        }
        if (mGameViewHolder != null && mGameViewHolder instanceof GameHdViewHolder) {
            mGameViewHolder.handleSocket(action, obj);
        }
        if (action == SocketGameUtil.GAME_ACTION_CLOSE) {
            mGameViewHolder = null;
            ((LiveActivity) mContext).setGamePlaying(false);
        }
    }

    /**
     * 收到 幸运转盘 socket回调
     */
    public void onGameZpSocket(JSONObject obj) {
        if (mEnd) {
            return;
        }
        int action = obj.getIntValue("action");
        if (action == SocketGameUtil.GAME_ACTION_OPEN_WINDOW) {
            if (mGameViewHolder != null) {
                mGameViewHolder.removeFromParent();
                mGameViewHolder.release();
                mGameViewHolder = null;
            }
            createGameViewHolder(GAME_ACTION_ZP);
        } else if (action == SocketGameUtil.GAME_ACTION_NOTIFY_BET) {
            if (mGameViewHolder != null) {
                if (!(mGameViewHolder instanceof GameZpViewHolder)) {
                    mGameViewHolder.removeFromParent();
                    mGameViewHolder.release();
                    mGameViewHolder = null;
                    createGameViewHolder(GAME_ACTION_ZP);
                }
            } else {
                createGameViewHolder(GAME_ACTION_ZP);
            }
        }
        if (mGameViewHolder != null && mGameViewHolder instanceof GameZpViewHolder) {
            mGameViewHolder.handleSocket(action, obj);
        }
        if (action == SocketGameUtil.GAME_ACTION_CLOSE) {
            mGameViewHolder = null;
            ((LiveActivity) mContext).setGamePlaying(false);
        }
    }

    /**
     * 收到 开心牛仔 socket回调
     */
    public void onGameNzSocket(JSONObject obj) {
        if (mEnd) {
            return;
        }
        int action = obj.getIntValue("action");
        if (action == SocketGameUtil.GAME_ACTION_OPEN_WINDOW) {
            if (mGameViewHolder != null) {
                mGameViewHolder.removeFromParent();
                mGameViewHolder.release();
                mGameViewHolder = null;
            }
            createGameViewHolder(GAME_ACTION_NZ);
        } else if (action == SocketGameUtil.GAME_ACTION_CREATE) {
            if (mGameViewHolder != null) {
                if (!(mGameViewHolder instanceof GameNzViewHolder)) {
                    mGameViewHolder.removeFromParent();
                    mGameViewHolder.release();
                    mGameViewHolder = null;
                    createGameViewHolder(GAME_ACTION_NZ);
                }
            } else {
                createGameViewHolder(GAME_ACTION_NZ);
            }
        }
        if (mGameViewHolder != null && mGameViewHolder instanceof GameNzViewHolder) {
            mGameViewHolder.handleSocket(action, obj);
        }
        if (action == SocketGameUtil.GAME_ACTION_CLOSE) {
            mGameViewHolder = null;
            ((LiveActivity) mContext).setGamePlaying(false);
        }
    }

    /**
     * 收到 二八贝 socket回调
     */
    public void onGameEbbSocket(JSONObject obj) {
        if (mEnd) {
            return;
        }
        int action = obj.getIntValue("action");
        if (action == SocketGameUtil.GAME_ACTION_OPEN_WINDOW) {
            if (mGameViewHolder != null) {
                mGameViewHolder.removeFromParent();
                mGameViewHolder.release();
                mGameViewHolder = null;
            }
            createGameViewHolder(GAME_ACTION_EBB);
        } else if (action == SocketGameUtil.GAME_ACTION_CREATE) {
            if (mGameViewHolder != null) {
                if (!(mGameViewHolder instanceof GameEbbViewHolder)) {
                    mGameViewHolder.removeFromParent();
                    mGameViewHolder.release();
                    mGameViewHolder = null;
                    createGameViewHolder(GAME_ACTION_EBB);
                }
            } else {
                createGameViewHolder(GAME_ACTION_EBB);
            }
        }
        if (mGameViewHolder != null && mGameViewHolder instanceof GameEbbViewHolder) {
            mGameViewHolder.handleSocket(action, obj);
        }
        if (action == SocketGameUtil.GAME_ACTION_CLOSE) {
            mGameViewHolder = null;
            ((LiveActivity) mContext).setGamePlaying(false);
        }
    }

    public void setLastCoin(String coin) {
        if (mGameViewHolder != null) {
            mGameViewHolder.setLastCoin(coin);
        }
    }

    public void clearGame(){
        if(mGameViewHolder!=null){
            mGameViewHolder.hideGameWindow();
            mGameViewHolder.release();
        }
        mGameViewHolder = null;
        if(mContext!=null){
            ((LiveActivity) mContext).setGamePlaying(false);
        }
    }


    public void release() {
        mEnd = true;
        if (mGameSoundPool != null) {
            mGameSoundPool.release();
        }
        if (mContext != null) {
            ((LiveActivity) mContext).setGamePlaying(false);
        }
        mContext = null;
        mGameList = null;
        mSocketClient = null;
        if (mGameViewHolder != null) {
            mGameViewHolder.release();
        }
        mGameViewHolder = null;
    }

}
