package com.tongchuang.phonelive.game.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.game.bean.GameNzSzBean;

import java.util.List;

/**
 * Created by cxf on 2018/11/5.
 */

public class GameNzSzAdapter extends RecyclerView.Adapter<GameNzSzAdapter.Vh> {

    private List<GameNzSzBean> mList;
    private LayoutInflater mInflater;

    public GameNzSzAdapter(Context context, List<GameNzSzBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.game_item_nz_sz, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mName;
        TextView mCoin;

        public Vh(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mCoin = itemView.findViewById(R.id.coin);
        }

        void setData(GameNzSzBean bean) {
            mName.setText(bean.getUser_nicename());
            if ("0".equals(bean.getId())) {
                mCoin.setText(bean.getCoin());
            } else {
                mCoin.setText(bean.getDeposit());
            }

        }
    }
}
