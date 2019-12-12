package com.tongchuang.phonelive.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tongchuang.phonelive.R;
import com.tongchuang.video.custom.NumberProgressBar;

/**
 * Created by cxf on 2018/12/6.
 * 视频处理进度条
 */

public class VideoProcessViewHolder extends AbsViewHolder implements View.OnClickListener {

    private TextView mTitle;
    private String mTitleString;
    private NumberProgressBar mProgressBar;
    private ActionListener mActionListener;

    public VideoProcessViewHolder(Context context, ViewGroup parentView, String title) {
        super(context, parentView, title);
    }

    @Override
    protected void processArguments(Object... args) {
        mTitleString = (String) args[0];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_video_process;
    }

    @Override
    public void init() {
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setText(mTitleString);
        mProgressBar = (NumberProgressBar) findViewById(R.id.progressbar);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                if (mActionListener != null) {
                    mActionListener.onCancelProcessClick();
                }
                break;
        }
    }

    public void setProgress(int progress) {
        if (mProgressBar != null) {
            mProgressBar.setProgress(progress);
        }
    }

    public interface ActionListener {
        void onCancelProcessClick();
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }
}
