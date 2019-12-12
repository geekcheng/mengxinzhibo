package com.tongchuang.phonelive.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.bean.CoinBean;
import com.tongchuang.phonelive.utils.WordUtil;

import java.util.ArrayList;

/**
 * Created by cxf on 2018/10/23.
 */

public class CoinAdapter extends RefreshAdapter<CoinBean> {
    private View.OnClickListener mOnClickListener;
    private String mGiveString;
    private String mCoinName;

    public CoinAdapter(Context context, String coinName) {
        super(context);
        mList = new ArrayList<>();
        mGiveString = WordUtil.getString(R.string.coin_give);
        mCoinName = coinName;
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick((CoinBean) tag, 0);
                }
            }
        };
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_coin, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position));
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mCoin;
        TextView mMoney;
        TextView mGive;

        public Vh(View itemView) {
            super(itemView);
            mCoin = itemView.findViewById(R.id.coin);
            mMoney = itemView.findViewById(R.id.money);
            mGive = itemView.findViewById(R.id.give);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(CoinBean bean) {
            itemView.setTag(bean);
            mCoin.setText(bean.getCoin());
            mMoney.setText("￥" + bean.getMoney());
            if (!"0".equals(bean.getGive())) {
                if (mGive.getVisibility() != View.VISIBLE) {
                    mGive.setVisibility(View.VISIBLE);
                }
                mGive.setText(mGiveString + bean.getGive() + mCoinName);
            } else {
                if (mGive.getVisibility() == View.VISIBLE) {
                    mGive.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
