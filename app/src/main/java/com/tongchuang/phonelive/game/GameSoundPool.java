package com.tongchuang.phonelive.game;

import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

import com.tongchuang.phonelive.AppContext;

/**
 * Created by cxf on 2017/10/16.
 */

public class GameSoundPool {

    public static final int GAME_SOUND_BET_START = 0;//开始下注提示音
    public static final int GAME_SOUND_BET_CHOOSE = 1;//选择下注提示音
    public static final int GAME_SOUND_BET_SUCCESS = 2;//下注成功提示音
    public static final int GAME_SOUND_RESULT = 3;//显示结果提示音

    private SoundPool mSoundPool;
    private SparseIntArray mSparseIntArray;

    public GameSoundPool() {
        mSparseIntArray = new SparseIntArray();
        mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        mSparseIntArray.put(GAME_SOUND_BET_START, mSoundPool.load(AppContext.sInstance, com.tongchuang.game.R.raw.game_bet_start, 1));
        mSparseIntArray.put(GAME_SOUND_BET_CHOOSE, mSoundPool.load(AppContext.sInstance, com.tongchuang.game.R.raw.game_bet_choose, 1));
        mSparseIntArray.put(GAME_SOUND_BET_SUCCESS, mSoundPool.load(AppContext.sInstance, com.tongchuang.game.R.raw.game_bet_success, 1));
        mSparseIntArray.put(GAME_SOUND_RESULT, mSoundPool.load(AppContext.sInstance, com.tongchuang.game.R.raw.game_show_result, 1));
    }

    public void play(int key) {
        mSoundPool.play(mSparseIntArray.get(key), 1f, 1f, 0, 0, 1);
    }

    public void release() {
        mSoundPool.release();
    }

}
