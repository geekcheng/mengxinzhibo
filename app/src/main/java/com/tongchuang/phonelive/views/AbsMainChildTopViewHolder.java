package com.tongchuang.phonelive.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.activity.MainActivity;
import com.tongchuang.phonelive.bean.LiveBean;

/**
 * Created by cxf on 2018/9/22.
 * MainActivity中的首页，附近 的子页面，具有头部的导航条，继承自 AbsMainChildViewHolder
 */

public abstract class AbsMainChildTopViewHolder extends AbsMainChildViewHolder {

    protected ViewGroup mTopContainer; //放置头部导航条的容器

    public AbsMainChildTopViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void init() {
        super.init();
        mTopContainer = (ViewGroup) findViewById(R.id.top_container);
    }

    public void addTopView(View view) {
        if (view != null && mTopContainer != null) {
            ViewParent parent = view.getParent();
            if (parent != null) {
                if (parent != mTopContainer) {
                    ((ViewGroup) parent).removeView(view);
                    mTopContainer.addView(view);
                }
            } else {
                mTopContainer.addView(view);
            }
        }
    }

    public void removeTopView() {
        if (mTopContainer != null && mTopContainer.getChildCount() > 0) {
            mTopContainer.removeAllViews();
        }
    }

    /**
     * 观看直播
     */
    public void watchLive(LiveBean liveBean, String key, int position) {
        ((MainActivity) mContext).watchLive(liveBean, key, position);
    }
}
