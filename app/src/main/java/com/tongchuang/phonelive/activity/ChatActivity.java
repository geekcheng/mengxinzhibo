package com.tongchuang.phonelive.activity;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.im.ImUserBean;
import com.tongchuang.phonelive.views.ChatListViewHolder;

/**
 * Created by cxf on 2018/10/24.
 */

public class ChatActivity extends AbsActivity {

    private ChatListViewHolder mChatListViewHolder;

    public static void forward(Context context) {
        context.startActivity(new Intent(context, ChatActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_list;
    }

    @Override
    protected void main() {
        mChatListViewHolder = new ChatListViewHolder(mContext, (ViewGroup) findViewById(R.id.root),ChatListViewHolder.TYPE_ACTIVITY);
        mChatListViewHolder.setActionListener(new ChatListViewHolder.ActionListener() {
            @Override
            public void onCloseClick() {
                onBackPressed();
            }

            @Override
            public void onItemClick(ImUserBean bean) {
                ChatRoomActivity.forward(mContext, bean, bean.getAttent() == 1);
            }
        });
        mChatListViewHolder.addToParent();
        mChatListViewHolder.loadData();
    }

    @Override
    protected void onDestroy() {
        if (mChatListViewHolder != null) {
            mChatListViewHolder.release();
        }
        super.onDestroy();
    }
}
