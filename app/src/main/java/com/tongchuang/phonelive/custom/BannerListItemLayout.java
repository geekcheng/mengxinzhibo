package com.tongchuang.phonelive.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class BannerListItemLayout extends RelativeLayout {

    public BannerListItemLayout(Context context) {
        super(context);
    }

    public BannerListItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BannerListItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (widthSize * 1 / 4), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}