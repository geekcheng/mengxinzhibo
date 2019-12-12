package com.tongchuang.phonelive.game.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.game.bean.GameNzLsBean;

import java.util.List;

/**
 * Created by cxf on 2018/11/5.
 */

public class GameNzLsAdapter extends RecyclerView.Adapter<GameNzLsAdapter.Vh> {

    private List<GameNzLsBean> mList;
    private LayoutInflater mInflater;

    public GameNzLsAdapter(Context context, List<GameNzLsBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.game_item_nz_ls, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mNum;
        TextView mCard;
        TextView mResult;
        View mLine;

        public Vh(View itemView) {
            super(itemView);
            mNum = itemView.findViewById(R.id.num);
            mCard = itemView.findViewById(R.id.card);
            mResult = itemView.findViewById(R.id.result);
            mLine = itemView.findViewById(R.id.line);
        }

        void setData(GameNzLsBean bean, int position) {
            mNum.setText(String.valueOf(mList.size() - position));
            mCard.setText(bean.getBanker_card());
            mResult.setText(bean.getBanker_profit());
            if (position == mList.size() - 1) {
                if (mLine.getVisibility() == View.VISIBLE) {
                    mLine.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mLine.getVisibility() != View.VISIBLE) {
                    mLine.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
