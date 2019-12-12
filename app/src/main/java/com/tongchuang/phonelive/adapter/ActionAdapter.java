package com.tongchuang.phonelive.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.bean.TicketBean;

public class ActionAdapter extends RefreshAdapter<TicketBean> {
    private View.OnClickListener mOnClickListener;

    public ActionAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag != null) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick((TicketBean) tag, 0);
                    }
                }
            }
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ActionAdapter.Vh(mInflater.inflate(R.layout.item_action, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((ActionAdapter.Vh) vh).setData(mList.get(position));
    }

    class Vh extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvTime;

        public Vh(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(TicketBean bean) {
            itemView.setTag(bean);
            tvTitle.setText(bean.getTitle());
            tvTime.setText(bean.getStime());
        }
    }
}
