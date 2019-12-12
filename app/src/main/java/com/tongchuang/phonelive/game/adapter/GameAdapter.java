package com.tongchuang.phonelive.game.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tongchuang.game.R;
import com.tongchuang.game.util.GameIconUtil;
import com.tongchuang.phonelive.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/31.
 */

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.Vh> {

    private List<Integer> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<Integer> mOnItemClickListener;

    public GameAdapter(Context context, List<Integer> list) {
        mList = list;
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick((Integer) tag, 0);
                }
            }
        };
    }


    public void setOnItemClickListener(OnItemClickListener<Integer> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_game, parent, false));
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

        ImageView mIcon;
        TextView mName;

        public Vh(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.icon);
            mName = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(Integer gameAction) {
            itemView.setTag(gameAction);
            mIcon.setImageResource(GameIconUtil.getGameIcon(gameAction));
            mName.setText(GameIconUtil.getGameName(gameAction));
        }
    }
}
