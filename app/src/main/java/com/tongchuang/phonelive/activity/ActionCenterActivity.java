package com.tongchuang.phonelive.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.FrameLayout;

import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.utils.WordUtil;
import com.tongchuang.phonelive.views.ActionCenterViewHolder;

public class ActionCenterActivity extends AbsActivity {

    public static void forward(Context context) {
        context.startActivity(new Intent(context, ActionCenterActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_action_center;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.action_center));

        ActionCenterViewHolder mainHomeTicketViewHolder = new ActionCenterViewHolder(mContext, (FrameLayout) findViewById(R.id.layoutActionCenter));
        mainHomeTicketViewHolder.addToParent();
        mainHomeTicketViewHolder.loadData();
    }
}
