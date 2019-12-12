package com.tongchuang.phonelive.custom;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.utils.WordUtil;

public class AdvertSkipView extends CountDownTimer {
    private AdvertSkipFinishListener advertSkipFinishListener;
    private TextView btn;// 按钮

    public void setAdvertSkipFinishListener(AdvertSkipFinishListener advertSkipFinishListener) {
        this.advertSkipFinishListener = advertSkipFinishListener;
    }

    // 一个是总的时间millisInFuture，一个是计时时间countDownInterval，然后就是你在哪个按钮上做这个是，就把这个按钮传过来就可以了
    public AdvertSkipView(long millisInFuture, long countDownInterval, TextView btn) {
        super(millisInFuture, countDownInterval);
        this.btn = btn;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        long second = millisUntilFinished / 1000;
        btn.setText(WordUtil.getString(R.string.recommend_skip) + second);
        if (second == 0 && advertSkipFinishListener != null) {
            advertSkipFinishListener.onFinish();
        }
    }

    @Override
    public void onFinish() {

    }

    public interface AdvertSkipFinishListener {
        void onFinish();
    }
}
